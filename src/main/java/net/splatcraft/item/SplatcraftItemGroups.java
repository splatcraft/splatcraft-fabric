package net.splatcraft.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;
import net.splatcraft.block.SplatcraftBlocks;

import java.util.function.Supplier;

public class SplatcraftItemGroups {
    public static final ItemGroup ALL = register("all", () -> new ItemStack(SplatcraftBlocks.CANVAS));

    public static final ItemGroup INKABLES = register("inkables", () -> new ItemStack(SplatcraftBlocks.INKWELL));

    private static ItemGroup register(String id, Supplier<ItemStack> item) {
        return FabricItemGroupBuilder.build(new Identifier(Splatcraft.MOD_ID, id), item);
    }
}
