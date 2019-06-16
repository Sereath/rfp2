This mod started as an excercise to learn java and get in to minecraft modding;
I wanted to add a couple hotkeys to RFPR to switch modes more easily, and stop
it from interfering with F1's hud toggle.

In order to do that, I needed to take apart and understand the existing code,
and figure out where I could insert the required changes for hotkeys.

As you can see, things sort of spiraled out of control. In the end, it ended up
as a near complete rewrite of RFPR from the ground up. The core functionality
is the same, but the rewrite integrates it much better with forge, and provides
some improved functionality:

-- Ability to toggle head rotation in first person (locked by default). This
   avoids clipping with held items, though turning may look slightly less natural.
   Head rotation is toggleable via a configurable hotkey.

-- Ability to toggle 3D arm visibility directly with a hotkey.

-- Ability to toggle the entire mod with a hotkey.

-- Mod no longer interferes with default F1 key behavior.

-- Forge Config GUI integration.
    * Bindable hotkeys for mod, arms, and head rotation toggles
    * Configurable default state of main options on login
    * Option to show chat log messages when hotkeys are pressed
    * List of items that disable arm rendering (compass, map, etc) is now editable in-game.
    * New list of ridden entities which can switch the mod off entirely.
    * Both lists support regex matching for easier configuration of item lists

-- Performance Improvements
    * Rendering of first person body now starts much faster
    * Rendering of first person body resumes quicker after deaths, teleports, and dimension shifts.
    * Conflict lists are now only checked once per few ticks to minimize performance impact.

-- Stability Improvements
    * Failsafe mechanism for any code that runs every frame or every tick.
        - should any of this code fail, the mod switches itself off to avoid lagging or crashing the game.

-- Code Improvements
    * Refactored mod to span several files and objects.
    * Everything is much more organized and clean.
    * Commented *everything*
    * More descriptive variable names


BUILD INSTRUCTIONS (linux, windows will use gradlew.bat instead of ./gradlew)

1. Clone into a local directory & cd into it
    git clone https://github.com/rejahtavi/rfp2
    cd rfp2

2. Clean & set up the workspace
    ./gradlew clean
    ./gradlew setupDecompWorkspace

3. create a 'libs' folder in that directory.

4. populate the 'libs' folder by downloading the latest 12.2 versions of these:
        CosmeticArmorReworked-1.12.2-(version).jar
        Morph-1.12.2-(version).jar

5. Build
    ./gradlew build
