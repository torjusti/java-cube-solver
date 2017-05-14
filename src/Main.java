import java.io.*;
import java.util.*;

public class Main {
    private static void timeSolve(Search search, String scramble, String description) {
        long start = System.currentTimeMillis();
        System.out.println(description + ": " + search.solve(scramble) + " (" + (System.currentTimeMillis() - start) + " ms)");
    }

    private static List<String> solveFile(String path) {
        List<String> scrambles = new ArrayList<>();

        Scanner in = null;

        try {
            in = new Scanner(new FileInputStream(path));

            while (in.hasNextLine()) {
                String line = in.nextLine();

                if (Scrambles.validateSequence(line)) {
                    scrambles.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error opening file");
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return scrambles;
    }

    private static void saveSolutions(List<String> scrambles, List<List<String>> solutions, String path) {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileOutputStream(path));

            for (int i = 0; i < scrambles.size(); i++) {
                writer.println(scrambles.get(i));
                writer.println("EOLine: " + solutions.get(i).get(0));
                writer.println("EOCross: " + solutions.get(i).get(1));
                writer.println("Cross: " + solutions.get(i).get(2));
                writer.println("XCross: " + solutions.get(i).get(3));
                writer.println("EOXCross: " + solutions.get(i).get(3));
            }
        } catch (IOException e) {
            System.err.println("Error saving file");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Initializing solvers");

        long initializationStart = System.currentTimeMillis();

        Search EOLineSolver = new Search(Arrays.asList(5, 7),  Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), null, null);
        Search EOCrossSolver = new Search(Arrays.asList(4, 5, 6, 7), Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), null, null);
        Search CrossSolver = new Search(Arrays.asList(4, 5, 6, 7), Arrays.asList(4, 5, 6, 7), null, null);
        Search FRXCrossSolver = new Search(Arrays.asList(4, 5, 6, 7, 8), Arrays.asList(4, 5, 6, 7, 8), Arrays.asList(4), Arrays.asList(4));
        Search EOXCrossSolver = new Search(Arrays.asList(4, 5, 6, 7, 8), Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), Arrays.asList(4), Arrays.asList(4));

        EOLineSolver.initialize();
        EOCrossSolver.initialize();
        CrossSolver.initialize();
        FRXCrossSolver.initialize();
        EOXCrossSolver.initialize();

        System.out.println("Solvers initialized in " + (System.currentTimeMillis() - initializationStart) + " ms");

        Scanner in = new Scanner(System.in);

        do {
            System.out.println("Scramble:");
            String line = in.nextLine();

            if (line.matches("^load .+$")) {
                List<String> scrambles = solveFile(line.replaceAll("^load[ ]+", ""));

                List<List<String>> solutions = new ArrayList<>();

                for (String scramble : scrambles) {
                    List<String> subSol = new ArrayList<>();
                    subSol.add(EOLineSolver.solve(scramble));
                    subSol.add(EOCrossSolver.solve(scramble));
                    subSol.add(CrossSolver.solve(scramble));
                    subSol.add(FRXCrossSolver.solve(scramble));
                    subSol.add(EOXCrossSolver.solve(scramble));
                    solutions.add(subSol);
                }

                saveSolutions(scrambles, solutions, "solutions-" + line.replaceAll("^load[ ]+", ""));

                System.out.println("Solutions saved!");
            } else if (!Scrambles.validateSequence(line)) {
                System.out.println("Unable to parse scramble");
            } else {
                timeSolve(EOLineSolver, line, "EOLine");
                timeSolve(EOCrossSolver, line, "EOCross");
                timeSolve(CrossSolver, line, "Cross");
                timeSolve(FRXCrossSolver, line, "XCross");
                timeSolve(EOXCrossSolver, line, "EOXCross");
            }
        } while (in.hasNext());

        in.close();
    }
}
