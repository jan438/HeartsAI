package com.mylab;

import java.util.*;

class RandomPlayAI extends Player {
	Random rng;

	RandomPlayAI(String name) {
		super(name);
		if (!Hearts.stubmode)
			rng = new Random();
		else
			rng = new Random(3L);
		System.out.println("Random Play AI (" + name + ") initialized.");
	}

	boolean setDebug() {
		return false;
	}

	Card performAction(State masterCopy) {
		printHandStubMode();
		if (masterCopy.firstMove())
			return hand.remove(0);
		printHand();
		Suit firstSuit = getFirstSuit(masterCopy.currentRound);
		if (firstSuit == null) {
			int index = rng.nextInt(hand.size());
			return hand.remove(index);
		}
		SuitRange range = getSuitRange(firstSuit, hand);
		if (range.getRange() == 0)
			return hand.remove(rng.nextInt(hand.size()));
		int index = rng.nextInt(range.getRange());
		return hand.remove(range.startIndex + index);
	}
}