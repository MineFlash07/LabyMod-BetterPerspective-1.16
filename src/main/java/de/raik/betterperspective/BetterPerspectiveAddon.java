package de.raik.betterperspective;

import com.google.gson.JsonObject;
import net.labymod.api.LabyModAddon;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.events.client.TickEvent;
import net.labymod.core.LabyModCore;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.KeyElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Keyboard;
import net.labymod.utils.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;

import java.util.List;

/**
 * Addon instance to control and setup the
 * addons and its listeners
 *
 * @author Raik
 * @version 1.0
 */
public class BetterPerspectiveAddon extends LabyModAddon {

    /**
     * The addon instance of the addon to access in the byte code
     * manipulation
     *
     * Using singleton to prevent a bunch of boilerplate code with custom events e.g.
     * even though it's a critical anti pattern
     */
    private static BetterPerspectiveAddon addonInstance;

    /**
     * The state whether the addon is enabled or not
     */
    private boolean enabled;

    /**
     * The key needed to press to use the better perspective
     */
    private int key;

    /**
     * Whether you need to hold the key down or the key
     * toggles the view
     */
    private boolean toggleMode;

    /**
     * Method indicating whether the better perspective mode
     * is enabled or not
     */
    private boolean perspectiveEnabled = false;

    /**
     * Indicator whether a key was pressed or not to check for toggle mode
     */
    private boolean wasPressed = false;

    /**
     * The rotation yaw of the player before the perspective got enabled
     * to switch it back later
     */
    private float rotationYaw;

    /**
     * The rotation pitch of the player before it got enabled
     * to switch back later
     */
    private float rotationPitch;

    /**
     * The players point of view to turn back later after it got disabled
     */
    private PointOfView previousPointOfView;

    /**
     * Method called to enable the plugin
     * it will setup all basic stuff
     */
    @Override
    public void onEnable() {
        addonInstance = this;
        this.api.getEventService().registerListener(this);
    }

    /**
     * On tick listener to listen for the key button
     * to toggle it
     *
     * @param event The event
     */
    @Subscribe
    public void onTick(TickEvent event) {
        if (event.getPhase() != TickEvent.Phase.POST) {
            return;
        }

        //Check key, enabled and screen
        if (this.key == -1 || Minecraft.getInstance().currentScreen != null || !this.enabled) {
            this.perspectiveEnabled = false;
            this.wasPressed = false;
            return;
        }

        boolean keyDown = Keyboard.isKeyDown(this.key);

        //Mode specific switch
        if (this.toggleMode) {
            //Check pressed
            if (this.wasPressed) {
                this.wasPressed = keyDown;
                return;
            }
            this.wasPressed = keyDown;
            //Check pressed
            if (keyDown) {
                this.switchPerspectiveEnabled();
            }
            return;
        }

        //Hold mode
        if ((keyDown && !this.wasPressed) || (!keyDown && this.wasPressed)) {
            this.switchPerspectiveEnabled();
        }
        this.wasPressed = keyDown;
    }

    /**
     * Method to switch the perspective
     * enabled gety called in onTick key listener
     */
    private void switchPerspectiveEnabled() {
        this.perspectiveEnabled = !this.perspectiveEnabled;

        //Set old point of view
        if (!this.perspectiveEnabled) {
            Minecraft.getInstance().gameSettings.setPointOfView(this.previousPointOfView);
            return;
        }

        //Set previous stuff
        this.rotationYaw = LabyModCore.getMinecraft().getPlayer().rotationYaw;
        this.rotationPitch = LabyModCore.getMinecraft().getPlayer().rotationPitch;
        this.previousPointOfView = Minecraft.getInstance().gameSettings.getPointOfView();
        //Set forced point of view
        Minecraft.getInstance().gameSettings.setPointOfView(PointOfView.THIRD_PERSON_BACK);
    }

    /**
     * Method to load the config
     * of the addon
     */
    @Override
    public void loadConfig() {
        JsonObject config = this.getConfig();
        this.enabled = !config.has("enabled") || config.get("enabled").getAsBoolean();
        this.key = config.has("key") ? config.get("key").getAsInt() : -1;
        this.toggleMode = config.has("toggleMode") && config.get("toggleMode").getAsBoolean();
    }

    /**
     * Method to add settings to the addon
     * to configure everything
     *
     * @param settings The setting list
     */
    @Override
    protected void fillSettings(List<SettingsElement> settings) {
        settings.add(new BooleanElement("Enabled", this, new ControlElement.IconData(Material.EMERALD), "enabled", this.enabled));
        settings.add(new KeyElement("Key", this, new ControlElement.IconData(Material.LEVER), "key", this.key)
                .bindDescription("The key you need to press to use the addon."));
        settings.add(new BooleanElement("Togglemode", this, new ControlElement.IconData(Material.STONE_PRESSURE_PLATE), "toggleMode", this.toggleMode)
                .bindDescription("The key will toggle the view and not wont need to be hold if enabled."));
    }

    public static BetterPerspectiveAddon getAddonInstance() {
        return addonInstance;
    }

    public float getRotationYaw() {
        return this.rotationYaw;
    }

    public void setRotationYaw(float rotationYaw) {
        this.rotationYaw = rotationYaw;
    }

    public float getRotationPitch() {
        return this.rotationPitch;
    }

    public void setRotationPitch(float rotationPitch) {
        this.rotationPitch = rotationPitch;
    }

    public boolean isPerspectiveEnabled() {
        return this.perspectiveEnabled;
    }
}
