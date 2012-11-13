package com.flashofsilver.emeraldmarket;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class EmeraldmarketEventListener implements Listener {

	private Emeraldmarket plugin;

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		// create player object for the player who joined
		Player player = event.getPlayer();
		plugin.notify(player);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {

		Block block = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);

		if ((block.getType() == Material.SOIL) || (block.getType() == Material.CROPS))

			if (event.hasItem()) {

				if (event.getItem().getTypeId() == 1) {
					return;
				}
			}
	}
}
