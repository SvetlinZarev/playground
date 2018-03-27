package com.github.svetlinzarev.playground.simulation.lottary;

import com.github.svetlinzarev.playground.util.prng.RandomNumberGenerator;
import com.github.svetlinzarev.playground.util.prng.XoRoShiRo128Plus;

public final class Toto {
    private static final int NUMBER_OF_SIMULATIONS = 10_000_000;
    private final int numberOfBalls;
    private final int numberOfBallsToSelect;

    public Toto(int numberOfBalls, int numberOfBallsToSelect) {
        this.numberOfBalls = numberOfBalls;
        this.numberOfBallsToSelect = numberOfBallsToSelect;
    }

    public double findProbabilityOfWinning(int numberOfBallsToGuess, int numberOfSimulations) {
        if (numberOfBallsToGuess > numberOfBallsToSelect) {
            throw new IllegalArgumentException("The number of guessed balls must be less than or equals to the number of balls to select.");
        }
        final RandomNumberGenerator prng = new XoRoShiRo128Plus();

        int numberOfGuesses = 0;
        for (int sim = 0; sim < numberOfSimulations; sim++) {
            int ballsGuessed = 0;
            for (int selectedBalls = 0; selectedBalls < numberOfBallsToSelect; selectedBalls++) {
                if (numberOfBallsToSelect - ballsGuessed > prng.nextValue(0, numberOfBalls - selectedBalls)) {
                    ballsGuessed++;
                }
            }

            if (ballsGuessed == numberOfBallsToGuess) {
                numberOfGuesses++;
            }
        }
        return ((double) numberOfGuesses) / numberOfSimulations;
    }

    public static void main(String[] args) {
        printProbabilitiesFor(49, 6);
        printProbabilitiesFor(42, 6);
        printProbabilitiesFor(35, 5);
    }

    private static void printProbabilitiesFor(int numberOfBalls, int numberOfBallsToSelect) {
        System.out.println("################################################################################");
        Toto toto = new Toto(numberOfBalls, numberOfBallsToSelect);
        for (int i = 0; i <= numberOfBallsToSelect; i++) {
            final double probability = toto.findProbabilityOfWinning(i, NUMBER_OF_SIMULATIONS);
            System.out.printf("Probability of guessing %d balls out of %d/%d is %.6e\n",
              i, numberOfBallsToSelect, numberOfBalls, probability);
        }
        System.out.println();
    }
}
