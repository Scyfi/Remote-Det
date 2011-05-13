package org.scyfi.RemoteDet;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class RDListener extends PlayerListener{
	
	public RemoteDet plugin;
	public RDListener(RemoteDet instance){
		plugin = instance;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event){
		
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			if(block.getType() == Material.TNT){
				if(player.getItemInHand().getType() == Material.STONE_BUTTON){
					if(plugin.isRemoteEnabled(player)){
						plugin.armRemote(player, block);
					}
					event.setCancelled(true);
				}
			}
		}
		else if(event.getAction().equals(Action.LEFT_CLICK_AIR)){
			if(player.getItemInHand().getType() == Material.STONE_BUTTON){
				if (plugin.isRemoteArmed(player)){
					if ((plugin.armedRemote.get(player)).getType() == Material.TNT){
						plugin.detonateTNT(player);
					}
				}
			}
		}
	}

}
