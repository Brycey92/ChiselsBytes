package mods.belgabor.chiselsbytes.client;

import mods.belgabor.chiselsbytes.common.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by Belgabor on 28.02.2016.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        KeybindHandler.init();
        MinecraftForge.EVENT_BUS.register(new TickHandler());
    }
}
