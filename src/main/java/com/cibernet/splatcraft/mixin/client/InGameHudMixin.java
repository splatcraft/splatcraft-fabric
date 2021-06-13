package com.cibernet.splatcraft.mixin.client;

import com.cibernet.splatcraft.SplatcraftClient;
import com.cibernet.splatcraft.client.config.SplatcraftConfig;
import com.cibernet.splatcraft.client.config.enums.InkAmountIndicator;
import com.cibernet.splatcraft.client.signal.SignalRendererManager;
import com.cibernet.splatcraft.component.Charge;
import com.cibernet.splatcraft.component.Cooldown;
import com.cibernet.splatcraft.component.LazyPlayerDataComponent;
import com.cibernet.splatcraft.init.SplatcraftGameRules;
import com.cibernet.splatcraft.inkcolor.ColorUtil;
import com.cibernet.splatcraft.item.InkTankArmorItem;
import com.cibernet.splatcraft.item.weapon.ChargerItem;
import com.cibernet.splatcraft.util.InkItemUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("SuspiciousNameCombination")
@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow private int scaledHeight;
    @Shadow private int scaledWidth;
    @Shadow private ItemStack currentStack;
    @Shadow private int heldItemTooltipFade;
    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract PlayerEntity getCameraPlayer();
    @Shadow protected abstract void renderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed);

    private static final Identifier splatcraft_WIDGETS = SplatcraftClient.texture("gui/widgets");
    private static final Identifier splatcraft_SQUID_GUI_ICONS_TEXTURE = SplatcraftClient.texture("gui/squid_icons");

    @Inject(method = "render", at = @At("TAIL"))
    private void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (this.client.player != null && this.client.options.getPerspective().isFirstPerson() && SignalRendererManager.PLAYER_TO_SIGNAL_MAP.containsKey(this.client.player)) {
            InventoryScreen.drawEntity(30, 70, SplatcraftConfig.UI.renderPaperDollWhenSignalling.value ? 25 : 0, 0, 0, this.client.player);
        }
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void renderHotbar(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            PlayerEntity player = client.player;

            // render ink tank
            this.splatcraft_renderInkTank(matrices);

            if (player != null) {
                Hand hand = getChargerHand();
                if (hand != null) {
                    if (!LazyPlayerDataComponent.isSquid(player)) {
                        Item item = player.getStackInHand(hand).getItem();
                        if (item instanceof ChargerItem) {
                            matrices.push();

                            RenderSystem.setShader(GameRenderer::getPositionTexShader);
                            RenderSystem.setShaderTexture(0, splatcraft_WIDGETS);
                            RenderSystem.enableBlend();
                            float[] color = ColorUtil.getInkColor(player).getColorOrLockedComponents();
                            if (SplatcraftConfig.INK.inkColoredChargerIndicator.value) {
                                RenderSystem.clearColor(color[0], color[1], color[2], 1.0f);
                            }

                            boolean firstPerson = this.client.options.getPerspective().isFirstPerson();
                            boolean targetingEntity = this.client.targetedEntity != null;
                            float attackCooldownProgress = !LazyPlayerDataComponent.isSquid(this.client.player) && this.client.options.attackIndicator == AttackIndicator.CROSSHAIR && firstPerson
                                    ? player.getAttackCooldownProgress(0.0f)
                                    : 1.0f;
                            int x = this.scaledWidth / 2 - 15 + (firstPerson ? + 0 : + 39);
                            int y = this.scaledHeight / 2 + 15 + (firstPerson ? + 0 : - 16) + (targetingEntity || attackCooldownProgress < 1.0f ? 8 : -2);

                            splatcraft_drawTexture(
                                matrices,
                                x, y,
                                30, 9,
                                88, 0,
                                30, 9,
                                256, 256
                            );

                            float charge = Charge.getCharge(player).getTime();
                            float maxCharge = ((ChargerItem) item).component.maxCharge;
                            int fullWidth = 28;
                            int width = (int) ((charge / maxCharge) * fullWidth) + 1;
                            splatcraft_drawTexture(
                                matrices,
                                x, y,
                                width, 9,
                                88, 9,
                                width, 9,
                                256, 256
                            );

                            matrices.pop();
                        }
                    }
                }

                if (SplatcraftConfig.UI.invisibleHotbarWhenSquid.value && LazyPlayerDataComponent.isSquid(client.player)) {
                    // render held item
                    if (SplatcraftConfig.UI.renderHeldItemWhenHotbarInvisible.value) {
                        if (!this.currentStack.isEmpty()) {
                            boolean isCreative = player.isCreative();

                            int x = (this.scaledWidth / 2) - 90 + 4 * 20 + 2;
                            int y = this.scaledHeight - 16 - 70 + (isCreative ? +0 : +8);
                            this.renderHotbarItem(x, y + (((Math.min((int) ((float) this.heldItemTooltipFade * 256.0f / 10.0f), 255) > 0) ? 0 : +(isCreative ? +46 : +21))) - SplatcraftConfig.UI.invisibleHotbarStatusBarsShift.value, tickDelta, player, this.currentStack, 1);
                        }
                    }

                    // hide hotbar when squid
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"))
    private void renderStatusBars(MatrixStack matrices, CallbackInfo ci) {
        if (SplatcraftConfig.UI.invisibleHotbarWhenSquid.value && LazyPlayerDataComponent.isSquid(client.player)) {
            // shift status bars down when squid (inverted for user-friendliness)
            this.scaledHeight -= SplatcraftConfig.UI.invisibleHotbarStatusBarsShift.value;
        }
    }

    @Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFuncSeparate(Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void changeCrosshairColor(MatrixStack matrices, CallbackInfo ci) {
        if (this.client.player != null) {
            if (LazyPlayerDataComponent.isSquid(this.client.player)) {
                if (SplatcraftConfig.UI.invisibleCrosshairWhenSquid.value) {
                    ci.cancel();
                } else if (SplatcraftConfig.INK.inkColoredCrosshairWhenSquid.value) {
                    ci.cancel();

                    // set color
                    float[] color = ColorUtil.getInkColor(client.player).getColorOrLockedComponents();
                    RenderSystem.clearColor(color[0], color[1], color[2], 1.0f);

                    // render crosshair
                    InGameHud $this = InGameHud.class.cast(this);
                    $this.drawTexture(matrices, (this.scaledWidth - 15) / 2, (this.scaledHeight - 15) / 2, 0, 0, 15, 15);
                }
            }
        }
    }

    private void splatcraft_renderInkTank(MatrixStack matrices) {
        InGameHud $this = InGameHud.class.cast(this);

        if (this.client.player != null && SplatcraftGameRules.getBoolean(this.client.player.world, SplatcraftGameRules.REQUIRE_INK_TANK)) {
            ItemStack chestStack = this.client.player.getEquippedStack(EquipmentSlot.CHEST);
            if (chestStack.getItem() instanceof InkTankArmorItem) {
                float inkAmount = InkTankArmorItem.getInkAmount(chestStack) / InkItemUtil.getInkTankCapacity(chestStack);
                if (inkAmount < 1.0f || SplatcraftConfig.INK.inkAmountIndicatorAlwaysVisible.value) {
                    if (SplatcraftConfig.INK.inkAmountIndicator.value != InkAmountIndicator.OFF) {
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        RenderSystem.setShader(GameRenderer::getPositionTexShader);
                        RenderSystem.setShaderTexture(0, splatcraft_SQUID_GUI_ICONS_TEXTURE);
                        RenderSystem.enableBlend();
                        float[] color = ColorUtil.getInkColor(client.player).getColorOrLockedComponents();
                        RenderSystem.clearColor(color[0], color[1], color[2], 1.0f);

                        if (SplatcraftConfig.INK.inkAmountIndicator.value == InkAmountIndicator.CROSSHAIR) {
                            float attackCooldownProgress = LazyPlayerDataComponent.isSquid(this.client.player) || this.client.options.attackIndicator != AttackIndicator.CROSSHAIR ? 1.0f : this.client.player.getAttackCooldownProgress(0.0f);
                            boolean targetingEntity = false;
                            if (this.client.options.attackIndicator == AttackIndicator.CROSSHAIR && this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity && attackCooldownProgress >= 1.0f) {
                                targetingEntity = this.client.player.getAttackCooldownProgressPerTick() > 5.0f;
                                targetingEntity &= this.client.targetedEntity.isAlive();
                            }

                            int y = this.scaledHeight / 2 - 7 + 17 + (this.client.options.getPerspective().isFirstPerson() ? (targetingEntity || attackCooldownProgress < 1.0f ? 8 : -2) : - 18);
                            int x = this.scaledWidth / 2 + (this.client.options.getPerspective().isFirstPerson() ? - 9 : + 23);

                            float width = (inkAmount * 17.0f);
                            // draw foreground (colored)
                            splatcraft_drawTexture(matrices, x, y, 52, 94, width, 4);
                            // draw background (uncolored)
                            RenderSystem.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
                            $this.drawTexture(matrices, x, y, 36, 94, 16, 4);
                        } else if (SplatcraftConfig.INK.inkAmountIndicator.value == InkAmountIndicator.HOTBAR) {
                            int halfScaledWidth = this.scaledWidth / 2;
                            int y = this.scaledHeight - 20;
                            int x = halfScaledWidth + 91 + 32 + (this.getCameraPlayer().getMainArm().getOpposite() == Arm.RIGHT ? -91 - 22 : 0);

                            float offset = inkAmount * 19.0f;
                            // draw foreground (colored)
                            splatcraft_drawTexture(matrices, x, y + 18 - offset, 18, 112 - offset, 18, offset);
                            RenderSystem.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
                            // draw background (uncolored)
                            $this.drawTexture(matrices, x, y, 0, 94, 18, 18);

                            if (SplatcraftConfig.INK.inkAmountIndicatorExclamations.value) {
                                if (inkAmount <= (float) SplatcraftConfig.INK.inkAmountIndicatorExclamationsMin.value / 100) {
                                    $this.drawTexture(matrices, x, y, 0, 112, 18, 18);
                                } else if (inkAmount >= (float) SplatcraftConfig.INK.inkAmountIndicatorExclamationsMax.value / 100) {
                                    $this.drawTexture(matrices, x, y, 18, 112, 18, 18);
                                }
                            }
                        }
                    }
                }
            }
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
        RenderSystem.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void splatcraft_drawTexture(MatrixStack matrices, float x, float y, float u, float v, float width, float height) {
        InGameHud $this = InGameHud.class.cast(this);
        splatcraft_drawTexture(matrices, x, y, $this.getZOffset(), u, v, width, height, 256, 256);
    }
    private static void splatcraft_drawTexture(MatrixStack matrices, float x, float y, float z, float u, float v, float width, float height, float textureHeight, float textureWidth) {
        splatcraft_drawTexture(matrices, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight);
    }
    private static void splatcraft_drawTexture(MatrixStack matrices, float x0, float x1, float y0, float y1, float z, float regionWidth, float regionHeight, float u, float v, float textureWidth, float textureHeight) {
        splatcraft_drawTexturedQuad(matrices.peek().getModel(), x0, x1, y0, y1, z, (u + 0.0f) / textureWidth, (u + regionWidth) / textureWidth, (v + 0.0f) / textureHeight, (v + regionHeight) / textureHeight);
    }
    private static void splatcraft_drawTexture(MatrixStack matrices, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        splatcraft_drawTexture(matrices, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
    }
    private static void splatcraft_drawTexture(MatrixStack matrices, int x0, int y0, int x1, int y1, int z, int regionWidth, int regionHeight, float u, float v, int textureWidth, int textureHeight) {
        splatcraft_drawTexturedQuad(matrices.peek().getModel(), x0, y0, x1, y1, z, (u + 0.0F) / (float)textureWidth, (u + (float)regionWidth) / (float)textureWidth, (v + 0.0F) / (float)textureHeight, (v + (float)regionHeight) / (float)textureHeight);
    }
    private static void splatcraft_drawTexturedQuad(Matrix4f matrices, float x0, float x1, float y0, float y1, float z, float u0, float u1, float v0, float v1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrices, x0, y1, z).texture(u0, v1).next();
        bufferBuilder.vertex(matrices, x1, y1, z).texture(u1, v1).next();
        bufferBuilder.vertex(matrices, x1, y0, z).texture(u1, v0).next();
        bufferBuilder.vertex(matrices, x0, y0, z).texture(u0, v0).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    @Nullable
    private static Hand getChargerHand() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            PlayerEntity player = client.player;

            if (player != null) {
                ItemStack stack = player.getMainHandStack();
                if (stack != null) {
                    if (!(isChargerItem(player, stack))) {
                        ItemStack offhandStack = player.getOffHandStack();
                        if (isChargerItem(player, offhandStack)) {
                            return Hand.OFF_HAND;
                        }
                    } else {
                        return Hand.MAIN_HAND;
                    }
                }
            }
        }

        return null;
    }
    private static boolean isChargerItem(PlayerEntity player, ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ChargerItem) {
            Cooldown cooldown = Cooldown.getCooldown(player);
            return cooldown.preventsWeaponUse() && cooldown.getTime() == 0;
        }

        return false;
    }
}
