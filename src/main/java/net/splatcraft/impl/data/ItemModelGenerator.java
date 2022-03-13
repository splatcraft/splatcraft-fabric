package net.splatcraft.impl.data;

import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.moddingplayground.frame.api.toymaker.v0.generator.model.InheritingModelGen;
import net.moddingplayground.frame.api.toymaker.v0.generator.model.ModelGen;
import net.moddingplayground.frame.api.toymaker.v0.generator.model.item.AbstractItemModelGenerator;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.block.SplatcraftBlocks;
import net.splatcraft.api.entity.SplatcraftEntityType;
import net.splatcraft.api.item.SplatcraftItems;

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
        this.block(SplatcraftBlocks.GRATE_RAMP);

        this.block(SplatcraftBlocks.STAGE_BARRIER);
        this.block(SplatcraftBlocks.STAGE_VOID);

        this.add(SpawnEggItem.forEntity(SplatcraftEntityType.INK_SQUID), item -> spawnEgg());

        this.generated(SplatcraftItems.INK_CLOTH_HELMET);
        this.generated(SplatcraftItems.INK_CLOTH_CHESTPLATE);
        this.generated(SplatcraftItems.INK_CLOTH_LEGGINGS);
        this.generated(SplatcraftItems.INK_CLOTH_BOOTS);

        for (int i = 1; i <= 7; i++) {
            final int ii = i;
            this.add(SplatcraftItems.INK_TANK, item -> name(item, "%s_" + ii), item -> this.inkTank(item, ii));
        }

        this.generated(SplatcraftItems.SPLATFEST_BAND);

        this.generated(SplatcraftItems.INK_SQUID_BANNER_PATTERN);
        this.generated(SplatcraftItems.OCTOLING_BANNER_PATTERN);
    }

    public ModelGen inkTank(Item item, int i) {
        return InheritingModelGen.inherit(name(item, "item/%s"))
                                 .texture("layer0", name(item, "item/%s_" + i));
    }
}
