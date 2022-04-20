package com.thana.sugarapi.common.mixin;

import com.thana.sugarapi.client.config.ClientConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z"))
    public boolean aiStep(LocalPlayer player, MobEffect mobEffect) {
        return !ClientConfig.is("blindnessSprint") && player.hasEffect(mobEffect);
    }
}
