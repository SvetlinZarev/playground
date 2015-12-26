package com.github.svetlinzarev.playground.contest.euler.problem15;

public final class LatticePathsCombinatoricsSolver implements LatticePathsSolver {
    private final int gridSize;

    public LatticePathsCombinatoricsSolver() {
        this(20);
    }

    public LatticePathsCombinatoricsSolver(int gridSize) {
        this.gridSize = gridSize;
    }


    /**
     * It's noticeable that the length of each path is 2N - N rows and N columns.
     * So if we find all rows then the places for the columns will be given.
     * Hence we can calculate the number of different paths as a combination of
     * N out of 2N possible places.
     * <p>
     * C(n,k) = V(n,k)/P(k) = (n! / (n-k)!) / k! = n! / k!(n-k)!
     *
     * @return the number of different paths
     */
    @Override
    public long findNumberOfPaths() {
        double numberOfPaths = 1;

        //GridSize == k in the formula
        //PathLength == n in the formula (n == 2k by definition)
        final long pathLength = 2 * gridSize;
        for (int i = 0; i < gridSize; i++) {
            numberOfPaths *= pathLength - i;

            /*
             * We use (i + 1) instead of (gridSize - i) because of
             * integer arithmetic issues. From mathematical PoV
             * there is  no difference
             */
            numberOfPaths /= i + 1;
        }
        return (long) numberOfPaths;
    }
}
