package net.splatcraft.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.entity.InkEntityAccess;
import net.splatcraft.inkcolor.InkType;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.tag.SplatcraftBlockTags;
import net.splatcraft.tag.SplatcraftEntityTypeTags;
import net.splatcraft.world.SplatcraftGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class InkEntityMixin implements InkEntityAccess {
    @Shadow public World world;

    @Shadow public abstract BlockPos getLandingPos();
    @Shadow public abstract boolean hasVehicle();
    @Shadow public abstract boolean isSpectator();
    @Shadow public abstract EntityType<?> getType();

    @Override
    public InkType getInkType() {
        Entity that = Entity.class.cast(this);
        if (that instanceof PlayerEntity player) {
            PlayerDataComponent data = PlayerDataComponent.get(player);
            if (data.hasSplatfestBand()) return InkType.GLOWING;
        }
        return InkType.NORMAL;
    }

    @Override
    public boolean isOnInk() {
        return this.world.getBlockEntity(this.getLandingPos()) instanceof Inkable;
    }

    @Override
    public boolean isOnOwnInk() {
        if (this instanceof Inkable inkable) {
            BlockPos pos = this.getLandingPos();
            return this.world.getBlockEntity(pos) instanceof Inkable block && block.getInkColor().equals(inkable.getInkColor());
        }
        return false;
    }

    @Override
    public boolean isOnEnemyInk() {
        if (this instanceof Inkable inkable) {
            BlockPos pos = this.getLandingPos();
            if (this.world.getGameRules().getBoolean(SplatcraftGameRules.INKWELL_CHANGES_INK_COLOR)) {
                BlockState state = this.world.getBlockState(pos);
                if (SplatcraftBlockTags.INK_COLOR_CHANGERS.contains(state.getBlock())) return false;
            }

            return this.world.getBlockEntity(pos) instanceof Inkable block && !block.getInkColor().equals(inkable.getInkColor());
        }

        return false;
    }

    @Override
    public boolean canEnterSquidForm() {
        return !this.hasVehicle();
    }

    @Override
    public boolean canSubmergeInInk() {
        Entity that = Entity.class.cast(this);

        if (that instanceof PlayerEntity player) {
            PlayerDataComponent data = PlayerDataComponent.get(player);
            return data.isSquid() && !this.isSpectator() && this.isOnOwnInk();
        }

        return false;
    }

    @Override
    public boolean doesInkPassing() {
        Entity that = Entity.class.cast(this);
        if (that instanceof PlayerEntity player) {
            PlayerDataComponent data = PlayerDataComponent.get(player);
            return data.isSquid();
        } else return SplatcraftEntityTypeTags.INK_PASSABLES.contains(this.getType());
    }
}
