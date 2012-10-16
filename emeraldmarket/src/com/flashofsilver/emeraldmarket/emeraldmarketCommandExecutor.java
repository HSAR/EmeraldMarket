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
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// shortcut to administrate
		if (cmd.getName().equalsIgnoreCase("emeraldmarketadmin")) {
			if (sender.hasPermission("emeraldmarket.admin.settings")) {
				if (args.length < 1) {
					// if no arguments supplied, show help.
					return false;
				} else {
					if ((args.length == 3) && (args[0].equals("setalias"))) {
						// pass the arguments (minus the command argument)
						String[] clippedargs = new String[args.length - 1];
						System.arraycopy(args, 1, clippedargs, 0, args.length - 1);
						// partial name recognition
						clippedargs[0] = plugin.matchPartialUser(sender, clippedargs[0]);
						plugin.forceAlias(sender, clippedargs);
						return true;
					}
					if ((args.length == 2) && (args[0].equals("getalias"))) {
						// pass the arguments (minus the command argument)
						// (this one is short because getalias has only one arg)
						// partial name recognition
						String aliasinput;
						if (plugin.matchPartialUser(sender, args[1]) == null) {
							// if there's a problem matching the user,
							// terminate.
							return true;
						} else {
							aliasinput = plugin.matchPartialUser(sender, args[1]);
						}
						String aliasresult = plugin.getAlias(sender, aliasinput);
						sender.sendMessage("Alias of user '" + aliasinput + "' is '" + aliasresult + "'");
						return true;
					}
				}
				return false;
			} else {
				sender.sendMessage(ChatColor.RED + "You don't have permission to use the admin commands.");
				return true;
			}
		}
		// if (sender instanceof Player) {
		// shortcut to buy
		if (cmd.getName().equalsIgnoreCase("emeraldmarketbuy")) {
			if (sender.hasPermission("emeraldmarket.basic.buy")) {
				plugin.buy(sender, args);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "You don't have permission to buy emeralds.");
				return true;
			}
		}
		// shortcut to sell
		if (cmd.getName().equalsIgnoreCase("emeraldmarketsell")) {
			if (sender.hasPermission("emeraldmarket.basic.sell")) {
				plugin.sell(sender, args);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "You don't have permission to sell emeralds.");
				return true;
			}
		}
		// main command choice tree
		if (args.length > 0) {
			if (cmd.getName().equalsIgnoreCase("emeraldmarket") && args[0].equals("buy")) {
				if (sender.hasPermission("emeraldmarket.basic.buy")) {
					// pass the arguments (minus the command argument)
					String[] clippedargs = new String[args.length - 1];
					System.arraycopy(args, 1, clippedargs, 0, args.length - 1);
					plugin.buy(sender, clippedargs);
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "You don't have permission to buy emeralds.");
					return true;
				}
			}
			if (cmd.getName().equalsIgnoreCase("emeraldmarket") && args[0].equals("sell")) {
				if (sender.hasPermission("emeraldmarket.basic.sell")) {
					// pass the arguments (minus the command argument)
					String[] clippedargs = new String[args.length - 1];
					System.arraycopy(args, 1, clippedargs, 0, args.length - 1);
					plugin.sell(sender, clippedargs);
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "You don't have permission to sell emeralds.");
					return true;
				}
			}
		}

		// if nothing has fitted, show the help by returning false.
		sender.sendMessage(ChatColor.RED + "Command failed.");
		return false;
	}
}
