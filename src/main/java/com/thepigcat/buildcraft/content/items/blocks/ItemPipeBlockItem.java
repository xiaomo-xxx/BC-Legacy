package com.thepigcat.buildcraft.content.items.blocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ItemPipeBlockItem extends BlockItem {
    public ItemPipeBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull net.minecraft.network.chat.Component getName(ItemStack stack) {
        // Use standard Minecraft translation key (item.buildcraft.<pipe_id>)
        // This allows proper i18n support for all languages
        return super.getName(stack);
    }
}
