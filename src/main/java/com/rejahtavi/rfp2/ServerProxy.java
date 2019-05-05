package com.rejahtavi.rfp2;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

// RFP2.PROXY will be instantiated with this class if we are running on the server side.
// When this is the case, RFP2 should do nothing, so there is nothing here.
public class ServerProxy implements IProxy
{
    // Called at the start of mod loading
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        // unused in this mod
    }
    
    // Called after all other mod preInit()s have run
    @Override
    public void init(FMLInitializationEvent event)
    {
        // unused in this mod
    }
    
    // Called after all other mod init()s have run
    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        // unused in this mod
    }
    
    // Called when starting up a dedicated server
    @Override
    public void serverStarting(FMLServerStartingEvent event)
    {
        // unused in this mod
    }
}
