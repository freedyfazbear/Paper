package org.bukkit.inventory;

/**
 * A item's rarity determines the default color of its name. This enum is
 * ordered from least rare to most rare.
 */
public enum ItemRarity {

    // Paper start - Generated/ItemRarity
    // @GeneratedFrom 1.21.1
    COMMON(net.kyori.adventure.text.format.NamedTextColor.WHITE),
    UNCOMMON(net.kyori.adventure.text.format.NamedTextColor.YELLOW),
    RARE(net.kyori.adventure.text.format.NamedTextColor.AQUA),
    EPIC(net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE);
    // Paper end - Generated/ItemRarity
    // Paper start - improve ItemRarity
    private final net.kyori.adventure.text.format.NamedTextColor color;
    ItemRarity(final net.kyori.adventure.text.format.NamedTextColor color) {
        this.color = color;
    }

    /**
     * Gets the color formatting associated with this rarity.
     *
     * @return the color
     */
    public net.kyori.adventure.text.format.@org.jetbrains.annotations.NotNull TextColor color() {
        return this.color;
    }
    // Paper end
}
