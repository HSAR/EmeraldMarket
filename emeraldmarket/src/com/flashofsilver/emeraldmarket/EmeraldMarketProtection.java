package com.flashofsilver.emeraldmarket;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class EmeraldMarketProtection implements Listener {

	// pointer back to the main class
	private WorldGuardPlugin worldGuard;

	// constructor
	public EmeraldMarketProtection(WorldGuardPlugin worldGuard) {
		this.worldGuard = worldGuard;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onProtectionCheck(ProtectionCheckEvent event) {
		// if the result has already been determined, pass through
		if (event.getResult() == Event.Result.DENY) {
			return;
		}

		Block block = event.getBlock();
		Player player = event.getPlayer();

		Vector blockPos = BukkitUtil.toVector(block);
		RegionManager manager = worldGuard.getRegionManager(block.getWorld());
		ApplicableRegionSet set = manager.getApplicableRegions(blockPos);

		LocalPlayer localPlayer = worldGuard.wrapPlayer(player);

		// else, check in WG whether the player can use EmeraldMarket
		if (!canAccess(localPlayer, block, set)) {
			event.setResult(Event.Result.DENY);
		}
	}

	private boolean canAccess(LocalPlayer player, Block block, ApplicableRegionSet set) {
		return worldGuard.getGlobalRegionManager().hasBypass(player, block.getWorld())
				|| set.getFlag(DefaultFlag.BUYABLE, player);
	}

}
