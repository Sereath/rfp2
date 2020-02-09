package com.rejahtavi.rfp2.compat.handlers;

import net.minecraft.entity.player.EntityPlayer;

//compatibility module base class / template
public class RFP2CompatHandler
{
    // Mod Info Template
    // public static final String modId = "";
    
    // Constructor
    public RFP2CompatHandler()
    {
        return;
    }
    
    // Behavior Getter Templates
    public boolean getDisableRFP2(EntityPlayer player)
    {
        // By default, do nothing unless overridden.
        // @Overrides should return TRUE if they want RFP2 to completely skip on the current frame, letting vanilla rendering take over.
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
