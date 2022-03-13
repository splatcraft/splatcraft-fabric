package net.splatcraft.api.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.api.block.SplatcraftBlocks;

import java.util.function.Supplier;

public interface SplatcraftItemGroups {
    ItemGroup ALL = register("all", () -> new ItemStack(SplatcraftBlocks.CANVAS));
    ItemGroup INKABLES = register("inkables", () -> new ItemStack(SplatcraftBlocks.INKWELL));

    private static ItemGroup register(String id, Supplier<ItemStack> item) {
        return FabricItemGroupBuilder.build(new Identifier(Splatcraft.MOD_ID, id), item);
    }
}
