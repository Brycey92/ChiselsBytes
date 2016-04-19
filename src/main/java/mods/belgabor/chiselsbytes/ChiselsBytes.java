package mods.belgabor.chiselsbytes;

import mod.chiselsandbits.api.ChiselsAndBitsAddon;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.IChiselsAndBitsAddon;
import mods.belgabor.chiselsbytes.common.CommonProxy;
import mods.belgabor.chiselsbytes.common.Constants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by Belgabor on 28.02.2016.
 */

@Mod(modid = Constants.MOD_ID, version = Constants.VERSION, name = Constants.MOD_NAME, dependencies = "required-after:chiselsandbits", clientSideOnly = true)
@ChiselsAndBitsAddon
public class ChiselsBytes implements IChiselsAndBitsAddon
{
    @Mod.Instance(Constants.MOD_ID)
    public static ChiselsBytes instance;
    public static IChiselAndBitsAPI cnb_api;

    @SidedProxy(clientSide = Constants.PROXY_CLIENT)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);
    }

    @Override
    public void onReadyChiselsAndBits(IChiselAndBitsAPI api) {
        cnb_api = api;
    }
}
