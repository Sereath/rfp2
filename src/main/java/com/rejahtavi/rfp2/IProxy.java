package com.rejahtavi.rfp2;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public interface IProxy
{
    // FML life cycle events for mod loading
    void preInit(FMLPreInitializationEvent event);
    
    void init(FMLInitializationEvent event);
    
    void postInit(FMLPostInitializationEvent event);
    
    void serverStarting(FMLServerStartingEvent event);
}
