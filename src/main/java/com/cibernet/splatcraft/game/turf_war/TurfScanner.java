package com.cibernet.splatcraft.game.turf_war;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.block.AbstractInkableBlock;
import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.tag.SplatcraftBlockTags;
import com.cibernet.splatcraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkManager;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurfScanner {
    public static final String TEXT_UNCHECKED = createTurfScanLang("unchecked");
    public static final String TEXT_SCORE = createTurfScanLang("score");
    public static final String TEXT_SCORE_WIN = createTurfScanLang("score.win");

    public final HashMap<Identifier, Integer> scores = new HashMap<>();
    public final List<Identifier> winners = new ArrayList<>();

    protected int scanSize = 0;
    protected int totalValid = 0;
    protected int totalCounted = 0;
    protected int totalUnchecked = 0;

    public final BlockPos from;
    public final BlockPos to;
    public final World world;

    public final TurfScanMode scanMode;
    @Nullable public final PlayerEntity player;
    public final boolean countOnlyInkable;
    public final boolean debug;

    protected TurfScanner(BlockPos from, BlockPos to, World world, TurfScanMode scanMode, PlayerEntity player, boolean countOnlyInkable, boolean debug) {
        this.from = from;
        this.to = to;
        this.world = world;

        this.scanMode = scanMode;
        this.player = player;
        this.countOnlyInkable = countOnlyInkable;
        this.debug = debug;
    }

    public static Pair<Integer, Integer> scanArea(World world, BlockPos from, BlockPos to, TurfScanMode scanMode, PlayerEntity player, boolean countOnlyInkable, boolean debug) throws IllegalArgumentException {
        return new TurfScanner(from, to, world, scanMode, player, countOnlyInkable, debug).scanArea();
    }

    protected Pair<Integer, Integer> scanArea() throws IllegalArgumentException {
        int minX = Math.min(this.from.getX(), this.to.getX());
        int maxX = Math.max(this.from.getX(), this.to.getX());
        int minY = Math.min(this.from.getY(), this.to.getY());
        int maxY = Math.max(this.from.getY(), this.to.getY());
        int minZ = Math.min(this.from.getZ(), this.to.getZ());
        int maxZ = Math.max(this.from.getZ(), this.to.getZ());

        if (minY < 0 || maxY > (this.world.getDimensionHeight() - 1)) {
            throw new IllegalArgumentException("out_of_world");
        } else {
            ChunkManager chunkManager = this.world.getChunkManager();

            if (!chunkManager.isChunkLoaded(minX >> 4, minZ >> 4)
             || !chunkManager.isChunkLoaded(maxX >> 4, maxZ >> 4)) {
                throw new IllegalArgumentException("not_loaded");
            }
        }

        /*
            GET SCORES
         */

        if (this.scanMode == TurfScanMode.TOP_DOWN) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    int topY = this.world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z) - 1;

                    if (topY >= minY && topY <= maxY) {
                        BlockPos pos = new BlockPos(x, topY, z);
                        this.scanPos(pos);
                    } else if (topY > maxY) {
                        BlockPos.Mutable mutable = new BlockPos.Mutable(x, maxY, z);
                        while (mutable.getY() >= minY) {
                            if (Heightmap.ALWAYS_TRUE.test(this.world.getBlockState(mutable))) {
                                this.scanPos(mutable);
                                break;
                            }

                            mutable.move(Direction.DOWN);
                        }
                    }
                }
            }
        } else if (this.scanMode == TurfScanMode.MULTI_LAYERED) {
            for (BlockPos pos : BlockPos.iterate(this.from, this.to)) {
                if (this.world.getBlockState(pos.up()).isAir()) {
                    this.scanPos(pos);
                }
            }
        } else {
            for (BlockPos pos : BlockPos.iterate(this.from, this.to)) {
                this.scanPos(pos);
            }
        }

        if (!(this.totalCounted > 0)) {
            throw new IllegalStateException("no_ink");
        }

        /*
            ANNOUNCE SCORES / WINNERS
         */

        if (!this.world.isClient) {
            /*
                WINNERS
             */

            // find highest value
            int highestValue = 0;
            for (Integer score : scores.values()) {
                if (score > highestValue) {
                    highestValue = score;
                }
            }

            // get a list of winners with the highest value
            for (Map.Entry<Identifier, Integer> entry : scores.entrySet()) {
                if (entry.getValue().equals(highestValue)) {
                    this.winners.add(entry.getKey());
                }
            }

            /*
                SCORES
             */

            // announce all scores
            for (Map.Entry<Identifier, Integer> entry : scores.entrySet()) {
                this.announceScore(InkColor.fromNonNull(entry.getKey()), entry.getValue());
            }

            // announce uninked blocks under certain conditions
            if (this.player != null && !this.countOnlyInkable && this.totalUnchecked > 0 && (this.totalUnchecked != this.totalValid && this.totalUnchecked != this.totalCounted)) {
                this.announceScore(null, this.totalUnchecked);
            }
        }

        return new Pair<>(this.totalCounted, this.totalValid);
    }

    protected void scanPos(BlockPos pos) throws IllegalArgumentException {
        this.scanSize++;

        BlockState state = this.world.getBlockState(pos);
        if (!state.isAir() && this.world.getFluidState(pos).isEmpty()) {
            this.totalValid++;

            Block block = state.getBlock();
            if (block instanceof AbstractInkableBlock && !SplatcraftBlockTags.SCAN_TURF_IGNORED.contains(block)) {
                BlockEntity blockEntity = this.world.getBlockEntity(pos);
                if (blockEntity instanceof AbstractInkableBlockEntity) {
                    Identifier inkColorId = ((AbstractInkableBlockEntity) blockEntity).getInkColor().id;
                    scores.merge(inkColorId, 1, Integer::sum);

                    this.totalCounted++;

                    if (this.world.isClient && this.debug) {
                        net.minecraft.client.MinecraftClient.getInstance().worldRenderer.addParticle(ParticleTypes.END_ROD, true, true, pos.getX() + 0.5d, pos.getY() + 0.5d + 1.0d, pos.getZ() + 0.5d, 0.0d, 0.0d, 0.0d);
                    }
                }
            } else {
                this.totalUnchecked++;
            }
        }
    }

    protected void announceScore(@Nullable InkColor inkColor, int score) {
        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.DOWN);

        TranslatableText text = new TranslatableText(
            inkColor != null && this.winners.contains(inkColor.id)
                ? TEXT_SCORE_WIN
                : TEXT_SCORE,

            inkColor == null
                ? new TranslatableText(TEXT_UNCHECKED)
                : ColorUtils.getFormattedColorName(inkColor),
            df.format((((float) score) / (this.countOnlyInkable ? this.totalCounted : this.totalValid)) * 100)
        );

        Utils.announceMessageIfNonNull(text, this.player, this.world);
    }

    public static final String TURF_SCAN_TRANSLATION = "text." + Splatcraft.MOD_ID + ".scan_turf";
    public static String createTurfScanLang(String append) {
        return TURF_SCAN_TRANSLATION + "." + append;
    }
}
