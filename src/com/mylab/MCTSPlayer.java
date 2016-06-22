package com.mylab;

import java.util.*;
import java.util.ArrayList;

class MCTSPlayer extends Player {
	ArrayList<Card> playoutHand;
	Random rng;
	final int noIterations = 26;
	final int expansionDepth = 3;
	Node root;

	public class Node {
		State thisState;
		ArrayList<Card> currentHand;
		int bestReward;
		int visitCount;
		Node parent;
		Node[] children;
		int depth;

		Node(State s, ArrayList<Card> hand, Node par) {
			thisState = s;
			currentHand = hand;
			bestReward = 0;
			visitCount = 0;
			parent = par;
			children = new Node[hand.size()];
			if (par != null)
				depth = par.depth + 1;
			else
				depth = 0;
		}
	}

	MCTSPlayer(String name) {
		super(name);
		System.out.println("MCTSPlayer AI (" + name + ") initialized.");
		playoutHand = new ArrayList<Card>(hand);
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

	int runMCTS(State origState) {
		root = new Node(origState, playoutHand, null);
		for (int i = 0; i < noIterations; i++) {
			Node expanded = treePolicy(root);
			if ((i == noIterations - 1) && (expanded.currentHand.size() == 0)) {
				System.out.println(
						"Iteration: " + i + " size:" + expanded.thisState.currentRound.size() + " " + " playerscores:"
								+ expanded.thisState.playerScores + " " + super.cardstosymbols(expanded.currentHand));
				Hearts.writer.println(
						"Iteration: " + i + " size:" + expanded.thisState.currentRound.size() + " " + " playerscores:"
								+ expanded.thisState.playerScores + " " + super.cardstosymbols(expanded.currentHand));
			}
			int valueChange = assignReward(expanded);
			backProp(expanded, valueChange);
			if ((i == noIterations - 1) && (expanded.currentHand.size() == 0)) {
				System.out.println(
						"Iteration: " + i + " size:" + expanded.thisState.currentRound.size() + " " + " playerscores:"
								+ expanded.thisState.playerScores + " " + super.cardstosymbols(expanded.currentHand));
				Hearts.writer.println(
						"Iteration: " + i + " size:" + expanded.thisState.currentRound.size() + " " + " playerscores:"
								+ expanded.thisState.playerScores + " " + super.cardstosymbols(expanded.currentHand));
			}
		}
		return bestRewardChild(root);
	}

	Node treePolicy(Node roNode) {
		Node thisNode = roNode;
		while (thisNode.thisState.isGameValid() && expansionDepth > thisNode.depth) {
			Suit firstSuit = getFirstSuit(thisNode.thisState.currentRound);
			SuitRange range = getSuitRange(firstSuit, thisNode.currentHand);
			int firstIndex = range.startIndex;
			int lastIndex = range.endIndex;
			System.out.println("Range: " + firstIndex + "-" + lastIndex);
			if (firstSuit == null) {
				if (thisNode.thisState.hasHeartsBroken || hasAllHearts(thisNode.currentHand)) {
					firstIndex = 0;
					lastIndex = thisNode.currentHand.size();
					System.out.println("Hearts Broken First Range: " + firstIndex + "-" + lastIndex);
				} else {
					SuitRange heartsRange = getSuitRange(Suit.HEARTS, thisNode.currentHand);
					if (heartsRange.startIndex == -1) {
						firstIndex = 0;
						lastIndex = thisNode.currentHand.size();
						System.out.println("Hearts Only Forced Range: " + firstIndex + "-" + lastIndex);
					} else {
						firstIndex = 0;
						lastIndex = heartsRange.startIndex;
						System.out.println("First Play Range: " + firstIndex + "-" + lastIndex);
					}
				}
			} else if (firstIndex == -1) {
				firstIndex = 0;
				lastIndex = thisNode.currentHand.size();
				System.out.println("Any Card Range: " + firstIndex + "-" + lastIndex);
			}
			for (int i = firstIndex; i < lastIndex; i++) {
				if (thisNode.children[i] == null) {
					return expandTree(thisNode, i);
				}
			}
			thisNode = bestChild(thisNode, 0.1);
		}
		return thisNode;
	}

	Node expandTree(Node roNode, int childNo) {
		State childState = new State(roNode.thisState);
		ArrayList<Card> childHand = new ArrayList<Card>(roNode.currentHand);
		Card removedcard = childHand.remove(childNo);
		int debug = childState.advance(removedcard, childHand);
		if (debug == -1) {
			System.out.println("Error, we've made a mistake.");
		}
		roNode.children[childNo] = new Node(childState, childHand, roNode);
		return roNode.children[childNo];
	}

	Node bestChild(Node someNode, double weight) {
		int bestindex = 0;
		double bestValue = -Double.MAX_VALUE;
		int totalVisits = someNode.visitCount;
		for (int i = 0; i < someNode.children.length; i++) {
			if (someNode.children[i] != null) {
				Node child = someNode.children[i];
				int reward = child.bestReward;
				int childVisits = child.visitCount;
				double thisValue = (reward) / (childVisits)
						+ weight * Math.sqrt((2 * Math.log(totalVisits)) / childVisits);
				if (thisValue > bestValue) {
					bestValue = thisValue;
					bestindex = i;
				}
			}
		}
		return someNode.children[bestindex];
	}

	int assignReward(Node baseNode) {
		State finalState = new State(baseNode.thisState);
		ArrayList<Card> finalHand = new ArrayList<Card>(baseNode.currentHand);
		while (finalState.isGameValid()) {
			Suit firstSuit = getFirstSuit(finalState.currentRound);
			SuitRange range = getSuitRange(firstSuit, finalHand);
			if (firstSuit == null) {
				int debug = -1;
				while (debug == -1) {
					int index = rng.nextInt(finalHand.size());
					Card playCard = finalHand.remove(index);
					debug = finalState.advance(playCard, finalHand);
					if (debug == -1) {
						finalHand.add(playCard);
						Collections.sort(finalHand);
					}
				}
			} else {
				if (range.getRange() == 0) {
					Card removedcard = finalHand.remove(rng.nextInt(finalHand.size()));
					finalState.advance(removedcard, finalHand);
				} else {
					int index = rng.nextInt(range.getRange());
					Card removedcard = finalHand.remove(range.startIndex + index);
					finalState.advance(removedcard, finalHand);
				}
			}
		}
		int points = finalState.getScore();
		int score = 26 - points;
		return score;
	}

	void backProp(Node baseNode, double value) {
		Node no = baseNode;
		while (no != null) {
			no.visitCount++;
			no.bestReward += value;
			no = no.parent;
		}
	}

	int bestRewardChild(Node roNode) {
		int highestReward = -999999;
		int bestChildNo = 0;
		for (int i = 0; i < roNode.children.length; i++) {
			if (roNode.children[i] != null) {
				if (roNode.children[i].bestReward > highestReward) {
					highestReward = roNode.children[i].bestReward;
					bestChildNo = i;
				}
			}
		}
		return bestChildNo;
	}

	Card performAction(State masterCopy) {
		if (masterCopy.firstMove())
			return hand.remove(0);
		playoutHand.clear();
		for (Card c : hand)
			playoutHand.add(c.copy());
		printHand();
		if (hand.size() == 1)
			return hand.remove(0);
		return hand.remove(runMCTS(masterCopy));
	}
}