
interface AlgorithmInterface {

    /**
     * Solve p.
     * @param p The problem that is to be solved.
     * @param timeRemaining the time that this method may take at most
     * @return A packing solution.
     */
    public PackingSolution solve(PackingProblem p) throws InterruptedException;

}