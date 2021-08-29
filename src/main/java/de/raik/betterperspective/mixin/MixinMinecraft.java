package de.raik.betterperspective.mixin;

import de.raik.betterperspective.BetterPerspectiveAddon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IWindowEventListener;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.util.concurrent.RecursiveEventLoop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Mixin of the Minecraft class to cancel the changing
 * of the perspective to prevent bugs
 *
 * @author Raik
 * @version 1.0
 */
@Mixin(Minecraft.class)
public abstract class MixinMinecraft extends RecursiveEventLoop<Runnable> implements ISnooperInfo, IWindowEventListener {

    /**
     * Super constructor because of extends stuff
     * it doesn't change any behavior
     */
    public MixinMinecraft(String name) {
        super(name);
    }

    /**
     * Modifying the arg when switch to prevent bugs and
     * stop switching with hotkey
     */
    @ModifyArg(method = "processKeyBinds()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/GameSettings.setPointOfView(Lnet/minecraft/client/settings/PointOfView;)V"
    ))
    public PointOfView preventViewPoint(PointOfView pointOfView) {
        return BetterPerspectiveAddon.getAddonInstance().isPerspectiveEnabled() ? PointOfView.THIRD_PERSON_BACK : pointOfView;
    }

}
