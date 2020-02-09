package com.rejahtavi.rfp2.compat.handlers;

import net.minecraft.entity.player.EntityPlayer;

//compatibility module for Morph mod
public class RFP2CompatHandlerIdo extends RFP2CompatHandler
{
    public static final String modId   = "ido";
    public static final String modName = "Ido";
    private static final float IDO_EYEHEIGHT_THRESHOLD = 0.5f;
    
    public RFP2CompatHandlerIdo()
    {
        super(modId, modName);
    }
    
    @Override
    public boolean getDisableRFP2(EntityPlayer player)
    {
        if (player.eyeHeight < IDO_EYEHEIGHT_THRESHOLD)
        {
            // Player is currently very short -- IDO crouching or swimming is active.
            return true;
        }
        else
        {
            // Player is currently crouching or standing -- IDO crouching or swimming is NOT active.
            return false;
        }
    }
}
