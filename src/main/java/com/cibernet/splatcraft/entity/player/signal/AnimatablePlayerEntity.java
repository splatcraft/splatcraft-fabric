package com.cibernet.splatcraft.entity.player.signal;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.client.signal.Signal;
import com.cibernet.splatcraft.client.signal.SignalRendererManager;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.Level;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.geo.exception.GeckoLibException;

public class AnimatablePlayerEntity implements IAnimatable {
    public final PlayerEntity player;
    public final Signal signal;
    protected AnimationState animationState;
    protected boolean hasPlayed = false;
    protected final AnimationFactory factory = new AnimationFactory(this);

    public AnimatablePlayerEntity(PlayerEntity player, Signal signal) {
        this.player = player;
        this.signal = signal;
    }

    private <E extends IAnimatable> PlayState controller(AnimationEvent<E> event) {
        try {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", false));

            this.animationState = event.getController().getAnimationState();
            if (!this.hasPlayed) {
                this.hasPlayed = this.animationState == AnimationState.Running;
            }
        } catch (GeckoLibException ignored) {
            SignalRendererManager.reset(this.player);
            Splatcraft.log(Level.ERROR, "Signal animation tried to play but wasn't found: " + signal.id);
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0.0f, this::controller));
    }

    public boolean isRunning() {
        return !this.hasPlayed || this.animationState == AnimationState.Running;
    }
    public static boolean canStart(PlayerEntity player) {
        return /*!PlayerDataComponent.isMoving(this.player) && player.isOnGround() &&*/ !player.isSpectator();
    }
    public static boolean shouldContinue(PlayerEntity player) {
        return AnimatablePlayerEntity.canStart(player);
    }
    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
