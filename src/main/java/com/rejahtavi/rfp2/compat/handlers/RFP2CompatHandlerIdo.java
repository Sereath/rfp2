package com.rejahtavi.rfp2.compat.handlers;

import net.minecraft.entity.player.EntityPlayer;

//compatibility module for Morph mod
public class RFP2CompatHandlerIdo extends RFP2CompatHandler
{
    // Mod Info
    public static final String modId = "ido";
    
    // Constants
    private static final float IDO_EYEHEIGHT_THRESHOLD = 0.5f;
    
    // Constructor
    public RFP2CompatHandlerIdo()
    {
        super();
    }
    
    // Detect when Ido swimming or crawling is happening, and disable RFP2 accordingly.
    @Override
    public boolean getDisableRFP2(EntityPlayer player)
    {
        if (player.eyeHeight < IDO_EYEHEIGHT_THRESHOLD)
        {
            // Player is currently very short -- IDO crawling or swimming is active.
            return true;
        }
        else
        {
            // Player is currently crouching or standing -- IDO crawling or swimming is NOT active.
            return false;
        }
    }
}
