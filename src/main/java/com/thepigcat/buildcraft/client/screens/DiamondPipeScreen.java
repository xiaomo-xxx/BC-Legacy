package com.thepigcat.buildcraft.client.screens;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.content.menus.DiamondPipeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Diamond pipe filter GUI screen.
 * 6 colored rows (one per direction) × 9 filter slots
 */
public class DiamondPipeScreen extends AbstractContainerScreen<DiamondPipeMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    private static final int[] SIDE_COLORS = {
            0xFFFFFFFF,  // DOWN  - White
            0xFFFF8C00,  // UP    - Orange
            0xFF00B4B4,  // NORTH - Cyan
            0xFF64C8FF,  // SOUTH - Light Blue
            0xFFFFFF00,  // WEST  - Yellow
            0xFF00FF00,  // EAST  - Lime
    };

    private static final String[] SIDE_LABELS = {"D", "U", "N", "S", "W", "E"};

    public DiamondPipeScreen(DiamondPipeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelY = 6;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Draw colored direction bars on the right edge of each filter row
        for (int side = 0; side < 6; side++) {
            int barX = x + 170;
            int barY = y + 18 + side * 18;
            guiGraphics.fill(barX, barY, barX + 4, barY + 16, SIDE_COLORS[side]);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Title
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);

        // Direction labels before the filter slots
        for (int side = 0; side < 6; side++) {
            int labelX = 0;
            int labelY = 22 + side * 18;
            guiGraphics.drawString(font, SIDE_LABELS[side], labelX, labelY, SIDE_COLORS[side] | 0xFF000000, false);
        }

        // Inventory label above player inventory
        guiGraphics.drawString(font, Component.translatable("container.inventory"), 8, 127, 0x404040, false);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
