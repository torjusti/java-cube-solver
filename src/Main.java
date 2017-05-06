import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Search EOLineSolver = new Search(Arrays.asList(5, 7),  null);
        System.out.println(EOLineSolver.solve("B' R' B2 R B D' L F2 U2 F R U R' U' L' D2 B U2 B L"));

        Search EOCrossSolver = new Search(Arrays.asList(4, 5, 6, 7),  null);
        System.out.println(EOCrossSolver.solve("B' R' B2 R B D' L F2 U2 F R U R' U' L' D2 B U2 B L"));

        Search CrossSolver = new Search(Arrays.asList(4, 5, 6, 7), Arrays.asList(4, 5, 6, 7));
        System.out.println(CrossSolver.solve("B' R' B2 R B D' L F2 U2 F R U R' U' L' D2 B U2 B L"));
    }
}
