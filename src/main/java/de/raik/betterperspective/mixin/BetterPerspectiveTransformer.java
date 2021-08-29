package de.raik.betterperspective.mixin;

import net.labymod.addon.AddonTransformer;
import net.labymod.api.TransformerType;

/**
 * Addon transformer to register mixins
 *
 * @author Raik
 * @version 1.0
 */
public class BetterPerspectiveTransformer extends AddonTransformer {

    /**
     * Method which registers
     * the mixins
     */
    @Override
    public void registerTransformers() {
        this.registerTransformer(TransformerType.VANILLA, "better-perspective.mixin.json");
    }
}
