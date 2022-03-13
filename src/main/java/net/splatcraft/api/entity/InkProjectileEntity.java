package net.splatcraft.api.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.splatcraft.api.entity.damage.SplatcraftDamageSource;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.inkcolor.InkColors;
import net.splatcraft.api.inkcolor.InkType;
import net.splatcraft.api.inkcolor.Inkable;
import net.splatcraft.api.item.SplatcraftItems;
import net.splatcraft.api.item.weapon.settings.InkProjectileSettings;
import net.splatcraft.api.item.weapon.settings.ShooterSettings;
import net.splatcraft.api.tag.SplatcraftBlockTags;
import net.splatcraft.impl.entity.access.InkableCaster;
import net.splatcraft.impl.util.TrackedDataUtil;

import static net.minecraft.entity.EntityStatuses.*;
import static net.splatcraft.api.particle.SplatcraftParticleType.*;
import static net.splatcraft.api.util.SplatcraftConstants.*;

public class InkProjectileEntity extends ThrownEntity implements Inkable, InkableCaster {
    public static final TrackedData<String> INK_COLOR = DataTracker.registerData(InkProjectileEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Integer> INK_TYPE = DataTracker.registerData(InkProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<String> SOURCE = DataTracker.registerData(InkProjectileEntity.class, TrackedDataHandlerRegistry.STRING);

    public static final byte INK_SPLASH_STATUS = 69;

    private boolean dropsInk = false;

    public InkProjectileEntity(EntityType<? extends ThrownEntity> entityType, World world) {
        super(entityType, world);
    }

    public InkProjectileEntity(double x, double y, double z, World world) {
        super(SplatcraftEntityType.INK_PROJECTILE, x, y, z, world);
    }

    public InkProjectileEntity(LivingEntity owner, World world) {
        super(SplatcraftEntityType.INK_PROJECTILE, owner, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(INK_COLOR, InkColors.getDefault().toString());
        this.dataTracker.startTracking(INK_TYPE, InkType.NORMAL.ordinal());
        this.dataTracker.startTracking(SOURCE, Registry.ITEM.getId(SplatcraftItems.SPLATTERSHOT).toString());
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (SOURCE.equals(data)) this.calculateDimensions();
        super.onTrackedDataSet(data);
    }

    @Override
    public InkColor getInkColor() {
        return TrackedDataUtil.inkColor(this.dataTracker, INK_COLOR);
    }

    @Override
    public boolean setInkColor(InkColor inkColor) {
        if (this.getInkColor().equals(inkColor)) return false;
        TrackedDataUtil.inkColor(this.dataTracker, INK_COLOR, inkColor);
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
        return TrackedDataUtil.inkType(this.dataTracker, INK_TYPE);
    }

    @Override
    public boolean setInkType(InkType inkType) {
        if (this.getInkType().equals(inkType)) return false;
        TrackedDataUtil.inkType(this.dataTracker, INK_TYPE, inkType);
        return true;
    }

    public String getSource() {
        return this.dataTracker.get(SOURCE);
    }

    public void setSource(String source) {
        if (!(Registry.ITEM.get(Identifier.tryParse(source)) instanceof ShooterSettings.Provider)) return;
        this.dataTracker.set(SOURCE, source);
    }

    public <T extends Item & ShooterSettings.Provider> void setSource(T item) {
        this.setSource(Registry.ITEM.getId(item).toString());
    }

    @SuppressWarnings("unchecked")
    public <T extends Item & ShooterSettings.Provider> T getSourceItem() {
        return (T) Registry.ITEM.get(Identifier.tryParse(getSource()));
    }

    public ShooterSettings getShooterSettings() {
        return this.getSourceItem().getWeaponSettings();
    }

    public InkProjectileSettings getProjectileSettings() {
        return this.getShooterSettings().getProjectileSettings();
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
                ShooterSettings settings = this.getShooterSettings();
                entity.damage(
                    owner instanceof LivingEntity livingEntity
                        ? SplatcraftDamageSource.inked(livingEntity)
                        : SplatcraftDamageSource.INKED_ENVIRONMENT,
                    settings.getDamage(new ShooterSettings.DamageCalculator.Context(settings, this.age))
                );
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
            if (blockEntity instanceof Inkable inkable && !(blockEntity.getCachedState().isIn(SplatcraftBlockTags.INK_COLOR_CHANGERS))) {
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
        if (blockEntity instanceof Inkable inkable && !(blockEntity.getCachedState().isIn(SplatcraftBlockTags.INK_COLOR_CHANGERS))) {
            this.copyInkableTo(inkable);
        }
    }

    @Override
    public void handleStatus(byte status) {
        super.handleStatus(status);
        if (status == INK_SPLASH_STATUS || status == DRIP_HONEY) inkSplash(this.world, this, this.getPos(), 0.75f);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.changing(1.0f, 1.0f).scaled(this.getProjectileSettings().getSize() * 1.25f);
    }

    @Override
    public boolean hasNoGravity() {
        return super.hasNoGravity() || this.age <= this.getProjectileSettings().getGravityDelay();
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString(NBT_INK_COLOR, this.getInkColor().toString());
        nbt.putString(NBT_INK_TYPE, this.getInkType().toString());
        nbt.putString(NBT_SOURCE, this.getSource());
        nbt.putBoolean(NBT_DROPS_INK, this.dropsInk());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setInkColor(InkColor.fromString(nbt.getString(NBT_INK_COLOR)));
        this.setInkType(InkType.safeValueOf(nbt.getString(NBT_INK_TYPE)));
        this.setSource(nbt.getString(NBT_SOURCE));
        this.setDropsInk(nbt.getBoolean(NBT_DROPS_INK));
    }
}
