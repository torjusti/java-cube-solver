import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Search EOLineSolver = new Search(Arrays.asList(5, 7),  Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
        Search EOCrossSolver = new Search(Arrays.asList(4, 5, 6, 7), Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
        Search CrossSolver = new Search(Arrays.asList(4, 5, 6, 7), Arrays.asList(4, 5, 6, 7));

        System.out.println("Initializing solvers");
        EOLineSolver.initialize();
        EOCrossSolver.initialize();
        CrossSolver.initialize();
        System.out.println("Solvers initialized");

        List<String> scrambles = Arrays.asList(
                "F U2 F2 R2 B' L2 D2 B' L' F2 D' L R F' D' U2 B'",
                "R2 B2 R2 B2 U F2 D2 U B2 D' B L F2 L F L D R2 B' R",
                "R U L D B R2 F' L F D' F2 L2 D2 B' R2 D2 B' U2 F L2",
                "L2 D2 R2 B2 U F2 D' U' L2 U' F2 R F' L' U2 B2 L2 R D'",
                "B2 F2 D B2 L2 U L2 U B2 D2 B2 L D' B U' F2 D2 U2 L D' U",
                "D L2 F2 B' D2 R2 U F R F' L2 B' L2 U2 F' U2 F R2 B L2",
                "D2 B2 L U2 L' U2 F2 R2 U2 R' F' U B F' U F D' L2 B2",
                "R D B2 R2 D2 L2 D B2 F2 R B D R' B F' D2 U' B",
                "L2 B' L F' U2 B U' D F U R2 U' F2 D R2 U' F2 D2 R2",
                "B' F2 R2 U L2 F2 D' R2 U L2 D2 R' F' L D B F' D2 U' L D'"
        );

        System.out.println("Solving EOLine");

        for (String scramble : scrambles) {
            long start = System.nanoTime();
            System.out.println(EOLineSolver.solve(scramble) + ", completed in " + String.valueOf(System.nanoTime() - start));
        }

        System.out.println("Solving Cross");

        for (String scramble : scrambles) {
            long start = System.nanoTime();
            System.out.println(CrossSolver.solve(scramble) + ", completed in " + String.valueOf(System.nanoTime() - start));
        }

        System.out.println("Solving EOCross");

        for (String scramble : scrambles) {
            long start = System.nanoTime();
            System.out.println(EOCrossSolver.solve(scramble) + ", completed in " + String.valueOf(System.nanoTime() - start));
        }
    }
}
