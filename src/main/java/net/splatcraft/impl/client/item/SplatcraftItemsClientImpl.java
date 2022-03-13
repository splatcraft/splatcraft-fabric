package net.splatcraft.impl.client.item;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.block.SplatcraftBlocks;
import net.splatcraft.api.client.render.InkTankRenderer;
import net.splatcraft.api.inkcolor.Inkable;
import net.splatcraft.api.item.InkTankItem;
import net.splatcraft.api.item.SplatcraftItems;

import static net.splatcraft.api.client.util.ClientUtil.*;
import static net.splatcraft.api.item.InkTankItem.*;

@Environment(EnvType.CLIENT)
public final class SplatcraftItemsClientImpl implements SplatcraftItems, ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ArmorRenderer.register(InkTankRenderer.INSTANCE::render, SplatcraftItems.INK_TANK);

        ColorProviderRegistry.ITEM.register( // inkable items
            (stack, tintIndex) -> {
                Inkable inkable = (Inkable) (Object) stack;
                return tintIndex == 0
                    ? stack.getItem() instanceof BlockItem
                    ? inkable.getInkColor().getDecimalColor()
                    : getDecimalColor(inkable.getInkColor())
                    : 0xFFFFFF;
            },
            SplatcraftBlocks.CANVAS, SplatcraftBlocks.INKWELL,

            SplatcraftItems.INK_CLOTH_HELMET, SplatcraftItems.INK_CLOTH_CHESTPLATE,
            SplatcraftItems.INK_CLOTH_LEGGINGS, SplatcraftItems.INK_CLOTH_BOOTS,
            SplatcraftItems.INK_TANK,

            SplatcraftItems.SPLAT_ROLLER, SplatcraftItems.SPLATTERSHOT
        );

        modelPredicate("using",
            (stack, world, entity, seed) -> {
                return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1 : 0;
            }, SplatcraftItems.SPLAT_ROLLER
        );
        modelPredicate("contained_ink",
            (stack, world, entity, seed) -> {
                float containedInk = getContainedInk(stack);
                float capacity = ((InkTankItem) stack.getItem()).getCapacity();
                return containedInk / capacity;
            }, SplatcraftItems.INK_TANK
        );
    }

    private static void modelPredicate(String id, UnclampedModelPredicateProvider provider, Item... items) {
        for (Item item : items) ModelPredicateProviderRegistry.register(item, new Identifier(Splatcraft.MOD_ID, id), provider);
    }
}
