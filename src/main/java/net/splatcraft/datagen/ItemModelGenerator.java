package net.splatcraft.datagen;

import net.minecraft.item.SpawnEggItem;
import net.splatcraft.Splatcraft;
import net.splatcraft.block.SplatcraftBlocks;
import net.splatcraft.datagen.impl.generator.model.InheritingModelGen;
import net.splatcraft.datagen.impl.generator.model.item.AbstractItemModelGenerator;
import net.splatcraft.entity.SplatcraftEntities;

public class ItemModelGenerator extends AbstractItemModelGenerator {
    public ItemModelGenerator() {
        super(Splatcraft.MOD_ID);
    }

    @Override
    public void generate() {
        this.add(SplatcraftBlocks.CANVAS);
        this.add(SplatcraftBlocks.GRATE, (block) -> InheritingModelGen.inherit(name(block, "block/%s_bottom")));

        this.add(SpawnEggItem.forEntity(SplatcraftEntities.INK_SQUID), spawnEgg());
    }
}
