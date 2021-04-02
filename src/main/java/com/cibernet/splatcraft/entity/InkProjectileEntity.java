package com.cibernet.splatcraft.entity;

import com.cibernet.splatcraft.entity.damage.SplatcraftDamageSources;
import com.cibernet.splatcraft.init.SplatcraftEntities;
import com.cibernet.splatcraft.init.SplatcraftItems;
import com.cibernet.splatcraft.init.SplatcraftSoundEvents;
import com.cibernet.splatcraft.inkcolor.*;
import com.cibernet.splatcraft.util.InkExplosion;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class InkProjectileEntity extends ThrownItemEntity implements InkableEntity {
    public static final String id = "ink_projectile";

    public static final TrackedData<String> INK_COLOR = DataTracker.registerData(InkProjectileEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Float> PROJ_SIZE = DataTracker.registerData(InkProjectileEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final DamageSource DAMAGE_SOURCE = SplatcraftDamageSources.ENEMY_INK;

    public float gravity = 0.075f;
    public int lifespan = 600;
    public boolean explodes = false;
    public float damage = 0;
    public float splashDamage = 0;
    public boolean damageMobs = false;
    public boolean canPierce = false;
    public ItemStack sourceWeapon = ItemStack.EMPTY;
    public float trailSize;
    public float trailCooldown = 0;
    public InkBlockUtils.InkType inkType;

    public InkProjectileEntity(EntityType<? extends InkProjectileEntity> entity, World world) {
        super(entity, world);
    }
    public InkProjectileEntity(World world, double x, double y, double z) {
        super(SplatcraftEntities.INK_PROJECTILE, x, y, z, world);
    }

    public InkProjectileEntity(World world, LivingEntity thrower, InkColor color, InkBlockUtils.InkType inkType, float size, float damage, ItemStack sourceWeapon) {
        super(SplatcraftEntities.INK_PROJECTILE, thrower, world);
        this.setProjectileSize(size);
        this.setInkColor(color);
        this.damage = damage;
        this.inkType = inkType;
        this.sourceWeapon = sourceWeapon;
    }

    public InkProjectileEntity(World world, LivingEntity thrower, ItemStack sourceWeapon, InkBlockUtils.InkType inkType, float size, float damage) {
        this(world, thrower, ColorUtils.getInkColor(sourceWeapon), inkType, size, damage, sourceWeapon);
    }

    @Override
    public void setProperties(Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
        super.setProperties(user, pitch, yaw, roll, modifierZ, modifierXYZ);
        Vec3d center = user.getBoundingBox().getCenter();
        this.updatePosition(center.getX(), user.getEyeY() - 0.10000000149011612d, center.getZ());
    }

    public InkProjectileEntity setShooterTrail() {
        trailCooldown = 4;
        trailSize = getProjectileSize() * 0.7f;
        return this;
    }

    public InkProjectileEntity setChargerStats(int lifespan) {
        trailSize = getProjectileSize() * 0.85f;
        this.lifespan = lifespan;
        gravity = 0;
        canPierce = true;
        return this;
    }

    public InkProjectileEntity setBlasterStats(int lifespan, float splashDamage) {
        this.lifespan = lifespan;
        this.splashDamage = splashDamage;
        gravity = 0;
        trailSize = getProjectileSize()*0.45f;
        explodes = true;
        return this;
    }

    @Override
    public DataTracker getIEDataTracker() {
        return this.dataTracker;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(INK_COLOR, InkColors.NONE.toString());
        dataTracker.startTracking(PROJ_SIZE, 1.0f);
    }

    @Override
    public TrackedData<String> getInkColorTrackedData() {
        return INK_COLOR;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (data.equals(PROJ_SIZE)) {
            this.calculateDimensions();
        }

        super.onTrackedDataSet(data);
    }

    @Override
    protected Item getDefaultItem() {
        return SplatcraftItems.SPLAT_ROLLER;
    }

    @Override
    public void tick() {
        super.tick();

        ColorUtils.addInkSplashParticle(world, this.getInkColor(), this.getPos(), 0.3f);

        if (lifespan-- <= 0) {
            InkExplosion.createInkExplosion(world, this.getOwner(), DAMAGE_SOURCE, this.getBlockPos(), getProjectileSize() * 0.85f, damage, splashDamage, damageMobs, this.getInkColor(), inkType, sourceWeapon);
            if (explodes) {
                ColorUtils.addInkSplashParticle(world, this.getInkColor(), this.getPos());
                world.playSound(null, this.getX(), this.getY(), this.getZ(), SplatcraftSoundEvents.BLASTER_EXPLOSION, SoundCategory.PLAYERS, 0.8f, ((world.random.nextFloat() - world.random.nextFloat()) * 0.1f + 1.0f) * 0.95f);
            }

            this.remove();
        }

        if (trailSize > 0 && this.age % this.trailCooldown == 0) {
            for (double y = this.getY(); y >= 0 && this.getY() - y <= 8; y--) {
                BlockPos inkPos = new BlockPos(this.getX(), y, this.getZ());
                if (!InkBlockUtils.canInkPassthrough(world, inkPos)) {
                    for (BlockPos pos : new BlockPos[]{ inkPos.up(), this.getBlockPos() }) {
                        InkExplosion.createInkExplosion(world, this.getOwner(), DAMAGE_SOURCE, pos, trailSize, 0, 0, damageMobs, this.getInkColor(), inkType, sourceWeapon);
                    }

                    break;
                }
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult result) {
        super.onEntityHit(result);
        ColorUtils.addInkSplashParticle(this.world, this.getInkColor(), this.getPos());

        Entity target = result.getEntity();

        if (!canPierce) {
            Entity owner = this.getOwner();
            if (target != owner) {
                if (target instanceof LivingEntity) {
                    InkDamageUtils.splatDamage(world, (LivingEntity) target, damage, this.getInkColor(), owner, damageMobs);
                }

                if (target instanceof SheepEntity) {
                    DyeColor dyeColor = DyeColor.byName(this.getInkColor().id.getPath(), ((SheepEntity) target).getColor());
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
        if (InkBlockUtils.canInkPassthrough(world, result.getBlockPos())) {
            return;
        }

        super.onBlockHit(result);
        ColorUtils.addInkSplashParticle(this.world, this.getInkColor(), this.getPos());

        InkExplosion.createInkExplosion(world, this.getOwner(), DAMAGE_SOURCE, this.getBlockPos(), getProjectileSize() * 0.85f, damage, splashDamage, damageMobs, this.getInkColor(), inkType, sourceWeapon);
        if (explodes) {
            //TODO particles
            world.playSound(null, this.getX(), this.getY(), this.getZ(), SplatcraftSoundEvents.BLASTER_EXPLOSION, SoundCategory.PLAYERS, 0.8f, ((world.random.nextFloat() - world.random.nextFloat()) * 0.1f + 1.0f) * 0.95f);
        }
        this.remove();
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        HitResult.Type rayType = hitResult.getType();
        if (rayType == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult) hitResult);
        } else {
            if (rayType == HitResult.Type.BLOCK) {
                onBlockHit((BlockHitResult) hitResult);
            }
        }
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.inkColorFromTag(tag);

        setProjectileSize(tag.getFloat("Size"));

        gravity = tag.getFloat("GravityVelocity");
        lifespan = tag.getInt("Lifespan");
        damage = tag.getFloat("Damage");
        splashDamage = tag.getFloat("SplashDamage");
        damageMobs = tag.getBoolean("DamageMobs");
        canPierce = tag.getBoolean("CanPierce");
        explodes = tag.getBoolean("Explodes");

        inkType = InkBlockUtils.InkType.values().clone()[tag.getInt("InkType")];

        sourceWeapon = ItemStack.fromTag(tag.getCompound("SourceWeapon"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putFloat("Size", getProjectileSize());

        tag.putFloat("GravityVelocity", gravity);
        tag.putInt("Lifespan", lifespan);
        tag.putFloat("Damage", damage);
        tag.putFloat("SplashDamage", splashDamage);
        tag.putBoolean("DamageMobs", damageMobs);
        tag.putBoolean("CanPierce", canPierce);
        tag.putBoolean("Explodes", explodes);

        tag.putInt("InkType", inkType.ordinal());
        tag.put("SourceWeapon",sourceWeapon.toTag(new CompoundTag()));

        this.inkColorToTag(tag);
        return super.toTag(tag);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose).scaled(getProjectileSize() / 2.0f);
    }

    @Override
    public float getGravity() {
        return gravity;
    }

    public float getProjectileSize() { return dataTracker.get(PROJ_SIZE);}
    public void setProjectileSize(float size) {
        dataTracker.set(PROJ_SIZE, size);
        this.moveToBoundingBoxCenter();
        this.calculateDimensions();
    }
}
