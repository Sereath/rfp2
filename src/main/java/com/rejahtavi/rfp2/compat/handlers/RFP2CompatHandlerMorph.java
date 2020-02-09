package com.rejahtavi.rfp2.compat.handlers;

import me.ichun.mods.morph.api.IApi;
import me.ichun.mods.morph.api.MorphApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

//compatibility module for Morph mod
public class RFP2CompatHandlerMorph extends RFP2CompatHandler
{
    public static final String modId = "morph";
    public static final String modName = "Morph";
        
    public RFP2CompatHandlerMorph() {
        super(modId, modName);
    }

    @Override
    public boolean getDisableRFP2(EntityPlayer player)
    {
        // get a handle to the morph API, null check, and see if the player currently has a morph
        IApi morphApiHandle = MorphApi.getApiImpl();
        if (morphApiHandle != null && morphApiHandle.hasMorph(player.getName(), Side.CLIENT))
        {
            // player is morphed
            return true;
        }
        // player is not morphed, or could not access morph API
        return false;
    }
}
