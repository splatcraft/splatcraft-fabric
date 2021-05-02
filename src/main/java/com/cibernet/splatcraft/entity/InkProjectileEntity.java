package com.cibernet.splatcraft.entity;

import com.cibernet.splatcraft.client.network.SplatcraftClientNetworking;
import com.cibernet.splatcraft.init.SplatcraftEntities;
import com.cibernet.splatcraft.init.SplatcraftSoundEvents;
import com.cibernet.splatcraft.init.SplatcraftTrackedDataHandlers;
import com.cibernet.splatcraft.inkcolor.*;
import com.cibernet.splatcraft.util.InkBlockUtil;
import com.cibernet.splatcraft.util.InkExplosion;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class InkProjectileEntity extends ThrownEntity implements InkableEntity {
    public static final String id = "ink_projectile";

    public static final TrackedData<InkColor> INK_COLOR = DataTracker.registerData(InkProjectileEntity.class, SplatcraftTrackedDataHandlers.INK_COLOR);
    public static final TrackedData<InkType> INK_TYPE = DataTracker.registerData(InkProjectileEntity.class, SplatcraftTrackedDataHandlers.INK_TYPE);
    private static final TrackedData<Float> PROJECTILE_SIZE = DataTracker.registerData(InkProjectileEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> LIFETIME = DataTracker.registerData(InkProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> TRAIL_SIZE = DataTracker.registerData(InkProjectileEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> PLAYS_EFFECTS = DataTracker.registerData(InkProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public float gravity = 0.075f;
    public float damage = 0;
    public float splashDamage = 0;
    public boolean damageMobs = false;
    public boolean canPierce = false;
    public ItemStack sourceWeapon = ItemStack.EMPTY;

    public InkProjectileEntity(EntityType<? extends InkProjectileEntity> entity, World world) {
        super(entity, world);
    }
    public InkProjectileEntity(World world, double x, double y, double z) {
        super(SplatcraftEntities.INK_PROJECTILE, x, y, z, world);
    }

    public InkProjectileEntity(World world, LivingEntity thrower, InkColor color, InkType inkType, float size, float damage, ItemStack sourceWeapon) {
        super(SplatcraftEntities.INK_PROJECTILE, thrower, world);
        this.setProjectileSize(size);
        this.setInkColor(color);
        this.damage = damage;
        this.setInkType(inkType);
        this.sourceWeapon = sourceWeapon;
    }
    public InkProjectileEntity(World world, LivingEntity thrower, ItemStack sourceWeapon, InkType inkType, float size, float damage) {
        this(world, thrower, ColorUtil.getInkColor(sourceWeapon), inkType, size, damage, sourceWeapon);
    }

    @Override
    public void setProperties(Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
        super.setProperties(user, pitch, yaw, roll, modifierZ, modifierXYZ);
        Vec3d center = user.getBoundingBox().getCenter();
        this.updatePosition(center.getX(), user.getEyeY() - 0.10000000149011612d, center.getZ());
    }

    public InkProjectileEntity setShooterTrail() {
        this.setTrailSize(this.getProjectileSize() * 0.7f);
        this.setPlaysEffects(false);
        return this;
    }

    public InkProjectileEntity setChargerStats(int lifetime, boolean canPierce) {
        this.setTrailSize(this.getProjectileSize() * 0.85f);
        this.setLifetime(lifetime);
        this.gravity = 0.002f;
        this.canPierce = canPierce;

        return this;
    }

    public InkProjectileEntity setBlasterStats(int lifetime, float splashDamage) {
        this.setLifetime(lifetime);
        this.splashDamage = splashDamage;
        this.gravity = 0;
        this.setTrailSize(this.getProjectileSize() * 0.45f);

        return this;
    }

    @Override
    public DataTracker inkable_getDataTracker() {
        return this.dataTracker;
    }

    @Override
    protected void initDataTracker() {
        this.inkable_initDataTracker();
        this.dataTracker.startTracking(PROJECTILE_SIZE, 1.0f);
        this.dataTracker.startTracking(LIFETIME, 60);
        this.dataTracker.startTracking(TRAIL_SIZE, 0.0f);
        this.dataTracker.startTracking(PLAYS_EFFECTS, true);
    }

    @Override
    public TrackedData<InkColor> inkable_getInkColorTrackedData() {
        return INK_COLOR;
    }
    @Override
    public TrackedData<InkType> inkable_getInkTypeTrackedData() {
        return INK_TYPE;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.inkable_fromTag(tag);

        this.setProjectileSize(tag.getFloat("Size"));

        this.gravity = tag.getFloat("Gravity");
        this.setLifetime(tag.getInt("Lifetime"));
        this.damage = tag.getFloat("Damage");
        this.splashDamage = tag.getFloat("SplashDamage");
        this.damageMobs = tag.getBoolean("DamageMobs");
        this.canPierce = tag.getBoolean("CanPierce");
        this.setPlaysEffects(tag.getBoolean("PlaysEffects"));

        this.sourceWeapon = ItemStack.fromTag(tag.getCompound("SourceWeapon"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putFloat("Size", this.getProjectileSize());

        tag.putFloat("Gravity", this.gravity);
        tag.putInt("Lifetime", this.getLifetime());
        tag.putFloat("Damage", this.damage);
        tag.putFloat("SplashDamage", this.splashDamage);
        tag.putBoolean("DamageMobs", this.damageMobs);
        tag.putBoolean("CanPierce", this.canPierce);
        tag.putBoolean("PlaysEffects", this.playsEffects());

        tag.put("SourceWeapon", this.sourceWeapon.toTag(new CompoundTag()));

        this.inkable_toTag(tag);
        return super.toTag(tag);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (data.equals(PROJECTILE_SIZE)) {
            this.calculateDimensions();
        }

        super.onTrackedDataSet(data);
    }

    public float getProjectileSize() { return this.dataTracker.get(PROJECTILE_SIZE);}
    public void setProjectileSize(float size) {
        this.dataTracker.set(PROJECTILE_SIZE, size);
        this.calculateDimensions();
    }

    public int getLifetime() { return this.dataTracker.get(LIFETIME);}
    public void setLifetime(int lifetime) {
        this.dataTracker.set(LIFETIME, lifetime);
    }

    public float getTrailSize() { return this.dataTracker.get(TRAIL_SIZE);}
    public void setTrailSize(float trailSize) {
        this.dataTracker.set(TRAIL_SIZE, trailSize);
    }

    public boolean playsEffects() { return this.dataTracker.get(PLAYS_EFFECTS);}
    public void setPlaysEffects(boolean playsEffects) {
        this.dataTracker.set(PLAYS_EFFECTS, playsEffects);
    }

    @Override
    public boolean isImmuneToExplosion() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        InkColor inkColor = this.getInkColor();
        InkType inkType = this.getInkType();

        SplatcraftClientNetworking.playBlockInkingEffects(this.world, inkColor, 0.3f, this.getPos().subtract(0.0d, 0.1d, 0.0d));

        int lifetime = this.getLifetime();
        if (lifetime <= 0) {
            InkExplosion.create(this.world, this.getOwner(), this.getBlockPos(), inkColor, inkType, this.getProjectileSize() * 0.85f, this.playsEffects());

            world.playSound(null, this.getX(), this.getY(), this.getZ(), SplatcraftSoundEvents.BLASTER_EXPLOSION, SoundCategory.PLAYERS, 0.8f, ((world.random.nextFloat() - world.random.nextFloat()) * 0.1f + 1.0f) * 0.95f);

            this.remove();
        } else {
            this.setProjectileSize(this.getProjectileSize() - 0.0001f);
            this.setLifetime(lifetime - 1);
        }

        float trailSize = this.getTrailSize();
        if (trailSize > 0) {
            BlockPos.Mutable mutable = new BlockPos.Mutable(this.getX(), 0, this.getZ());
            for (double y = this.getY(); y >= 0 && this.getY() - y <= 8; y--) {
                mutable.setY((int) y);
                if (!InkBlockUtil.canInkPassthrough(world, mutable)) {
                    for (BlockPos pos : new BlockPos[]{ mutable.up(), this.getBlockPos() }) {
                        InkExplosion.create(world, this.getOwner(), pos, inkColor, inkType, trailSize, false);
                    }

                    break;
                }
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult result) {
        super.onEntityHit(result);
        InkColor inkColor = this.getInkColor();

        ColorUtil.addInkSplashParticle(this.world, inkColor, this.getPos());

        Entity target = result.getEntity();
        if (!canPierce) {
            Entity owner = this.getOwner();
            if (owner != null && target.getEntityId() != owner.getEntityId()) {
                if (target instanceof LivingEntity) {
                    InkDamage.splat(world, (LivingEntity) target, damage, inkColor, owner, damageMobs);
                }

                if (target instanceof SheepEntity) {
                    DyeColor dyeColor = DyeColor.byName(inkColor.id.getPath(), ((SheepEntity) target).getColor());
                    if (dyeColor != null) {
                        ((SheepEntity) target).setColor(dyeColor);
                    }
                }

                this.remove();
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult result) {
        if (!InkBlockUtil.canInkPassthrough(world, result.getBlockPos())) {
            super.onBlockHit(result);
            InkColor inkColor = this.getInkColor();
            ColorUtil.addInkSplashParticle(this.world, inkColor, this.getPos());
            InkExplosion.create(world, this.getOwner(), this.getBlockPos(), inkColor, this.getInkType(), this.getProjectileSize() * 1.75f, this.playsEffects());

            this.remove();
        }
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose).scaled(getProjectileSize() / 2.0f);
    }

    @Override
    public float getGravity() {
        return (1 / (float) this.getVelocity().length()) * this.gravity;
    }
}
