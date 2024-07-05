package io.papermc.generator.rewriter.types.simple;

import io.papermc.typewriter.replace.SearchMetadata;
import io.papermc.typewriter.replace.SearchReplaceRewriter;
import java.awt.Color;
import net.minecraft.world.level.material.MapColor;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MapPaletteRewriter extends SearchReplaceRewriter {

    @Override
    protected void insert(final SearchMetadata metadata, final StringBuilder builder) {
        for (final @Nullable MapColor mapColor : MapColor.MATERIAL_COLORS) {
            if (mapColor == null) {
                continue;
            }

            for (final MapColor.Brightness brightness : MapColor.Brightness.values()) {
                builder.append(metadata.indent());
                Color color = fromABGR(mapColor.calculateRGBColor(brightness)); // int is encoded as 0xFF << 24 | blue << 16 | green << 8 | red (BGR and not RGB)
                if (color.getAlpha() != 0xFF) {
                    builder.append("new %s(%d, %d, %d, %d),".formatted(color.getClass().getSimpleName(), color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
                } else {
                    builder.append("new %s(%d, %d, %d),".formatted(color.getClass().getSimpleName(), color.getRed(), color.getGreen(), color.getBlue()));
                }
                builder.append('\n');
            }
        }
    }

    private static final int EXCEPT_RED_MASK = 0xFFFFFF00;
    private static final int RED_MASK = ~EXCEPT_RED_MASK;
    private static final int EXCEPT_BLUE_MASK = 0xFF00FFFF;
    private static final int BLUE_MASK = ~EXCEPT_BLUE_MASK;

    private Color fromABGR(int abgr) {
        int red = abgr & RED_MASK;
        int blue = (abgr & BLUE_MASK) >> 16;
        return new Color((abgr & EXCEPT_RED_MASK & EXCEPT_BLUE_MASK) | (red << 16) | blue, true);
    }
}
