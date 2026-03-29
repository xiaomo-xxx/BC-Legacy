package com.thepigcat.buildcraft.content.menus;

import com.thepigcat.buildcraft.content.blockentities.DiamondItemPipeBE;
import com.thepigcat.buildcraft.registries.BCMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Diamond pipe filter GUI.
 * Layout: 6 rows × 9 filter slots + player inventory (3×9) + hotbar (1×9)
 */
public class DiamondPipeMenu extends AbstractContainerMenu {
    public final DiamondItemPipeBE blockEntity;
    private static final int SLOTS_PER_SIDE = DiamondItemPipeBE.SLOTS_PER_SIDE;

    public DiamondPipeMenu(int containerId, @NotNull Inventory inv, @NotNull DiamondItemPipeBE blockEntity) {
        super(BCMenuTypes.DIAMOND_PIPE.get(), containerId);
        this.blockEntity = blockEntity;

        // 6 rows of 9 filter slots
        // Row starts at y=20, slots at y=30 (offset 10 into colored bar)
        for (int side = 0; side < 6; side++) {
            for (int i = 0; i < SLOTS_PER_SIDE; i++) {
                int index = side * SLOTS_PER_SIDE + i;
                int slotX = 7 + i * 18;
                int slotY = 30 + side * 28;
                addSlot(new SlotItemHandler(blockEntity.getFilterHandler(), index, slotX, slotY));
            }
        }

        // Player inventory (3 rows × 9 cols), starts at y=206
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 7 + col * 18, 206 + row * 18));
            }
        }

        // Player hotbar at y=230
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 7 + col * 18, 230));
        }
    }

    public DiamondPipeMenu(int containerId, @NotNull Inventory inv, @NotNull RegistryFriendlyByteBuf buf) {
        this(containerId, inv, (DiamondItemPipeBE) inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // Shift-click from player into first empty filter slot
        if (index >= 54) {
            ItemStack stack = getSlot(index).getItem();
            if (!stack.isEmpty()) {
                for (int i = 0; i < 54; i++) {
                    if (getSlot(i).getItem().isEmpty()) {
                        getSlot(i).set(stack.copy());
                        return ItemStack.EMPTY;
                    }
                }
            }
        } else {
            // From filter slot - clear it
            getSlot(index).set(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
