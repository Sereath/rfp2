package com.rejahtavi.rfp2;

import java.util.regex.PatternSyntaxException;
import org.apache.logging.log4j.Level;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

/* 
 * This class receives and processes all events related to the player dummy.
 * It is also responsible for processing events related to keybinds and configuration.
 */

// register this class as an event handler with forge
@Mod.EventBusSubscriber(Side.CLIENT)
public class RFP2State
{
    // Local objects to track mod internal state
    
    // handle to the player dummy entity
    EntityPlayerDummy dummy;
    
    // timers for performance waits
    int  spawnDelay;
    long checkEnableModDelay;
    long checkEnableRealArmsDelay;
    int  suspendApiDelay;
    
    // state flags
    boolean lastActivateCheckResult;
    boolean lastRealArmsCheckResult;
    boolean enableMod;
    boolean enableRealArms;
    boolean enableHeadTurning;
    boolean enableStatusMessages;
    boolean disabledForConflict = false;
    boolean conflictCheckDone   = false;
    
    // Constructor
    public RFP2State()
    {
        // No dummy exists at startup
        dummy = null;
        
        // Start a timer so that we wait a bit for things to load before first trying to spawn the dummy
        spawnDelay = RFP2.DUMMY_MIN_RESPAWN_INTERVAL;
        
        // Initialize local variables
        checkEnableModDelay      = 0;
        checkEnableRealArmsDelay = 0;
        suspendApiDelay          = 0;
        lastActivateCheckResult  = true;
        lastRealArmsCheckResult  = true;
        
        // Import initial state from config file
        enableMod            = RFP2Config.preferences.enableMod;
        enableRealArms       = RFP2Config.preferences.enableRealArms;
        enableHeadTurning    = RFP2Config.preferences.enableHeadTurning;
        enableStatusMessages = RFP2Config.preferences.enableStatusMessages;
        
        // Register ourselves on the bus so we can receive and process events
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    // Receive key press events for key binding handling
    @SubscribeEvent(
        priority = EventPriority.NORMAL,
        receiveCanceled = true)
    public void onEvent(KeyInputEvent event)
    {
        // kill mod completely when a conflict is detected.
        if (this.disabledForConflict) return;
        
        // Check key binding in turn for new presses
        if (RFP2.keybindArmsToggle.checkForNewPress())
        {
            enableRealArms = !enableRealArms;
            if (enableStatusMessages)
            {
                // log keybind-triggered state changes to chat if configured to do so
                RFP2.logToChat(RFP2.MODNAME + " arms " + (enableRealArms ? TextFormatting.GREEN + "enabled" : TextFormatting.RED + "disabled"));
            }
        }
        // Check key binding in turn for new presses
        if (RFP2.keybindModToggle.checkForNewPress())
        {
            enableMod = !enableMod;
            if (enableStatusMessages)
            {
                // log keybind-triggered state changes to chat if configured to do so
                RFP2.logToChat(RFP2.MODNAME + " mod " + (enableMod ? TextFormatting.GREEN + "enabled" : TextFormatting.RED + "disabled"));
            }
        }
        // Check key binding in turn for new presses
        if (RFP2.keybindHeadRotationToggle.checkForNewPress())
        {
            enableHeadTurning = !enableHeadTurning;
            if (enableStatusMessages)
            {
                // log keybind-triggered state changes to chat if configured to do so
                RFP2.logToChat(RFP2.MODNAME + " head rotation " + (enableHeadTurning ? TextFormatting.GREEN + "enabled" : TextFormatting.RED + "disabled"));
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (Loader.isModLoaded(RFP2.OBFUSCATE_MODID))
        {
            RFP2.logToChatByPlayer(TextFormatting.RED + "ERROR: RFP2 is not compatible with "
                                   + TextFormatting.GOLD
                                   + "Obfuscate Mod"
                                   + TextFormatting.RED
                                   + ".",
                                   event.player);
            RFP2.logToChatByPlayer(TextFormatting.RED + "Both mods modify the first person view, causing a known conflict.", event.player);
            RFP2.logToChatByPlayer(TextFormatting.RED + "RFP2 has been disabled.", event.player);
            RFP2.logger.log(Level.FATAL, ": first person rendering deactivated.");
            RFP2.logger.log(Level.FATAL, ": RFP2 is not compatible with Obfuscate Mod.");
            RFP2.rfp2State.enableMod           = false;
            RFP2.rfp2State.disabledForConflict = true;
        }
    }
    
    // Receive event when player hands are about to be drawn
    @SubscribeEvent(
        priority = EventPriority.HIGHEST)
    public void onEvent(RenderHandEvent event)
    {
        // kill mod completely when a conflict is detected.
        if (this.disabledForConflict) return;
        
        // Get local player reference
        EntityPlayer player = Minecraft.getMinecraft().player;
        // if: 1) player exists AND 2) mod is active AND 3) rendering real arms is active
        if (player != null && RFP2.rfp2State.isModEnabled(player) && RFP2.rfp2State.isRealArmsEnabled(player))
        {
            // then skip drawing the vanilla 2D HUD arms by canceling the event
            event.setCanceled(true);
        }
    }
    
    // Receive the main game tick event
    @SubscribeEvent
    public void onEvent(TickEvent.ClientTickEvent event)
    {
        // kill mod completely when a conflict is detected.
        if (this.disabledForConflict) return;
        
        // Make this block as fail-safe as possible, since it runs every tick
        try
        {
            // Decrement timers
            if (checkEnableModDelay > 0) --checkEnableModDelay;
            if (checkEnableRealArmsDelay > 0) --checkEnableRealArmsDelay;
            if (suspendApiDelay > 0) --suspendApiDelay;
            
            // Get player reference and null check it
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (player != null)
            {
                // Check if dummy needs to be spawned
                if (dummy == null)
                {
                    // It does, are we in a respawn waiting interval?
                    if (spawnDelay > 0)
                    {
                        // Yes, we are still waiting; is the mod enabled?
                        if (enableMod)
                        {
                            // Yes, the mod is enabled and we are waiting: decrement the counter
                            --spawnDelay;
                        }
                        else
                        {
                            // No, the mod is not enabled, and we are waiting:
                            // Hold the timer at full so that the delay works when the mod is turned back on
                            spawnDelay = RFP2.DUMMY_MIN_RESPAWN_INTERVAL;
                        }
                    }
                    else
                    {
                        // No, the spawn timer has expired: Go ahead and try to spawn the dummy.
                        attemptDummySpawn(player);
                    }
                }
                // The dummy already exists, let's check up on it
                else
                {
                    // Track whether we need to reset the existing dummy.
                    // We should only reset it ONCE, even if multiple reasons are true.
                    // (otherwise we will not be able to log the remaining reasons after it is reset)
                    // This is done this way to ease future troubleshooting.
                    boolean needsReset = false;
                    
                    // Did the player change dimensions on us? If so, reset the dummy.
                    if (dummy.world.provider.getDimension() != player.world.provider.getDimension())
                    {
                        needsReset = true;
                        RFP2.logger.log(RFP2.DEFAULT_LOGGING_LEVEL,
                                        this.getClass().getName() + ": Respawning dummy because player changed dimension.");
                    }
                    
                    // Did the player teleport, move too fast, or somehow else get separated? If so, reset the dummy.
                    if (dummy.getDistanceSq(player) > RFP2.DUMMY_MAX_SEPARATION)
                    {
                        needsReset = true;
                        RFP2.logger.log(RFP2.DEFAULT_LOGGING_LEVEL,
                                        this.getClass().getName() + ": Respawning dummy because player and dummy became separated.");
                    }
                    
                    // Has it been excessively long since we last updated the dummy's state? (perhaps due to lag?)
                    if (dummy.lastTickUpdated < player.world.getTotalWorldTime() - RFP2.DUMMY_UPDATE_TIMEOUT)
                    {
                        needsReset = true;
                        RFP2.logger.log(RFP2.DEFAULT_LOGGING_LEVEL,
                                        this.getClass().getName() + ": Respawning dummy because state became stale. (Is the server lagging?)");
                    }
                    
                    // Did one of the above checks necessitate a reset?
                    if (needsReset)
                    {
                        // Yes, proceed with the reset.
                        resetDummy();
                    }
                }
            }
        }
        catch (Exception e)
        {
            // If anything goes wrong, shut the mod off and write an error to the logs.
            RFP2.errorDisableMod(this.getClass().getName() + ".onEvent(TickEvent.ClientTickEvent)", e);
        }
    }
    
    // Handles dummy spawning
    void attemptDummySpawn(EntityPlayer player)
    {
        // kill mod completely when a conflict is detected.
        if (this.disabledForConflict) return;
        
        try
        {
            // Make sure any existing dummy is dead
            if (dummy != null) dummy.setDead();
            
            // Attempt to spawn a new one at the player's current position
            dummy = new EntityPlayerDummy(player.world);
            dummy.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
            player.world.spawnEntity(dummy);
        }
        catch (Exception e)
        {
            /*
             * Something went wrong trying to spawn the dummy!
             * We need to write a log entry and reschedule to try again later.
             * 
             * Note that because this code is protected against running too much by a respawn timer,
             * we do not call errorDisableMod() when encountering this error.
             * 
             * Should anything unexpected occur in the spawning, there is a good chance that it will
             * work itself out within a respawn delay or two.
             */
            RFP2.logger.log(Level.ERROR, this.getClass().getName() + ": failed to spawn PlayerDummy! Will retry. Exception:", e.toString());
            e.printStackTrace();
            resetDummy();
        }
    }
    
    // Handles killing off defunct dummies and scheduling respawns
    void resetDummy()
    {
        // If the existing dummy isn't dead, kill it before freeing the reference
        if (dummy != null) dummy.setDead();
        dummy = null;
        
        // kill mod completely when a conflict is detected.
        if (this.disabledForConflict) return;
        
        // Set timer to spawn a new one
        spawnDelay = RFP2.DUMMY_MIN_RESPAWN_INTERVAL;
    }
    
    public void setSuspendTimer(int ticks)
    {
        // kill mod completely when a conflict is detected.
        if (this.disabledForConflict) return;
        
        // check if tick value is valid; invalid values will be ignored
        if (ticks > 0 && ticks <= RFP2.MAX_SUSPEND_TIMER)
        {
            // Only allow increasing the timer externally
            //  * This is so multiple mods can use the API concurrently, and RFP2 being suspended is the preferred state.
            //  * Once all mods stop requesting suspension times, the timer will expire at the longest, last value requested.
            if (ticks > suspendApiDelay) suspendApiDelay = ticks;
        }
    }
    
    // Check if mod should be disabled for any reason
    public boolean isModEnabled(EntityPlayer player)
    {
        // kill mod completely when a conflict is detected.
        if (this.disabledForConflict) return false;
        
        // No need to check anything if we are configured to be disabled
        if (!enableMod) return false;
        
        // Don't do anything if we've been suspended by another mod
        if (suspendApiDelay > 0) return false;
        
        // No need to check anything else if player is dead or otherwise cannot be found
        if (player == null) return false;
        
        // No need to check anything else if dummy is dead or otherwise cannot be found
        if (dummy == null) return false;
        
        /* 
         * Only check the player's riding status if we haven't recently.
         * This saves on performance -- it is not necessary to check this list on every single frame!
         * Once every few ticks is more than enough to remain invisible to the player.
         * Keep in mind that "every few ticks", or several 20ths of a second,
         * could be tens of frames where we skip the check with a good GPU!
         */
        if (checkEnableModDelay == 0)
        {
            // The timer has expired, we need to run the checks
            
            // reset timer
            checkEnableModDelay = RFP2.MIN_ACTIVATION_CHECK_INTERVAL;
            
            // Implement swimming check functionality
            if (RFP2Config.compatability.disableWhenSwimming && dummy.isSwimming())
            {
                // we are swimming and are configured to disable when this is true, so we are disabled
                lastActivateCheckResult = false;
            }
            else
            {
                // we are not swimming, or that check is disabled. proceed to the mount check
                
                // get a reference to the player's mount, if it exists
                Entity playerMountEntity = player.getRidingEntity();
                if (playerMountEntity == null)
                {
                    // Player isn't riding, so we are enabled.
                    lastActivateCheckResult = true;
                }
                else
                {
                    // Player is riding something, find out what it is and if it's on our conflict list
                    if (stringMatchesRegexList(playerMountEntity.getName().toLowerCase(), RFP2Config.compatability.mountConflictList))
                    {
                        // player is riding a conflicting entity, so we are disabled.
                        lastActivateCheckResult = false;
                    }
                    else
                    {
                        // No conflicts found, so we are enabled.
                        lastActivateCheckResult = true;
                    }
                }
            }
            
        }
        return lastActivateCheckResult;
    }
    
    // Check if we should render real arms or not
    public boolean isRealArmsEnabled(EntityPlayer player)
    {
        // kill mod completely when a conflict is detected.
        if (this.disabledForConflict) return false;
        
        // No need to check anything if we don't want this enabled
        if (!enableRealArms) return false;
        
        // No need to check anything if player is dead
        if (player == null) return false;
        
        // only run the inventory check if we haven't done it recently
        // once per tick is enough -- isRealArmsEnabled might be called many times per tick!
        if (checkEnableRealArmsDelay == 0)
        {
            // need to check the player's inventory after all
            // reset the check timer
            checkEnableRealArmsDelay = RFP2.MIN_REAL_ARMS_CHECK_INTERVAL;
            
            // get the names of the player's currently held items
            String itemMainHand = player.inventory.getCurrentItem().getItem().getRegistryName().toString().toLowerCase();
            String itemOffHand  = player.inventory.offHandInventory.get(0).getItem().getRegistryName().toString().toLowerCase();
            
            // Modify the check logic based on whether the "any item" flag is set or not
            if (RFP2Config.compatability.disableArmsWhenAnyItemHeld)
            {
                // "any item held" behavior is enabled; check if player's hands are empty
                if (itemMainHand.equals("minecraft:air") && itemOffHand.equals("minecraft:air"))
                {
                    // player is not holding anything; enable arm rendering
                    lastRealArmsCheckResult = true;
                }
                else
                {
                    // player is holding something; disable arm rendering
                    lastRealArmsCheckResult = false;
                }
            }
            else
            {
                // The "any item" option is not in use, so we need to check the registry names of any
                // held items against the conflict list
                if (stringMatchesRegexList(itemMainHand, RFP2Config.compatability.heldItemConflictList)
                    || (stringMatchesRegexList(itemOffHand, RFP2Config.compatability.heldItemConflictList)))
                {
                    // player is holding a conflicting item in main or off hand; disable arm rendering
                    lastRealArmsCheckResult = false;
                }
                else
                {
                    // no conflicts found; enable arm rendering
                    lastRealArmsCheckResult = true;
                }
            }
        }
        return lastRealArmsCheckResult;
    }
    
    // Check if head rotation is enabled
    public boolean isHeadRotationEnabled(EntityPlayer player)
    {
        // kill mod completely when a conflict is detected.
        if (this.disabledForConflict) return false;
        
        return enableHeadTurning;
    }
    
    // Check a string against a list of regexes and return true if any of them match
    boolean stringMatchesRegexList(String string, String[] regexes)
    {
        // Loop through regex array
        for (String i : regexes)
        {
            // Handle errors due to bad regex syntax entered by user
            try
            {
                // Check if the provided string matches the regex
                if (string.matches(i))
                {
                    // Found a hit, return true
                    return true;
                }
            }
            catch (PatternSyntaxException e)
            {
                // Something is wrong with the regex, switch off the mod and notify the user
                enableMod = false;
                RFP2.logToChat(RFP2.MODNAME + " " + TextFormatting.RED + "Error: [ " + i + " ] is not a valid regex, please edit your configuration.");
                RFP2.logToChat(RFP2.MODNAME + " mod " + TextFormatting.RED + " disabled");
                return false;
            }
        }
        // Got through the whole array without a hit; return false
        return false;
    }
}
