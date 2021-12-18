package net.splatcraft.mixin;

import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;
import net.splatcraft.Splatcraft;
import net.splatcraft.command.argument.InkColorArgumentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.splatcraft.util.SplatcraftConstants.COMMAND_ARGUMENT_INK_COLOR;

@Mixin(ArgumentTypes.class)
public class ArgumentTypesMixin {
    @Inject(method = "register()V", at = @At("TAIL"))
    private static void onRegister(CallbackInfo ci) {
        ArgumentTypes.register(new Identifier(Splatcraft.MOD_ID, COMMAND_ARGUMENT_INK_COLOR).toString(), InkColorArgumentType.class, new ConstantArgumentSerializer<>(InkColorArgumentType::inkColor));
    }
}
