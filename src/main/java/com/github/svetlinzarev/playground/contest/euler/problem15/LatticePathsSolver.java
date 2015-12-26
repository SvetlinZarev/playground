package com.github.svetlinzarev.playground.contest.euler.problem15;


/**
 * Starting in the top left corner of a 2×2 grid, and only
 * being able to move to the right and down, there are
 * exactly 6 routes to the bottom right corner.
 * <p>
 * How many such routes are there through a NxN grid?
 */
public interface LatticePathsSolver {
    long findNumberOfPaths();
}
