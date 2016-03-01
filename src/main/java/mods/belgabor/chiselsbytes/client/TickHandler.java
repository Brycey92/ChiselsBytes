package mods.belgabor.chiselsbytes.client;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by Belgabor on 28.02.2016.
 */
public class TickHandler {

    private Boolean pressed = false;

    @SubscribeEvent
    public void clientTickEnd(TickEvent.ClientTickEvent event) {
        if(KeybindHandler.keybind.isKeyDown()) {
            if (!pressed) {
                pressed = true;
                KeybindHandler.keyDown();
            }
        } else {
            if (pressed)
                    pressed = false;
        }
    }

}
