package com.rejahtavi.rfp2.compat;

import com.rejahtavi.rfp2.RFP2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

/*
 * Compatibility API other mods can use to get the current state of RFP2.
 *
 * If you have a mod that modifies the player model or player rendering in any way,
 * you can call the functions below to determine whether you should hide certain things. 
 */
public class RFP2CompatApi
{
    
    // During frames that this returns TRUE:
    //  * RFP2 has hidden the player's head and helmet so that they don't block the first person view camera.
    //  * Make sure to adjust your mod's behavior to avoid rendering anything that could block the forward field of view.
    public boolean rfp2IsHeadHidden()
    {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return false;
        return RFP2.rfp2State.isModEnabled(player);
    }
    
    // During frames that this returns TRUE:
    //  * RFP2 has hidden the third person model's arms and is currently allowing "RenderHandEvent" to run normally.
    //  * This means that the player is in first person view, but is using the vanilla first person hands instead of the RFP2 third person hands.
    //  * Make sure you adjust your mod's rendering behavior accordingly as needed.
    // During frames that this returns FALSE:
    //  * RFP2 has NOT hidden the third person model's arms.
    //  * In most cases you should be able to render arms normally in this state.
    public boolean rfp2AreThirdPersonArmsHidden()
    {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return false;
        return !RFP2.rfp2State.isRealArmsEnabled(player);
    }
    
    // If you are making a mod that for some reason needs to temporarily suspend RFP2, you can use the following call to do so.
    // You cannot exceed RFP2.MAX_SUSPEND_TIMER ticks when calling this, if you need to keep RFP2 suspended for longer,
    // you will have to call this function at least once every RFP2.MAX_SUSPEND_TIMER ticks to keep it suspended.
    // You cannot decrease the timer through this method, so it is a good idea to set it as short as will work for your use case.
    // The idea here is that multiple mods can all be calling this function to suspend RFP2, and only once all mods have
    // stopped making requests will RFP2 allow itself to start back up again.
    public void rfp2AddSuspendTime(int ticks)
    {
        RFP2.rfp2State.setSuspendTimer(ticks);
    }
}
