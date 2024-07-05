package org.bukkit;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the art on a painting
 */
public enum Art implements Keyed {
    // Paper start - Generated/Art
    // @GeneratedFrom 1.21.1
    ALBAN(0, 1, 1),
    AZTEC(1, 1, 1),
    AZTEC2(2, 1, 1),
    BACKYARD(3, 3, 4),
    BAROQUE(4, 2, 2),
    BOMB(5, 1, 1),
    BOUQUET(6, 3, 3),
    BURNING_SKULL(7, 4, 4),
    BUST(8, 2, 2),
    CAVEBIRD(9, 3, 3),
    CHANGING(10, 4, 2),
    COTAN(11, 3, 3),
    COURBET(12, 2, 1),
    CREEBET(13, 2, 1),
    DONKEY_KONG(14, 4, 3),
    EARTH(15, 2, 2),
    ENDBOSS(16, 3, 3),
    FERN(17, 3, 3),
    FIGHTERS(18, 4, 2),
    FINDING(19, 4, 2),
    FIRE(20, 2, 2),
    GRAHAM(21, 1, 2),
    HUMBLE(22, 2, 2),
    KEBAB(23, 1, 1),
    LOWMIST(24, 4, 2),
    MATCH(25, 2, 2),
    MEDITATIVE(26, 1, 1),
    ORB(27, 4, 4),
    OWLEMONS(28, 3, 3),
    PASSAGE(29, 4, 2),
    PIGSCENE(30, 4, 4),
    PLANT(31, 1, 1),
    POINTER(32, 4, 4),
    POND(33, 3, 4),
    POOL(34, 2, 1),
    PRAIRIE_RIDE(35, 1, 2),
    SEA(36, 2, 1),
    SKELETON(37, 4, 3),
    SKULL_AND_ROSES(38, 2, 2),
    STAGE(39, 2, 2),
    SUNFLOWERS(40, 3, 3),
    SUNSET(41, 2, 1),
    TIDES(42, 3, 3),
    UNPACKED(43, 4, 4),
    VOID(44, 2, 2),
    WANDERER(45, 1, 2),
    WASTELAND(46, 1, 1),
    WATER(47, 2, 2),
    WIND(48, 2, 2),
    WITHER(49, 2, 2);
    // Paper end - Generated/Art

    private final int id, width, height;
    private final NamespacedKey key;
    private static final HashMap<String, Art> BY_NAME = Maps.newHashMap();
    private static final HashMap<Integer, Art> BY_ID = Maps.newHashMap();

    private Art(int id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.key = NamespacedKey.minecraft(name().toLowerCase(Locale.ROOT));
    }

    /**
     * Gets the width of the painting, in blocks
     *
     * @return The width of the painting, in blocks
     */
    public int getBlockWidth() {
        return width;
    }

    /**
     * Gets the height of the painting, in blocks
     *
     * @return The height of the painting, in blocks
     */
    public int getBlockHeight() {
        return height;
    }

    /**
     * Get the ID of this painting.
     *
     * @return The ID of this painting
     * @apiNote Internal Use Only
     */
    @org.jetbrains.annotations.ApiStatus.Internal // Paper
    public int getId() {
        return id;
    }

    // Paper start - deprecate getKey
    /**
     * @deprecated use {@link Registry#getKey(Keyed)} and {@link Registry#ART}. Painting variants
     * can exist without a key.
     */
    @Deprecated(since = "1.21")
    // Paper end - deprecate getKey
    @NotNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    /**
     * Get a painting by its numeric ID
     *
     * @param id The ID
     * @return The painting
     * @apiNote Internal Use Only
     */
    @org.jetbrains.annotations.ApiStatus.Internal // Paper
    @Nullable
    public static Art getById(int id) {
        return BY_ID.get(id);
    }

    /**
     * Get a painting by its unique name
     * <p>
     * This ignores underscores and capitalization
     *
     * @param name The name
     * @return The painting
     */
    @Nullable
    public static Art getByName(@NotNull String name) {
        Preconditions.checkArgument(name != null, "Name cannot be null");

        return BY_NAME.get(name.toLowerCase(Locale.ROOT));
    }

    static {
        for (Art art : values()) {
            BY_ID.put(art.id, art);
            BY_NAME.put(art.toString().toLowerCase(Locale.ROOT), art);
        }
    }
}
