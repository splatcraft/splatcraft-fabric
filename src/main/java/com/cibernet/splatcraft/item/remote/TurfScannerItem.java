package com.cibernet.splatcraft.item.remote;

import com.cibernet.splatcraft.block.AbstractInkableBlock;
import com.cibernet.splatcraft.block.entity.AbstractInkableBlockEntity;
import com.cibernet.splatcraft.init.SplatcraftRegistries;
import com.cibernet.splatcraft.inkcolor.InkBlockUtils;
import com.cibernet.splatcraft.inkcolor.InkColor;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.cibernet.splatcraft.tag.SplatcraftBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.Map;
import java.util.TreeMap;

public class TurfScannerItem extends RemoteItem {
    public TurfScannerItem(Item.Settings settings) {
        super(settings, 2);
    }

    @Override
    public RemoteResult onRemoteUse(World world, BlockPos posA, BlockPos posB, ItemStack stack, InkColor colorIn, int mode) {
        return scanTurf(world, posA, posB, mode, null);
    }

    @SuppressWarnings("all")
    public static RemoteResult scanTurf(World world, BlockPos from, BlockPos to, int mode, ServerPlayerEntity target) {
        BlockPos minPos = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(to.getY(), from.getY()), Math.min(from.getZ(), to.getZ()));
        BlockPos maxPos = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(to.getY(), from.getY()), Math.max(from.getZ(), to.getZ()));


        if (!(minPos.getY() >= 0 && maxPos.getY() < 256)) {
            return createResult(false, new TranslatableText("status.scan_turf.out_of_world"));
        }

        for (int j = minPos.getZ(); j <= maxPos.getZ(); j += 16) {
            for (int k = minPos.getX(); k <= maxPos.getX(); k += 16) {
                if (!world.isChunkLoaded(new BlockPos(k, maxPos.getY() - minPos.getY(), j))) {
                    return createResult(false, new TranslatableText("status.scan_turf.out_of_world"));
                }
            }
        }

        int blockTotal = 0;
        int affectedBlockTotal = 0;
        TreeMap<InkColor, Integer> scores = new TreeMap<>();
        if (world.isClient) {
            return createResult(true, null);
        } else {
            if (mode == 0) {
                for (int x = minPos.getX(); x <= maxPos.getX(); x++)
                    for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                        int y = getTopSolidOrLiquidBlock(new BlockPos(x, 1, z), world, Math.min(maxPos.getY() + 2, 255)).down().getY();


                        if (y > maxPos.getY() || y < minPos.getY()) {
                            continue;
                        }

                        BlockPos checkPos = new BlockPos(x, y, z);
                        BlockState checkState = world.getBlockState(checkPos);


                        if (!InkBlockUtils.canInk(world, checkPos)) {
                            continue;
                        }

                        if (!checkState.getMaterial().blocksMovement() || checkState.getMaterial().isLiquid() || !InkBlockUtils.canInk(world, checkPos)) {
                            continue;
                        }

                        blockTotal++;

                        if (world.getBlockEntity(checkPos) instanceof AbstractInkableBlockEntity && world.getBlockState(checkPos).getBlock() instanceof AbstractInkableBlock) {
                            AbstractInkableBlockEntity blockEntity = (AbstractInkableBlockEntity) world.getBlockEntity(checkPos);
                            AbstractInkableBlock block = (AbstractInkableBlock) world.getBlockState(checkPos).getBlock();
                            if (blockEntity != null) {
                                InkColor color = blockEntity.getInkColor();

                                if (block.countsTowardsTurf(world, checkPos)) {
                                    if (scores.containsKey(color)) {
                                        scores.replace(color, scores.get(color) + 1);
                                    } else {
                                        scores.put(color, 1);
                                    }
                                    affectedBlockTotal++;
                                }
                            }
                        }
                    }
            } else if (mode == 1) {
                for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
                    for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                        for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
                            BlockPos checkPos = new BlockPos(x, y, z);
                            BlockState checkState = world.getBlockState(checkPos);
                            boolean isWall = false;

                            for (int j = 1; j <= 2; j++) {
                                if (World.isOutOfBuildLimitVertically(checkPos.up(j))) {
                                    break;
                                }
                                if (!InkBlockUtils.canInkPassthrough(world, checkPos.up(j))) {
                                    isWall = true;
                                    break;
                                }

                                if (j > maxPos.getY()) {
                                    break;
                                }
                            }

                            if (isWall || !InkBlockUtils.canInk(world, checkPos)) {
                                continue;
                            }

                            if (!checkState.getMaterial().blocksMovement() || checkState.getMaterial().isLiquid() || !InkBlockUtils.canInk(world, checkPos)) {
                                continue;
                            }

                            blockTotal++;

                            if (world.getBlockEntity(checkPos) instanceof AbstractInkableBlockEntity && world.getBlockState(checkPos).getBlock() instanceof AbstractInkableBlock) {
                                AbstractInkableBlockEntity blockEntity = (AbstractInkableBlockEntity) world.getBlockEntity(checkPos);
                                AbstractInkableBlock block = (AbstractInkableBlock) world.getBlockState(checkPos).getBlock();
                                if (blockEntity != null) {
                                    InkColor color = blockEntity.getInkColor();

                                    if (block.countsTowardsTurf(world, checkPos)) {
                                        if (scores.containsKey(color)) {
                                            scores.replace(color, scores.get(color) + 1);
                                        } else {
                                            scores.put(color, 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        InkColor[] colors = new InkColor[scores.size()];
        Float[] colorScores = new Float[scores.size()];

        InkColor winner = InkColors.NONE;
        float winnerScore = -1;
        int i = 0;
        for (Map.Entry<InkColor, Integer> entry : scores.entrySet()) {
            //world.getMinecraftServer().getPlayerList().sendMessage(new TextComponentTranslation("commands.turfWar.score", SplatCraftUtils.getColorName(entry.getKey()), String.format("%.1f",(entry.getValue()/(float)blockTotal)*100)));
            colors[i] = entry.getKey();
            colorScores[i] = (entry.getValue() / (float) blockTotal) * 100;

            if(winnerScore < entry.getValue()) {
                winner = entry.getKey();
                winnerScore = entry.getValue();
            }

            i++;
        }


        /*for(PlayerEntity player : world.getPlayers()) {
            InkColor color = ColorUtils.getInkColor(player);
            if (!ScoreboardHandler.hasColorCriterion(color)) {
                continue;
            }

            ScoreCriteria criterion = color == winner ? ScoreboardHandler.getColorWins(color) : ScoreboardHandler.getColorLosses(color);
            world.getScoreboard().forAllObjectives(criterion, player.getScoreboardName(), score -> score.increaseScore(1));
        }*/
		
			/*
			if(SplatcraftScoreboardHandler.hasGoal(entry.getKey()))
			{
				Iterator<ScoreObjective> iter;
				if(entry.getKey() == winner)
					iter = world.getScoreboard().getObjectivesFromCriteria(SplatcraftScoreboardHandler.getColorWins(entry.getKey())).iterator();
				else
					iter = world.getScoreboard().getObjectivesFromCriteria(SplatcraftScoreboardHandler.getColorLosses(entry.getKey())).iterator();
				while(iter.hasNext())
				{
					Iterator<Score> scoreIter = world.getScoreboard().getSortedScores(iter.next()).iterator();
					
					while(scoreIter.hasNext())
						scoreIter.next().increaseScore(1);
					
				}
			}
			*/

        if (scores.isEmpty()) {
            return createResult(false, new TranslatableText("status.scan_turf.no_ink"));
        }/* else {
            SendScanTurfResultsPacket packet = new SendScanTurfResultsPacket(colors, colorScores);
            if(target == null) {
                SplatcraftPacketHandler.sendToDim(packet, world);
            } else {
                SplatcraftPacketHandler.sendToPlayer(packet, target);
            }
        } TODO */

        return createResult(true, null).setIntResults(SplatcraftRegistries.INK_COLORS.getRawId(winner), (int) (((float) affectedBlockTotal / blockTotal) * 15));
    }

    private static BlockPos getTopSolidOrLiquidBlock(BlockPos pos, World world, int min) {
        Chunk chunk = world.getChunk(pos);
        BlockPos pos1;
        BlockPos pos2;

        for (pos1 = new BlockPos(pos.getX(), Math.min(chunk.getHighestNonEmptySectionYOffset() + 16, min), pos.getZ()); pos1.getY() >= 0; pos1 = pos2) {
            pos2 = pos1.down();
            BlockState state = chunk.getBlockState(pos2);

            if (SplatcraftBlockTags.BLOCKS_TURF.contains(state.getBlock()) || !InkBlockUtils.canInkPassthrough(world, pos2) || state.getMaterial().blocksMovement()) {
                break;
            }
        }

        return pos1;
    }
}
