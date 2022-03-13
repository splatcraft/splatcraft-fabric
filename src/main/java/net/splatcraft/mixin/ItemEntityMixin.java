package net.splatcraft.mixin;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.splatcraft.api.block.InkableBlock;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.inkcolor.InkType;
import net.splatcraft.api.inkcolor.Inkable;
import net.splatcraft.api.tag.SplatcraftBlockTags;
import net.splatcraft.impl.entity.access.ItemEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.splatcraft.api.world.SplatcraftGameRules.*;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements Inkable, ItemEntityAccess {
    @Shadow public abstract ItemStack getStack();

    private ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    @Override
    public InkColor getInkColor() {
        return ((Inkable) (Object) this.getStack()).getInkColor();
    }

    @Unique
    @Override
    public boolean setInkColor(InkColor inkColor) {
        ItemStack stack = this.getStack();
        if (stack == null) return false;
        Inkable inkable = (Inkable) (Object) stack;

        if (inkColor.equals(inkable.getInkColor()) && this.hasInkColor()) return false;
        if (stack.getItem() instanceof BlockItem item && item.getBlock() instanceof InkableBlock) {
            inkable.setInkColor(inkColor);

            // force sync because minecraft dumb
            if (!this.world.isClient) {
                for (ServerPlayerEntity player : PlayerLookup.tracking(this)) {
                    player.networkHandler.sendPacket(new EntityTrackerUpdateS2CPacket(this.getId(), this.dataTracker, true));
                }
            }

            return true;
        }

        return false;
    }

    @Unique
    @Override
    public boolean hasInkColor() {
        return ((Inkable) (Object) this.getStack()).hasInkColor();
    }

    @Unique
    @Override
    public InkType getInkType() {
        return ((Inkable) (Object) this.getStack()).getInkType();
    }

    @Unique
    @Override
    public boolean setInkType(InkType inkType) {
        ItemStack stack = this.getStack();
        if (stack == null) return false;
        Inkable inkable = (Inkable) (Object) stack;

        if (inkType.equals(inkable.getInkType()) && this.hasInkType()) return false;
        if (stack.getItem() instanceof BlockItem item && item.getBlock() instanceof InkableBlock) {
            inkable.setInkType(inkType);

            // force sync because minecraft dumb
            if (!this.world.isClient) {
                for (ServerPlayerEntity player : PlayerLookup.tracking(this)) {
                    player.networkHandler.sendPacket(new EntityTrackerUpdateS2CPacket(this.getId(), this.dataTracker, true));
                }
            }

            return true;
        }

        return false;
    }

    @Unique
    @Override
    public boolean hasInkType() {
        return ((Inkable) (Object) this.getStack()).hasInkType();
    }

    @Unique
    @Override
    public Text getTextForCommand() {
        return this.getDisplayName();
    }

    @Unique
    @Override
    public boolean isInkable() {
        ItemStack stack = this.getStack();
        return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof InkableBlock;
    }

    /**
     * Changes the ink color of the stack if on an inkable block and configured.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (gameRule(this.world, INKWELL_CHANGES_INK_COLOR) && this.isOnGround()) {
            if (this.isInkable()) {
                BlockEntity blockEntity = this.world.getBlockEntity(this.getLandingPos());
                if (blockEntity != null && blockEntity.getCachedState().isIn(SplatcraftBlockTags.INK_COLOR_CHANGERS)) {
                    if (blockEntity instanceof Inkable inkable) this.setInkColor(inkable.getInkColor());
                }
            }
        }
    }
}
