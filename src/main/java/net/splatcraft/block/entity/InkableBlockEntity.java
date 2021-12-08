package net.splatcraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.splatcraft.Splatcraft;
import net.splatcraft.inkcolor.InkColor;
import net.splatcraft.inkcolor.InkColors;
import net.splatcraft.inkcolor.Inkable;

import static net.splatcraft.util.SplatcraftConstants.*;

public class InkableBlockEntity extends BlockEntity implements Inkable {
    /**
     * Defines a block's ink color.
     */
    private InkColor inkColor = InkColors._DEFAULT;

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
        if (this.world != null) {
            this.markDirty();
            this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
        }
        return true;
    }

    @Override
    public Text getTextForCommand() {
        return new TranslatableText(
            T_BLOCK_ENTITY_DESCRIPTION.formatted(Splatcraft.MOD_ID),
            "InkableBlockEntity", this.world, this.getPos()
        );
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString(NBT_INK_COLOR, InkColor.toString(this.inkColor));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.setInkColor(InkColor.fromString(nbt.getString(NBT_INK_COLOR)));
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
}
