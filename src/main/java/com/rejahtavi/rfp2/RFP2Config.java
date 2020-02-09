package com.rejahtavi.rfp2;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/*
 * Config Annotation system documentation can be found here:
 * https://mcforge.readthedocs.io/en/latest/config/annotations/
 */

// Identify this as the config class to forge
@Config(
    modid = RFP2.MODID,
    type = Type.INSTANCE,
    name = RFP2.MODID,
    category = "")
@Mod.EventBusSubscriber(Side.CLIENT)
public class RFP2Config
{
    // Constructor
    public RFP2Config()
    {
        // Register the config handler to the bus so that it shows up in the forge config gui
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    // Create preferences section
    @Comment({ "Personal preferences for " + RFP2.MODNAME })
    @Name("Preferences")
    public static final Preferences preferences = new Preferences();
    
    // Create compatibility section
    @Comment("Item and Mount compatability lists for " + RFP2.MODNAME)
    @Name("Compatability")
    public static final Compatability compatability = new Compatability();
    
    // Define structure and defaults of Preferences section
    public static class Preferences
    {
        @Comment({ "Enables/disables mod at startup.", "Default: true" })
        @Name("Enable Mod")
        public boolean enableMod = true;
        
        @Comment({ "Enables/disables real arms at startup", "Default: true" })
        @Name("Enable Real Arm Rendering")
        public boolean enableRealArms = true;
        
        @Comment({ "Enables/disables head turning at startup", "Default: false" })
        @Name("Enable Head Turning")
        public boolean enableHeadTurning = false;
        
        @Comment({ "Enables/disables status messages when a keybind is pressed.", "Default: false" })
        @Name("Enable Status Messages")
        public boolean enableStatusMessages = true;
        
        @Comment({ "How far behind the camera to put the first person player model", "Default: 0.35" })
        @Name("Player Model Offset")
        @RangeDouble(
            min = 0.0f,
            max = 2.0f)
        public double playerModelOffset = 0.35f;
    }
    
    // Define structure and defaults of Compatibility section
    public static class Compatability
    {
        @Comment({ "Vanilla arms are used when holding one of these items.",
                   "Needed for compasses and maps, stops big items blocking the view.",
                   "Note: Not case sensitive, accepts simple item names and regex patterns:",
                   ".* = wildcard, ^ = match beginning of name, $ = match end of name." })
        @Name("Held Item Conflicts")
        public String[] heldItemConflictList = { "minecraft:filled_map",
                                                 "minecraft:clock",
                                                 "minecraft:shield",
                                                 "minecraft:bow",
                                                 ".*compass$",
                                                 "tconstruct:.*bow",
                                                 "tconstruct:battlesign",
                                                 "thermalfoundation:shield_.*" };
        
        @Comment({ "Mod temporarily disables when riding one of these mounts.",
                   "Stops legs clipping through minecarts.",
                   "Note: Not case sensitive, accepts simple item names and regex patterns.",
                   ".* = wildcard, ^ = match beginning of name, $ = match end of name." })
        @Name("Mount Conflicts")
        public String[] mountConflictList = { ".*minecart.*" };
        
        @Comment("Disables the mod when swimming.")
        @Name("Disable when swimming")
        public boolean disableWhenSwimming = false;
        
        @Comment("Enforces a more aggressive version of the swimming checks.")
        @Name("Use aggressive swimming checks")
        public boolean useAggressiveSwimmingCheck = false;
        
        @Comment("Disables the mod when sneaking.")
        @Name("Disable when sneaking")
        public boolean disableWhenSneaking = false;
        
        @Comment("Switches to vanilla arms when *any* item is held, not just conflict items.")
        @Name("Use vanilla arms when holding any item")
        public boolean disableArmsWhenAnyItemHeld = false;
        
        @Comment("Disables rendering safety checks. May enable compatibility with mods that cause rendering exceptions, but cannot guarantee that the game will be stable.")
        @Name("Ignore rendering errors (not recommended).")
        public boolean disableRenderErrorCatching = false;
        
        @Comment("Disables mod compatibility checks. Allows RFP2 to run even when mods that are known to block the RFP2 first person view are loaded.")
        @Name("Ignore mod compatibility checks (not recommended).")
        public boolean disableModCompatibilityChecks = false;
    }
    
    // Subscribe to configuration change event
    // This fires whenever the user saves changes in the config gui.
    @SubscribeEvent
    public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event)
    {
        // Only respond to events meant for us
        if (event.getModID().contentEquals(RFP2.MODID))
        {
            // Inject the new values and save to the config file when the config has been changed from the GUI.
            RFP2.logger.log(RFP2.LOGGING_LEVEL_LOW, "synchronizing config file.");
            
            // Make sure all referenced items are lower case (makes matching later computationally cheaper)
            RFP2Config.compatability.heldItemConflictList = lowerCaseArray(RFP2Config.compatability.heldItemConflictList);
            RFP2Config.compatability.mountConflictList    = lowerCaseArray(RFP2Config.compatability.mountConflictList);
            
            // Save the config
            ConfigManager.sync(RFP2.MODID, Config.Type.INSTANCE);
            
            // Update current state to match preferences that were just selected in the GUI
            RFP2.state.enableMod            = preferences.enableMod;
            RFP2.state.enableRealArms       = preferences.enableRealArms;
            RFP2.state.enableHeadTurning    = preferences.enableHeadTurning;
            RFP2.state.enableStatusMessages = preferences.enableStatusMessages;
        }
    }
    
    // Takes in an array of strings and converts all members of it to lower case
    private static String[] lowerCaseArray(String[] array)
    {
        // Iterate over all elements in the array
        for (int i = 0; i < array.length; i++)
        {
            // Rewrite each element in the array into lower case
            array[i] = array[i].toLowerCase();
        }
        return array;
    }
}
