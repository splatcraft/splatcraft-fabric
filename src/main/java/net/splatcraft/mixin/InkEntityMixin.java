package net.splatcraft.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.splatcraft.block.InkableBlock;
import net.splatcraft.entity.access.InkEntityAccess;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.tag.SplatcraftBlockTags;
import net.splatcraft.tag.SplatcraftEntityTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

import static net.splatcraft.world.SplatcraftGameRules.*;
import static net.splatcraft.world.SynchronizedBooleanGameRuleRegistry.*;

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

    @Unique
    @Override
    public boolean isInSquidForm() {
        return this instanceof Inkable;
    }

    @Unique
    @Override
    public boolean isSubmergedInInk() {
        return false;
    }

    @Unique
    @Override
    public boolean isOnInk() {
        return this.world.getBlockEntity(this.getLandingPos()) instanceof Inkable;
    }

    @Unique
    @Override
    public boolean isOnOwnInk() {
        if (this instanceof Inkable inkable) {
            if (this.world.getBlockEntity(this.getLandingPos()) instanceof Inkable block) {
                if (syncedGameRule(this.world, UNIVERSAL_INK)) return true;
                return block.getInkColor().equals(inkable.getInkColor());
            }
        }
        return false;
    }

    @Unique
    @Override
    public boolean isOnEnemyInk() {
        if (this instanceof Inkable inkable) {
            BlockPos pos = this.getLandingPos();
            if (gameRule(this.world, INKWELL_CHANGES_INK_COLOR)) {
                BlockState state = this.world.getBlockState(pos);
                if (SplatcraftBlockTags.INK_COLOR_CHANGERS.contains(state.getBlock())) return false;
            }

            if (this.world.getBlockEntity(pos) instanceof Inkable block) {
                return !syncedGameRule(this.world, UNIVERSAL_INK) && !block.getInkColor().equals(inkable.getInkColor());
            }
        }

        return false;
    }

    @Unique
    @Override
    public boolean doesInkPassing() {
        return this.isInSquidForm() && SplatcraftEntityTypeTags.INK_PASSABLES.contains(this.getType());
    }

    @Unique
    @Override
    public boolean canSubmergeInInk() {
        return false;
    }

    @Unique
    @Override
    public boolean canClimbInk() {
        return this.getInkClimbingPos().isPresent();
    }

    @SuppressWarnings("ConstantConditions")
    @Unique
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
            boolean universalInk = syncedGameRule(this.world, UNIVERSAL_INK);
            for (int i = 0; i < 4; i++) {
                int n = i % 2 == 0 ? 1 : -1;
                float xo = ( (i < 2) ? 0 : 0.32f) * n;
                float zo = (!(i < 2) ? 0 : 0.32f) * n;

                pos.set(x - xo, y, z - zo);
                if (!pos.equals(this.getBlockPos())) {
                    Block block = this.world.getBlockState(pos).getBlock();
                    if (block instanceof InkableBlock && SplatcraftBlockTags.INK_CLIMBABLE.contains(block)) {
                        if (this.world.getBlockEntity(pos) instanceof Inkable inkableBlock) {
                            if (universalInk || inkable.getInkColor().equals(inkableBlock.getInkColor())) return Optional.of(pos);
                        }
                    }
                }
            }
        }

        return Optional.empty();
    }

    @Unique
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
