package com.mylab;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Hearts {
	static PrintWriter writer;

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		writer = new PrintWriter("/home/jan/heartAI.txt", "UTF-8");
		System.out.println("Welcome to Hearts version 1.1.0.");
		Deck thing = new Deck();
		Player p1 = new LowPlayAI("WellsLowPlay");
		Player p2 = new RandomPlayAI("JaiRandomPlay");
		Player p3 = new LookAheadPlayer("AntLookAhead");
		Player p4 = new MCTSPlayer("JulianMCTS");
		int numberOfGames = 1;
		Game round = new Game(thing, p1, p2, p3, p4);
		for (int i = 1; i <= numberOfGames; i++) {
			System.out.println("\n--------------------------------------------");
			System.out.println("--------------------------------------------");
			System.out.println("--------------------------------------------");
			System.out.println("Playing Game #" + i);
			System.out.println("--------------------------------------------");
			System.out.println("--------------------------------------------");
			System.out.println("--------------------------------------------\n");
			round.playNewGame();
		}
		writer.close();
	}
}