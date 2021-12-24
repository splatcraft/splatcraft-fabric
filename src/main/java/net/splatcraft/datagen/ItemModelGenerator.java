package net.splatcraft.datagen;

import net.minecraft.item.SpawnEggItem;
import net.splatcraft.Splatcraft;
import net.splatcraft.block.SplatcraftBlocks;
import net.splatcraft.datagen.impl.generator.model.InheritingModelGen;
import net.splatcraft.datagen.impl.generator.model.item.AbstractItemModelGenerator;
import net.splatcraft.entity.SplatcraftEntities;
import net.splatcraft.item.SplatcraftItems;

public class ItemModelGenerator extends AbstractItemModelGenerator {
    public ItemModelGenerator() {
        super(Splatcraft.MOD_ID);
    }

    @Override
    public void generate() {
        this.block(SplatcraftBlocks.CANVAS);

        this.block(SplatcraftBlocks.EMPTY_INKWELL);
        this.block(SplatcraftBlocks.INKWELL);

        this.block(SplatcraftBlocks.GRATE_BLOCK);
        this.add(SplatcraftBlocks.GRATE, (block) -> InheritingModelGen.inherit(name(block, "block/%s_bottom")));

        this.block(SplatcraftBlocks.STAGE_BARRIER);
        this.block(SplatcraftBlocks.STAGE_VOID);

        this.add(SpawnEggItem.forEntity(SplatcraftEntities.INK_SQUID), spawnEgg());

        this.generated(SplatcraftItems.INK_CLOTH_HELMET);
        this.generated(SplatcraftItems.INK_CLOTH_CHESTPLATE);
        this.generated(SplatcraftItems.INK_CLOTH_LEGGINGS);
        this.generated(SplatcraftItems.INK_CLOTH_BOOTS);

        this.generated(SplatcraftItems.SPLATFEST_BAND);

        this.generated(SplatcraftItems.INK_SQUID_BANNER_PATTERN);
        this.generated(SplatcraftItems.OCTOLING_BANNER_PATTERN);
    }
}
