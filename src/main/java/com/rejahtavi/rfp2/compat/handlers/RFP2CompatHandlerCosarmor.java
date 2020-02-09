package com.rejahtavi.rfp2.compat.handlers;

import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.inventory.InventoryCosArmor;
import net.minecraft.entity.player.EntityPlayer;

// compatibility module for Cosmetic Armor Reworked
public class RFP2CompatHandlerCosarmor extends RFP2CompatHandler
{
    public static final String modId   = "cosmeticarmorreworked";
    public static final String modName = "Cosmetic Armor Reworked";
    
    private static final int COSARMOR_HELMET_INDEX = 3;
    
    // Constructor
    public RFP2CompatHandlerCosarmor()
    {
        super(modId, modName);
    }
    
    @Override
    protected boolean isHeadHidden(EntityPlayer player)
    {
        return isCosArmorHeadHidden(player);
    }
    
    @Override
    public void hideHead(EntityPlayer player, boolean hideHelmet)
    {
        // By default, do nothing unless overridden.
        // @Overrides should call isHeadHidden(), store the result into prevHeadHiddenState, then hide all head objects.
        prevHeadHiddenState = isHeadHidden(player);
        setCosArmorHeadHidden(player, true);
    }
    
    @Override
    public void restoreHead(EntityPlayer player, boolean hideHelmet)
    {
        // By default, do nothing unless overridden.
        // @Overrides should read prevHeadHiddenState restore all head object visibility to this previous state.
        if (!prevHeadHiddenState) {
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
