package com.rejahtavi.rfp2.compat.handlers;

import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.inventory.InventoryCosArmor;
import net.minecraft.entity.player.EntityPlayer;

// compatibility module for Cosmetic Armor Reworked
public class RFP2CompatHandlerCosarmor extends RFP2CompatHandler
{
    // Mod Info
    public static final String modId   = "cosmeticarmorreworked";
    public static final String modName = "Cosmetic Armor Reworked";
    
    // Constructor
    public RFP2CompatHandlerCosarmor()
    {
        super(modId, modName);
    }
    
    // Constants
    private static final int COSARMOR_HELMET_INDEX = 3;
    
    // Local objects
    private boolean prevHeadHiddenState = false;
    

    // Hide the player's cosarmor head items when called, making sure to cache whether they were hidden before we got here or not
    @Override
    public void hideHead(EntityPlayer player, boolean hideHelmet)
    {
        prevHeadHiddenState = isCosArmorHeadHidden(player);
        setCosArmorHeadHidden(player, true);
    }
    
    // Restore player's cosarmor head items to previous state
    @Override
    public void restoreHead(EntityPlayer player, boolean hideHelmet)
    {
        // By default, do nothing unless overridden.
        // @Overrides should read prevHeadHiddenState restore all head object visibility to this previous state.
        if (!prevHeadHiddenState)
        {
            setCosArmorHeadHidden(player, false);
        }
    }
    
    // Get current head hiding state from Cosmetic Armor Reworked
    private boolean isCosArmorHeadHidden(EntityPlayer player)
    {
        if (player != null)
        {
            InventoryCosArmor cosArmorInv = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(player.getUniqueID());
            return cosArmorInv.isSkinArmor(COSARMOR_HELMET_INDEX);
        }
        else
        {
            return true;
        }
    }
    
    // Set current head hiding state in Cosmetic Armor Reworked
    private void setCosArmorHeadHidden(EntityPlayer player, boolean hideHelmet)
    {
        // Remotely set the "hidden" state of the helmet slot in CosmeticArmorReworked
        if (player != null)
        {
            InventoryCosArmor cosArmorInv = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(player.getUniqueID());
            cosArmorInv.setSkinArmor(COSARMOR_HELMET_INDEX, hideHelmet);
        }
    }
}
