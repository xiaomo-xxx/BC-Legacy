package com.thepigcat.buildcraft.events;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.capabilties.JumboItemHandler;
import com.thepigcat.buildcraft.content.blockentities.CrateBE;
import com.thepigcat.buildcraft.content.blocks.CrateBlock;
import com.thepigcat.buildcraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

@EventBusSubscriber(modid = BuildcraftLegacy.MODID)
public final class CommonEvents {
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        if (!level.isClientSide()) {
            BlockPos pos = event.getPos();
            BlockState blockState = level.getBlockState(pos);
            if (blockState.getBlock() instanceof CrateBlock && event.getFace().equals(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING))) {
                CrateBE be = BlockUtils.getBE(CrateBE.class, level, pos);
                if (be != null) {
                    JumboItemHandler itemHandler = be.getItemHandler();
                    ItemStack stack = itemHandler.getStackInSlot(0);
                    int count = 1;
                    Player player = event.getEntity();
                    if (player.isShiftKeyDown()) {
                        count = Math.min(stack.getMaxStackSize(), stack.getCount());
                    }
                    if (count > 0 && !stack.isEmpty()) {
                        ItemStack extracted = itemHandler.extractItem(0, count, false);
                        ItemHandlerHelper.giveItemToPlayer(player, extracted);
                        event.setCanceled(true); // prevent block break when extracting items
                    }
                }
            }
        }
    }
}
