package mods.belgabor.chiselsbytes.client;

import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.ItemType;
import mods.belgabor.chiselsbytes.ChiselsBytes;
import net.minecraft.block.Block;
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
public class KeybindHandler {
    public static KeyBinding keybind;

    public static void init() {
        keybind = new KeyBinding("chiselsbytes.keybind", Keyboard.KEY_PRIOR, "key.categories.misc");
        ClientRegistry.registerKeyBinding(keybind);
    }

    private static void findRegions(String[][][] textures, String[][][] tints, List<String> regions, int xdir, int ydir, int zdir, boolean state) {
        boolean[][][] handled = new boolean[16][16][16];
        int xmin = (xdir > 0) ? 0 : 15;
        int ymin = (ydir > 0) ? 0 : 15;
        int zmin = (zdir > 0) ? 0 : 15;

        for(int x=0; x<16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    if (textures[x][y][z] == null)
                        handled[x][y][z] = true;
                }
            }
        }


        for (int z = zmin; (z >= 0) && (z < 16); z += zdir) {
            for (int y = ymin; (y >= 0) && (y < 16); y += ydir) {
                for(int x = xmin; (x >= 0) && (x < 16); x += xdir) {
                    if (handled[x][y][z])
                        continue;
                    String current_texture = textures[x][y][z];
                    String current_tint = tints[x][y][z];
                    int xend = x;
                    int yend = y;
                    int zend = z;
                    for(int xr = x+xdir; (xr >= 0) && (xr < 16); xr += xdir) {
                        if (handled[xr][y][z])
                            break;
                        if (textures[xr][y][z].equals(current_texture) && tints[xr][y][z].equals(current_tint)) {
                            xend += xdir;
                        } else {
                            break;
                        }
                    }
                    int xfrom = Math.min(x, xend);
                    int xto = Math.max(xend,x);
                    for(int yr = y+ydir; (yr >= 0) && (yr < 16); yr += ydir) {
                        boolean ok = true;
                        for(int xr = xfrom; xr<=xto; xr++) {
                            if  (handled[xr][yr][z] || (!(textures[xr][yr][z].equals(current_texture) && tints[xr][yr][z].equals(current_tint)))) {
                                ok = false;
                                break;
                            }
                        }
                        if (ok)
                            yend += ydir;
                        else
                            break;
                    }
                    int yfrom = Math.min(y, yend);
                    int yto = Math.max(yend,y);
                    for(int zr = z+zdir; (zr >= 0) && (zr < 16); zr += zdir) {
                        boolean ok = true;
                        for(int yr = yfrom; yr<=yto; yr++) {
                            for(int xr = xfrom; xr<=xto; xr++) {
                                if  (handled[xr][yr][zr] || (!(textures[xr][yr][zr].equals(current_texture) && tints[xr][yr][zr].equals(current_tint)))) {
                                    ok = false;
                                    break;
                                }
                            }
                            if (!ok)
                                break;
                        }
                        if (ok)
                            zend += zdir;
                        else
                            break;
                    }
                    int zfrom = Math.min(z,zend);
                    int zto = Math.max(zend,z);
                    String res = String.format("    {%02d, %02d, %02d, %02d, %02d, %02d, texture = \"%s\"", xfrom, yfrom, 16-(zto+1), xto+1, yto+1, 16-zfrom, current_texture);
                    if (!current_tint.equals("FFFFFF"))
                        res += String.format(", tint = 0x%s", current_tint);
                    if (state)
                        res += ", state = true";
                    res += "}";
                    regions.add(res);
                    //System.out.println(res);
                    for(int zr = zfrom; zr<=zto; zr++) {
                        for (int yr = yfrom; yr <= yto; yr++) {
                            for (int xr = xfrom; xr <= xto; xr++) {
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
                    player.addChatComponentMessage(new TextComponentString(String.format("Testing variant %d (%d/%d/%d)...", i, x, y, z)));
                    ArrayList<String> tresults = new ArrayList<String>();
                    findRegions(textures, tints, tresults, x, y, z, state);
                    player.addChatComponentMessage(new TextComponentString(String.format("Number of shapes: %d", tresults.size())));
                    if ((results.size() == 0) || (results.size() > tresults.size()))
                        results = tresults;
                    i++;
                }
            }
        }

        return results;
    }

    private static void examineBlock(IBitAccess bits, Minecraft mc, String[][][] textures, String[][][] tints) {
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

                        Block tblock = state.getBlock();

                        if (tblock != null) {
                            if (tblock.getClass().getCanonicalName().startsWith("mod.flatcoloredblocks.block.BlockFlatColored")) {
                                // Ugh...
                                Class c = tblock.getClass();
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
                                        int col = (int) (m.invoke(tblock, params));
                                        tint = String.format("%06X", col & 0xFFFFFF);
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

    public static void keyDown() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        ItemStack stack = player.getHeldItemMainhand();
        if ((stack != null) && (ChiselsBytes.cnb_api != null)) {
            if (ChiselsBytes.cnb_api.getItemType(stack) == ItemType.CHISLED_BLOCK) {
                IBitAccess bits = ChiselsBytes.cnb_api.createBitItem(stack);
                if (bits != null) {
                    player.addChatComponentMessage(new TextComponentString("Assembling data..."));
                    String result = "{\n  label=\"Converted Chisels & Bits block\",\n";
                    result += "  shapes = {\n";
                    String[][][] textures = new String[16][16][16];
                    String[][][] tints = new String[16][16][16];

                    examineBlock(bits, mc, textures, tints);

                    List<String> results = findBest(player, textures, tints, false);
                    int shape_count = results.size();
                    result += String.join(",\n", results);

                    int shape_count_act = 0;
                    int slot = player.inventory.currentItem;
                    if (slot != 9) {
                        ItemStack stack_next = player.inventory.getStackInSlot(slot + 1);
                        if ((stack_next != null) && (ChiselsBytes.cnb_api.getItemType(stack_next) == ItemType.CHISLED_BLOCK)) {
                            bits = ChiselsBytes.cnb_api.createBitItem(stack_next);
                            if (bits != null) {
                                player.addChatComponentMessage(new TextComponentString("Assembling data for active state..."));
                                textures = new String[16][16][16];
                                tints = new String[16][16][16];

                                examineBlock(bits, mc, textures, tints);
                                results = findBest(player, textures, tints, true);
                                shape_count_act = results.size();
                                result += ",\n";
                                result += String.join(",\n", results);
                            }
                        }
                    }
                    player.addChatComponentMessage(new TextComponentString(String.format("Optimal number of shapes: %d", shape_count)));
                    if (shape_count_act > 0)
                        player.addChatComponentMessage(new TextComponentString(String.format("Optimal number of shapes (active state): %d", shape_count_act)));

                    result += "\n  }\n}\n";
                    if (Desktop.isDesktopSupported()) {

                        StringSelection sel = new StringSelection(result);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(sel, null);

                        player.addChatComponentMessage(new TextComponentString("Data copied to clipboard"));
                    } else {
                        player.addChatComponentMessage(new TextComponentString("Error: Failed to copy to clipboard"));
                    }
                }
            } else {
                player.addChatComponentMessage(new TextComponentString("Error: You need to hold a Chisel & Bits block in your hand"));
            }
        } else {
            player.addChatComponentMessage(new TextComponentString("Error: You need to hold a Chisel & Bits block in your hand"));
        }
    }

}
