package net.splatcraft.api.sound;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;

public interface SplatcraftBlockSoundGroup {
    BlockSoundGroup EMPTY_INKWELL = new BlockSoundGroup(
        1.0f, 1.0f,
        SoundEvents.BLOCK_GLASS_BREAK, SoundEvents.BLOCK_STONE_STEP,
        SoundEvents.BLOCK_GLASS_PLACE, SoundEvents.BLOCK_GLASS_HIT,
        SoundEvents.BLOCK_STONE_FALL
    );

    BlockSoundGroup INKWELL = new BlockSoundGroup(
        1.0f, 1.0f,
        SoundEvents.BLOCK_GLASS_BREAK, SoundEvents.BLOCK_SLIME_BLOCK_STEP,
        SoundEvents.BLOCK_GLASS_PLACE, SoundEvents.BLOCK_GLASS_HIT,
        SoundEvents.BLOCK_SLIME_BLOCK_FALL
    );
}
