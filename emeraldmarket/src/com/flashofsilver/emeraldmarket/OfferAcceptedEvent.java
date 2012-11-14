package com.flashofsilver.emeraldmarket;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.flashofsilver.emeraldmarket.Emeraldmarket.OfferType;

public class OfferAcceptedEvent extends Event {
	private final OfferType type;
	private final String firstParty;
	private final String secondParty;
	private final double price;
	private final int amount;

	private static final HandlerList handlers = new HandlerList();

	public OfferAcceptedEvent(OfferType type, String firstParty, String secondParty, double price, int amount) {
		this.type = type;
		this.firstParty = firstParty;
		this.secondParty = secondParty;
		this.price = price;
		this.amount = amount;
	}

    public OfferType getTransactionType() {
        return type;
    }

    public String getFirstParty() {
        return firstParty;
    }

    public String getSecondParty() {
        return secondParty;
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

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
