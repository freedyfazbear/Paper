package io.papermc.generator.rewriter.registration;

import io.papermc.generator.rewriter.utils.Annotations;
import io.papermc.generator.types.SimpleGenerator;
import io.papermc.paper.generated.GeneratedFrom;
import io.papermc.typewriter.ClassNamed;
import io.papermc.typewriter.IndentUnit;
import io.papermc.typewriter.SourceFile;
import io.papermc.typewriter.SourceRewriter;
import io.papermc.typewriter.registration.SourceSetRewriterImpl;
import io.papermc.typewriter.replace.CompositeRewriter;
import io.papermc.typewriter.replace.ReplaceOptions;
import io.papermc.typewriter.replace.ReplaceOptionsLike;
import io.papermc.typewriter.replace.SearchReplaceRewriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.SharedConstants;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@DefaultQualifier(NonNull.class)
public class PaperPatternSourceSetRewriter extends SourceSetRewriterImpl<PatternSourceSetRewriter> implements PatternSourceSetRewriter {

    private static final String PAPER_START_FORMAT = "Paper start";
    private static final String PAPER_END_FORMAT = "Paper end";
    private static final String COMMENT_MARKER_FORMAT = "%s - Generated/%s"; // {0} = PAPER_START_FORMAT|PAPER_END_FORMAT {1} = pattern
    private static final IndentUnit INDENT_UNIT = IndentUnit.parse(SimpleGenerator.INDENT_UNIT);

    @Deprecated
    private final Path alternateOutput;

    @Deprecated
    public PaperPatternSourceSetRewriter(Path alternateOutput) {
        this.alternateOutput = alternateOutput;
    }

    @Deprecated
    public Path getAlternateOutput() {
        return this.alternateOutput;
    }

    private static ReplaceOptionsLike getOptions(String pattern, @Nullable ClassNamed targetClass) {
        return ReplaceOptions.between(
                COMMENT_MARKER_FORMAT.formatted(PAPER_START_FORMAT, pattern),
                COMMENT_MARKER_FORMAT.formatted(PAPER_END_FORMAT, pattern)
            )
            .generatedComment(Annotations.annotationStyle(GeneratedFrom.class) + " " + SharedConstants.getCurrentVersion().getName())
            .targetClass(targetClass);
    }

    @Override
    public PatternSourceSetRewriter register(final String pattern, final ClassNamed targetClass, final SearchReplaceRewriter rewriter) {
        return super.register(SourceFile.of(targetClass.topLevel(), INDENT_UNIT), rewriter.withOptions(getOptions(pattern, targetClass)).customName(pattern));
    }

    @Override
    public PatternSourceSetRewriter register(final ClassNamed mainClass, final CompositeRewriter rewriter) {
        return super.register(SourceFile.of(mainClass, INDENT_UNIT), rewriter);
    }

    @Contract(value = "_ -> new", pure = true)
    public static CompositeRewriter composite(final RewriterHolder... holders) {
        return CompositeRewriter.bind(Arrays.stream(holders)
            .map(holder -> holder.transform(PaperPatternSourceSetRewriter::getOptions))
            .toArray(SearchReplaceRewriter[]::new));
    }

    @Override
    public void apply(final Path output) throws IOException { // todo remove
        for (Map.Entry<SourceFile, SourceRewriter> rewriter : this.rewrites.entrySet()) {
            rewriter.getValue().writeToFile(output, this.alternateOutput, rewriter.getKey());
        }
    }
}
