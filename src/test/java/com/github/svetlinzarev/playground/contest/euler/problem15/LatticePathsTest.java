package com.github.svetlinzarev.playground.contest.euler.problem15;

import org.junit.Assert;
import org.junit.Test;


public class LatticePathsTest {
    private static final int GRID_SIZE = 20;
    private static final long EXPECTED_SOLUTION = 137846528820L;

    @Test
    public void testSolveUsingCombinatorialSolver() throws Exception {
        testSolve(new LatticePathsCombinatoricsSolver(GRID_SIZE));
    }

    @Test
    public void testSolveUsingDynamicProgrammingSolver() throws Exception {
        testSolve(new LatticePathsDynamicProgrammingSolver(GRID_SIZE));
    }

    private void testSolve(LatticePathsSolver solver) {
        Assert.assertEquals(EXPECTED_SOLUTION, solver.findNumberOfPaths());
    }
}