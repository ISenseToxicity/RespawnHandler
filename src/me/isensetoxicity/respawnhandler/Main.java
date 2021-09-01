package me.isensetoxicity.respawnhandler;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    private int randX;
    private int randZ;
    public void onEnable(){
        this.getServer().getPluginManager().registerEvents(this,this);
    }

    @Override
    public void onDisable(){
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        if(!event.isBedSpawn() || !event.isAnchorSpawn()){
            World world = Bukkit.getServer().getWorld("World");
            boolean foundLocation = false;
            int max = 1000;
            int min = -1000;
            int range = max - min + 1;

            while (!foundLocation){
                randX = (int)(Math.random() * range) + min;
                randZ = (int)(Math.random() * range) + min;


                Block highestYSolidBlock = world.getHighestBlockAt(randX,randZ);

                int highestYSolidCoordinates = highestYSolidBlock.getY();

                if(CoordinatesAboveValidation(world,highestYSolidCoordinates + 1) && CoordinatesAboveValidation(world,highestYSolidCoordinates + 2) && !(highestYSolidBlock.getBlockData() instanceof Leaves) && !highestYSolidBlock.isLiquid()){
                    event.setRespawnLocation(highestYSolidBlock.getLocation());
                    foundLocation = true;
                }
            }

        }
    }

    private boolean CoordinatesAboveValidation(World world, int y){

        Block oneAbove = world.getBlockAt(randX,y,randZ);
        return oneAbove.isPassable() && !oneAbove.isLiquid();
    }

}
