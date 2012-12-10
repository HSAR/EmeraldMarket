package com.flashofsilver.emeraldmarket;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import ca.xshade.bukkit.questioner.Questioner;
import ca.xshade.questionmanager.Option;
import ca.xshade.questionmanager.Question;
import ca.xshade.questionmanager.QuestionTask;

public class EmeraldMarketTransaction {
	
	private CommandSender target;
	private Object offer;
	private Object item;
	
	public EmeraldMarketTransaction(Questioner questioner, CommandSender sender, Object offer, Object item)
	// a PLAYER makes an OFFER on an ITEM
	// OFFERS and ITEMS may be emerald items or economy money items - hence why generics are used.
	{
		this.target = sender;
		this.offer = offer;
		this.item = item;
		
		// ask the player for confirmation
		List<Option> options = new ArrayList<Option>();
		options.add(new Option("confirm", new QuestionTask() {
			public void run() {
				return;
			}
		}));
		options.add(new Option("cancel", new QuestionTask() {
			@Override
			public void run() {
				plugin.finishDeal(getSender().getName(), amount, price);
				return;
			}
		}));
		try {
			Question question = new Question(sender.getName(), "Please confirm the transaction.", options);
			questioner.appendQuestion(question);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public CommandSender getSender() {
		return target;
	}
	
	public Object getOffer() {
		return offer;
	}

}
