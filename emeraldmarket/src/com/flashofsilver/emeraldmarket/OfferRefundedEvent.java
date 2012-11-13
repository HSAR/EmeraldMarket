package com.flashofsilver.emeraldmarket;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.flashofsilver.emeraldmarket.Emeraldmarket.TransactionType;

public class OfferRefundedEvent extends Event {
	private final TransactionType type;
	private final String firstParty;
	private final double price;
	private final int amount;

	private static final HandlerList handlers = new HandlerList();

	public OfferRefundedEvent(TransactionType type, String firstParty, double price, int amount) {
		this.type = type;
		this.firstParty = firstParty;
		this.price = price;
		this.amount = amount;
	}

    public TransactionType getTransactionType() {
        return type;
    }

    public String getFirstParty() {
        return firstParty;
    }

    public double getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

	public HandlerList getHandlers() {
		return handlers;
	}

}
