package com.cibernet.splatcraft.client.gui.screen;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.SplatcraftClient;
import com.cibernet.splatcraft.client.init.SplatcraftKeyBindings;
import com.cibernet.splatcraft.client.signal.Signal;
import com.cibernet.splatcraft.client.signal.SignalRegistryManager;
import com.cibernet.splatcraft.client.signal.SignalRendererManager;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(EnvType.CLIENT)
public class SignalSelectionScreen extends Screen {
    private static final Identifier TEXTURE = SplatcraftClient.texture("gui/signal_selection");
    private static final int BUTTON_SIZE = 30;
    private static final int BUTTON_TEXTURE_SIZE = BUTTON_SIZE - 2;
    private static final int BUTTON_MARGIN = 3;
    private static final int HEADER_SIZE = 30;
    private static final int BUTTON_SPACE = BUTTON_SIZE + BUTTON_MARGIN;
    private static final int WIDGETS_PER_ROW = 4;

    private final int uiWidth;
    private Signal signal;
    private int lastMouseX;
    private int lastMouseY;
    private int nextTime;
    private boolean mouseUsedForSelection;
    private final List<SignalSelectionScreen.ButtonWidget> signalButtons = Lists.newArrayList();

    public SignalSelectionScreen() {
        super(NarratorManager.EMPTY);

        this.uiWidth = (WIDGETS_PER_ROW * BUTTON_SPACE) - 18;
        this.signal = SignalRegistryManager.fromIndex(SignalRegistryManager.SIGNALS.size() / 2);
    }

    @Override
    protected void init() {
        super.init();

        int size = SignalRegistryManager.SIGNALS.size();
        int sizeRounded = (size / WIDGETS_PER_ROW) * WIDGETS_PER_ROW;
        int lastRowCount = size - sizeRounded;

        for (int i = 0; i < size; i++) {
            this.signalButtons.add(
                new ButtonWidget(
                    SignalRegistryManager.fromIndex(i),
                    (this.width / 2) + ((i % WIDGETS_PER_ROW) * BUTTON_SIZE) - (
                        i < sizeRounded
                            ? (uiWidth / 2)
                            : ((lastRowCount * BUTTON_SPACE) / 2) - 6
                    ),
                    ((this.height / 2) - HEADER_SIZE) + ((i / WIDGETS_PER_ROW) * BUTTON_SIZE)
                )
            );
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.client != null && !this.checkForClose()) {
            this.fillGradient(matrices, 0, 0, this.width, this.height, 0x4D101010, 0x4D101010);

            matrices.push();

            // centralise
            int xOffset = -1;
            mouseX -= xOffset;

            int rowCount = (SignalRegistryManager.SIGNALS.size() + (WIDGETS_PER_ROW - 1)) / WIDGETS_PER_ROW;
            int height = rowCount * BUTTON_SIZE + HEADER_SIZE;
            int yOffset = (-height / 2) + HEADER_SIZE + BUTTON_SIZE;
            mouseY -= yOffset;

            matrices.translate(xOffset, yOffset, 0.0d);

            // automatically move to next signal
            if (!mouseUsedForSelection) {
                nextTime++;
                if (nextTime >= 23) {
                    this.signal = this.signal.next();
                    nextTime = 0;
                }
            }

            // render background texture(s)
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            matrices.push();
            RenderSystem.enableBlend();
            RenderSystem.setShaderTexture(0, TEXTURE);
            int x = this.width / 2 - 62;
            int y = this.height / 2 - (HEADER_SIZE + BUTTON_SIZE);
            drawTexture(matrices, x, y, 0.0f, 0.0f, 125, 75, 128, 128);
            matrices.pop();

            // render super + text
            super.render(matrices, mouseX, mouseY, delta);
            if (this.signal != null) {
                drawCenteredText(matrices, this.textRenderer, signal.text, this.width / 2, (this.height / 2) - HEADER_SIZE - 23, -1);
            }

            // render signal buttons
            for (ButtonWidget buttonWidget : this.signalButtons) {
                buttonWidget.render(matrices, mouseX, mouseY, delta);
                if (this.signal != null) {
                    buttonWidget.setSelected(signal == buttonWidget.signal);
                }
                if (buttonWidget.isHovered() && !(this.lastMouseX == mouseX && this.lastMouseY == mouseY)) {
                    this.signal = buttonWidget.signal;
                    this.mouseUsedForSelection = true;
                }
            }

            // update values for next tick
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;

            matrices.pop();
        }
    }

    private void apply() {
        if (this.client != null) {
            apply(this.client, this.signal);
        }
    }

    private static void apply(MinecraftClient client, Signal signal) {
        if (client.player != null && signal != null) {
            SignalRendererManager.sendSignal(client.player, signal);
        }
    }

    private boolean checkForClose() {
        if (this.client != null && !InputUtil.isKeyPressed(this.client.getWindow().getHandle(), SplatcraftKeyBindings.OPEN_SIGNAL_SELECTION_SCREEN.boundKey.getCode())) {
            this.apply();
            this.client.openScreen(null);
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == SplatcraftKeyBindings.OPEN_SIGNAL_SELECTION_SCREEN.boundKey.getCode()) {
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public static class ButtonWidget extends ClickableWidget {
        private final Signal signal;
        private boolean selected;

        protected final Identifier texture;
        protected final Identifier selectedTexture;

        public ButtonWidget(Signal signal, int x, int y) {
            super(x, y, BUTTON_SIZE - 2, BUTTON_TEXTURE_SIZE, signal.text);
            this.signal = signal;

            this.texture = new Identifier(signal.id.getNamespace(), "textures/" + Splatcraft.MOD_ID + "/gui/signals/" + signal.id.getPath() + ".png");
            this.selectedTexture = new Identifier(signal.id.getNamespace(), "textures/" + Splatcraft.MOD_ID + "/gui/signals/" + signal.id.getPath() + "_selected.png");
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            MinecraftClient client = MinecraftClient.getInstance();
            TextureManager textureManager = client.getTextureManager();

            this.drawBackground(matrices, textureManager);
            this.renderIcon(matrices, client.getTextureManager());
            if (this.selected) {
                this.drawSelectionBox(matrices, textureManager);
            }
        }

        private void renderIcon(MatrixStack matrices, TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, this.selected ? selectedTexture : texture);
            matrices.push();
            matrices.translate(this.x, this.y, 0.0d);
            drawTexture(matrices, 0, 0, BUTTON_TEXTURE_SIZE, 0.0f, BUTTON_TEXTURE_SIZE, BUTTON_TEXTURE_SIZE, 28, 28);
            matrices.pop();
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        private void drawBackground(MatrixStack matrices, TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            matrices.push();
            matrices.translate(this.x, this.y, 0.0d);
            drawTexture(matrices, 0, 0, 0.0f, 75.0f, BUTTON_TEXTURE_SIZE, BUTTON_TEXTURE_SIZE, 128, 128);
            matrices.pop();
        }

        private void drawSelectionBox(MatrixStack matrices, TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            matrices.push();
            matrices.translate(this.x, this.y, 0.0d);
            drawTexture(matrices, 0, 0, BUTTON_TEXTURE_SIZE, 75.0f, BUTTON_TEXTURE_SIZE, BUTTON_TEXTURE_SIZE, 128, 128);
            matrices.pop();
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {
            this.method_37021(builder);
        }
    }
}
