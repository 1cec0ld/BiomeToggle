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

import java.util.HashMap;

import static org.bukkit.Bukkit.createBlockData;

public class TestPlugin extends JavaPlugin implements CommandExecutor {
    private HashMap<String, Integer> map = new HashMap<>();

    public void onEnable(){
        Bukkit.getLogger().info("Enabling TestPlugin");
        getServer().getPluginCommand("btoggle").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if((sender instanceof Player)){
            if(args.length == 0){
                Player player = (Player)sender;
                if(map.containsKey(player.getName())){
                    Bukkit.getScheduler().cancelTask(map.get(player.getName()));
                    map.remove(player.getName());
                    player.sendMessage("Stopping biome outlining task!");
                    return true;
                }
                int output = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                    doOutlines(player);
                }, 10, 40);
                map.put(player.getName(),output);
                player.sendMessage("Starting biome outlining task!");
                return true;
            }
        }
        return false;
    }
    public void doOutlines(Player player){
        BlockData bd = createBlockData(Material.GLOWSTONE);
        int currentX = player.getLocation().getBlockX();
        int currentZ = player.getLocation().getBlockZ();
        int radius = 40;
        for(int row = currentX-radius; row < currentX+radius; row++){
            for(int col = currentZ-radius; col < currentZ+radius; col++){
                Block checking = player.getWorld().getHighestBlockAt(row,col).getRelative(BlockFace.UP);
                Biome b = checking.getBiome();
                //Bukkit.getLogger().info(row + ", " + col + ":  " + b.toString());
                if(!b.equals(checking.getRelative(BlockFace.NORTH).getBiome()) ||
                        !b.equals(checking.getRelative(BlockFace.EAST).getBiome())){
                    player.sendBlockChange(checking.getLocation(), bd);
                }
            }
        }
    }
}
