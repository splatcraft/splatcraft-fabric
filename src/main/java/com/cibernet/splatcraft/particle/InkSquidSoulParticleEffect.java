package com.cibernet.splatcraft.particle;

import com.cibernet.splatcraft.component.PlayerDataComponent;
import com.cibernet.splatcraft.entity.InkableEntity;
import com.cibernet.splatcraft.init.SplatcraftParticles;
import com.cibernet.splatcraft.inkcolor.InkColors;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class InkSquidSoulParticleEffect extends InkedParticleEffect {
    public static final Codec<InkSquidSoulParticleEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("r").forGetter(InkedParticleEffect::getRed),
        Codec.FLOAT.fieldOf("g").forGetter(InkedParticleEffect::getGreen),
        Codec.FLOAT.fieldOf("b").forGetter(InkedParticleEffect::getBlue),
        Codec.FLOAT.fieldOf("scale").forGetter(InkedParticleEffect::getScale)
    ).apply(instance, InkSquidSoulParticleEffect::new));

    @SuppressWarnings("deprecation")
    public static final ParticleEffect.Factory<InkSquidSoulParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<InkSquidSoulParticleEffect>() {
        @Override
        public InkSquidSoulParticleEffect read(ParticleType<InkSquidSoulParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            float r = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float g = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float b = (float)stringReader.readDouble();
            stringReader.expect(' ');
            float scale = (float)stringReader.readDouble();
            return new InkSquidSoulParticleEffect(r, g, b, scale);
        }

        @Override
        public InkSquidSoulParticleEffect read(ParticleType<InkSquidSoulParticleEffect> particleType, PacketByteBuf buf) {
            return new InkSquidSoulParticleEffect(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
        }
    };

    public InkSquidSoulParticleEffect(float red, float green, float blue, float scale) {
        super(red, green, blue, scale);
    }
    public InkSquidSoulParticleEffect(Entity entity) {
        super(
            entity instanceof PlayerEntity
                ? PlayerDataComponent.getInkColor((PlayerEntity) entity)
                : entity instanceof InkableEntity
                    ? ((InkableEntity) entity).getInkColor()
                    : InkColors.NONE
        );
    }

    @Override
    public ParticleType<?> getType() {
        return SplatcraftParticles.INK_SQUID_SOUL;
    }
}
