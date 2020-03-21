/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */

/**
 *
 * @author 20182300
 */
public class CompoundSolver implements AlgorithmInterface {
    @Override
    public PackingSolution solve(PackingProblem p){
        long bigBang = System.currentTimeMillis();
        
        // Array of solvers to use in order of non-increasing running-time
        AlgorithmInterface[] solvers;
        
        if (((p.rectangles.length <= 25 && p.largestHeight < 3500 
                && p.largestWidth < 3500) || (p.rectangles.length <= 10 
                && p.largestHeight < 5300 && p.largestWidth < 5300) 
                || (p.rectangles.length <= 4 && p.largestHeight < 8000 
                && p.largestWidth < 8000)) && !p.getSettings().rotation) {
               
            if (p.getSettings().fixed){
                solvers = new AlgorithmInterface[]
                {new BruteForceSolver(), new GlobalMaxRectsSolver(), new BSSF_DESCSS_Solver(), 
                    new BestFitFast(), new SteinbergSolver()};
            } else {
                solvers = new AlgorithmInterface[]
                {new BruteForceSolver(), new GlobalMaxRectsSolver(), new BSSF_DESCSS_Solver(), 
                    new BasicBinPacking(), new BestFitFast(), new SteinbergSolver()};
            }
        } else { 
            if (p.getSettings().fixed){
                solvers = new AlgorithmInterface[]
                {new GlobalMaxRectsSolver1(), new GlobalMaxRectsSolver(), new BSSF_DESCSS_Solver(), new BestFitFast(), new SteinbergSolver()};
            } else {
                solvers = new AlgorithmInterface[]
                {new GlobalMaxRectsSolver1(), new GlobalMaxRectsSolver(), new BSSF_DESCSS_Solver(), new BasicBinPacking(), new BestFitFast(), new SteinbergSolver()};
            }
        }
        
        PackingSolution bestSolution = null;
        
        PackingProblem altP = new PackingProblem(p);
        // seconds used for previous algorithm
        double secondsUsed = 0;
        // seconds left till total time reaches 30
        double secondsLeft = 30 - (System.currentTimeMillis() - bigBang) / 1000;

        boolean useAlt = true;
        for (int i = 0; i < solvers.length && secondsLeft > secondsUsed; i++){
            long startTime = System.currentTimeMillis();
            PackingSolution sol;
            if (useAlt){
                sol = solvers[i].solve(altP);
            } else {
                sol = solvers[i].solve(p);
            }
            long endTime = System.currentTimeMillis();
            long time = endTime - startTime;

            secondsUsed = (double) time / 1000;
            secondsLeft = 30 - secondsUsed;

            System.out.println();
            System.out.println(solvers[i].getClass().getName());
            System.out.println(sol.area());

            if (bestSolution == null || sol.area() < bestSolution.area()){
                bestSolution = sol;
                useAlt = !useAlt;
            }
        }
        
        return bestSolution;
    }
}
