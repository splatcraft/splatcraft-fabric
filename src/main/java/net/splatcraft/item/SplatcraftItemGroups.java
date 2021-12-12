package net.splatcraft.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;
import net.splatcraft.block.SplatcraftBlocks;
import net.splatcraft.inkcolor.InkColors;

import java.util.function.Supplier;

import static net.splatcraft.util.SplatcraftUtil.setInkColorOnStack;

public class SplatcraftItemGroups {
    public static final ItemGroup ALL = register("all", () -> new ItemStack(SplatcraftBlocks.CANVAS));
    public static final ItemGroup INKABLES = register("inkables", () -> setInkColorOnStack(new ItemStack(SplatcraftBlocks.CANVAS), InkColors.random()));

    private static ItemGroup register(String id, Supplier<ItemStack> item) {
        return FabricItemGroupBuilder.build(new Identifier(Splatcraft.MOD_ID, id), item);
    }
}
