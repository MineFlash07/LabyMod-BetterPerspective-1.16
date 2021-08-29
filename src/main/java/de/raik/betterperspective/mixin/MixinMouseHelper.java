package de.raik.betterperspective.mixin;

import de.raik.betterperspective.BetterPerspectiveAddon;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin of the mouse helper to cancel and
 * redirect player movement to only adjust camera later
 *
 * @author Raik
 * @version 1.0
 */
@Mixin(MouseHelper.class)
public class MixinMouseHelper {

    /**
     * Turning the behavior of the rotate towards to get mouse movement and set
     * the values to use for the camera
     */
    @Redirect(method = "updatePlayerLook()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/entity/player/ClientPlayerEntity.rotateTowards(DD)V"
    ))
    private void redirectPlayerRotation(ClientPlayerEntity player, double x, double y) {
        BetterPerspectiveAddon addon = BetterPerspectiveAddon.getAddonInstance();
        //Do normal action in case it's disabled
        if (!addon.isPerspectiveEnabled()) {
            player.rotateTowards(x, y);
            return;
        }

        //Adjusting the camera values to use with changed camera
        //Adjusting the same way as in Entity.class
        addon.setRotationYaw(addon.getRotationYaw() + (float) x / 8.0F);
        addon.setRotationPitch(MathHelper.clamp(addon.getRotationPitch() + (float) y / 8.0F, -90F, 90F));
    }

}
