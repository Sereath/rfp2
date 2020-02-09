package com.rejahtavi.rfp2.compat.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;

public class RFP2CompatHandler
{
    // Mod Info
    public static String modId;
    public static String modName;
    protected boolean isModLoaded = false;

    // State Cache
    protected boolean prevHeadHiddenState = false;
    protected boolean prevArmsHiddenState = false;
    
    // Constructor -- ensures these strings get overridden by the extending classes
    public RFP2CompatHandler(String newHandlerModId, String newHandlerModName)
    {
        modId   = newHandlerModId;
        modName = newHandlerModName;
    }
    
    // Initialization (call from PostInit event to check whether the associated mod is loaded)
    public void postInit() {
        // Check to see if our extension's modId is loaded. If so, store that flag so we don't have to ask again.
        if (Loader.isModLoaded(modId)) {
            isModLoaded = true;
        }
    }
    
    // Mod Info Getters
    public String getModId()
    {
        // return forge mod id
        return modId;
    }
    
    public String getModName()
    {
        // return friendly mod name
        return modName;
    }
    
    public boolean isLoaded()
    {
        // Will be false unless the mod id is loaded AND the handler has been initialized
        // Note -- there is no need to check this in extension functions because handlers that do not load during postInit()
        // will be removed from the compatHandlers list, and therefore never checked.
        return isModLoaded;
    }
    
    // Behavior Getter Templates
    public boolean getDisableRFP2(EntityPlayer player)
    {
        // By default, do nothing unless overridden.
        // @Overrides should return TRUE if they want RFP2 to completely skip on the current frame, letting vanilla rendering take over.
        return false;
    }
    
    protected boolean isHeadHidden(EntityPlayer player)
    {
        // By default, do nothing unless overridden.
        // @Overrides should return TRUE if their head items are currently hidden, and FALSE otherwise.
        return false;
    }
    
    protected boolean areArmsHidden(EntityPlayer player)
    {
        // By default, do nothing unless overridden.
        // @Overrides should return TRUE if their arm items are currently hidden, and FALSE otherwise.
        return false;
    }
    
    // Behavior Setter Templates
    public void hideHead(EntityPlayer player, boolean hideHelmet)
    {
        // By default, do nothing unless overridden.
        // @Overrides should call isHeadHidden(), store the result into prevHeadHiddenState, then hide all head objects.
        return;
    }
    
    public void hideArms(EntityPlayer player, boolean hideHelmet)
    {
        // By default, do nothing unless overridden.
        // @Overrides should call areArmsHidden(), store the result into prevArmsHiddenState, then hide all arm objects.
        return;
    }

    public void restoreHead(EntityPlayer player, boolean hideHelmet)
    {
        // By default, do nothing unless overridden.
        // @Overrides should read prevHeadHiddenState, and if it is FALSE, restore all head object visibility.
        return;
    }
    
    public void restoreArms(EntityPlayer player, boolean hideHelmet)
    {
        // By default, do nothing unless overridden.
        // @Overrides should read prevArmsHiddenState, and if it is FALSE, restore all arm object visibility.
        return;
    }

}
