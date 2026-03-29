package com.thepigcat.buildcraft.datagen.data;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.PipesRegistry;
import com.thepigcat.buildcraft.api.pipes.Pipe;
import com.thepigcat.buildcraft.registries.BCBlocks;
import com.thepigcat.buildcraft.registries.BCItems;
import com.thepigcat.buildcraft.tags.BCTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BCTagProvider {
    public static void createTagProviders(DataGenerator generator, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper, boolean isServer) {
        Block provider = new Block(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(isServer, provider);
        generator.addProvider(isServer, new Item(packOutput, lookupProvider, provider.contentsGetter()));
        generator.addProvider(isServer, new Fluid(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(isServer, new Biome(packOutput, lookupProvider, existingFileHelper));
    }

    public static class Block extends BlockTagsProvider {
        public Block(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, BuildcraftLegacy.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(BlockTags.MINEABLE_WITH_AXE)
                    .add(BCBlocks.CRATE.get());
            tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .add(BCBlocks.TANK.get());

            // Add dynamically registered pipe blocks to mineable tags
            List<net.minecraft.world.level.block.Block> pickaxeBlocks = new ArrayList<>();
            List<net.minecraft.world.level.block.Block> axeBlocks = new ArrayList<>();
            List<net.minecraft.world.level.block.Block> shovelBlocks = new ArrayList<>();

            for (Map.Entry<String, Pipe> entry : PipesRegistry.PIPES.entrySet()) {
                ResourceLocation rl = BuildcraftLegacy.rl(entry.getKey());
                if (BuiltInRegistries.BLOCK.containsKey(rl)) {
                    net.minecraft.world.level.block.Block pipeBlock = BuiltInRegistries.BLOCK.get(rl);
                    List<TagKey<net.minecraft.world.level.block.Block>> tools = entry.getValue().miningTools();
                    if (tools.contains(BlockTags.MINEABLE_WITH_AXE)) {
                        axeBlocks.add(pipeBlock);
                    } else if (tools.contains(BlockTags.MINEABLE_WITH_SHOVEL)) {
                        shovelBlocks.add(pipeBlock);
                    } else {
                        pickaxeBlocks.add(pipeBlock);
                    }
                }
            }

            if (!pickaxeBlocks.isEmpty()) {
                tag(BlockTags.MINEABLE_WITH_PICKAXE).add(pickaxeBlocks.toArray(new net.minecraft.world.level.block.Block[0]));
            }
            if (!axeBlocks.isEmpty()) {
                tag(BlockTags.MINEABLE_WITH_AXE).add(axeBlocks.toArray(new net.minecraft.world.level.block.Block[0]));
            }
            if (!shovelBlocks.isEmpty()) {
                tag(BlockTags.MINEABLE_WITH_SHOVEL).add(shovelBlocks.toArray(new net.minecraft.world.level.block.Block[0]));
            }
        }
    }

    public static class Item extends ItemTagsProvider {
        public Item(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<net.minecraft.world.level.block.Block>> blockTags) {
            super(output, lookupProvider, blockTags);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(BCTags.Items.GEARS).add(
                    BCItems.WOODEN_GEAR.get(),
                    BCItems.STONE_GEAR.get(),
                    BCItems.IRON_GEAR.get(),
                    BCItems.GOLD_GEAR.get()
            );
            tag(BCTags.Items.WOODEN_GEAR).add(BCItems.WOODEN_GEAR.get());
            tag(BCTags.Items.STONE_GEAR).add(BCItems.STONE_GEAR.get());
            tag(BCTags.Items.IRON_GEAR).add(BCItems.IRON_GEAR.get());
            tag(BCTags.Items.GOLD_GEAR).add(BCItems.GOLD_GEAR.get());
        }
    }

    public static class Fluid extends FluidTagsProvider {
        public Fluid(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, provider, BuildcraftLegacy.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
        }
    }

    public static class Biome extends BiomeTagsProvider {
        public Biome(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, provider, BuildcraftLegacy.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
        }
    }

}
