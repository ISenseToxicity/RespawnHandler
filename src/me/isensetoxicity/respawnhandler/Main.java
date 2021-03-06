package me.isensetoxicity.respawnhandler;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin implements Listener {
    private int randX;
    private int randZ;
    private final FileConfiguration config = this.getConfig();
    private int max;
    private int min;
    private boolean bedRespawn;
    private boolean anchorRespawn;
    private World world;

    public void onEnable(){
        this.getServer().getPluginManager().registerEvents(this,this);
         config.addDefault("min",-1000);
         config.addDefault("max",1000);
         config.addDefault("pluginStatus", "on");
         config.addDefault("bedRespawnRandomTeleport",false);
         config.addDefault("AnchorRespawnRandomTeleport",false);
         config.options().copyDefaults(true);
         this.saveConfig();
         min = config.getInt("min");
         max = config.getInt("max");
         bedRespawn = config.getBoolean("bedRespawnRandomTeleport");
         anchorRespawn = config.getBoolean("AnchorRespawnRandomTeleport");
         world = Bukkit.getServer().getWorld("World");
    }

    @Override
    public void onDisable(){
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        if(getPluginstatus().equalsIgnoreCase("on")){
            if((respawnBedAndAnchorCheck(event.isAnchorSpawn(),anchorRespawn)) && respawnBedAndAnchorCheck(event.isBedSpawn(),bedRespawn)){
                handlePlayerRespawnEvent(event);
            }
        }
    }

    private boolean respawnBedAndAnchorCheck(boolean respawnBoolean, boolean configBoolean){
        return !respawnBoolean || configBoolean;
    }

    private void handlePlayerRespawnEvent(PlayerRespawnEvent event){
        Location foundLocation = calculatePosition();
        foundLocation.getChunk().load();
        event.setRespawnLocation(foundLocation);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(!player.hasPlayedBefore()){
            Location foundLocation = calculatePosition();
            foundLocation.getChunk().load();
            player.teleport(foundLocation);
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if(sender.hasPermission("respawnhandler.use")){
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
                                    return true;
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

                if(label.equalsIgnoreCase("togglerespawnhandler")){
                    if(args.length == 0){
                        sender.sendMessage("the plugin is currently "+ getPluginstatus());
                        return true;
                    }
                    if(args.length==1){
                        if(args[0].equalsIgnoreCase("on")||args[0].equalsIgnoreCase("off")){
                            setPluginstatus(args[0]);
                            sender.sendMessage("the plugin is now "+ getPluginstatus());
                            return true;
                        }
                    }
                }
            }else{
                sender.sendMessage("You dont have permissions to use this command.");
                return true;
            }
       return false;
    }

    private Location calculatePosition(){
        int range = max - min + 1;
        while (true){
            randX = (int)(Math.random() * range) + min;
            randZ = (int)(Math.random() * range) + min;

            Block highestYSolidBlock = world.getHighestBlockAt(randX,randZ);

            int highestYSolidCoordinates = highestYSolidBlock.getY();

            if(coordinatesAboveValidation(world,highestYSolidCoordinates + 1) && coordinatesAboveValidation(world,highestYSolidCoordinates + 2) && !(highestYSolidBlock.getBlockData() instanceof Leaves) && !highestYSolidBlock.isLiquid()){

                return highestYSolidBlock.getLocation();
            }
        }

    }
    private boolean coordinatesAboveValidation(World world, int y){
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

    public String getPluginstatus() {
        return config.getString("pluginStatus");
    }

    public void setPluginstatus(String pluginstatus) {
        config.set("pluginStatus", pluginstatus);
        this.saveConfig();
    }
}
