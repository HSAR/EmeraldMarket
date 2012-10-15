package com.flashofsilver.emeraldmarket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class emeraldmarket extends JavaPlugin {

	// Important plugin objects
	public Logger logger;
	public static Economy econ = null;
	//
	public boolean verbose;
	// sql vars
	private String URL;
	private String dbUser;
	private String dbPass;
	// currency format
	DecimalFormat currency = new DecimalFormat("#.##");

	private Connection connection;

	private String getAlias(CommandSender sender) {
		// checks if user has an alias already - if not, autocreates one.
		ResultSet resultset;
		String testalias = "";
		try {

			Statement statement = connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			// try to get the alias from the table
			resultset = statement
					.executeQuery("SELECT masteralias FROM emeraldmarket_aliases "
							+ "WHERE user = '" + sender.getName() + "';");
			// if not set then use the uppercased first 4 letters of their name
			if (resultset == null || !resultset.first()) {
				if (verbose) {
					logger.info("Alias for user '" + sender.getName()
							+ "' not found. Generating...");
				}
				testalias = sender.getName().substring(0, 4).toUpperCase();
				// check whether the alias has been used before...
				resultset = statement
						.executeQuery("SELECT user FROM emeraldmarket_aliases "
								+ "WHERE masteralias = '" + testalias + "';");
				// if nothing, then it hasn't been used before.
				if (resultset == null || !resultset.first()) {
					statement
							.executeUpdate("INSERT INTO emeraldmarket_aliases (user, masteralias) "
									+ "VALUES ('"
									+ sender.getName()
									+ "', '"
									+ testalias + "');");
					if (verbose) {
						logger.info("Added user " + sender.getName()
								+ " to alias table under " + testalias);
					}
					// close things down.
					if (statement != null) {
						statement.close();
					}
					if (resultset != null) {
						resultset.close();
					}
					return testalias;
				} else {
					// if generation fails then tell the user and return null.
					sender.sendMessage(ChatColor.RED
							+ "Alias generation failed. Ask an admin.");
					//
					//
					// You'll need to add them manually using the admin
					// commands.
					//
					//
					// close things down.
					if (statement != null) {
						statement.close();
					}
					if (resultset != null) {
						resultset.close();
					}
					return null;
				}
			} else {
				// if the query returns an alias, return that.
				testalias = resultset.getString("masteralias");

				if (statement != null) {
					statement.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (resultset != null) {
					resultset.close();
				}
				return testalias;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private boolean createSQL() throws SQLException, ClassNotFoundException {
		Statement statement = connection.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		// select
		/*
		 * try { ResultSet res =
		 * Stmt.executeQuery("CREATE DATABASE EmeraldMarket"); while
		 * (res.next()) { logger.info("result:" + res.getString(0) + ","); } }
		 * catch (SQLException e) { logger.info("[mdbc] SQL Exception: " + e); }
		 */
		// or update
		try {
			statement.executeUpdate("CREATE TABLE emeraldmarket_aliases ( "
					+ "user  VARCHAR( 32 )  PRIMARY KEY NOT NULL UNIQUE, "
					+ "masteralias VARCHAR( 4 )   UNIQUE);");
		} catch (SQLException e) {
			logger.info(" SQL Exception: " + e);
			return false;
		}
		try {
			statement
					.executeUpdate("CREATE TABLE emeraldmarket_buy ( "
							+ "user   VARCHAR( 32 )    NOT NULL,"
							+ "alias  VARCHAR( 4 ) NOT NULL REFERENCES emeraldmarket_aliases( masteralias ) "
							+ "ON DELETE RESTRICT ON UPDATE CASCADE MATCH FULL, "
							+ "price  DOUBLE( 64, 2 )  NOT NULL,"
							+ "amount INT( 5 )         NOT NULL,"
							+ "date   DATETIME         NOT NULL, "
							+ "PRIMARY KEY (user, date)" + ");");
		} catch (SQLException e) {
			logger.info(" SQL Exception: " + e);
			return false;
		}
		try {
			statement
					.executeUpdate("CREATE TABLE emeraldmarket_sell ( "
							+ "user   VARCHAR( 32 )    NOT NULL,"
							+ "alias  VARCHAR( 4 ) NOT NULL REFERENCES emeraldmarket_aliases( masteralias ) "
							+ "ON DELETE RESTRICT ON UPDATE CASCADE MATCH FULL, "
							+ "price  DOUBLE( 64, 2 )  NOT NULL,"
							+ "amount INT( 5 )         NOT NULL,"
							+ "date   DATETIME         NOT NULL, "
							+ "PRIMARY KEY (user, date)" + ");");
		} catch (SQLException e) {
			logger.info(" SQL Exception: " + e);
			return false;
		}
		return true;
	}

	private String getDateDiff(Timestamp input) {
		// returns the elapsed time from a Timestamp in String form until now.
		// ----
		// get current date/time for comparison.
		java.util.Date currtime = new Date();
		// convert both to milliseconds and subtract them
		long diffInMS = Math.abs(currtime.getTime() - input.getTime());
		// if it's been under a minute, use seconds.
		if (diffInMS <= 60000) {
			int diffInS = (int) (diffInMS / 1000);
			if (diffInS == 1) {
				return (diffInS + " second");
			} else {
				return (diffInS + " seconds");
			}
		} else {
			// if it's been under an hour, use minutes.
			if (diffInMS <= 3600000) {
				int diffInMin = (int) (diffInMS / 60000);
				if (diffInMin == 1) {
					return (diffInMin + " minute");
				} else {
					return (diffInMin + " minutes");
				}
			} else {
				// if it's been under a day, use hours.
				if (diffInMS <= 86400000) {
					int diffInH = (int) (diffInMS / 3600000);
					if (diffInH == 1) {
						return (diffInH + " hour");
					} else {
						return (diffInH + " hours");
					}
				} else {
					// if it's been under a month, use days.
					// note - capital L at the end of the number below denotes a
					// LongInt
					if (diffInMS <= 2628000000L) {
						int diffInD = (int) (diffInMS / 86400000);
						if (diffInD == 1) {
							return (diffInD + " day");
						} else {
							return (diffInD + " days");
						}
					} else {
						// if it's bigger than all those, use months (unlikely!)
						// note - capital L at the end of the number below
						// denotes a LongInt
						int diffInMonths = (int) (diffInMS / 2628000000L);
						if (diffInMonths == 1) {
							return (diffInMonths + "month");
						} else {
							return (diffInMonths + " months");
						}
					}
				}
			}
		}
	}

	private int getResultSetNumRows(ResultSet res) {
		try {
			// get row at beginning so as to not affect it
			int originalPlace = res.getRow();
			res.last();
			// Get the row number of the last row which is also the row count
			int rowCount = res.getRow();
			// move row back to original position
			res.absolute(originalPlace);
			return rowCount;
		} catch (SQLException e) {
		}
		return -1;
	}

	private void displayBuyOffers(CommandSender sender) {
		ResultSet resultset;
		try {
			Statement statement = connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			// query for buy listings
			resultset = statement
					.executeQuery("SELECT DISTINCT price FROM emeraldmarket_buy "
							+ "ORDER BY price ASC;");
			if (resultset == null || !resultset.first()) {
				// if none, tell the user so.
				sender.sendMessage(ChatColor.DARK_GREEN + "========= "
						+ ChatColor.WHITE + "OFFERS TO BUY"
						+ ChatColor.DARK_GREEN + " =========");
				sender.sendMessage(ChatColor.WHITE + "None found.");
				sender.sendMessage(ChatColor.DARK_GREEN
						+ "=================================");
			} else {
				// continue querying to find the offer at the "top of the pile"
				// defined by "oldest offer" for that particular price point.
				// write the header, then write the rest as they come in.
				sender.sendMessage(ChatColor.DARK_GREEN + "========= "
						+ ChatColor.WHITE + "OFFERS TO BUY"
						+ ChatColor.DARK_GREEN + " =========");
				// find the number of repeats to do
				// 5 (arbitrary low number)
				// or the number of rows in the result set, whichever is lower.
				int numreps;
				if (getResultSetNumRows(resultset) < 5) {
					numreps = getResultSetNumRows(resultset);
				} else {
					numreps = 5;
				}
				// move cursor before line 1
				resultset.beforeFirst();
				for (int i = 0; i < numreps; i++) {
					// cycle to next row
					if (resultset.next()) {
						double currprice = resultset.getDouble("price");
						ResultSet rs = statement
								.executeQuery("SELECT alias, date FROM emeraldmarket_buy "
										+ "WHERE price = "
										+ Double.toString(currprice)
										+ " ORDER BY date ASC;");
						rs.first(); // move to first row
						sender.sendMessage(ChatColor.WHITE + " "
								+ currency.format(currprice) + ChatColor.GRAY
								+ " - " + ChatColor.DARK_GREEN
								+ rs.getString("alias") + ChatColor.GRAY
								+ " - " + ChatColor.WHITE
								+ this.getDateDiff(rs.getTimestamp("date")));
					}
				}
				sender.sendMessage(ChatColor.DARK_GREEN
						+ "=================================");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void displaySellOffers(CommandSender sender) {
		ResultSet resultset;
		try {
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			// query for buy listings
			resultset = statement
					.executeQuery("SELECT DISTINCT price FROM emeraldmarket_sell "
							+ "ORDER BY price;");
			if (resultset == null || !resultset.first()) {
				// if none, tell the user so.
				sender.sendMessage(ChatColor.DARK_GREEN + "========= "
						+ ChatColor.WHITE + "OFFERS TO SELL"
						+ ChatColor.DARK_GREEN + " =========");
				sender.sendMessage(ChatColor.WHITE + "None found.");
				sender.sendMessage(ChatColor.DARK_GREEN
						+ "=================================");
			} else {
				// continue querying to find the offer at the "top of the pile"
				// defined by "oldest offer" for that particular price point.
				// write the header, then write the rest as they come in.
				sender.sendMessage(ChatColor.DARK_GREEN + "======== "
						+ ChatColor.WHITE + "EMERALDS FOR SALE"
						+ ChatColor.DARK_GREEN + " ========");
				// find the number of repeats to do
				// 5 (arbitrary low number)
				// or the number of rows in the result set, whichever is lower.
				int numreps;
				if (getResultSetNumRows(resultset) < 5) {
					numreps = getResultSetNumRows(resultset);
				} else {
					numreps = 5;
				}
				resultset.beforeFirst();
				for (int i = 1; i < (numreps + 1); i++) {
					// cycle to next row
					if (resultset.next()) {
						double currprice = resultset.getDouble("price");
						Statement s = connection.createStatement(
								ResultSet.TYPE_SCROLL_SENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
						ResultSet amountRS = s
								.executeQuery("SELECT COUNT(price) FROM emeraldmarket_sell "
										+ "WHERE price = "
										+ Double.toString(currprice) + ";");
						amountRS.first(); // move to first row
						sender.sendMessage(ChatColor.GRAY + Integer.toString(i)
								+ ". " + ChatColor.WHITE
								+ Double.toString(currprice) + " "
								+ econ.currencyNamePlural() + ChatColor.GRAY
								+ " (" + ChatColor.DARK_GREEN
								+ amountRS.getString("COUNT(price)")
								+ " on offer" + ChatColor.GRAY + ") ");
						s.close();
					}
				}
				sender.sendMessage(ChatColor.DARK_GREEN
						+ "=================================");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	@Override
	public void onEnable() {
		// start the logger
		logger = getLogger();
		// save config to default location if not already there
		this.saveDefaultConfig();
		// verbose logging? retrieve value from config file.
		verbose = this.getConfig().getBoolean("verboselogging");
		if (verbose) {
			logger.info("Verbose logging enabled.");
		} else {
			logger.info("Verbose logging disabled.");
		}
		// enable command executor
		emeraldmarketCommandExecutor emeraldmarketCommandExecutor = new emeraldmarketCommandExecutor(
				this);
		getCommand("emeraldmarket").setExecutor(emeraldmarketCommandExecutor);
		getCommand("emeraldmarketbuy")
				.setExecutor(emeraldmarketCommandExecutor);
		getCommand("emeraldmarketsell").setExecutor(
				emeraldmarketCommandExecutor);
		getCommand("emeraldmarketadmin").setExecutor(
				emeraldmarketCommandExecutor);
		// retrieve SQL variables from config
		URL = this.getConfig().getString("URL");
		dbUser = this.getConfig().getString("Username");
		dbPass = this.getConfig().getString("Password");
		// create database connection
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:" + URL + "?user="
					+ dbUser + "&password=" + dbPass);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		// first-run initialisation
		final boolean firstrun = this.getConfig().getBoolean("firstrun");
		if (firstrun) {
			try {
				boolean SQLsuccess = this.createSQL();
				if (verbose && SQLsuccess) {
					logger.info("Tables created successfully.");
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.getConfig().set("firstrun", false);
			this.saveConfig();
			if (verbose) {
				logger.info("First-run initialisation complete.");
			}
		}
		if (!setupEconomy()) {
			logger.info("Vault dependency unsatisfied (Vault could not be found)!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
	}

	@Override
	public void onDisable() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (verbose) {
			logger.info("Plugin shutdown complete.");
		}
	}

	public void onPlayerJoin(PlayerJoinEvent evt) {
		Player player = evt.getPlayer(); // The player who joined
		PlayerInventory inventory = player.getInventory(); // The player's
															// inventory
		ItemStack itemstack = new ItemStack(Material.DIAMOND, 64); // A stack of
																	// diamonds

		if (inventory.contains(itemstack)) {
			inventory.addItem(itemstack); // Adds a stack of diamonds to the
											// player's inventory
			player.sendMessage("Welcome! You seem to be reeeally rich, so we gave you some more diamonds!");
		}
	}

	public void sell(CommandSender sender, String[] args) {
		sender.sendMessage("Welcome, seller! HSAR is working on this.");
		if (args.length != 2) {
			if (args.length < 1) {
				// if no arguments supplied, query for the "buy" trade listings.
				this.displayBuyOffers(sender);
			} else {
				if (args.length < 2) {
					// sell a single emerald if nothing else written.
					args[1] = Integer.toString(1);
				} else {
					sender.sendMessage(ChatColor.RED
							+ "Wrong number of arguments.");
					sender.sendMessage(ChatColor.RED + "/ems [price] [amount]");
				}
			}
		} else {

			if (args.length == 2) {
				// check the player actually has enough emeralds
				// first, retrieve the player's inventory
				Player player = (Player) sender;
				PlayerInventory inventory = player.getInventory();
				ItemStack itemstack = new ItemStack(Material.EMERALD,
						Integer.parseInt(args[1]));
				// if the inventory contains the right amount of emeralds...
				// begin.
				if (inventory.contains(itemstack)) {
					// prep data - USER, PRICE, AMOUNT and DATE.
					// Also retrieve or create ALIAS, a 4-letter alias.
					String useralias = getAlias(sender);
					if (useralias != null) {
						try {
							Statement statement = connection.createStatement();
							// get timestamp for entry to DB
							java.util.Date date = new Date();
							Object datestamp = new java.sql.Timestamp(
									date.getTime());
							statement
									.executeUpdate("INSERT INTO emeraldmarket_sell (user, alias, price, amount, date) "
											+ "VALUES ('"
											+ sender.getName()
											+ "', "
											+ "(SELECT masteralias from emeraldmarket_aliases where user = '"
											+ sender.getName()
											+ "')"
											+ ", "
											+ args[0]
											+ ", "
											+ args[1]
											+ ", '"
											+ datestamp + "');");
							if (statement != null) {
								statement.close();
							}
						} catch (SQLException e) {
							e.printStackTrace();
						} finally {
							// when all has been added successfully, remove the
							// items from the user.
							inventory.removeItem(itemstack);
							econ.withdrawPlayer(sender.getName(),
									Integer.parseInt(args[1]));
							sender.sendMessage(ChatColor.YELLOW
									+ "Placed sell offer for " + args[1]
									+ " emeralds at " + args[0] + "/emerald.");
						}
					}

				} else {
					sender.sendMessage(ChatColor.RED
							+ "You can't sell emeralds you don't have!");
					return;
				}
			}
		}
	}

	public void buy(CommandSender sender, String[] args) {
		sender.sendMessage("Welcome, buyer! HSAR is working on this.");
		if (args.length != 2) {
			if (args.length < 1) {
				// if no arguments supplied, query for the "sell" trade
				// listings.
				this.displaySellOffers(sender);
			} else {
				if (args.length < 2) {
					// buy a single emerald if nothing else written.
					args[1] = Integer.toString(1);
				} else {
					sender.sendMessage(ChatColor.RED
							+ "Wrong number of arguments.");
					sender.sendMessage(ChatColor.RED + "/emb [price] [amount]");
				}
			}
		} else {
			if (args.length == 2) {
				// check the player actually has enough emeralds for the
				// transaction
				// first, retrieve the player's inventory
				// if the inventory contains the right amount of emeralds...
				// begin.
				if (Double.parseDouble(args[1]) <= econ.bankBalance(sender
						.getName()).amount) {
					// prep data - USER, PRICE, AMOUNT and DATE.
					// Also retrieve or create ALIAS, a 4-letter alias.
					String useralias = getAlias(sender);
					if (useralias != null) {
						try {
							Statement statement = connection.createStatement(
									ResultSet.TYPE_SCROLL_INSENSITIVE,
									ResultSet.CONCUR_READ_ONLY);
							// get timestamp for entry to DB
							java.util.Date date = new Date();
							Object datestamp = new java.sql.Timestamp(
									date.getTime());
							statement
									.executeUpdate("INSERT INTO emeraldmarket_buy (user, price, amount, date) "
											+ "VALUES ('"
											+ sender.getName()
											+ "', "
											+ args[0]
											+ ", "
											+ args[1]
											+ ", '" + datestamp + "');");
							if (statement != null) {
								statement.close();
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							// when all has been added successfully, remove the
							// money from the user.
							EconomyResponse r = econ.withdrawPlayer(
									sender.getName(), 1.05);
							if (r.transactionSuccess()) {
								sender.sendMessage(ChatColor.YELLOW
										+ "Placed buy offer for "
										+ args[0]
										+ " emeralds "
										+ " at "
										+ currency.format(Double
												.parseDouble(args[0]))
										+ "/emerald.");

							} else {
								sender.sendMessage(String.format(
										"An error occured: %s", r.errorMessage));
							}
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED
							+ "You don't have enough money!");
					return;
				}
			}
		}

	}
}
