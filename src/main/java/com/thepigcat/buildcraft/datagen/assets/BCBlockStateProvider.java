package com.thepigcat.buildcraft.datagen.assets;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.blocks.ExtractingPipeBlock;
import com.thepigcat.buildcraft.api.blocks.PipeBlock;
import com.thepigcat.buildcraft.content.blocks.CrateBlock;
import com.thepigcat.buildcraft.content.blocks.TankBlock;
import com.thepigcat.buildcraft.registries.BCBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BCBlockStateProvider extends BlockStateProvider {
    public BCBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, BuildcraftLegacy.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        crateBlock(BCBlocks.CRATE.get());
        tankBlock(BCBlocks.TANK.get());

        for (Block block : BCBlocks.BLOCKS.getRegistry().get()) {
            if (block instanceof ExtractingPipeBlock) {
                extractingPipeBlock(block);
            } else if (block instanceof PipeBlock) {
                pipeBlock(block);
            }
        }
    }

    private void crateBlock(CrateBlock block) {
        horizontalBlock(block, models().cube(name(block),
                blockTexture(block, "_top"),
                blockTexture(block, "_top"),
                blockTexture(block, "_front"),
                blockTexture(block, "_side"),
                blockTexture(block, "_side"),
                blockTexture(block, "_side")
        ).texture("particle", blockTexture(block, "_top")));
    }

    private void tankBlock(Block block) {
        ResourceLocation blockTex = blockTexture(block);
        ResourceLocation topTexture = suffix(blockTex, "_top");
        ResourceLocation topJoinedTexture = suffix(blockTex, "_top_joined");
        ResourceLocation sideTexture = suffix(blockTex, "_side");
        ResourceLocation sideJoinedTexture = suffix(blockTex, "_side_joined");

        getVariantBuilder(block)
                .partialState().with(TankBlock.TOP_JOINED, true).with(TankBlock.BOTTOM_JOINED, true)
                .modelForState().modelFile(tankModel(suffix(blockTex, "_top_and_bottom_joined"), topJoinedTexture, sideJoinedTexture, topJoinedTexture)).addModel()
                .partialState().with(TankBlock.TOP_JOINED, true).with(TankBlock.BOTTOM_JOINED, false)
                .modelForState().modelFile(tankModel(suffix(blockTex, "_top_joined"), topJoinedTexture, sideTexture, topTexture)).addModel()
                .partialState().with(TankBlock.TOP_JOINED, false).with(TankBlock.BOTTOM_JOINED, true)
                .modelForState().modelFile(tankModel(suffix(blockTex, "_bottom_joined"), topTexture, sideJoinedTexture, topJoinedTexture)).addModel()
                .partialState().with(TankBlock.TOP_JOINED, false).with(TankBlock.BOTTOM_JOINED, false)
                .modelForState().modelFile(tankModel(blockTex, topTexture, sideTexture, topTexture)).addModel();
    }

    private void pillarBlock(Block block, ResourceLocation base) {
        ResourceLocation side = suffix(base, "_side");
        ResourceLocation top = suffix(base, "_top");
        simpleBlock(block, models().cube(name(block), top, top, side, side, side, side).texture("particle", side));
    }

    // ===================== Pipes =====================

    private void pipeBlock(Block block) {
        ResourceLocation loc = key(block);
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);
        pipeConnection(builder, loc, Direction.DOWN, 0, 0);
        pipeConnection(builder, loc, Direction.UP, 180, 0);
        pipeConnection(builder, loc, Direction.NORTH, 90, 180);
        pipeConnection(builder, loc, Direction.EAST, 90, 270);
        pipeConnection(builder, loc, Direction.SOUTH, 90, 0);
        pipeConnection(builder, loc, Direction.WEST, 90, 90);
        builder.part().modelFile(pipeBaseModel(loc)).addModel().end();
    }

    private void pipeConnection(MultiPartBlockStateBuilder builder, ResourceLocation loc, Direction direction, int x, int y) {
        builder.part().modelFile(pipeConnectionModel(loc)).rotationX(x).rotationY(y).addModel()
                .condition(PipeBlock.CONNECTION[direction.get3DDataValue()], PipeBlock.PipeState.CONNECTED).end();
    }

    private void extractingPipeBlock(Block block) {
        ResourceLocation loc = key(block);
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);
        extractingPipeConnection(builder, loc, Direction.DOWN, 0, 0);
        extractingPipeConnection(builder, loc, Direction.UP, 180, 0);
        extractingPipeConnection(builder, loc, Direction.NORTH, 90, 180);
        extractingPipeConnection(builder, loc, Direction.EAST, 90, 270);
        extractingPipeConnection(builder, loc, Direction.SOUTH, 90, 0);
        extractingPipeConnection(builder, loc, Direction.WEST, 90, 90);
        builder.part().modelFile(pipeBaseModel(loc)).addModel().end();
    }

    private void extractingPipeConnection(MultiPartBlockStateBuilder builder, ResourceLocation loc, Direction direction, int x, int y) {
        builder.part().modelFile(pipeConnectionModel(loc)).rotationX(x).rotationY(y).addModel()
                .condition(PipeBlock.CONNECTION[direction.get3DDataValue()], PipeBlock.PipeState.CONNECTED).end()
                .part().modelFile(pipeExtractingModel(loc)).rotationX(x).rotationY(y).addModel()
                .condition(PipeBlock.CONNECTION[direction.get3DDataValue()], PipeBlock.PipeState.EXTRACTING).end();
    }

    /**
     * Base (center junction) model.
     * Diamond: 6 directional textures with diagonal flow markers.
     * Emerald: flat texture (no directional textures exist).
     * Others: flat texture.
     */
    private ModelFile pipeBaseModel(ResourceLocation blockLoc) {
        String path = blockLoc.getPath();
        String ns = blockLoc.getNamespace();

        if (path.equals("diamond")) {
            return models().withExistingParent(path + "_base", modLoc("block/pipe_base_colored"))
                    .texture("down",  rl(ns, "block/" + path + "_down"))
                    .texture("up",    rl(ns, "block/" + path + "_up"))
                    .texture("north", rl(ns, "block/" + path + "_north"))
                    .texture("south", rl(ns, "block/" + path + "_south"))
                    .texture("west",  rl(ns, "block/" + path + "_west"))
                    .texture("east",  rl(ns, "block/" + path + "_east"));
        }

        return models().withExistingParent(path + "_base", modLoc("block/pipe_base"))
                .texture("texture", rl(ns, "block/" + path));
    }

    /**
     * Connection arm model.
     * Diamond & emerald: use dedicated "_connection" texture (clean grid, no corner diagonals).
     */
    private ModelFile pipeConnectionModel(ResourceLocation blockLoc) {
        String path = blockLoc.getPath();
        String ns = blockLoc.getNamespace();

        if (path.equals("diamond") || path.equals("emerald")) {
            return models().withExistingParent(path + "_connection", modLoc("block/pipe_connection"))
                    .texture("texture", rl(ns, "block/" + path + "_connection"));
        }
        return models().withExistingParent(path + "_connection", modLoc("block/pipe_connection"))
                .texture("texture", rl(ns, "block/" + path));
    }

    /**
     * Extracting connection arm model.
     * Diamond & emerald: use dedicated "_connection_extracting" texture with bright arrows.
     */
    private ModelFile pipeExtractingModel(ResourceLocation blockLoc) {
        String path = blockLoc.getPath();
        String ns = blockLoc.getNamespace();

        if (path.equals("diamond") || path.equals("emerald")) {
            return models().withExistingParent(path + "_connection_extracting", modLoc("block/pipe_connection"))
                    .texture("texture", rl(ns, "block/" + path + "_connection_extracting"));
        }
        return models().withExistingParent(path + "_connection_extracting", modLoc("block/pipe_connection"))
                .texture("texture", rl(ns, "block/" + path + "_extracting"));
    }

    // ===================== Tank =====================

    private ModelFile tankModel(ResourceLocation baseLoc, ResourceLocation topLoc, ResourceLocation sideLoc, ResourceLocation bottomLoc) {
        return models().withExistingParent(baseLoc.getPath(), modLoc("block/tank_base"))
                .texture("top", rl(topLoc.getNamespace(), topLoc.getPath()))
                .texture("bottom", rl(bottomLoc.getNamespace(), bottomLoc.getPath()))
                .texture("side", rl(sideLoc.getNamespace(), sideLoc.getPath()));
    }

    // ===================== Helpers =====================

    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private String name(Block block) {
        return key(block).getPath();
    }

    private ResourceLocation rl(String ns, String path) {
        return ResourceLocation.fromNamespaceAndPath(ns, path);
    }

    public ResourceLocation blockTexture(Block block, String suffix) {
        ResourceLocation loc = key(block);
        return ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath() + suffix);
    }

    public ResourceLocation blockTexture(Block block) {
        ResourceLocation loc = key(block);
        return ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath());
    }

    private ResourceLocation suffix(ResourceLocation rl, String suffix) {
        return rl.withSuffix(suffix);
    }

    private ResourceLocation prefix(String prefix, ResourceLocation rl) {
        return rl.withPrefix(prefix);
    }
}
