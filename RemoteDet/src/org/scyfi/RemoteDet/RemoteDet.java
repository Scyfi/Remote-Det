package org.scyfi.RemoteDet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import net.minecraft.server.EntityTNTPrimed;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftTNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class RemoteDet extends JavaPlugin{
	
	public final Logger log = Logger.getLogger("Minecraft");
	public final String pluginName = "Remote Det";
	public static PermissionHandler permissionHandler;
	
	private RDListener playerListener = new RDListener(this);
	CraftTNTPrimed tntp;
	
	public final LinkedList<Player> remoteEnabled = new LinkedList<Player>();
	public HashMap<Player, Block> armedRemote = new HashMap<Player, Block>();
	
	public void onEnable(){
		log.info(pluginName + " - Version " + this.getDescription().getVersion() + " Enabled");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
		setupPermissions();
		
	}
	
	public void onDisable(){
		log.info(pluginName + " - Version " + this.getDescription().getVersion() + " has been disabled.");
		
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if(!(sender instanceof Player)) {
				return false;
			}
			
			Player player = (Player) sender;
			if (command.getName().equalsIgnoreCase("rd")){
				toggleRemote(player);
				return true;
			}	
			return false;
	}
	
	@SuppressWarnings("static-access")
	private void setupPermissions() {
	      Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

	      if (this.permissionHandler == null) {
	          if (permissionsPlugin != null) {
	              this.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	          }else {
	              log.info("Permission system not detected, defaulting to OP");
	          } 
	      }
	  }
	
	public boolean checkPermission(Player player, String perm){
		if (permissionHandler == null){
			return true;
		}else{
			return 	permissionHandler.has(player, perm);
		}
	}
	
	public void toggleRemote(Player player){
		if (!checkPermission(player, "remotedet.remote")){
			player.sendMessage("You do not have permission to use RemoteDet.");
		}else{
			if(remoteEnabled.contains(player)){
				remoteEnabled.remove(player);
				player.sendMessage(ChatColor.YELLOW + "Remote Detonator disabled!");
			} else {
				remoteEnabled.add(player);
				player.sendMessage(ChatColor.YELLOW + "Remote Detonator enabled!");
			}
		}
	}
	
	public boolean isRemoteEnabled(Player player){
		return remoteEnabled.contains(player);
	}
	
	public void armRemote(Player player, Block block){
			armedRemote.put(player, block);
			player.sendMessage(ChatColor.RED + "Remote is ARMED!!");
	}
	
	public boolean isRemoteArmed(Player player){
		if(armedRemote.containsKey(player)){
			return true;
		}
		return false;
	}
	
	//Credit to toasterktn and his source code for this. 
	public void detonateTNT(Player player) {
		final Block b;
		b = armedRemote.get(player);
		Server server = this.getServer();
		CraftWorld cWorld = (CraftWorld) b.getWorld();
		EntityTNTPrimed tnt = new EntityTNTPrimed(cWorld.getHandle(), b.getLocation().getX(), b.getLocation().getY(), b.getLocation().getZ());
		tnt.setPositionRotation(b.getLocation().getBlockX() + 0.5, b.getLocation().getBlockY(), b.getLocation().getBlockZ() + 0.5, 0, 0);
		cWorld.getHandle().addEntity(tnt);
		tntp = new CraftTNTPrimed((CraftServer) server, tnt);
		b.setType(Material.AIR);
		armedRemote.remove(player);
	}
}
