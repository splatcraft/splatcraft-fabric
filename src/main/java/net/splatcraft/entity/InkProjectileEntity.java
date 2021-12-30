package net.splatcraft.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.splatcraft.entity.damage.SplatcraftDamageSource;
import net.splatcraft.entity.data.SplatcraftTrackedDataHandlerRegistry;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.inkcolor.InkType;
import net.splatcraft.inkcolor.Inkable;
import net.splatcraft.tag.SplatcraftBlockTags;

import static net.splatcraft.particle.SplatcraftParticles.inkSplash;
import static net.splatcraft.util.SplatcraftConstants.*;

public class InkProjectileEntity extends ThrownEntity implements Inkable, InkableCaster {
    public static final TrackedData<InkColor> INK_COLOR = DataTracker.registerData(InkProjectileEntity.class, SplatcraftTrackedDataHandlerRegistry.INK_COLOR);
    public static final TrackedData<InkType> INK_TYPE = DataTracker.registerData(InkProjectileEntity.class, SplatcraftTrackedDataHandlerRegistry.INK_TYPE);
    public static final TrackedData<Float> SIZE = DataTracker.registerData(InkProjectileEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public static final byte INK_SPLASH_STATUS = 69;

    private boolean dropsInk = false;

    public InkProjectileEntity(EntityType<? extends ThrownEntity> entityType, World world) {
        super(entityType, world);
    }

    public InkProjectileEntity(double x, double y, double z, World world) {
        super(SplatcraftEntities.INK_PROJECTILE, x, y, z, world);
    }

    public InkProjectileEntity(LivingEntity owner, World world) {
        super(SplatcraftEntities.INK_PROJECTILE, owner, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(INK_COLOR, InkColors.getDefault());
        this.dataTracker.startTracking(INK_TYPE, InkType.NORMAL);
        this.dataTracker.startTracking(SIZE, 1.0f);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (SIZE.equals(data)) this.calculateDimensions();
        super.onTrackedDataSet(data);
    }

    @Override
    public InkColor getInkColor() {
        return this.dataTracker.get(INK_COLOR);
    }

    @Override
    public boolean setInkColor(InkColor inkColor) {
        if (this.getInkColor().equals(inkColor)) return false;
        this.dataTracker.set(INK_COLOR, inkColor);
        return true;
    }

    @Override
    public Text getTextForCommand() {
        return this.getDisplayName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Entity & Inkable> T toInkable() {
        return (T) this;
    }

    @Override
    public InkType getInkType() {
        return this.dataTracker.get(INK_TYPE);
    }

    @Override
    public boolean setInkType(InkType inkType) {
        if (this.getInkType().equals(inkType)) return false;
        this.dataTracker.set(INK_TYPE, inkType);
        return true;
    }

    public float getSize() {
        return this.dataTracker.get(SIZE);
    }

    public void setSize(float size) {
        this.dataTracker.set(SIZE, size);
    }

    public boolean dropsInk() {
        return this.dropsInk;
    }

    public void setDropsInk(boolean dropsInk) {
        this.dropsInk = dropsInk;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isWet()) this.damage(DamageSource.DROWN, 1.0f);

        if (!this.world.isClient && this.dropsInk() && this.world.random.nextFloat() <= 0.8f) {
            for (BlockPos pos : this.iterateDimensionsFrom(this.getPos())) {
                this.dropInk(pos);
            }
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!super.damage(source, amount)) {
            this.onHit();
            return false;
        }

        return true;
    }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        Entity entity = hit.getEntity();
        if (entity instanceof Inkable inkable) {
            if (!this.getInkColor().equals(inkable.getInkColor())) {
                Entity owner = this.getOwner();
                entity.damage(owner instanceof LivingEntity livingEntity ? SplatcraftDamageSource.inked(livingEntity) : SplatcraftDamageSource.INKED_ENVIRONMENT, 1.0f);
            }
        }

        super.onEntityHit(hit);
        this.onHit();
    }

    @Override
    protected void onBlockHit(BlockHitResult hit) {
        Vec3d origin = hit.getPos().add(0.0d, hit.getSide() == Direction.UP ? -1.0d : 0.0d, 0.0d);
        for (BlockPos pos : this.iterateDimensionsFrom(origin)) {
            BlockEntity blockEntity = this.world.getBlockEntity(pos);
            if (blockEntity instanceof Inkable inkable && !(SplatcraftBlockTags.INK_COLOR_CHANGERS.contains(blockEntity.getCachedState().getBlock()))) {
                this.copyInkableTo(inkable);
            }
        }

        super.onBlockHit(hit);
        this.onHit();
    }

    protected void onHit() {
        if (!this.world.isClient) this.world.sendEntityStatus(this, INK_SPLASH_STATUS);
        this.discard();
    }

    public Iterable<BlockPos> iterateDimensionsFrom(Vec3d origin) {
        Box box = this.getDimensions(this.getPose()).getBoxAt(origin);
        return BlockPos.iterate(new BlockPos(box.minX, box.minY, box.minZ), new BlockPos(box.maxX, box.maxY, box.maxZ));
    }

    public void dropInk(BlockPos pos) {
        int limit = 0;
        BlockPos.Mutable mutable = pos.mutableCopy();
        while (limit <= this.world.random.nextInt(7) && !this.world.canCollide(this, new Box(mutable))) {
            limit++;
            mutable.move(Direction.DOWN);
        }

        BlockEntity blockEntity = this.world.getBlockEntity(mutable);
        if (blockEntity instanceof Inkable inkable && !(SplatcraftBlockTags.INK_COLOR_CHANGERS.contains(blockEntity.getCachedState().getBlock()))) {
            this.copyInkableTo(inkable);
        }
    }

    @Override
    public void handleStatus(byte status) {
        super.handleStatus(status);
        if (status == INK_SPLASH_STATUS || status == 53) inkSplash(this.world, this, this.getPos(), 0.75f);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.changing(1.0f, 1.0f).scaled(this.getSize() * 1.25f);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString(NBT_INK_COLOR, this.getInkColor().toString());
        nbt.putString(NBT_INK_TYPE, this.getInkType().toString());
        nbt.putFloat(NBT_SIZE, this.getSize());
        nbt.putBoolean(NBT_DROPS_INK, this.dropsInk());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.setInkColor(InkColor.fromString(nbt.getString(NBT_INK_COLOR)));
        this.setInkType(InkType.safeValueOf(nbt.getString(NBT_INK_TYPE)));
        this.setSize(nbt.getFloat(NBT_SIZE));
        this.setDropsInk(nbt.getBoolean(NBT_DROPS_INK));
    }
}
