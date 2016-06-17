package com.mylab;

import java.util.*;
import java.util.ArrayList;

class LookAheadPlayer extends Player {
	ArrayList<Card> playoutHand;
	Random rng;

	LookAheadPlayer(String name) {
		super(name);
		System.out.println("LookAheadPlayer AI (" + name + ") initialized.");
		playoutHand = new ArrayList<Card>(hand);
		for (Card c : hand)
			playoutHand.add(c.copy());
		rng = new Random();
	}

	boolean setDebug() {
		return false;
	}

	boolean hasAllHearts(ArrayList<Card> hand) {
		boolean flag = true;
		for (Card c : hand) {
			if (c.getSuit() != Suit.HEARTS)
				flag = false;
		}
		return flag;
	}

	int playoutGame(State gameCopy, ArrayList<Card> gameHand) {
		int totalpoints = 0;
		while (gameCopy.isGameValid()) {
			Suit firstSuit = getFirstSuit(gameCopy.currentRound);
			SuitRange range = getSuitRange(firstSuit, gameHand);
			if (range.getRange() == 0) {
				int index = rng.nextInt(gameHand.size());
				while (gameCopy.firstInRound() && !gameCopy.hasHeartsBroken
						&& gameHand.get(index).getSuit() == Suit.HEARTS && !hasAllHearts(gameHand)) {
					index = rng.nextInt(gameHand.size());
				}
				totalpoints += gameCopy.advance(gameHand.remove(index), gameHand);
			} else {
				int index = rng.nextInt(range.getRange());
				totalpoints += gameCopy.advance(gameHand.remove(range.startIndex + index), gameHand);
			}
		}
		return totalpoints;
	}

	Card performAction(State masterCopy) {
		playoutHand.clear();
		for (Card c : hand)
			playoutHand.add(c.copy());
		if (masterCopy.firstMove())
			return hand.remove(0);
		printHand();
		Suit firstSuit = getFirstSuit(masterCopy.currentRound);
		SuitRange range = getSuitRange(firstSuit, hand);
		int bestIndex = 0;
		if (range.startIndex != -1)
			bestIndex = range.startIndex;
		int lowestScore = 100;
		if (range.getRange() == 0) {
			for (int i = 0; i < playoutHand.size(); i++) {
				ArrayList<Card> gameHand = new ArrayList<Card>(playoutHand);
				if (masterCopy.firstInRound() && !masterCopy.hasHeartsBroken
						&& gameHand.get(i).getSuit() == Suit.HEARTS)
					break;
				State gameCopy = new State(masterCopy);
				int score = gameCopy.advance(gameHand.remove(i), gameHand);
				score += playoutGame(gameCopy, gameHand);
				if (score < lowestScore) {
					lowestScore = score;
					bestIndex = i;
				}
			}
			return hand.remove(bestIndex);
		}
		for (int i = range.startIndex; i < range.endIndex; i++) {
			ArrayList<Card> gameHand = new ArrayList<Card>(playoutHand);
			if (masterCopy.firstInRound() && !masterCopy.hasHeartsBroken && gameHand.get(i).getSuit() == Suit.HEARTS)
				break;
			State gameCopy = new State(masterCopy);
			int score = gameCopy.advance(gameHand.remove(i), gameHand);
			score += playoutGame(gameCopy, gameHand);
			if (score < lowestScore) {
				lowestScore = score;
				bestIndex = i;
			}
		}
		return hand.remove(bestIndex);
	}
}