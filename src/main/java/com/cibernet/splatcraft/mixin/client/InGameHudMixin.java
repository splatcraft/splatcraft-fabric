package com.cibernet.splatcraft.mixin.client;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.config.SplatcraftConfig;
import com.cibernet.splatcraft.client.config.enums.InkAmountIndicator;
import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.init.SplatcraftGameRules;
import com.cibernet.splatcraft.inkcolor.ColorUtils;
import com.cibernet.splatcraft.item.AbstractWeaponItem;
import com.cibernet.splatcraft.item.InkTankArmorItem;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.options.AttackIndicator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("deprecation")
@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow private int scaledHeight;
    @Shadow private int scaledWidth;
    @Shadow @Final private MinecraftClient client;
    @Shadow protected abstract PlayerEntity getCameraPlayer();

    private static final Identifier SQUID_GUI_ICONS_TEXTURE = new Identifier(Splatcraft.MOD_ID, "textures/gui/squid_icons.png");

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void renderHotbar(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        if (SplatcraftConfig.UI.invisibleHotbarWhenSquid.getBoolean() && PlayerDataComponent.isSquid(client.player)) {
            // hide hotbar when squid
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"))
    private void renderStatusBars(MatrixStack matrices, CallbackInfo ci) {
        if (SplatcraftConfig.UI.invisibleHotbarWhenSquid.getBoolean() && PlayerDataComponent.isSquid(client.player)) {
            // shift status bars down when squid (inverted for user-friendliness)
            this.scaledHeight -= SplatcraftConfig.UI.invisibleHotbarStatusBarsShift.getInt();
        }
    }

    @Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFuncSeparate(Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void changeCrosshairColor(MatrixStack matrices, CallbackInfo ci) {
        if (this.client.player != null) {
            if (PlayerDataComponent.isSquid(this.client.player)) {
                if (SplatcraftConfig.UI.invisibleCrosshairWhenSquid.getBoolean()) {
                    ci.cancel();
                } else if (SplatcraftConfig.UI.inkColoredCrosshairWhenSquid.getBoolean()) {
                    ci.cancel();

                    // set color
                    int color = ColorUtils.getInkColor(client.player).getColor();
                    float r = (float) (color >> 16 & 255) / 255.0F;
                    float g = (float) (color >> 8 & 255) / 255.0F;
                    float b = (float) (color & 255) / 255.0F;
                    RenderSystem.color4f(r, g, b, 1.0F);

                    // render crosshair
                    InGameHud $this = InGameHud.class.cast(this);
                    $this.drawTexture(matrices, (this.scaledWidth - 15) / 2, (this.scaledHeight - 15) / 2, 0, 0, 15, 15);
                }
            }

            // render ink tank
            this.renderInkTank(matrices);
        }
    }

    private void renderInkTank(MatrixStack matrices) {
        InGameHud $this = InGameHud.class.cast(this);

        if (this.client.player != null && SplatcraftGameRules.getBoolean(this.client.player.world, SplatcraftGameRules.REQUIRE_INK_TANK)) {
            ItemStack chestStack = this.client.player.getEquippedStack(EquipmentSlot.CHEST);
            if (chestStack.getItem() instanceof InkTankArmorItem) {
                float inkAmount = AbstractWeaponItem.getInkAmount(this.client.player, chestStack) / ((InkTankArmorItem) chestStack.getItem()).capacity;
                if (inkAmount < 1.0F || SplatcraftConfig.UI.inkAmountIndicatorAlwaysVisible.getBoolean()) {
                    if (SplatcraftConfig.UI.inkAmountIndicator.getEnum() != InkAmountIndicator.OFF) {
                        this.client.getTextureManager().bindTexture(SQUID_GUI_ICONS_TEXTURE);
                        int color = ColorUtils.getInkColor(client.player).getColor();
                        float r = (float) (color >> 16 & 255) / 255.0F;
                        float g = (float) (color >> 8 & 255) / 255.0F;
                        float b = (float) (color & 255) / 255.0F;
                        RenderSystem.color4f(r, g, b, 1.0F);

                        if (SplatcraftConfig.UI.inkAmountIndicator.getEnum() == InkAmountIndicator.CROSSHAIR) {
                            float attackCooldownProgress = PlayerDataComponent.isSquid(this.client.player) || this.client.options.attackIndicator != AttackIndicator.CROSSHAIR ? 1.0F : this.client.player.getAttackCooldownProgress(0.0F);
                            boolean targetingEntity = false;
                            if (this.client.options.attackIndicator == AttackIndicator.CROSSHAIR && this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity && attackCooldownProgress >= 1.0F) {
                                targetingEntity = this.client.player.getAttackCooldownProgressPerTick() > 5.0F;
                                targetingEntity &= this.client.targetedEntity.isAlive();
                            }

                            int y = this.scaledHeight / 2 - 7 + 17 + (targetingEntity || attackCooldownProgress < 1.0F ? 8 : -2);
                            int x = this.scaledWidth / 2 - 9;

                            int width = (int) (inkAmount * 17.0F);
                            // draw foreground (colored)
                            $this.drawTexture(matrices, x, y, 52, 94, width, 4);
                            // draw background (uncolored)
                            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                            $this.drawTexture(matrices, x, y, 36, 94, 16, 4);
                        } else if (SplatcraftConfig.UI.inkAmountIndicator.getEnum() == InkAmountIndicator.HOTBAR) {
                            int halfScaledWidth = this.scaledWidth / 2;
                            int y = this.scaledHeight - 20;
                            int x = halfScaledWidth + 91 + 32 + (this.getCameraPlayer().getMainArm().getOpposite() == Arm.RIGHT ? -91 - 22 : 0);

                            int offset = (int) (inkAmount * 19.0F);
                            // draw foreground (colored)
                            $this.drawTexture(matrices, x, y + 18 - offset, 18, 112 - offset, 18, offset);
                            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                            // draw background (uncolored)
                            $this.drawTexture(matrices, x, y, 0, 94, 18, 18);

                            if (SplatcraftConfig.UI.inkAmountIndicatorExclamations.getBoolean()) {
                                if (inkAmount <= (float) SplatcraftConfig.UI.inkAmountIndicatorExclamationsMin.getInt() / 100) {
                                    $this.drawTexture(matrices, x, y, 0, 112, 18, 18);
                                } else if (inkAmount >= (float) SplatcraftConfig.UI.inkAmountIndicatorExclamationsMax.getInt() / 100) {
                                    $this.drawTexture(matrices, x, y, 18, 112, 18, 18);
                                }
                            }
                        }
                    }
                }
            }
        }

        this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
