package io.papermc.generator;

import io.papermc.generator.rewriter.types.registry.definition.RegistryDefinitionRewriters;
import io.papermc.generator.types.SourceGenerator;
import io.papermc.generator.types.craftblockdata.CraftBlockDataGenerators;
import io.papermc.generator.types.goal.MobGoalGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.Util;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface Generators {

    List<SourceGenerator> API = Collections.unmodifiableList(Util.make(new ArrayList<>(), list -> {
        RegistryDefinitionRewriters.bootstrap(list);
        list.add(new MobGoalGenerator("VanillaGoal", "com.destroystokyo.paper.entity.ai"));
        // todo extract fields for registry based api
    }));

    List<SourceGenerator> SERVER = Collections.unmodifiableList(Util.make(new ArrayList<>(), CraftBlockDataGenerators::bootstrap));
}
