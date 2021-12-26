package net.splatcraft.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.splatcraft.block.InkableBlock;
import net.splatcraft.component.PlayerDataComponent;
import net.splatcraft.entity.InkEntityAccess;
import net.splatcraft.inkcolor.InkType;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.tag.SplatcraftBlockTags;
import net.splatcraft.tag.SplatcraftEntityTypeTags;
import net.splatcraft.world.SplatcraftGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class InkEntityMixin implements InkEntityAccess {
    @Shadow public World world;

    @Shadow public abstract BlockPos getLandingPos();
    @Shadow public abstract boolean hasVehicle();
    @Shadow public abstract boolean isSpectator();
    @Shadow public abstract EntityType<?> getType();
    @Shadow public abstract double getX();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();
    @Shadow public abstract BlockPos getBlockPos();
    @Shadow public abstract Vec3d getPos();
    @Shadow public abstract EntityDimensions getDimensions(EntityPose pose);
    @Shadow public abstract EntityPose getPose();
    @Shadow public abstract boolean isSwimming();
    @Shadow public abstract boolean isInLava();
    @Shadow public abstract boolean isWet();

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
    public boolean isInSquidForm() {
        Entity that = Entity.class.cast(this);
        if (that instanceof PlayerEntity player) {
            PlayerDataComponent data = PlayerDataComponent.get(player);
            return data.isSquid();
        }

        return this instanceof Inkable;
    }

    @Override
    public boolean isSubmerged() {
        Entity that = Entity.class.cast(this);
        if (that instanceof PlayerEntity player) {
            PlayerDataComponent data = PlayerDataComponent.get(player);
            return data.isSubmerged();
        }

        return false;
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
    public boolean doesInkPassing() {
        return this.isInSquidForm() && SplatcraftEntityTypeTags.INK_PASSABLES.contains(this.getType());
    }

    @Override
    public boolean canSubmergeInInk() {
        Entity that = Entity.class.cast(this);

        if (that instanceof PlayerEntity player) {
            PlayerDataComponent data = PlayerDataComponent.get(player);
            return data.isSquid() && !this.isSpectator() && (this.isOnOwnInk() || this.canClimbInk());
        }

        return false;
    }

    @Override
    public boolean canClimbInk() {
        return this.getInkClimbingPos().isPresent();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Optional<BlockPos> getInkClimbingPos() {
        Entity that = Entity.class.cast(this);

        if (this.isSwimming() || this.hasVehicle() || this.isInLava() || this.isWet()
            || (that instanceof LivingEntity living && living.hasStatusEffect(StatusEffects.LEVITATION))
            || (that instanceof PlayerEntity player && player.getAbilities().flying)
        ) return Optional.empty();

        if (this instanceof Inkable inkable) {
            BlockPos.Mutable pos = new BlockPos.Mutable();
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            for (int i = 0; i < 4; i++) {
                int n = i % 2 == 0 ? 1 : -1;
                float xo = ( (i < 2) ? 0 : 0.32f) * n;
                float zo = (!(i < 2) ? 0 : 0.32f) * n;

                pos.set(x - xo, y, z - zo);
                if (!pos.equals(this.getBlockPos())) {
                    Block block = this.world.getBlockState(pos).getBlock();
                    if (block instanceof InkableBlock && SplatcraftBlockTags.INK_CLIMBABLE.contains(block)) {
                        if (this.world.getBlockEntity(pos) instanceof Inkable inkableBlock && inkable.getInkColor().equals(inkableBlock.getInkColor())) {
                            return Optional.of(pos);
                        }
                    }
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Vec3d getInkSplashParticlePos() {
        return new Vec3d(
            this.getX(),
            this.canClimbInk() && !this.isOnOwnInk()
                ? this.getPos().getY() + (this.getDimensions(this.getPose()).height / 2)
                : this.getLandingPos().getY() + 1.0d,
            this.getZ()
        );
    }
}
