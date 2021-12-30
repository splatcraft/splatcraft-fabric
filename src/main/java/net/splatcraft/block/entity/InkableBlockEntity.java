package net.splatcraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.ServerWorldProperties;
import net.splatcraft.Splatcraft;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.inkcolor.InkType;
import net.splatcraft.inkcolor.Inkable;

import static net.splatcraft.util.SplatcraftConstants.*;

public class InkableBlockEntity extends BlockEntity implements Inkable {
    private InkColor inkColor = InkColors.getDefault();
    private InkType inkType = InkType.NORMAL;

    public InkableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public InkColor getInkColor() {
        return this.inkColor;
    }

    @Override
    public boolean setInkColor(InkColor inkColor) {
        if (this.inkColor.equals(inkColor)) return false;
        this.inkColor = inkColor;
        this.sync();
        return true;
    }

    @Override
    public InkType getInkType() {
        return this.inkType;
    }

    @Override
    public boolean setInkType(InkType inkType) {
        if (this.getInkType().equals(inkType)) return false;
        this.inkType = inkType;
        return true;
    }

    @Override
    public Text getTextForCommand() {
        return new TranslatableText(
            T_BLOCK_ENTITY_DESCRIPTION.formatted(Splatcraft.MOD_ID),
            this.getClass().getSimpleName(),
            this.world instanceof ServerWorld world
                ? world.getLevelProperties() instanceof ServerWorldProperties properties
                    ? properties.getLevelName()
                    : "[]"
                : "[]",
            this.getPos().toShortString()
        );
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString(NBT_INK_COLOR, InkColor.toString(this.inkColor));
        nbt.putString(NBT_INK_TYPE, this.inkType.toString());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.setInkColor(InkColor.fromString(nbt.getString(NBT_INK_COLOR)));
        this.setInkType(InkType.safeValueOf(nbt.getString(NBT_INK_TYPE)));
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt);
        return nbt;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    protected void sync() {
        if (this.world != null) {
            this.markDirty();
            this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
        }
    }
}
