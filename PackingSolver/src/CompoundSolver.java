
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Steven van den Broek
 */
public class CompoundSolver implements AlgorithmInterface {
    boolean debug;
    
    CompoundSolver(){
        debug = false;
    }
    
    CompoundSolver(boolean debug){
        this.debug = debug;
    }
    
    @Override
    public PackingSolution solve(PackingProblem p){
        long bigBang = System.currentTimeMillis();
        
        // Array of solvers to use in order of non-increasing running-time
        ArrayList<AlgorithmInterface> solvers = new ArrayList<>();
        solvers.add(new BestFitFast());
        solvers.add(new SteinbergSolver());
//        solvers.add(new GlobalMaxRectsSolver());
//        solvers.add(new GlobalMaxRectsSolver1());
//        solvers.add(new GlobalMaxRectsSolver2());
        
        if (p.rectangles.length <= 25){
            MaxRectsSortingSubroutine[] mrsss = new MaxRectsSortingSubroutine[]
            { new DESCSS(), new DESCLS(), new DESCPERIM(), new DESCA() };

            MaxRectsHeuristicSubroutine[] mrhss = new MaxRectsHeuristicSubroutine[]
            { new BSSF(), new BLSF(), new BL(), new BAF() };

            for (MaxRectsSortingSubroutine mrss : mrsss){
                for (MaxRectsHeuristicSubroutine mrhs : mrhss){
                    solvers.add(new MaxRectsSolver(mrhs, mrss));
                }
            }
        } else {
//            solvers.add(new MaxRectsSolver(new BSSF(), new DESCSS()));
        }
        
        if (((p.rectangles.length <= 25 && p.largestHeight < 3500 
                && p.largestWidth < 3500) || (p.rectangles.length <= 10 
                && p.largestHeight < 5300 && p.largestWidth < 5300) 
                || (p.rectangles.length <= 4 && p.largestHeight < 8000 
                && p.largestWidth < 8000)) && !p.getSettings().rotation) {
               
            solvers.add(0, new BruteForceSolver());
        }
        
        if (!p.getSettings().fixed){
            solvers.add(new BasicBinPacking());
        }
        
        PackingSolution bestSolution = null;
        String bestSolver = null;
        
        PackingProblem altP = new PackingProblem(p);
        // seconds used for previous algorithm
        double secondsUsed = 0;
        // seconds left till total time reaches 30
        double secondsLeft = 30 - (System.currentTimeMillis() - bigBang) / 1000;

        boolean useAlt = true;
//        for (int i = 0; i < solvers.size() && secondsLeft > secondsUsed; i++){
        for (int i = 0; i < solvers.size(); i++){
            long startTime = System.currentTimeMillis();
            PackingSolution sol;
            if (useAlt){
                sol = solvers.get(i).solve(altP);
            } else {
                sol = solvers.get(i).solve(p);
            }
            long endTime = System.currentTimeMillis();
            long time = endTime - startTime;

            secondsUsed = (double) time / 1000;
            secondsLeft = 30 - secondsUsed;
            
            String solverName = solvers.get(i).getClass().getName();
            if (solverName.equals("MaxRectsSolver")){
                MaxRectsSolver solver = (MaxRectsSolver) solvers.get(i);
                solverName = String.format("MAXRECTS_%s_%s", solver.mrhs.getClass().getName(), solver.mrss.getClass().getName());
            } 
            
            if (debug){
                System.out.println();
                System.out.println(solverName);

                System.out.print("Area: ");
                System.out.println(sol.area());
                System.out.print("Seconds used: ");
                System.out.println(secondsUsed);
            }

            if (bestSolution == null || sol.area() < bestSolution.area()){
                bestSolution = sol;
                bestSolver = solverName;
                useAlt = !useAlt;
            }
        }
        
        if (debug){
            System.out.println("------");
            System.out.format("Best solver: %s with area %d", bestSolver, bestSolution.area());
            System.out.println();
        }
        
        return bestSolution;
    }
}
