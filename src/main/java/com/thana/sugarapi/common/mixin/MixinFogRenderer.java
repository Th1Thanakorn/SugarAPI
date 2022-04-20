package com.thana.sugarapi.common.mixin;

import com.thana.sugarapi.client.config.ClientConfig;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public class MixinFogRenderer {

    @Redirect(method = "setupColor", at =  @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z"))
    private static boolean updateFogColor(LivingEntity entity, MobEffect effect) {
        if (effect == MobEffects.BLINDNESS) {
            return ClientConfig.is("renderBlindness") && entity.hasEffect(MobEffects.BLINDNESS);
        }
        return entity.hasEffect(effect);
    }

    @Redirect(method = "setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z"))
    private static boolean setupFog(LivingEntity entity, MobEffect effect) {
        if (effect == MobEffects.BLINDNESS) {
            return ClientConfig.is("renderBlindness") && entity.hasEffect(MobEffects.BLINDNESS);
        }
        return entity.hasEffect(effect);
    }
}
