package com.github.svetlinzarev.playground.simulation.lottary;

import java.util.Random;

public final class Toto {
    private final int numberOfBalls;
    private final int numberOfSelectedBalls;

    public Toto(int numberOfBalls, int numberOfSelectedBalls) {
        this.numberOfBalls = numberOfBalls;
        this.numberOfSelectedBalls = numberOfSelectedBalls;
    }

    public double findProbabilityOfWinning(int numberOfBallsToGuess, int numberOfSimulations) {
        if (numberOfBallsToGuess > numberOfSelectedBalls) {
            throw new IllegalArgumentException("The number of guessed balls must be less than or equals to the number of selected balls.");
        }
        final Random random = new Random();

        int numberOfGuesses = 0;
        for (int sim = 0; sim < numberOfSimulations; sim++) {
            int ballsGuessed = 0;
            for (int i = 0; i < numberOfSelectedBalls; i++) {
                if (numberOfSelectedBalls - ballsGuessed > random.nextInt(numberOfBalls)) {
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

    private static void printProbabilitiesFor(int numberOfBalls, int numberOfSelectedBalls) {
        System.out.println("################################################################################");
        Toto toto = new Toto(numberOfBalls, numberOfSelectedBalls);
        for (int i = 0; i <= numberOfSelectedBalls; i++) {
            final double probability = toto.findProbabilityOfWinning(i, 10_000_000);
            System.out.printf("Probability of guessing %d balls out of %d/%d is %f\n",
                    i, numberOfSelectedBalls, numberOfBalls, probability * 100);
        }
        System.out.println();
    }
}
