package com.github.dirtpowered.releasetobeta.utils;

import com.github.dirtpowered.betaprotocollib.utils.BetaItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.pmw.tinylog.Logger;

import java.util.Arrays;
import java.util.Objects;

public class Utils {
    public static ItemStack betaItemStackToItemStack(BetaItemStack itemStack) {
        return new ItemStack(itemStack.getBlockId(), itemStack.getAmount(), itemStack.getData());
    }

    public static ItemStack[] convertItemStacks(BetaItemStack[] itemStacks) {
        return Arrays.stream(itemStacks).filter(Objects::nonNull).map(item ->
                new ItemStack(item.getBlockId(), item.getAmount(), item.getData())).toArray(ItemStack[]::new);
    }

    public static void debug(Object clazz) {
        Logger.info("[DEBUG] {}", ReflectionToStringBuilder.toString(clazz, ToStringStyle.SHORT_PREFIX_STYLE));
    }
}
