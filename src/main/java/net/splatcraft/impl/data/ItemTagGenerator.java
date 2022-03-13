package net.splatcraft.impl.data;

import dev.emi.trinkets.TrinketsMain;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.moddingplayground.frame.api.toymaker.v0.generator.tag.AbstractTagGenerator;
import net.splatcraft.api.Splatcraft;

import static net.splatcraft.api.item.SplatcraftItems.*;

public class ItemTagGenerator extends AbstractTagGenerator<Item> {
    public static final TagKey<Item> TRINKETS_HAND_RING = trinketTag("hand/ring");
    public static final TagKey<Item> TRINKETS_OFFHAND_RING = trinketTag("offhand/ring");

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

    public static TagKey<Item> trinketTag(String id) {
        return TagKey.of(Registry.ITEM_KEY, new Identifier(TrinketsMain.MOD_ID, id));
    }
}
