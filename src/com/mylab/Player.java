package com.mylab;

import java.util.ArrayList;
import java.util.Collections;

abstract class Player {
	String name;
	int points;
	ArrayList<Card> hand = new ArrayList<Card>();

	Player(String id) {
		name = id;
		points = 0;
	}

	class SuitRange {
		int startIndex;
		int endIndex;

		SuitRange() {
			startIndex = -1;
			endIndex = -1;
		}

		int getRange() {
			return endIndex - startIndex;
		}
	}

	void addToHand(Card newCard) {
		hand.add(newCard);
	}

	String cardstosymbols(ArrayList<Card> hand) {
		String symbols = "";
		for (int i = 0; i < hand.size(); i++) {
			String symbol1 = "";
			String symbol2 = "";
			switch (hand.get(i).suit) {
			case SPADES:
				symbol1 = "♠";
				break;
			case HEARTS:
				symbol1 = "♥";
				break;
			case CLUBS:
				symbol1 = "♣";
				break;
			case DIAMONDS:
				symbol1 = "♦";
				break;
			}
			switch (hand.get(i).value) {
			case KING:
				symbol2 = "H";
				break;
			case QUEEN:
				symbol2 = "V";
				break;
			case JACK:
				symbol2 = "B";
				break;
			case TEN:
				symbol2 = "0";
				break;
			case NINE:
				symbol2 = "9";
				break;
			case EIGHT:
				symbol2 = "8";
				break;
			case SEVEN:
				symbol2 = "7";
				break;
			case SIX:
				symbol2 = "6";
				break;
			case FIVE:
				symbol2 = "5";
				break;
			case FOUR:
				symbol2 = "4";
				break;
			case THREE:
				symbol2 = "3";
				break;
			case TWO:
				symbol2 = "2";
				break;
			case ACE:
				symbol2 = "A";
				break;
			}
			symbols = symbols + symbol1 + symbol2;
		}
		return symbols;
	}

	boolean comparecards(String card1, String card2) {
		String suitsymbols = "♠♥♣♦";
		String ranksymbols = "234567890BVHA";
		int suit1 = 0;
		int suit2 = 0;
		for (int i = 0; i < 4; i++) {
			if (card1.charAt(0) == suitsymbols.charAt(i)) {
				suit1 = i;
				break;
			}
		}
		for (int i = 0; i < 4; i++) {
			if (card2.charAt(0) == suitsymbols.charAt(i)) {
				suit2 = i;
				break;
			}
		}
		int rank1 = 0;
		int rank2 = 0;
		for (int i = 0; i < 13; i++) {
			if (card1.charAt(1) == ranksymbols.charAt(i)) {
				rank1 = i;
				break;
			}
		}
		for (int i = 0; i < 13; i++) {
			if (card2.charAt(1) == ranksymbols.charAt(i)) {
				rank2 = i;
				break;
			}
		}
		boolean result;
		if (suit1 == suit2) {
			result = rank1 > rank2;
		} else {
			result = suit1 > suit2;
		}
		return result;
	}

	void sortHand() {
		Collections.sort(hand);
	}

	/*
	 * void sortHand() { String s = cardstosymbols(hand); ArrayList<String>
	 * cardsymbols = new ArrayList<String>(); int index; for (int i = 0; i <
	 * hand.size(); i++) { index = i * 2; String symbols = s.substring(index,
	 * index + 2); cardsymbols.add(symbols); } Card tempcard; String
	 * tempsymbols; for (int i = hand.size() - 1; i >= 1; --i) { for (int j = 0;
	 * j < i; ++j) { if (comparecards(cardsymbols.get(j), cardsymbols.get(j +
	 * 1))) { tempcard = hand.get(j); hand.set(j, hand.get(j + 1)); hand.set(j +
	 * 1, tempcard); tempsymbols = cardsymbols.get(j); cardsymbols.set(j,
	 * cardsymbols.get(j + 1)); cardsymbols.set(j + 1, tempsymbols); } } } }
	 */
	void clearHand() {
		hand.clear();
	}

	boolean checkSuit(Suit check) {
		boolean flag = false;
		if (check == null)
			return false;
		for (Card c : hand) {
			if (c.getSuit() == check)
				flag = true;
		}
		return flag;
	}

	boolean hasTwoOfClubs() {
		if (hand.size() == 0)
			return false;
		Card holder = new Card(Suit.CLUBS, Value.TWO);
		return holder.equals(hand.get(0));
	}

	boolean hasAllHearts() {
		boolean flag = true;
		for (Card c : hand) {
			if (c.getSuit() != Suit.HEARTS)
				flag = false;
		}
		return flag;
	}

	Suit getFirstSuit(ArrayList<Card> currentRound) {
		if (currentRound.size() == 0)
			return null;
		return currentRound.get(0).getSuit();
	}

	SuitRange getSuitRange(Suit check, ArrayList<Card> currentHand) {
		SuitRange range = new SuitRange();
		if (check == null)
			return range;
		for (int i = 0; i < currentHand.size(); i++) {
			if (range.startIndex == -1 && currentHand.get(i).getSuit() == check)
				range.startIndex = i;
			if (range.startIndex != -1 && currentHand.get(i).getSuit() != check) {
				range.endIndex = i;
				break;
			}
		}
		if (range.startIndex != -1 && range.endIndex == -1)
			range.endIndex = currentHand.size();
		return range;
	}

	void printHand() {
		System.out.print("\n" + name + "`s hand (" + hand.size() + " card");
		if (hand.size() > 1)
			System.out.print("s");
		System.out.print("):\n|");
		for (int i = 0; i < hand.size(); i++) {
			System.out.format("%3d|", i);
		}
		System.out.print("\n|");
		for (int i = 0; i < hand.size(); i++) {
			System.out.format("%3s|", hand.get(i).printCardShort());
		}
		System.out.println("");
	}

	void printHandStubMode() {
		if (hand.size() == 13) {
			Hearts.writer.println(name);
			for (int i = 0; i < hand.size(); i++) {
				Hearts.writer.print(hand.get(i).printCardShort());
			}
			Hearts.writer.println("");
		}
	}

	String getName() {
		return name;
	}

	void addPoints(int pnts) {
		points += pnts;
	}

	int getPoints() {
		return points;
	}

	void clearPlayer() {
		clearHand();
		points = 0;
	}

	abstract boolean setDebug();

	abstract Card performAction(State masterCopy);
}