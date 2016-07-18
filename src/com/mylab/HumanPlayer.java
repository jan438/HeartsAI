package com.mylab;

import java.util.Scanner;

class HumanPlayer extends Player {
	Scanner sc;

	HumanPlayer(String name) {
		super(name);
		sc = new Scanner(System.in);
		System.out.println("Human player (" + name + ") initialized.");
	}

	boolean setDebug() {
		return true;
	}

	Card performAction(State masterCopy) {
		printHandStubMode();
		boolean flag = true;
		int i = 0;
		while (flag) {
			printHand();
			flag = false;
			System.out.print("\nInput the index of the card you (" + name + ") wish to play:\n> ");
			while (!sc.hasNextInt()) {
				sc.next();
				System.out.println("\nYou did not input a valid integer index! Try again!");
				printHand();
				System.out.print("\nInput the index of the card you (" + name + ") wish to play:\n> ");
			}
			i = sc.nextInt();
			if (i > hand.size() - 1) {
				System.out.println("Invalid card index! Please input a valid number!");
				flag = true;
			}
		}
		System.out.println();
		return hand.remove(i);
	}
}