package mods.belgabor.chiselsbytes.client;

import com.typesafe.config.ConfigException;
/*
import li.cil.oc.api.API;
*/
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.ItemType;
import mods.belgabor.chiselsbytes.ChiselsBytes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Belgabor on 28.02.2016.
 */
@SideOnly(Side.CLIENT)
public class KeybindHandler {
    public static KeyBinding keybind;

    public static void init() {
        keybind = new KeyBinding("chiselsbytes.keybind", Keyboard.KEY_PRIOR, "key.categories.misc");
        ClientRegistry.registerKeyBinding(keybind);
    }

    private static void findRegions(String[][][] textures, String[][][] tints, List<String> regions, int xDir, int yDir, int zDir, boolean state) {
        boolean[][][] handled = new boolean[16][16][16];
        int xMin = (xDir > 0) ? 0 : 15;
        int yMin = (yDir > 0) ? 0 : 15;
        int zMin = (zDir > 0) ? 0 : 15;

        for(int x=0; x<16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    if (textures[x][y][z] == null)
                        handled[x][y][z] = true;
                }
            }
        }


        for (int z = zMin; (z >= 0) && (z < 16); z += zDir) {
            for (int y = yMin; (y >= 0) && (y < 16); y += yDir) {
                for(int x = xMin; (x >= 0) && (x < 16); x += xDir) {
                    if (handled[x][y][z])
                        continue;
                    String current_texture = textures[x][y][z];
                    String current_tint = tints[x][y][z];
                    int xEnd = x;
                    int yEnd = y;
                    int zEnd = z;
                    for(int xr = x+xDir; (xr >= 0) && (xr < 16); xr += xDir) {
                        if (handled[xr][y][z])
                            break;
                        if (textures[xr][y][z].equals(current_texture) && tints[xr][y][z].equals(current_tint)) {
                            xEnd += xDir;
                        } else {
                            break;
                        }
                    }
                    int xFrom = Math.min(x, xEnd);
                    int xTo = Math.max(xEnd,x);
                    for(int yr = y+yDir; (yr >= 0) && (yr < 16); yr += yDir) {
                        boolean ok = true;
                        for(int xr = xFrom; xr<=xTo; xr++) {
                            if  (handled[xr][yr][z] || (!(textures[xr][yr][z].equals(current_texture) && tints[xr][yr][z].equals(current_tint)))) {
                                ok = false;
                                break;
                            }
                        }
                        if (ok)
                            yEnd += yDir;
                        else
                            break;
                    }
                    int yFrom = Math.min(y, yEnd);
                    int yTo = Math.max(yEnd,y);
                    for(int zr = z+zDir; (zr >= 0) && (zr < 16); zr += zDir) {
                        boolean ok = true;
                        for(int yr = yFrom; yr<=yTo; yr++) {
                            for(int xr = xFrom; xr<=xTo; xr++) {
                                if  (handled[xr][yr][zr] || (!(textures[xr][yr][zr].equals(current_texture) && tints[xr][yr][zr].equals(current_tint)))) {
                                    ok = false;
                                    break;
                                }
                            }
                            if (!ok)
                                break;
                        }
                        if (ok)
                            zEnd += zDir;
                        else
                            break;
                    }
                    int zFrom = Math.min(z,zEnd);
                    int zTo = Math.max(zEnd,z);
                    String res = String.format("    {%02d, %02d, %02d, %02d, %02d, %02d, texture = \"%s\"", xFrom, yFrom, 16-(zTo+1), xTo+1, yTo+1, 16-zFrom, current_texture);
                    if (!current_tint.equals("FFFFFF"))
                        res += String.format(", tint = 0x%s", current_tint);
                    if (state)
                        res += ", state = true";
                    res += "}";
                    regions.add(res);
                    //System.out.println(res);
                    for(int zr = zFrom; zr<=zTo; zr++) {
                        for (int yr = yFrom; yr <= yTo; yr++) {
                            for (int xr = xFrom; xr <= xTo; xr++) {
                                handled[xr][yr][zr] = true;
                            }
                        }
                    }
                }
            }
        }
    }

    private static List<String> findBest(EntityPlayer player, String[][][] textures, String[][][] tints, boolean state) {
        ArrayList<String> results = new ArrayList<String>();

        int i = 1;
        for(int x = -1; x<=1; x+=2) {
            for(int y = -1; y<=1; y+=2) {
                for (int z = -1; z <= 1; z += 2) {
                    player.sendMessage(new TextComponentTranslation("chiselsbytes.message.info.variant", i, x, y, z));
                    ArrayList<String> tResults = new ArrayList<String>();
                    findRegions(textures, tints, tResults, x, y, z, state);
                    player.sendMessage(new TextComponentTranslation("chiselsbytes.message.info.shapes", tResults.size()));
                    if ((results.size() == 0) || (results.size() > tResults.size()))
                        results = tResults;
                    i++;
                }
            }
        }

        return results;
    }
    
    private static String colorToString(int color) {
        return String.format("%06X", color & 0xFFFFFF);
    }

    @SuppressWarnings("unchecked")
    private static void examineBlock(IBitAccess bits, Minecraft mc, String[][][] textures, String[][][] tints, Features features, boolean sneaking) {
        for(int x=0; x<16; x++) {
            for(int y=0; y<16; y++) {
                for(int z=0; z<16; z++) {
                    IBlockState state = bits.getBitAt(x,y,z).getState();
                    if (state != null) {
                        String texture = "error";
                        String tint = "FFFFFF";
                        IBakedModel model = mc.getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);
                        if (model != null && model.getParticleTexture() != null && model.getParticleTexture().getIconName() != null) {
                            texture = model.getParticleTexture().getIconName();
                        }

                        Block tBlock = state.getBlock();

                        if (tBlock != null) {
                            Fluid fluid = null;
                            
                            if (state.getLightValue() > features.lightLevel)
                                features.lightLevel = state.getLightValue();
                            
                            if (tBlock instanceof IFluidBlock) {
                                fluid = ((IFluidBlock) tBlock).getFluid();
                            } else if (tBlock instanceof BlockStaticLiquid) {
                                if (tBlock.getUnlocalizedName().equals("tile.water"))
                                    fluid = FluidRegistry.getFluid("water");
                                else if (tBlock.getUnlocalizedName().equals("tile.lava"))
                                    fluid = FluidRegistry.getFluid("lava");
                            }
                            
                            if (fluid != null) {
                                features.hasFluids = true;
                                texture = sneaking?fluid.getFlowing().toString():fluid.getStill().toString();
                                tint = colorToString(fluid.getColor());
                            }
                            
                            if (tBlock.getClass().getCanonicalName().startsWith("mod.flatcoloredblocks.block.BlockFlatColored")) {
                                // Ugh...
                                Class c = tBlock.getClass();
                                Method m = null;
                                Class pTypes[] = new Class[1];
                                pTypes[0] = IBlockState.class;
                                try {
                                    m = c.getMethod("colorFromState", pTypes);
                                } catch (NoSuchMethodException e) {}
                                if (m != null) {
                                    Object params[] = new Object[1];
                                    params[0] = state;
                                    try {
                                        //String col = (String)(m.invoke(tblock, params));
                                        int col = (int) (m.invoke(tBlock, params));
                                        tint = colorToString(col);
                                    }
                                    catch (InvocationTargetException ite) {}
                                    catch (IllegalAccessException iae) {}
                                    catch (ClassCastException ce) {}
                                }
                            }
                        }

                        textures[x][y][z] = texture;
                        tints[x][y][z] = tint;
                    }
                }
            }
        }
    }
    
    private static boolean isCompatibleItem(ItemStack stack) {
        if (ChiselsBytes.cnb_api == null || stack == null)
            return false;
        ItemType type = ChiselsBytes.cnb_api.getItemType(stack);
        return type != null && type != ItemType.NEGATIVE_DESIGN && type.isBitAccess;
    }

    public static void keyDown() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        ItemStack stack = player.getHeldItemMainhand();
        Features features = new Features();
        if (isCompatibleItem(stack)) {
            IBitAccess bits = ChiselsBytes.cnb_api.createBitItem(stack);
            if (bits != null) {
                String[][][] textures = new String[16][16][16];
                String[][][] tints = new String[16][16][16];
                
                player.sendMessage(new TextComponentTranslation("chiselsbytes.message.info.start"));
                String result = "{\n  label=\"Converted Chisels & Bits block\",\n";
                
                examineBlock(bits, mc, textures, tints, features, player.isSneaking());

                List<String> results = findBest(player, textures, tints, false);
                List<String> results_active = null;
                
                int shape_count = results.size();
                
                int shape_count_act = 0;
                ItemStack stack_next = player.getHeldItemOffhand();
                if (!isCompatibleItem(stack_next)) {
                    int slot = player.inventory.currentItem;
                    if (slot != 9)
                        stack_next = player.inventory.getStackInSlot(slot + 1);
                }
                
                if (isCompatibleItem(stack_next)) {
                    bits = ChiselsBytes.cnb_api.createBitItem(stack_next);
                    if (bits != null) {
                        player.sendMessage(new TextComponentTranslation("chiselsbytes.message.info.startact"));
                        textures = new String[16][16][16];
                        tints = new String[16][16][16];

                        examineBlock(bits, mc, textures, tints, features, player.isSneaking());
                        results_active = findBest(player, textures, tints, true);
                        shape_count_act = results.size();
                    }
                }
                
                if (features.lightLevel > 0) {
                    result += String.format("  lightLevel = %d,\n", Math.min(8, features.lightLevel));
                    if (features.lightLevel > 8)
                        player.sendMessage(new TextComponentTranslation("chiselsbytes.message.warning.light", features.lightLevel));
                }

                result += "  shapes = {\n";
                result += String.join(",\n", results);
                if (shape_count_act > 0) {
                    result += ",\n";
                    result += String.join(",\n", results_active);
                }
                result += "\n  }\n}\n";
                
                player.sendMessage(new TextComponentTranslation("chiselsbytes.message.info.optimal", shape_count));
                if (shape_count_act > 0)
                    player.sendMessage(new TextComponentTranslation("chiselsbytes.message.info.optimalact", shape_count_act));

                
                if (features.hasFluids) {
                    player.sendMessage(new TextComponentTranslation("chiselsbytes.message.info.fluid" + (player.isSneaking()?"sneak":"nosneak")));
                }
                
                /* TODO: Reenable when OC becomes available
                if (Loader.isModLoaded("OpenComputers")) {
                    int maxShapes = -1;
                    try {
                        maxShapes = API.config.getInt("printer.maxShapes");
                    } catch (ConfigException e) {}
                    if (maxShapes >= 0) {
                        if (shape_count > maxShapes || shape_count_act > maxShapes)
                            player.sendMessage(new TextComponentTranslation("chiselsbytes.message.warning.shapes", maxShapes));
                    }
                }
                */
                
                if (Desktop.isDesktopSupported()) {

                    StringSelection sel = new StringSelection(result);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(sel, null);

                    player.sendMessage(new TextComponentTranslation("chiselsbytes.message.info.clipsuccess"));
                } else {
                    player.sendMessage(new TextComponentTranslation("chiselsbytes.message.error.clipfail"));
                }
            }
        } else {
            player.sendMessage(new TextComponentTranslation("chiselsbytes.message.error.nocnb"));
        }
    }

    
    private static class Features {
        protected boolean hasFluids = false;
        protected int lightLevel = 0;
    }
}
