package com.rejahtavi.rfp2.compat;

import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.inventory.InventoryCosArmor;
import net.minecraft.entity.player.EntityPlayer;

// compatibility module for Cosmetic Armor Reworked
public class RFP2CosArmor
{   
    // Return the "hidden" state of the helmet slot in CosmeticArmorReworked
    public boolean getHelmetHidden(EntityPlayer player) {
        if (player != null) {
            InventoryCosArmor cosArmorInv = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(player.getUniqueID());
            return cosArmorInv.isSkinArmor(3);
        } else {
            return true;
        }
    }
    
    // Remotely set the "hidden" state of the helmet slot in CosmeticArmorReworked
    public void setHelmetHidden(EntityPlayer player, boolean hideHelmet) {
        if (player != null) {
            InventoryCosArmor cosArmorInv = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(player.getUniqueID());
            cosArmorInv.setSkinArmor(3, hideHelmet);
        }
    }
}
