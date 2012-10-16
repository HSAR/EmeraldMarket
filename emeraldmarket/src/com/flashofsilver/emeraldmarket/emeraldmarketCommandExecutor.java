package com.flashofsilver.emeraldmarket;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class emeraldmarketCommandExecutor implements CommandExecutor {

	private emeraldmarket plugin; // pointer to your main class, unrequired if
									// you don't need methods from the main
									// class

	public emeraldmarketCommandExecutor(emeraldmarket plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (sender instanceof Player) {
			// shortcut to buy
			if (cmd.getName().equalsIgnoreCase("emeraldmarketbuy")) {
				if (sender.hasPermission("emeraldmarket.basic.buy")) {
					plugin.buy(sender, args);
					return true;
				} else {
					sender.sendMessage(ChatColor.RED
							+ "You don't have permission to buy emeralds.");
					return true;
				}
			}
			// shortcut to sell
			if (cmd.getName().equalsIgnoreCase("emeraldmarketsell")) {
				if (sender.hasPermission("emeraldmarket.basic.sell")) {
					plugin.sell(sender, args);
					return true;
				} else {
					sender.sendMessage(ChatColor.RED
							+ "You don't have permission to sell emeralds.");
					return true;
				}
			}
			// shortcut to administrate
			if (cmd.getName().equalsIgnoreCase("emeraldmarketadmin")) {
				if (sender.hasPermission("emeraldmarket.admin.settings")) {
					if (args.length < 1) {
						// if no arguments supplied, show help.
						return false;
					} else {
						if ((args.length == 3) && (args[0] == "setalias")) {

							System.arraycopy(args, 1, args, 0, args.length);
							plugin.forceAlias(sender, args);
						}
					}
					return true;
				} else {
					sender.sendMessage(ChatColor.RED
							+ "You don't have permission to use the admin commands.");
					return true;
				}
			}
			// main command choice tree
			if (args.length > 0) {
				if (cmd.getName().equalsIgnoreCase("emeraldmarket")
						&& args[0] == "buy") {
					if (sender.hasPermission("emeraldmarket.basic.buy")) {
						System.arraycopy(args, 1, args, 0, args.length);
						plugin.buy(sender, args);
						return true;
					} else {
						sender.sendMessage(ChatColor.RED
								+ "You don't have permission to buy emeralds.");
						return true;
					}
				}
				if (cmd.getName().equalsIgnoreCase("emeraldmarket")
						&& args[0] == "sell") {
					if (sender.hasPermission("emeraldmarket.basic.sell")) {
						System.arraycopy(args, 1, args, 0, args.length);
						plugin.sell(sender, args);
						return true;
					} else {
						sender.sendMessage(ChatColor.RED
								+ "You don't have permission to sell emeralds.");
						return true;
					}
				}
			}

			// if nothing has fitted, show the help by returning false.
			sender.sendMessage(ChatColor.RED + "Command failed.");
			return false;

			// do something
		} else {
			// else, they are not a player.
			// Admin commands available, make buy/sell come from and go to
			// nowhere, etc.
			sender.sendMessage("You are a console entity.");
			return false;
		}
	}
}
