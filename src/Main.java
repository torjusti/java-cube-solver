import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Search EOLineSolver = new Search(Arrays.asList(5, 7),  null);
        System.out.println(EOLineSolver.solve("B' D2 B' U2 F' D2 U2 B' U2 F2 R B L2 B2 D F R' B L"));

        Search EOCrossSolver = new Search(Arrays.asList(4, 5, 6, 7),  null);
        System.out.println(EOCrossSolver.solve("B' D2 B' U2 F' D2 U2 B' U2 F2 R B L2 B2 D F R' B L"));

        Search CrossSolver = new Search(Arrays.asList(4, 5, 6, 7), Arrays.asList(4, 5, 6, 7));
        System.out.println(CrossSolver.solve("B' D2 B' U2 F' D2 U2 B' U2 F2 R B L2 B2 D F R' B L"));
    }
}
