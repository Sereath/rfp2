package com.rejahtavi.rfp2.compat.handlers;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

// compatibility module for Cosmetic Armor Reworked
public class RFP2CompatHandlerBaubles extends RFP2CompatHandler
{
    // Mod Info
    public static final String modId = "baubles";
    
    // Constants
    private static final int BAUBLES_HEAD_INDEX = 4;
    
    // Local objects
    private ItemStack headBauble = ItemStack.EMPTY;
    
    // Hide the player's baubles head items when called, making sure to cache whether they were hidden before we got here or not
    @Override
    public void hideHead(EntityPlayer player, boolean hideHelmet)
    {
        // Get handle to the player's bauble inventory
        IBaublesItemHandler baublesInventory = BaublesApi.getBaublesHandler(player);
        
        // Grab a copy of the item in the player's bauble head slot
        headBauble = baublesInventory.getStackInSlot(BAUBLES_HEAD_INDEX);
        
        // Remove any item from the player's head slot
        baublesInventory.setStackInSlot(BAUBLES_HEAD_INDEX, ItemStack.EMPTY);
    }
    
    // Restore player's baubles head items to previous state
    @Override
    public void restoreHead(EntityPlayer player, boolean hideHelmet)
    {
        // Get handle to the player's bauble inventory
        IBaublesItemHandler baublesInventory = BaublesApi.getBaublesHandler(player);
        
        // Restore items to player's head slot
        baublesInventory.setStackInSlot(BAUBLES_HEAD_INDEX, headBauble);
    }
}
