package net.splatcraft.datagen;

import net.minecraft.block.Block;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;
import net.splatcraft.block.SplatcraftBlocks;
import net.splatcraft.datagen.impl.generator.model.InheritingModelGen;
import net.splatcraft.datagen.impl.generator.model.StateGen;
import net.splatcraft.datagen.impl.generator.model.StateModelInfo;
import net.splatcraft.datagen.impl.generator.model.block.AbstractStateModelGenerator;
import net.splatcraft.datagen.impl.generator.model.block.VariantsStateGen;

public class StateModelGenerator extends AbstractStateModelGenerator {
    private static final BlockHalf[] BLOCK_HALVES = BlockHalf.values();

    public StateModelGenerator() {
        super(Splatcraft.MOD_ID);
    }

    @Override
    public void generate() {
        this.add(SplatcraftBlocks.CANVAS, block -> this.simple(name(block), cubeAllTinted(name(block))));
        this.add(SplatcraftBlocks.INKED_BLOCK, block -> this.simple(name(block), cubeAllTinted(name(block))));
        this.add(SplatcraftBlocks.GLOWING_INKED_BLOCK, block -> this.simple(name(block), cubeAllTinted(name(block))));
        this.add(SplatcraftBlocks.GRATE, this::grate);
    }

    public StateGen grate(Block block) {
        VariantsStateGen gen = VariantsStateGen.variants();

        for (BlockHalf half : BLOCK_HALVES) {
            String halfId = half.asString();
            String variant = "half=" + halfId;
            gen.variant(variant, StateModelInfo.create(name(block, "block/%s_" + halfId), sidedTrapdoor(block, half)));
        }

        return gen;
    }

    public InheritingModelGen sidedTrapdoor(Block block, BlockHalf half) {
        Identifier texture = name(block);
        Identifier side = name(block, "block/%s_side");
        return new InheritingModelGen(new Identifier(Splatcraft.MOD_ID, "block/template_sided_trapdoor_" + half.asString()))
            .texture("texture", texture)
            .texture("side", side);
    }

    public static InheritingModelGen cubeAllTinted(Identifier texture) {
        return new InheritingModelGen(new Identifier(Splatcraft.MOD_ID, "block/cube_all_tinted"))
            .texture("all", texture);
    }
}
