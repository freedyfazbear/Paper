package io.papermc.generator;

import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.logging.LogUtils;
import io.papermc.generator.rewriter.registration.PaperPatternSourceSetRewriter;
import io.papermc.generator.rewriter.registration.PatternSourceSetRewriter;
import io.papermc.generator.types.SourceGenerator;
import io.papermc.generator.utils.experimental.TagCollector;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import org.apache.commons.io.file.PathUtils;
import org.slf4j.Logger;

public final class Main {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final RegistryAccess.Frozen REGISTRY_ACCESS;
    public static final Map<TagKey<?>, String> EXPERIMENTAL_TAGS;

    static {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        Bootstrap.validate();

        final PackRepository resourceRepository = ServerPacksSource.createVanillaTrustedRepository();
        resourceRepository.reload();
        final MultiPackResourceManager resourceManager = new MultiPackResourceManager(PackType.SERVER_DATA, resourceRepository.getAvailablePacks().stream().map(Pack::open).toList());
        LayeredRegistryAccess<RegistryLayer> layers = RegistryLayer.createRegistryAccess();
        layers = WorldLoader.loadAndReplaceLayer(resourceManager, layers, RegistryLayer.WORLDGEN, RegistryDataLoader.WORLDGEN_REGISTRIES);
        REGISTRY_ACCESS = layers.compositeAccess().freeze();
        final ReloadableServerResources datapack = ReloadableServerResources.loadResources(resourceManager, layers, FeatureFlags.REGISTRY.allFlags(), Commands.CommandSelection.DEDICATED, 0, MoreExecutors.directExecutor(), MoreExecutors.directExecutor()).join();
        datapack.updateRegistryTags();

        EXPERIMENTAL_TAGS = TagCollector.grabExperimental(resourceManager);
    }

    private Main() {
    }

    public static void main(final String[] args) {
        Path generatedApiPath = Path.of(args[0]); // todo remove
        Path generatedServerPath = Path.of(args[2]); // todo remove

        PatternSourceSetRewriter apiSourceSet = new PaperPatternSourceSetRewriter(generatedApiPath);
        PatternSourceSetRewriter serverSourceSet = new PaperPatternSourceSetRewriter(generatedServerPath);
        Rewriters.bootstrap(apiSourceSet, serverSourceSet);

        try {
            LOGGER.info("Running API generators...");
            generate(generatedApiPath, Generators.API);
            apiSourceSet.apply(Path.of(args[1]));

            LOGGER.info("Running Server generators...");
            generate(generatedServerPath, Generators.SERVER);
            serverSourceSet.apply(Path.of(args[3]));
        } catch (final RuntimeException ex) {
            throw ex;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void generate(final Path output, final Collection<SourceGenerator> generators) throws IOException {
        if (Files.exists(output)) {
            PathUtils.deleteDirectory(output);
        }

        for (final SourceGenerator generator : generators) {
            generator.writeToFile(output);
        }
        LOGGER.info("Files written to {}", output.toAbsolutePath());
    }
}
