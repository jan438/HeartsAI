package com.mylab;

import java.util.*;

public class Deck {
	boolean initCounter;
	ArrayList<Card> allCards = new ArrayList<Card>();
	ArrayList<Card> invertDeck = new ArrayList<Card>();

	Deck() {
		initCounter = true;
		initDeck();
		shuffleDeck();
	}

	Deck(Deck toCopy) {
		initCounter = toCopy.initCounter;
		allCards = new ArrayList<Card>(toCopy.allCards);
		invertDeck = new ArrayList<Card>(toCopy.invertDeck);
	}

	void initDeck() {
		if (initCounter) {
			System.out.println("The deck has been initialized.");
			for (Suit sui : Suit.values()) {
				for (Value val : Value.values()) {
					allCards.add(new Card(sui, val));
				}
			}
			initCounter = false;
		}
	}

	void shuffleDeck() {
		long seed = System.nanoTime();
		Collections.shuffle(allCards, new Random(seed));
	}

	void printDeck() {
		for (Card car : allCards) {
			System.out.print(car.printCard() + " ");
		}
		System.out.println("\nSize of Deck: " + allCards.size() + "\n");
	}

	Card drawTop() {
		if (allCards.size() != 0) {
			invertDeck.add(allCards.get(allCards.size() - 1));
			return allCards.remove(allCards.size() - 1);
		} else
			System.out.println("Error! The Deck is empty; cannot draw from it!");
		return null;
	}

	int size() {
		return allCards.size();
	}

	void restockDeck(Card returned) {
		allCards.add(returned);
		invertDeck.remove(returned);
	}

	boolean checkDeck() {
		if (allCards.size() == 52 && invertDeck.size() == 0)
			return true;
		return false;
	}
}