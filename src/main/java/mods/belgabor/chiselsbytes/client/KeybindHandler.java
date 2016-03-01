package mods.belgabor.chiselsbytes.client;

import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.ItemType;
import mods.belgabor.chiselsbytes.ChiselsBytes;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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

    private static void findRegions(String[][][] textures, String[][][] tints, List<String> regions) {
        boolean[][][] handled = new boolean[16][16][16];

        for(int x=0; x<16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    if (textures[x][y][z] == null)
                        handled[x][y][z] = true;
                }
            }
        }


        for(int x=0; x<16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    if (handled[x][y][z])
                        continue;
                    String current_texture = textures[x][y][z];
                    String current_tint = tints[x][y][z];
                    int xend = x;
                    int yend = y;
                    int zend = z;
                    for(int xr = x+1; xr<16; xr++) {
                        if (handled[xr][y][z])
                            break;
                        if (textures[xr][y][z].equals(current_texture) && tints[xr][y][z].equals(current_tint)) {
                            xend += 1;
                        } else {
                            break;
                        }
                    }
                    for(int yr = y+1; yr<16; yr++) {
                        boolean ok = true;
                        for(int xr = x; xr<=xend; xr++) {
                            if  (handled[xr][yr][z] || (!(textures[xr][yr][z].equals(current_texture) && tints[xr][yr][z].equals(current_tint)))) {
                                ok = false;
                                break;
                            }
                        }
                        if (ok)
                            yend += 1;
                        else
                            break;
                    }
                    for(int zr = z+1; zr<16; zr++) {
                        boolean ok = true;
                        for(int yr = y; yr<=yend; yr++) {
                            for(int xr = x; xr<=xend; xr++) {
                                if  (handled[xr][yr][zr] || (!(textures[xr][yr][zr].equals(current_texture) && tints[xr][yr][zr].equals(current_tint)))) {
                                    ok = false;
                                    break;
                                }
                            }
                            if (!ok)
                                break;
                        }
                        if (ok)
                            zend += 1;
                        else
                            break;
                    }
                    //String res = String.format("{%02d, %02d, %02d, %02d, %02d, %02d, texture = \"%s\"", x, y, z, xend+1, yend+1, zend+1, current_texture);
                    String res = String.format("    {%02d, %02d, %02d, %02d, %02d, %02d, texture = \"%s\"", x, y, 16-(zend+1), xend+1, yend+1, 16-z, current_texture);
                    if (!current_tint.equals("FFFFFF"))
                        res += String.format(", tint = 0x%s", current_tint);
                    res += "}";
                    regions.add(res);
                    for(int zr = z; zr<=zend; zr++) {
                        for (int yr = y; yr <= yend; yr++) {
                            for (int xr = x; xr <= xend; xr++) {
                                handled[xr][yr][zr] = true;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void keyDown() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        ItemStack stack = player.getCurrentEquippedItem();
        if ((stack != null) && (ChiselsBytes.cnb_api != null)) {
            if (ChiselsBytes.cnb_api.getItemType(stack) == ItemType.CHISLED_BLOCK) {
                IBitAccess bits = ChiselsBytes.cnb_api.createBitItem(stack);
                if (bits != null) {
                    player.addChatComponentMessage(new ChatComponentText("Assembling data..."));
                    String result = "{\n  label=\"Converted Chisels & Bits block\",\n";
                    result += "  shapes = {\n";
                    String[][][] textures = new String[16][16][16];
                    String[][][] tints = new String[16][16][16];

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
                                        tint = String.format("%06X", tblock.getRenderColor(state) & 0xFFFFFF);
                                    }
                                    textures[x][y][z] = texture;
                                    tints[x][y][z] = tint;
                                    /*
                                    result += String.format("    {%02d, %02d, %02d, %02d, %02d, %02d, texture = \"%s\"", x, y, z, x+1, y+1, z+1, texture);
                                    if (!tint.equals("FFFFFF"))
                                        result += String.format(", tint = 0x%s", tint);
                                    result += "},\n";
                                    */
                                }
                            }
                        }
                    }

                    ArrayList<String> results = new ArrayList<String>();
                    findRegions(textures, tints, results);
                    player.addChatComponentMessage(new ChatComponentText(String.format("Number of shapes: %d", results.size())));
                    result += String.join(",\n", results);
                    result += "\n  }\n}\n";
                    if (Desktop.isDesktopSupported()) {

                        StringSelection sel = new StringSelection(result);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(sel, null);

                        player.addChatComponentMessage(new ChatComponentText("Data copied to clipboard"));
                    } else {
                        player.addChatComponentMessage(new ChatComponentText("Error: Failed to copy to clipboard"));
                    }
                }
            } else {
                player.addChatComponentMessage(new ChatComponentText("Error: You need to hold a Chisel & Bits block in your hand"));
            }
        } else {
            player.addChatComponentMessage(new ChatComponentText("Error: You need to hold a Chisel & Bits block in your hand"));
        }
    }

}
