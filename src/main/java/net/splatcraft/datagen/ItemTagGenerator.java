package net.splatcraft.datagen;

import dev.emi.trinkets.TrinketsMain;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.moddingplayground.frame.api.toymaker.v0.generator.tag.AbstractTagGenerator;
import net.splatcraft.Splatcraft;

import static net.splatcraft.item.SplatcraftItems.*;

public class ItemTagGenerator extends AbstractTagGenerator<Item> {
    public static final Tag<Item> TRINKETS_HAND_RING = trinketTag("hand/ring");
    public static final Tag<Item> TRINKETS_OFFHAND_RING = trinketTag("offhand/ring");

    public ItemTagGenerator() {
        super(Splatcraft.MOD_ID, Registry.ITEM);
    }

    @Override
    public void generate() {
        this.add(TRINKETS_HAND_RING,
            SPLATFEST_BAND
        );

        this.add(TRINKETS_OFFHAND_RING,
            SPLATFEST_BAND
        );
    }

    public static Tag<Item> trinketTag(String id) {
        return TagFactory.ITEM.create(new Identifier(TrinketsMain.MOD_ID, id));
    }
}
