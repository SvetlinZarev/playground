package com.github.svetlinzarev.playground.contest.euler.problem15;

public class LatticePathsDynamicProgrammingSolver implements LatticePathsSolver {
    private final int gridSize;
    private final long[][] cache;

    public LatticePathsDynamicProgrammingSolver() {
        this(4);
    }

    public LatticePathsDynamicProgrammingSolver(int gridSize) {
        this.gridSize = gridSize;
        cache = new long[gridSize + 1][gridSize + 1];
    }

    @Override
    public long findNumberOfPaths() {
        //Init the boundary conditions data
        for (int i = 0; i < gridSize; i++) {
            //Only 1 possible path for each rightmost cell in the grid
            cache[i][gridSize] = 1;
            //Only 1 possible path for each bottom-most cell in the grid
            cache[gridSize][i] = 1;
        }

        for (int r = gridSize - 1; r >= 0; r--) {
            for (int c = gridSize - 1; c >= 0; c--) {
                cache[r][c] = cache[r][c + 1] + cache[r + 1][c];
            }
        }

        return cache[0][0];
    }
}
