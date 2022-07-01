package net.splatcraft.mixin;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.splatcraft.api.Splatcraft;
import net.splatcraft.impl.command.argument.InkColorArgumentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.splatcraft.api.util.SplatcraftConstants.*;

@Mixin(ArgumentTypes.class)
public abstract class ArgumentTypesMixin {
    @Shadow
    private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> ArgumentSerializer<A, T> register(Registry<ArgumentSerializer<?, ?>> registry, String id, Class<? extends A> clazz, ArgumentSerializer<A, T> serializer) { throw new AssertionError(); }

    @Inject(method = "register(Lnet/minecraft/util/registry/Registry;)Lnet/minecraft/command/argument/serialize/ArgumentSerializer;", at = @At("TAIL"))
    private static void onRegister(Registry<ArgumentSerializer<?, ?>> registry, CallbackInfoReturnable<ArgumentSerializer<?, ?>> cir) {
        splatcraft_register(registry, COMMAND_ARGUMENT_INK_COLOR, InkColorArgumentType.class, ConstantArgumentSerializer.of(InkColorArgumentType::inkColor));
    }

    @Unique
    private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> ArgumentSerializer<A, T> splatcraft_register(Registry<ArgumentSerializer<?, ?>> registry, String id, Class<? extends A> clazz, ArgumentSerializer<A, T> serializer) {
        return register(registry, new Identifier(Splatcraft.MOD_ID, id).toString(), clazz, serializer);
    }
}
