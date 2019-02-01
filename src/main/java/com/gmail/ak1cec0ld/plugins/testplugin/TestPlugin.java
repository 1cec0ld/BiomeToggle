package com.gmail.ak1cec0ld.plugins.testplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestPlugin extends JavaPlugin implements CommandExecutor {
    private HashMap<String, Integer> activeToggles = new HashMap<>();
    private Set<Biome> categorizedBiomes = new HashSet<>();
    private HashMap<Biome,BlockData> biomeAssociations = new HashMap<>();
    private final List<Material> availableMaterials = new ArrayList<>(Arrays.asList(Material.BLACK_STAINED_GLASS,Material.BLUE_STAINED_GLASS,
                                                                                    Material.BROWN_STAINED_GLASS,Material.CYAN_STAINED_GLASS,
                                                                                    Material.GREEN_STAINED_GLASS,Material.GRAY_STAINED_GLASS,
                                                                                    Material.LIGHT_BLUE_STAINED_GLASS,Material.LIGHT_GRAY_STAINED_GLASS,
                                                                                    Material.LIME_STAINED_GLASS,Material.MAGENTA_STAINED_GLASS,
                                                                                    Material.ORANGE_STAINED_GLASS,Material.PINK_STAINED_GLASS,
                                                                                    Material.PURPLE_STAINED_GLASS,Material.RED_STAINED_GLASS,
                                                                                    Material.WHITE_STAINED_GLASS,Material.YELLOW_STAINED_GLASS));

    public void onEnable(){
        Bukkit.getLogger().info("Enabling TestPlugin");
        getServer().getPluginCommand("btoggle").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if((sender instanceof Player)){
            if(args.length == 0){
                Player player = (Player)sender;
                if(activeToggles.containsKey(player.getName())){
                    Bukkit.getScheduler().cancelTask(activeToggles.get(player.getName()));
                    activeToggles.remove(player.getName());
                    player.sendMessage("Stopping biome outlining task!");
                    return true;
                }
                int output = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                    doOutlines(player);
                }, 10, 40);
                activeToggles.put(player.getName(),output);
                player.sendMessage("Starting biome outlining task!");
                return true;
            }
        }
        return false;
    }
    public void doOutlines(Player player){
        if(player==null){
            for(int eachActive : activeToggles.values()){
                Bukkit.getScheduler().cancelTask(eachActive);
            }
            activeToggles.clear();
        }
        int currentX = player.getLocation().getBlockX();
        int currentZ = player.getLocation().getBlockZ();
        int radius = 50;
        for(int row = currentX-radius; row < currentX+radius; row++){
            for(int col = currentZ-radius; col < currentZ+radius; col++){
                Block checking = player.getWorld().getHighestBlockAt(row,col).getRelative(BlockFace.UP);
                Block checkNorth = checking.getRelative(BlockFace.NORTH);
                Block checkEast = checking.getRelative(BlockFace.EAST);
                
                Biome b = checking.getBiome();
                Biome bn = checkNorth.getBiome();
                Biome be = checkEast.getBiome();
                categorize(b);
                if(!b.equals(bn)){
                    categorize(bn);
                    player.sendBlockChange(checking.getLocation(), biomeAssociations.get(b));
                    player.sendBlockChange(checkNorth.getLocation(), biomeAssociations.get(bn));
                } 
                if(!b.equals(be)){
                    categorize(be);
                    player.sendBlockChange(checking.getLocation(), biomeAssociations.get(b));
                    player.sendBlockChange(checkEast.getLocation(), biomeAssociations.get(be));
                }
            }
        }
    }
    private void categorize(Biome b){
        if(this.categorizedBiomes.contains(b))return;
        categorizedBiomes.add(b);
        biomeAssociations.put(b, availableMaterials.get((categorizedBiomes.size()-1) % availableMaterials.size()).createBlockData());
    }
}
