package com.thepigcat.buildcraft.client.blockentities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thepigcat.buildcraft.content.blockentities.ItemPipeBE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PipeBERenderer implements BlockEntityRenderer<ItemPipeBE> {
    public PipeBERenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ItemPipeBE pipeBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        ItemStack stack = pipeBlockEntity.getItemHandler().getStackInSlot(0);
        if (stack.isEmpty()) return;

        Direction from = pipeBlockEntity.getFrom();
        Direction to = pipeBlockEntity.getTo();

        poseStack.pushPose();
        {
            if (from != null && to != null) {
                // Interpolate movement (may be negative for smooth handoff from previous pipe)
                float progress = Mth.lerp(partialTicks, pipeBlockEntity.lastMovement, pipeBlockEntity.movement);

                Vec3i fromNormal = from.getNormal();
                Vec3i toNormal = to.getNormal();

                // Start: entry face (progress = -0.5)
                // Center: pipe center (progress = 0.0)
                // End: exit face (progress = 1.0)
                // Map: at progress=-0.5 → at "from" face, at progress=0.0 → center, at progress=1.0 → "to" face
                float startX = 0.5f + fromNormal.getX() * 0.5f;
                float startY = 0.5f + fromNormal.getY() * 0.5f;
                float startZ = 0.5f + fromNormal.getZ() * 0.5f;

                float midX = 0.5f;
                float midY = 0.5f;
                float midZ = 0.5f;

                float endX = 0.5f + toNormal.getX() * 0.5f;
                float endY = 0.5f + toNormal.getY() * 0.5f;
                float endZ = 0.5f + toNormal.getZ() * 0.5f;

                float x, y, z;
                if (progress < 0f) {
                    // Smooth handoff phase: from entry face to center
                    float t = progress + 0.5f; // 0.0 to 0.5 → maps to 0.0 to 1.0
                    t = Mth.clamp(t, 0f, 1f);
                    x = Mth.lerp(t, startX, midX);
                    y = Mth.lerp(t, startY, midY);
                    z = Mth.lerp(t, startZ, midZ);
                } else {
                    // Normal phase: center to exit face with ease-in-out
                    float t = smoothStep(Mth.clamp(progress, 0f, 1f));
                    x = Mth.lerp(t, midX, endX);
                    y = Mth.lerp(t, midY, endY);
                    z = Mth.lerp(t, midZ, endZ);
                }

                poseStack.translate(x, y, z);
            } else {
                // No direction info, float at center
                poseStack.translate(0.5, 0.5, 0.5);
            }

            // Scale: 0.5 for normal items, 0.25 for block items
            float scale = 0.5f;
            if (stack.getItem() instanceof BlockItem) {
                scale = 0.25f;
            }
            poseStack.scale(scale, scale, scale);

            // Rotation: smooth continuous spin like dropped items
            long gameTime = pipeBlockEntity.getLevel() != null ? pipeBlockEntity.getLevel().getGameTime() : 0;
            float tick = gameTime + partialTicks;
            float rotation = tick * 3.0f; // degrees per tick
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));

            // Gentle bobbing animation (sinusoidal, like vanilla items)
            float bob = Mth.sin(tick * 0.15f) * 0.03f;
            float bobX = Mth.cos(tick * 0.12f) * 0.02f;
            poseStack.translate(bobX, bob, 0);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack, ItemDisplayContext.NONE, packedLight, packedOverlay,
                    poseStack, multiBufferSource, pipeBlockEntity.getLevel(), 1
            );
        }
        poseStack.popPose();
    }

    /**
     * Smooth-step easing for natural acceleration/deceleration.
     */
    private static float smoothStep(float t) {
        return t * t * (3f - 2f * t);
    }
}
