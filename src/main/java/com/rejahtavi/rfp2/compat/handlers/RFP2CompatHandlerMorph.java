package com.rejahtavi.rfp2.compat.handlers;

import me.ichun.mods.morph.api.IApi;
import me.ichun.mods.morph.api.MorphApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;

//compatibility module for Morph mod
public class RFP2CompatHandlerMorph extends RFP2CompatHandler
{
    // Mod Info
    public static final String modId = "morph";
    
    // Local Variables
    IApi morphApiHandle = null;
    
    // Constructor
    public RFP2CompatHandlerMorph()
    {
        super();
        
        // Check to see if Morph is loaded before attempting to access its API
        if (Loader.isModLoaded(modId))
        {
            // Obtain handle to Morph API
            morphApiHandle = MorphApi.getApiImpl();
        }
    }
    
    @Override
    public boolean getDisableRFP2(EntityPlayer player)
    {
        // Check if our API handle is valid before calling on it to check for a player morph
        if (morphApiHandle != null && morphApiHandle.hasMorph(player.getName(), Side.CLIENT))
        {
            // player is morphed
            return true;
        }
        else
        {
            // player is not morphed, or could not access morph API
            return false;
        }
    }
}
