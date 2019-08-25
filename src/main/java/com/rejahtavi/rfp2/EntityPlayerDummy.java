package com.rejahtavi.rfp2;

import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/* 
 * This class implements the functionality of the PlayerDummy entity.
 * 
 * The PlayerDummy's presence is used as a trigger to draw the fake player body every frame,
 * as well as a way to manipulate the precise positioning of the fake player body.
 */
public class EntityPlayerDummy extends Entity
{
    // Stores last time the entity state changed
    public long lastTickUpdated;
    
    // Stores the last known swimming state
    private boolean swimming = false;
    
    // Constructor
    public EntityPlayerDummy(World world)
    {
        // Call parent Entity() constructor with appropriate world reference
        super(world);
        
        // Set up new dummy object
        this.ignoreFrustumCheck = true;
        this.setSize(0, 2);
        this.lastTickUpdated = world.getTotalWorldTime();
    }
    
    // Called when entity should update itself
    public void onUpdate()
    {
        // Get reference to current local player and null check it
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null)
        {
            // Can't find our player, so remove ourself from the world
            this.setDead();
        }
        else
        {
            // Record the current tick number to prove we did an update
            this.lastTickUpdated = world.getTotalWorldTime();
            
            // Match our position and rotation to player, then record the current tick
            this.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
            
            // Update swimming status; some conditions are necessary to start or stop swimming
            // this provides hysteresis and avoids any "flickering" of the swimming state
            
            // Get references to the block at the player's feet, plus one above and one below
            BlockPos atHead    = new BlockPos(player.posX, player.posY + player.eyeHeight, player.posZ);
            BlockPos atFeet    = new BlockPos(player.posX, player.posY, player.posZ);
            BlockPos belowFeet = atFeet.down();
            
            // Check each block location for liquid
            boolean liquidAtHead    = player.world.getBlockState(atHead).getBlock() instanceof BlockLiquid;
            boolean liquidAtFeet    = player.world.getBlockState(atFeet).getBlock() instanceof BlockLiquid;
            boolean liquidBelowFeet = player.world.getBlockState(belowFeet).getBlock() instanceof BlockLiquid;
            
            // Are we currently swimming?
            if (swimming)
            {
                // Currently swimming. Figure out if we should stop.
                if (RFP2Config.compatability.useAggressiveSwimmingCheck)
                {
                    // use aggressive version of swimming checks
                    // requires player to FULLY clear water to stop swimming
                    if (!liquidAtHead && !liquidAtFeet && !liquidBelowFeet) swimming = false;
                }
                else
                {
                    // use normal version of swimming checks
                    // considers player no longer swimming when standing in 1 block deep water
                    if (!liquidAtHead && !liquidBelowFeet) swimming = false;
                }
            }
            else
            {
                // Currently NOT swimming. Figure out if we should start.
                if (RFP2Config.compatability.useAggressiveSwimmingCheck)
                {
                    // use aggressive version of swimming checks
                    // only requires player to touch water to start swimming
                    // (below feet not checked, because you can "lean over" the edge of water and this is definitely not swimming)
                    if (liquidAtHead || liquidAtFeet) swimming = true;
                }
                else
                {
                    // use normal version of swimming checks
                    // only considers player swimming once their head goes under
                    if (liquidAtHead && liquidAtFeet) swimming = true;
                }
            }
        }
    }
    
    // returns whether player is swimming or not
    public boolean isSwimming()
    {
        return swimming;
    }
    
    // Remaining methods are required by the <Entity> interface but we don't have anything special to do in them.
    public void entityInit()
    {
    }
    
    public void readEntityFromNBT(NBTTagCompound x)
    {
    }
    
    public void writeEntityToNBT(NBTTagCompound x)
    {
    }
}
