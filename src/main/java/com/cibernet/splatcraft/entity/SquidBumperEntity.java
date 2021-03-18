package com.cibernet.splatcraft.entity;

import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.init.SplatcraftBlocks;
import com.cibernet.splatcraft.init.SplatcraftGameRules;
import com.cibernet.splatcraft.init.SplatcraftItems;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Collections;

public class SquidBumperEntity extends LivingEntity implements InkableEntity {
    public static final String id = "squid_bumper";

    public static final TrackedData<String> INK_COLOR = DataTracker.registerData(SquidBumperEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> RESPAWN_TIME = DataTracker.registerData(SquidBumperEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> SPLAT_HEALTH = DataTracker.registerData(SquidBumperEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private static final float maxInkHealth = 20.0F;
    public static final int maxRespawnTime = 60;
    public boolean inkproof = false;

    public long punchCooldown;
    public long hurtCooldown;

    public SquidBumperEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
        this.setInkColor(ColorUtils.getRandomStarterColor(this.random));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SPLAT_HEALTH, maxInkHealth);
        this.dataTracker.startTracking(RESPAWN_TIME, maxRespawnTime);
        this.dataTracker.startTracking(INK_COLOR, InkColors.NONE.toString());
    }

    @Override
    public TrackedData<String> getInkColorTrackedData() {
        return INK_COLOR;
    }

    @Override
    public DataTracker getDataTracker() {
        return this.dataTracker;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        hurtCooldown = Math.max(hurtCooldown - 1, 0);

        if (getRespawnTime() > 1) {
            setRespawnTime(getRespawnTime() - 1);
        }
        else if (getRespawnTime() == 1) {
            respawn();
        }

        BlockPos pos = this.getVelocityAffectingPos();

        if (world.getBlockState(pos).getBlock() == SplatcraftBlocks.INKWELL && world.getBlockEntity(pos) instanceof AbstractInkableBlockEntity) {
            AbstractInkableBlockEntity blockEntity = (AbstractInkableBlockEntity) world.getBlockEntity(pos);
            if (blockEntity != null && blockEntity.getInkColor() != this.getInkColor()) {
                this.setInkColor(blockEntity.getInkColor());
            }
        }
    }

    @Override
    public boolean onEntityInked(DamageSource source, float damage, InkColor color) {
        if (!inkproof && (this.getInkColor() != color || SplatcraftGameRules.getBoolean(this.world, SplatcraftGameRules.INK_FRIENDLY_FIRE))) {
            ink(damage);
            if (getInkHealth() <= 0) {
                this.world.sendEntityStatus(this, (byte) 34);
            }
        }

        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.world.isClient && this.isAlive()) {
            if (DamageSource.OUT_OF_WORLD.equals(source)) {
                this.remove();
                return false;
            } else if (!this.isInvulnerableTo(source)) {
                if (source.isExplosive()) {
                    this.breakAndDropItem(source);
                    this.remove();
                    return false;
                } else if (DamageSource.IN_FIRE.equals(source)) {
                    if (this.isOnFire()) {
                        this.damage(source, 0.15F);
                    } else {
                        this.setFireTicks(5);
                    }

                    return false;
                } else if (DamageSource.ON_FIRE.equals(source) && this.getHealth() > 0.5F) {
                    this.damage(source, 4.0F);
                    return false;
                } else {
                    boolean flag = source.getSource() instanceof PersistentProjectileEntity;
                    boolean flag1 = flag && ((PersistentProjectileEntity) source.getSource()).getPierceLevel() > 0;
                    boolean flag2 = "player".equals(source.getName());
                    if (!flag2 && !flag) {
                        return false;
                    } else if (source.getAttacker() instanceof PlayerEntity && !((PlayerEntity) source.getAttacker()).abilities.allowModifyWorld) {
                        return false;
                    } else if (source.isSourceCreativePlayer()) {
                        this.playBrokenSound();
                        this.playParticles();
                        this.remove();
                        return flag1;
                    } else {
                        long i = this.world.getTime();
                        if (i - this.punchCooldown > 5L && !flag) {
                            this.world.sendEntityStatus(this, (byte) 32);
                            this.punchCooldown = i;
                        } else {
                            this.playParticles();
                            this.breakAndDropItem(source);
                        }

                        return true;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void playParticles() {
        if (this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.WHITE_WOOL.getDefaultState()), this.getX(), this.getBodyY(0.6666666666666666D), this.getZ(), 10, this.getWidth() / 4.0F, this.getHeight() / 4.0F, this.getWidth() / 4.0F, 0.05D);
        }
    }

    private void playPopParticles() //TODO
    {
		/*
		for(int i = 0; i < 10; i++)
			SplatCraftParticleSpawner.spawnInkParticle(posX, posY + height * 0.5, posZ, rand.nextDouble() * 0.5 - 0.25, rand.nextDouble() * 0.5 - 0.25, rand.nextDouble() * 0.5 - 0.25, getColor(), 2);
		SplatCraftParticleSpawner.spawnInksplosionParticle(posX, posY + height * 0.5, posZ, 0, 0, 0, getColor(), 2);
		*/
    }

    private void playBrokenSound() {
        this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ARMOR_STAND_BREAK, this.getSoundCategory(), 1.0F, 1.0F);
    }

    private void breakAndDropItem(DamageSource damageSource) {
        Block.dropStack(this.world, this.getBlockPos(), new ItemStack(SplatcraftItems.SQUID_BUMPER));
        this.drop(damageSource);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(byte id) {
        switch(id) {
            case 31:
                if (this.world.isClient)
                    hurtCooldown = world.getTime();
                break;
            case 32:
                if (this.world.isClient) {
                    this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ARMOR_STAND_HIT, this.getSoundCategory(), 0.3F, 1.0F, false);
                    this.punchCooldown = this.world.getTime();
                }
                break;
            case 34:
                if (this.world.isClient) {
                    this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, this.getSoundCategory(), 0.5F, 20.0F, false);
                    playPopParticles();
                }
                break;



            default: super.handleStatus(id);
        }

    }

    @Override
    public boolean collides() {
        return !this.removed && getInkHealth() > 0;
    }

    @Override
    protected void pushAway(Entity entityIn) {
        if (getInkHealth() > 0)
            this.pushAwayFrom(entityIn);
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if (!this.isConnectedThroughVehicle(entity)) {
            if (!entity.noClip && !this.noClip) {
                double d0 = entity.getX() - this.getX();
                double d1 = entity.getZ() - this.getZ();
                double d2 = MathHelper.absMax(d0, d1);

                if (d2 >= 0.009999999776482582D) {
                    d2 = MathHelper.sqrt(d2);
                    d0 = d0 / d2;
                    d1 = d1 / d2;
                    double d3 = 1.0D / d2;

                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }

                    d0 = d0 * d3;
                    d1 = d1 * d3;
                    d0 = d0 * 0.05000000074505806D;
                    d1 = d1 * 0.05000000074505806D;
                    d0 = d0 * (double)(1.0F - this.pushSpeedReduction);
                    d1 = d1 * (double)(1.0F - this.pushSpeedReduction);
                    d0 *= 3;
                    d1 *= 3;

                    if (!entity.hasPassengers())
                        entity.addVelocity(d0, 0.0D, d1);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<ItemStack> getArmorItems() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {}

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.inkColorFromTag(tag);

        inkproof = tag.getBoolean("Inkproof");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putBoolean("Inkproof", inkproof);

        this.inkColorToTag(tag);
        return super.toTag(tag);
    }

    public float getInkHealth() {
        return dataTracker.get(SPLAT_HEALTH);
    }
    public void setInkHealth(float value) {
        dataTracker.set(SPLAT_HEALTH, value);
    }

    public int getRespawnTime() {
        return dataTracker.get(RESPAWN_TIME);
    }
    public void setRespawnTime(int value) {
        dataTracker.set(RESPAWN_TIME, value);
    }

    public void ink(float damage) {
        setInkHealth(getInkHealth() - damage);
        setRespawnTime(maxRespawnTime);
        this.world.sendEntityStatus(this, (byte) 31);
        hurtCooldown = world.getTime();
        hurtTime = maxHurtTime;
    }

    public void respawn() {
        if (getInkHealth() <= 0) {
            world.playSound(null, getX(), getY(), getZ(), SoundEvents.BLOCK_NOTE_BLOCK_CHIME, getSoundCategory(), 1, 4);
        }
        setInkHealth(maxInkHealth);
        setRespawnTime(0);
        //updateBoundingBox();
    }
}
