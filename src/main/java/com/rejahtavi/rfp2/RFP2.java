package com.rejahtavi.rfp2;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

// Register mod with forge
@Mod(
    modid = RFP2.MODID,
    name = RFP2.MODNAME,
    version = RFP2.MODVER,
    clientSideOnly = true,
    acceptedMinecraftVersions = "1.12.2",
    acceptableRemoteVersions = "*")
public class RFP2
{
    // Mod info
    public static final String MODID   = "rfp2";
    public static final String MODNAME = "Real First Person 2";
    public static final String MODVER  = "@VERSION@";
    
    // Constants controlling dummy behavior
    public static final int DUMMY_MIN_RESPAWN_INTERVAL = 40;    // min ticks between spawn attempts
    public static final int DUMMY_UPDATE_TIMEOUT       = 20;    // max ticks between dummy entity updates
    public static final int DUMMY_MAX_SEPARATION       = 5;     // max blocks separation between dummy and player
    
    // Constants controlling optimization / load limiting
    
    // every 4 ticks is enough for global mod enable/disable checks
    public static final int MIN_ACTIVATION_CHECK_INTERVAL = 4;  // min ticks between mod enable checks
    
    // arm checks need to be faster to keep up with hotbar scrolling, but we still want to limit it to once per tick.
    public static final int MIN_REAL_ARMS_CHECK_INTERVAL = 1;   // min ticks between arms enable checks
    
    // Main class instance forge will use to reference the mod
    @Mod.Instance(MODID)
    public static RFP2 INSTANCE;
    
    // The proxy reference will be set to either ClientProxy or ServerProxy depending on execution context.
    @SidedProxy(
        clientSide = "com.rejahtavi." + MODID + ".ClientProxy",
        serverSide = "com.rejahtavi." + MODID + ".ServerProxy")
    public static IProxy PROXY;
    
    // Key bindings
    public static RFP2Keybind keybindArmsToggle         = new RFP2Keybind("key.arms.desc", Keyboard.KEY_SEMICOLON, "key.rfp2.category");
    public static RFP2Keybind keybindModToggle          = new RFP2Keybind("key.mod.desc", Keyboard.KEY_APOSTROPHE, "key.rfp2.category");
    public static RFP2Keybind keybindHeadRotationToggle = new RFP2Keybind("key.head.desc", Keyboard.KEY_H, "key.rfp2.category");
    
    // State objects
    public static RFP2Config rfp2Config;
    public static RFP2State  rfp2State;
    public static Logger     logger;
    
    // Sets the logging level for most messages written by the mod
    // Change to Level.WARN or Level.ERROR if higher visibility is desired in your launcher logs
    public static final Level DEFAULT_LOGGING_LEVEL = Level.DEBUG;
    
    // Mod Initialization - call correct proxy events based on the @SidedProxy picked above
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        PROXY.preInit(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        PROXY.init(event);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        PROXY.postInit(event);
    }
    
    // Provides facility to write a message to the local player's chat log
    public static void logToChat(String message)
    {
        // get a reference to the player
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null)
        {
            // compose text component from message string and send it to the player
            ITextComponent textToSend = new TextComponentString(message);
            player.sendMessage(textToSend);
        }
    }
    
    public static void errorDisableMod(String sourceMethod, Exception e)
    {
        // If anything goes wrong, this method will be called to shut off the mod and write an error to the logs.
        // The user can still try to re-enable it with a keybind or via the config gui.
        // This might just result in another error, but at least it will prevent us from
        // slowing down the game or flooding the logs if something is really broken.
        
        // Temporarily disable the mod
        RFP2.rfp2State.enableMod = false;
        
        // Write an error, including a stack trace, to the logs
        RFP2.logger.log(Level.FATAL, ": first person rendering deactivated.");
        RFP2.logger.log(Level.FATAL, ": " + sourceMethod + " encountered an exception:" + e.toString());
        e.printStackTrace();
        
        // Announce the issue to the player in-game
        RFP2.logToChat(RFP2.MODNAME + " mod " + TextFormatting.RED + " disabled");
        RFP2.logToChat(sourceMethod + " encountered an exception:");
        RFP2.logToChat(TextFormatting.RED + e.getMessage());
        RFP2.logToChat(TextFormatting.GOLD + "Please check your minecraft log file for more details.");
    }
}
