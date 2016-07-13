package com.mylab;

import java.util.ArrayList;
import java.util.*;

class State {
	Deck cardsPlayed;
	ArrayList<Card> currentRound;
	ArrayList<Integer> playerScores;
	boolean hasHeartsBroken;
	Random rng = new Random();
	int playerIndex;
	String playerName;

	State(Deck deck, ArrayList<Card> round, ArrayList<Integer> scores, boolean hearts, int index, String name) {
		cardsPlayed = new Deck(deck);
		currentRound = new ArrayList<Card>(round);
		playerScores = new ArrayList<Integer>(scores);
		hasHeartsBroken = hearts;
		playerIndex = index;
		playerName = name;
	}

	State(State secondCopy) {
		cardsPlayed = new Deck(secondCopy.cardsPlayed);
		currentRound = new ArrayList<Card>(secondCopy.currentRound);
		playerScores = new ArrayList<Integer>(secondCopy.playerScores);
		hasHeartsBroken = secondCopy.hasHeartsBroken;
		playerIndex = secondCopy.playerIndex;
		playerName = secondCopy.playerName;
	}

	int getRoundNumber() {
		return cardsPlayed.size() / 4 + 1;
	}

	boolean isGameValid() {
		int countcardsplayed = cardsPlayed.size();
		return countcardsplayed < 52;
	}

	boolean validRound() {
		return currentRound.size() < 4;
	}

	boolean firstMove() {
		return cardsPlayed.allCards.size() == 0;
	}

	boolean firstInRound() {
		return currentRound.size() == 0;
	}

	int getScore() {
		int index = playerIndex;
		String name = playerName;
		return playerScores.get(index);
	}

	boolean isInMyHand(Card c, ArrayList<Card> playoutHand) {
		for (Card d : playoutHand)
			if (c.equals(d))
				return true;
		return false;
	}

	void playCard(Card c) {
		for (Card d : cardsPlayed.invertDeck) {
			if (c.equals(d)) {
				cardsPlayed.allCards.add(d);
				cardsPlayed.invertDeck.remove(d);
				currentRound.add(d);
				break;
			}
		}
	}

	boolean hasAllHearts(ArrayList<Card> hand) {
		boolean flag = true;
		for (Card c : hand) {
			if (c.getSuit() != Suit.HEARTS)
				flag = false;
		}
		return flag;
	}

	boolean checkRound(Card c, ArrayList<Card> playoutHand) {
		Card twoClubs = new Card(Suit.CLUBS, Value.TWO);
		if (firstMove() && !c.equals(twoClubs)) {
			System.out.println("Simulation issue: Must play two of clubs to start the game.");
			return false;
		}
		if (firstInRound()) {
			if (c.getSuit() == Suit.HEARTS && !hasHeartsBroken && !hasAllHearts(playoutHand))
				return false;
			return true;
		} else {
			Suit firstSuit = currentRound.get(0).getSuit();
			if (firstSuit != c.getSuit()) {
				boolean flag = false;
				for (Card d : playoutHand) {
					if (d.getSuit() == firstSuit)
						flag = true;
				}
				if (flag)
					return false;
			}
			if (c.getSuit() == Suit.HEARTS) {
				hasHeartsBroken = true;
			}
		}
		return true;
	}

	int calculatePoints() {
		int points = 0;
		for (Card c : currentRound) {
			if (c.getSuit() == Suit.HEARTS)
				points++;
			if (c.getValue() == Value.QUEEN && c.getSuit() == Suit.SPADES)
				points += 13;
		}
		return points;
	}

	int findTaker(int firstPlayer) {
		Suit firstSuit = currentRound.get(0).getSuit();
		Value largestValue = currentRound.get(0).getValue();
		int taker = firstPlayer;
		for (int i = 0; i < playerScores.size(); i++) {
			int index = (firstPlayer + i) % playerScores.size();
			if (currentRound.get(i).getSuit() == firstSuit) {
				if (largestValue.compareTo(currentRound.get(i).getValue()) < 0) {
					taker = index;
					largestValue = currentRound.get(i).getValue();
				}
			}
		}
		return taker % playerScores.size();
	}

	int advance(Card c, ArrayList<Card> playoutHand) {
		if (!checkRound(c, playoutHand))
			return -1;
		int playTurn = currentRound.size();
		playCard(c);
		while (validRound()) {
			int index = rng.nextInt(cardsPlayed.invertDeck.size());
			while (isInMyHand(cardsPlayed.invertDeck.get(index), playoutHand)) {
				index = rng.nextInt(cardsPlayed.invertDeck.size());
			}
			playCard(cardsPlayed.invertDeck.get(index));

		}
		int firstPlayer = (playerIndex - playTurn + playerScores.size()) % playerScores.size();
		int points = calculatePoints();
		int taker = findTaker(firstPlayer);
		playerScores.set(taker, playerScores.get(taker) + points);
		int returnpoints = 0;
		if (taker == playerIndex)
			returnpoints = points;
		currentRound.clear();
		if (isGameValid()) {
			while (taker != playerIndex) {
				int index = rng.nextInt(cardsPlayed.invertDeck.size());
				while (isInMyHand(cardsPlayed.invertDeck.get(index), playoutHand)) {
					index = rng.nextInt(cardsPlayed.invertDeck.size());
				}
				playCard(cardsPlayed.invertDeck.get(index));
				taker = (taker + 1) % playerScores.size();
			}
		}
		return returnpoints;
	}
}