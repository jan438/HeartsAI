package com.mylab;

class LowPlayAI extends Player {
	LowPlayAI(String name) {
		super(name);
		System.out.println("Low Play AI (" + name + ") initialized.");
	}

	boolean setDebug() {
		return false;
	}

	Card performAction(State masterCopy) {
		printHand();
		Suit firstSuit = getFirstSuit(masterCopy.currentRound);
		if (firstSuit == null)
			return hand.remove(0);
		for (int i = 0; i < hand.size(); i++)
			if (hand.get(i).getSuit() == firstSuit)
				return hand.remove(i);
		return hand.remove(0);
	}
}