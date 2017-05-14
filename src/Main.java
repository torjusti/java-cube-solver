import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static void timeSolve(Search search, String scramble, String description) {
        long start = System.currentTimeMillis();
        System.out.println(description + ": " + search.solve(scramble) + " (" + (System.currentTimeMillis() - start) + " ms)");
    }

    public static void main(String[] args) {
        System.out.println("Initializing solvers");

        long initializationStart = System.currentTimeMillis();

        Search EOLineSolver = new Search(Arrays.asList(5, 7),  Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), null, null);
        Search EOCrossSolver = new Search(Arrays.asList(4, 5, 6, 7), Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), null, null);
        Search CrossSolver = new Search(Arrays.asList(4, 5, 6, 7), Arrays.asList(4, 5, 6, 7), null, null);
        Search FRXCrossSolver = new Search(Arrays.asList(4, 5, 6, 7, 8), Arrays.asList(4, 5, 6, 7, 8), Arrays.asList(4), Arrays.asList(4));

        EOLineSolver.initialize();
        EOCrossSolver.initialize();
        CrossSolver.initialize();
        FRXCrossSolver.initialize();

        System.out.println("Solvers initialized in " + (System.currentTimeMillis() - initializationStart) + " ms");

        Scanner in = new Scanner(System.in);

        do {
            System.out.println("Scramble:");
            String scramble = in.nextLine();

            if (!Scrambles.validateSequence(scramble)) {
                System.out.println("Unable to parse scramble");
            } else {
                timeSolve(EOLineSolver, scramble, "EOLine");
                timeSolve(EOCrossSolver, scramble, "EOCross");
                timeSolve(CrossSolver, scramble, "Cross");
                timeSolve(FRXCrossSolver, scramble, "XCross");
            }
        } while (in.hasNext());

        in.close();
    }
}
