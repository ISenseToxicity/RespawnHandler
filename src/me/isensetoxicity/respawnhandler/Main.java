package me.isensetoxicity.respawnhandler;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin implements Listener {
    private int randX;
    private int randZ;
    private final FileConfiguration config = this.getConfig();
    private int max;
    private int min;
    private World world;

    public void onEnable(){
        this.getServer().getPluginManager().registerEvents(this,this);
         config.addDefault("min",-1000);
         config.addDefault("max",1000);
         config.options().copyDefaults(true);
         this.saveConfig();
         min = config.getInt("min");
         max = config.getInt("max");
         world = Bukkit.getServer().getWorld("World");
    }

    @Override
    public void onDisable(){
    }


    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        if(!event.isBedSpawn() || !event.isAnchorSpawn()){
            boolean foundLocation = false;
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            if(sender.hasPermission("changeradius.use")){
                if(label.equalsIgnoreCase("changeradius")|| label.equalsIgnoreCase("cr")){
                    if(args.length == 2){
                        if(NumberUtils.isNumber(args[0]) && NumberUtils.isNumber(args[1])){
                            int min =  Integer.parseInt(args[0]);
                            int max =  Integer.parseInt(args[1]);
                            if(min < max){
                                double worldSize = world.getWorldBorder().getSize()/2;
                                if(worldSize >= max && worldSize *-1 <= min){
                                    setMin(min);
                                    setMax(max);
                                    sender.sendMessage("The radius is now changed to: " + getMin() + " and " + getMax());
                                    return true;
                                }else{
                                    sender.sendMessage("Dont go over the world border.");
                                    return false;
                                }
                            }else{
                                sender.sendMessage("The max needs to be higher then the min");
                                return false;
                            }
                        }else{
                            sender.sendMessage("Please only just use numbers.");
                            return false;
                        }
                    }else if(args.length == 0){
                        sender.sendMessage("The range is between: " + getMin() + " and " + getMax()+".");
                        return true;
                    }
                }
            }else{
                sender.sendMessage("You dont have permissions to use this command.");
                return true;
            }

        }
        return false;
    }

    private boolean CoordinatesAboveValidation(World world, int y){
        Block oneAbove = world.getBlockAt(randX,y,randZ);
        return oneAbove.isPassable() && !oneAbove.isLiquid();
    }

    public int getMax() {
        return config.getInt("max");
    }

    public void setMax(int max) {
        config.set("max",max);
        this.max = max;
        this.saveConfig();
    }

    public int getMin() {
        return config.getInt("min");
    }

    public void setMin(int min) {
       config.set("min",min);
       this.min = min;
        this.saveConfig();
    }
}
