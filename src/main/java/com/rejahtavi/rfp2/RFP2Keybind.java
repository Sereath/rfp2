package com.rejahtavi.rfp2;

import net.minecraft.client.settings.KeyBinding;

/*
 * This helper class implements basic key bindings and state tracking of each key
 * Notes:
 *      Each key binding will spawn its own instance of this class
 *      Each instance will track the last known state of the binding, and watch for "rising edges" to trigger on
 *      This prevents annoying "multi-triggers" if the button is held down for more than one frame
 */
public class RFP2Keybind
{
    
    // Handle to the current instance
    public KeyBinding keyBindingInstance;
    
    // Tracks state of the key on the previous tick
    private boolean wasPressed = false;
    
    // Constructor
    public RFP2Keybind(String description, int keyCode, String category)
    {
        keyBindingInstance = new KeyBinding(description, keyCode, category);
    }
    
    /*
     *  This function acts as a monostable filter to isolate "rising edges" of key press signals
     *  
     *  In other words, this returns true ONLY on the very first tick key is pressed, and false at all other times.
     *  The player must release and re-press the key to get a second event to fire.
     *  
     *  This stops the mod from spam-toggling options when a key is held.
     */
    public boolean checkForNewPress()
    {
        
        // Check current key state
        boolean currentlyPressed = this.keyBindingInstance.isKeyDown();
        if (!wasPressed && currentlyPressed)
        {
            // The key WASN'T pressed before, but now it is; Winner! Return True!
            wasPressed = true;
            return true;
        }
        else
        {
            // Something else happened and we don't particularly care what.
            // Save the current state of the button and return false.
            wasPressed = currentlyPressed;
            return false;
        }
    }
}
