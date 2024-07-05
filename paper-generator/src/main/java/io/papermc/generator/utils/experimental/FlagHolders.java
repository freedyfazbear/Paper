package io.papermc.generator.utils.experimental;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;
import org.bukkit.MinecraftExperimental;

public class FlagHolders {

    public static final SingleFlagHolder NEXT_UPDATE = null/*SingleFlagHolder.fromValue(FeatureFlags.UPDATE_1_22)*/;
    public static final SingleFlagHolder BUNDLE = SingleFlagHolder.fromValue(FeatureFlags.BUNDLE);
    public static final SingleFlagHolder TRADE_REBALANCE = SingleFlagHolder.fromValue(FeatureFlags.TRADE_REBALANCE);

    static final Map<FeatureFlag, MinecraftExperimental.Requires> ANNOTATION_EQUIVALENT = Util.make(new HashMap<SingleFlagHolder, MinecraftExperimental.Requires>(), map -> {
        //map.put(NEXT_UPDATE, MinecraftExperimental.Requires.UPDATE_1_22);
        map.put(BUNDLE, MinecraftExperimental.Requires.BUNDLE);
        map.put(TRADE_REBALANCE, MinecraftExperimental.Requires.TRADE_REBALANCE);
    }).entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().flag(), Map.Entry::getValue));
}
