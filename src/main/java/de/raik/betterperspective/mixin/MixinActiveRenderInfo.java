package de.raik.betterperspective.mixin;

import de.raik.betterperspective.BetterPerspectiveAddon;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * Mixin class which sets the camera to set own camera adjustments
 * with the values from the MouseHelper
 *
 * @author Raik
 * @version 1.0
 */
@Mixin(ActiveRenderInfo.class)
public class MixinActiveRenderInfo {

    @Shadow
    private float pitch;

    @Shadow
    private float yaw;

    /**
     * Injector changing the yaw and pitch when moving the position
     * to make safe adjustments
     */
    @Inject(method = "update(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/entity/Entity;ZZF)V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/renderer/ActiveRenderInfo.movePosition(DDD)V",
            ordinal = 0
    ))
    private void movePositionAdjustments(IBlockReader area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        BetterPerspectiveAddon addon = BetterPerspectiveAddon.getAddonInstance();
        if (addon.isPerspectiveEnabled()) {
            this.pitch = addon.getRotationPitch();
            this.yaw = addon.getRotationYaw();
        }
    }

    /**
     * Method to modify the arguments of the
     * move position argument to make adjustment
     * for the mode
     */
    @ModifyArgs(method = "update(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/entity/Entity;ZZF)V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/renderer/ActiveRenderInfo.setDirection(FF)V",
            ordinal = 0
    ))
    private void setDirectionYawAndPitch(Args args) {
        BetterPerspectiveAddon addon = BetterPerspectiveAddon.getAddonInstance();
        if (addon.isPerspectiveEnabled()) {
            args.setAll(addon.getRotationYaw(), addon.getRotationPitch());
        }
    }

}
