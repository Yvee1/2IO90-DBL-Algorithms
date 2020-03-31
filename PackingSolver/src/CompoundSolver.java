
import java.util.ArrayList;
/**
 *
 * @author Steven van den Broek and Pim van Leeuwen 
 */
public class CompoundSolver implements AlgorithmInterface {
    boolean debug;

    PackingSolution bestSolution = null;
    String bestSolver = null;

    PackingProblem prob;
    
    CompoundSolver(){
        debug = false;
    }
    
    CompoundSolver(boolean debug){
        this.debug = debug;
    }
    
    PackingSolution sol;
    
    @Override
    public PackingSolution solve(PackingProblem p){
        //System.err.println(p.settings.rotation);

        this.prob = p;

        /* Initially run BestFitFast. */
        bestSolution = new BestFitFast().solve(new PackingProblem(p));
        bestSolver = "BestFitFast";
        if (debug) {
            System.out.println();
            System.out.println(bestSolver);
            System.out.print("Area: ");
            System.out.println(bestSolution.area());
        }

        long bff_area = bestSolution.area();

        boolean steinberg, globmaxrect, globmaxrect1, binpack, downscale, maxrects, brute;
        steinberg = true;
        globmaxrect = globmaxrect1 = binpack = downscale = maxrects = brute = false;

        /* Everything is safe for 25 or fewer rectangles. */
        if (p.rectangles.length <= 25) {
            binpack = globmaxrect = globmaxrect1 = downscale = maxrects = true;
            if (bff_area == 2112) { brute = true; downscale = false; }
        }
        else {
            /* Tweak algorithms for 10000 rectangles. */
            if (p.settings.rotation && p.settings.fixed) {
                binpack = true;
            }
            else if (p.settings.rotation && !p.settings.fixed) {
                globmaxrect = bff_area <= 100000000000L;
                binpack = !globmaxrect;
            }
            else if (!p.settings.rotation && p.settings.fixed) {
                binpack = true;
            }
            else if (!p.settings.rotation && !p.settings.fixed) {
                globmaxrect1 = bff_area <= 10000000000L;
                binpack = !globmaxrect1;
            }
        }

        if (steinberg) { run_alg(new SteinbergSolver(), false); }
        if (downscale) { run_alg(new DownScaleSolver(), true); }
        if (brute) { run_alg(new BruteForceSolver(), false); }
        if (binpack) { run_alg(new BasicBinPacking(), false); }
        if (globmaxrect) { run_alg(new GlobalMaxRectsSolver(27), false); }
        if (globmaxrect1) { run_alg(new GlobalMaxRectsSolver1(27), false); }
        if (maxrects) {
            MaxRectsSortingSubroutine[] mrsss = { new DESCSS(), new DESCLS(), new DESCPERIM(), new DESCA() };
            MaxRectsHeuristicSubroutine[] mrhss = { new BSSF(), new BLSF(), new BL(), new BAF() };

            for (MaxRectsSortingSubroutine mrss : mrsss){
                for (MaxRectsHeuristicSubroutine mrhs : mrhss){
                    run_alg(new MaxRectsSolver(mrhs, mrss), false);
                }
            }
        }

//
//        // Array of solvers to use in order of non-increasing running-time
//        ArrayList<AlgorithmInterface> solvers = new ArrayList<>();
//
////        if (p.getSettings().fixed){
////            solvers.add(new BestFitFast());
////        }
//        solvers.add(new SteinbergSolver());
//
//        if (bestSolution.area() < 1000000) {
//            solvers.add(new GlobalMaxRectsSolver1(7));
//            solvers.add(new GlobalMaxRectsSolver(7));
//        } else if (bestSolution.area() < 2000000) {
//            solvers.add(new GlobalMaxRectsSolver(18));
//        }
//
////        solvers.add(new GlobalMaxRectsSolver2());
//
//        if (p.rectangles.length <= 25){
//            MaxRectsSortingSubroutine[] mrsss = new MaxRectsSortingSubroutine[]
//            { new DESCSS(), new DESCLS(), new DESCPERIM(), new DESCA() };
//
//            MaxRectsHeuristicSubroutine[] mrhss = new MaxRectsHeuristicSubroutine[]
//            { new BSSF(), new BLSF(), new BL(), new BAF() };
//
//            for (MaxRectsSortingSubroutine mrss : mrsss){
//                for (MaxRectsHeuristicSubroutine mrhs : mrhss){
//                    solvers.add(new MaxRectsSolver(mrhs, mrss));
//                }
//            }
//        }
//
//        /* Run the DownScaleSolver when possible. */
//        if (p.rectangles.length <= 25) {
//            solvers.add(new DownScaleSolver(false));
//            solvers.add(new BasicBinPacking());
//        }
//
//        long startTime = System.currentTimeMillis();
//        long endTime = startTime + 20000;
//
//        for (int i = 0; i < solvers.size() && System.currentTimeMillis() < endTime; i++){
//            long solverStartTime = System.currentTimeMillis();
//            try {
//                sol = solvers.get(i).solve(new PackingProblem(p));
//            } catch (InterruptedException e) {
//                System.err.print(solvers.get(i));
//                continue;
//            }
//            long solverEndTime = System.currentTimeMillis();
//            long time = solverEndTime - solverStartTime;
//
//            double secondsUsed = (double) time / 1000;
//
//            String solverName = solvers.get(i).getClass().getName();
//            if (solverName.equals("MaxRectsSolver")){
//                MaxRectsSolver solver = (MaxRectsSolver) solvers.get(i);
//                solverName = String.format("MAXRECTS_%s_%s", solver.mrhs.getClass().getName(), solver.mrss.getClass().getName());
//            }
//
//            if (debug){
//                System.out.println();
//                System.out.println(solverName);
//
//                System.out.print("Area: ");
//                System.out.println(sol.area());
//                System.out.print("Seconds used: ");
//                System.out.println(secondsUsed);
//            }
//
////            if (bestSolution == null || sol.area() < bestSolution.area() && !bestSolution.hasOverlap()){
//            if (bestSolution == null || sol.area() < bestSolution.area()){
//                bestSolution = sol;
//                bestSolver = solverName;
//            }
//
//
//        }
//
//        if (debug){
//            System.out.println("------");
//            System.out.format("Best solver: %s with area %d", bestSolver, bestSolution.area());
//            System.out.println();
//        }
        
        return bestSolution;
    }

    private void run_alg(AlgorithmInterface alg, boolean verify) {
        long start_t = System.currentTimeMillis();

        PackingSolution sol;
        try {
            sol = alg.solve(prob);
        } catch (Exception e) {
            return;
        }

        /* If the solution is not valid, skip. */
        if (verify && !sol.isValid()) { return; }

        if (sol.area() < bestSolution.area()) {
            bestSolution = sol;
        }

    }
    
}
