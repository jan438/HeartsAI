package com.mylab;

import java.util.ArrayList;
import java.util.Scanner;

class Game {
	boolean debug;
	ArrayList<Player> playerOrder;
	int firstPlayer;
	Deck cardsPlayed;
	ArrayList<Card> currentRound;
	boolean twoClubsPlayed;
	boolean hasHeartsBroken;
	ArrayList<Integer> playerScores;
	Scanner in;
	String s;

	Game(Deck deck, Player p1, Player p2, Player p3, Player p4) {
		debug = false;
		playerOrder = new ArrayList<Player>();
		playerOrder.add(p1);
		playerOrder.add(p2);
		playerOrder.add(p3);
		playerOrder.add(p4);
		for (Player p : playerOrder)
			if (p.setDebug())
				debug = true;
		firstPlayer = 0;
		cardsPlayed = deck;
		currentRound = new ArrayList<Card>();
		twoClubsPlayed = false;
		hasHeartsBroken = false;
		playerScores = new ArrayList<Integer>();
		in = new Scanner(System.in);
	}

	void initNewGame() {
		cardsPlayed.shuffleDeck();
		for (Player p : playerOrder) {
			p.clearHand();
		}
		for (int i = 0; i < 13; i++) {
			for (Player p : playerOrder) {
				p.addToHand(cardsPlayed.drawTop());
			}
		}
		for (Player p : playerOrder) {
			p.sortHand();
		}
		for (int i = 0; i < 4; i++) {
			if (playerOrder.get(i).hasTwoOfClubs()) {
				firstPlayer = i;
				break;
			}
		}
		System.out.println(playerOrder.get(firstPlayer).getName() + " has the two of clubs and will play first.\n");
		currentRound.clear();
		playerScores.clear();
		playerScores.add(0);
		playerScores.add(0);
		playerScores.add(0);
		playerScores.add(0);
		System.out.println();
		System.out.flush();
	}

	void checkHeartsOnly(int index) {
		boolean flag = playerOrder.get(index).hasAllHearts();
		if (flag)
			hasHeartsBroken = true;
	}

	void printRound(int firstPlayer) {
		System.out.println("\nCards played this round:");
		System.out.println("------------------------");
		if (currentRound.size() == 0) {
			System.out.println("No cards have been played this round.");
		}
		for (int i = 0; i < currentRound.size(); i++) {
			int index = (i + firstPlayer) % playerOrder.size();
			System.out.format("%15s", playerOrder.get(index).getName());
			System.out.print(" played ");
			System.out.format("%3s\n", currentRound.get(i).printCardShort());
		}
	}

	boolean checkRound(Card playedCard, int index) {
		Card twoClubs = new Card(Suit.CLUBS, Value.TWO);
		if (!twoClubsPlayed && !playedCard.equals(twoClubs)) {
			System.out.println("You must play the Two of Clubs to start the game.");
			return false;
		}
		if (!twoClubsPlayed && playedCard.equals(twoClubs))
			twoClubsPlayed = true;
		if (currentRound.size() == 0) {
			if (!hasHeartsBroken && playedCard.getSuit() == Suit.HEARTS) {
				System.out.println("Hearts has not broken yet. You cannot play a Heart suit.");
				return false;
			}
			return true;
		}
		Suit firstSuit = currentRound.get(0).getSuit();
		if (playerOrder.get(index).checkSuit(firstSuit) && playedCard.getSuit() != firstSuit) {
			System.out.println("You still have a card that is " + firstSuit + ". You must play that first.");
			return false;
		}
		if (playedCard.getSuit() == Suit.HEARTS && !hasHeartsBroken) {
			System.out.println("Hearts has been broken!");
			hasHeartsBroken = true;
		}
		return true;
	}

	int findTaker(int firstPlayer) {
		Suit firstSuit = currentRound.get(0).getSuit();
		Value largestValue = currentRound.get(0).getValue();
		int taker = firstPlayer;
		for (int i = 0; i < playerOrder.size(); i++) {
			int index = (firstPlayer + i) % playerOrder.size();
			if (currentRound.get(i).getSuit() == firstSuit) {
				if (largestValue.compareTo(currentRound.get(i).getValue()) < 0) {
					taker = index;
					largestValue = currentRound.get(i).getValue();
				}
			}
		}
		return taker % playerOrder.size();
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

	void printPoints() {
		System.out.println("Points received this game:");
		System.out.println("--------------------------");
		for (int i = 0; i < playerOrder.size(); i++) {
			System.out.println(playerOrder.get(i).getName() + " has " + playerScores.get(i) + " points.");
		}
		System.out.println();
	}

	void printWinner() {
		int smallestScore = playerOrder.get(0).getPoints();
		int index = 0;
		for (int i = 0; i < playerOrder.size(); i++) {
			if (smallestScore > playerOrder.get(i).getPoints()) {
				index = i;
				smallestScore = playerOrder.get(i).getPoints();
			}
		}
		System.out.println(playerOrder.get(index).getName() + " is in the lead after this round.\n");
	}

	void printTotalPoints() {
		System.out.println("Total cumulative points between all games:");
		System.out.println("------------------------------------------");
		for (Player p : playerOrder) {
			System.out.println(p.getName() + " has " + p.getPoints() + " points.");
		}
		System.out.println();
	}

	void shotTheMoon() {
		int index = -1;
		for (int i = 0; i < playerScores.size(); i++) {
			if (playerScores.get(i) == 26) {
				System.out.println(playerOrder.get(i).getName() + " shot the moon!");
				index = i;
			}
		}
		if (index > -1) {
			for (int i = 0; i < playerOrder.size(); i++) {
				if (i != index) {
					playerOrder.get(i).addPoints(26);
					playerScores.set(i, 26);
				} else {
					playerOrder.get(i).addPoints(-26);
					playerScores.set(i, 0);
				}
			}
			if (playerOrder.get(index).getPoints() < 0)
				playerOrder.get(index).clearPlayer();
		}
	}

	void playNewGame() {
		initNewGame();
		for (int i = 1; i < 14; i++) {
			System.out.println("--------------------------------------------");
			System.out.println("Round #" + i + ":");
			System.out.println("--------------------------------------------");
			currentRound.clear();
			for (int j = 0; j < 4; j++) {
				int index = (j + firstPlayer) % playerOrder.size();
				if (debug)
					printRound(firstPlayer); // for debugging: print the cards
												// that were played this round
				if (j == 0)
					checkHeartsOnly(index); // if this is the first player this
											// round, check if only hearts
				boolean validPlay = false;
				Card playedCard = null;
				String name = playerOrder.get(index).getName();
				State gameCopy = new State(cardsPlayed, currentRound, playerScores, hasHeartsBroken, index, name);
				while (!validPlay) {
					playedCard = playerOrder.get(index).performAction(gameCopy);
					validPlay = checkRound(playedCard, index);
					if (!validPlay) {
						System.out.println("This was an invalid play. Please pick a valid card.");
						playerOrder.get(index).addToHand(playedCard);
						playerOrder.get(index).sortHand();
					}
				}
				System.out.println(playerOrder.get(index).getName() + " played " + playedCard.printCard() + ".");
				currentRound.add(playedCard);
				cardsPlayed.restockDeck(playedCard);
				System.out.println();
				System.out.flush();
			}
			if (debug) {
				System.out.println("--------------------------------------------");
				System.out.println("Round " + i + " Summary:");
				System.out.println("--------------------------------------------");
				printRound(firstPlayer);
			}
			firstPlayer = findTaker(firstPlayer);
			int points = calculatePoints();
			playerScores.set(firstPlayer, playerScores.get(firstPlayer) + points);
			playerOrder.get(firstPlayer).addPoints(points);
			if (debug) {
				System.out.println("\n" + playerOrder.get(firstPlayer).getName() + " played the highest card "
						+ "and took " + points + " points this round.\n");
				printPoints();
			}
		}
		shotTheMoon();
		System.out.println("------------------------------------------");
		System.out.println("Game Summary:");
		System.out.println("------------------------------------------\n");
		printPoints();
		printWinner();
		printTotalPoints();
//		System.out.println("Press ENTER to start the next game.");
//		s = in.nextLine();
		System.out.println();
		System.out.flush();
	}
}
