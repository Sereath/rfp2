package com.rejahtavi.rfp2.compat;

import me.ichun.mods.morph.api.IApi;
import me.ichun.mods.morph.api.MorphApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class RFP2MorphHandler
{
    public boolean isPlayerMorphed(EntityPlayer player)
    {
        // get a handle to the morph API, null check, and see if the player currently has a morph
        IApi mApi = MorphApi.getApiImpl();
        if (mApi != null && mApi.hasMorph(player.getName(), Side.CLIENT))
        {
            // player is morphed
            return true;
        }
        // player is not morphed, or could not access morph API
        return false;
    }
}
