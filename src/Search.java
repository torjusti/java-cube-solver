import javax.print.attribute.IntegerSyntax;
import java.util.*;

public class Search {
    private boolean initialized = false;

    private MoveTable orientationMoves;
    private MoveTable permutationMoves;

    private MoveTable cornerOrientationMoves;
    private MoveTable cornerPermutationMoves;

    private PruningTable pruneOrientation;
    private PruningTable prunePermutation;

    private PruningTable pruneCornerOrientation;
    private PruningTable pruneCornerPermutation;

    private List<Integer> affectedPermutationPieces;
    private List<Integer> affectedOrientationPieces;

    private List<Integer> affectedCornerOrientationPieces;
    private List<Integer> affectedCornerPermutationPieces;

    private Set<Integer> correctOrientations = new HashSet<Integer>();
    private Set<Integer> correctCornerOrientations = new HashSet<Integer>();

    private int NUM_EDGE_PERMUTATIONS;
    private int DEFAULT_EDGE_PERMUTATION;

    private int NUM_CORNER_PERMUTATIONS;
    private int DEFAULT_CORNER_PERMUTATION;

    public Search(List<Integer> affectedPermutationPieces, List<Integer> affectedOrientationPieces, List<Integer> affectedCornerOrientationPieces, List<Integer> affectedCornerPermutationPieces) {
        this.affectedPermutationPieces = affectedPermutationPieces;
        this.affectedOrientationPieces = affectedOrientationPieces;

        this.affectedCornerOrientationPieces = affectedCornerOrientationPieces;
        this.affectedCornerPermutationPieces =  affectedCornerPermutationPieces;

        NUM_EDGE_PERMUTATIONS = Tools.factorial(12) / Tools.factorial(12 - affectedPermutationPieces.size());
        DEFAULT_EDGE_PERMUTATION = Coordinates.getIndexFromPermutation(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), affectedPermutationPieces);

        if (affectedCornerPermutationPieces != null) {
            NUM_CORNER_PERMUTATIONS = Tools.factorial(8) / Tools.factorial(8 - affectedCornerPermutationPieces.size());
            DEFAULT_CORNER_PERMUTATION = Coordinates.getIndexFromPermutation(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7), affectedCornerPermutationPieces);
        }
    }

    private void populateCorrectOrientations() {
        for (int i = 0; i < 2048; i++) {
            List<Integer> orientation = Coordinates.getEdgeOrientationFromIndex(i);

            if (affectedOrientationPieces.stream().allMatch(piece -> orientation.get(piece) == 0)) {
                correctOrientations.add(i);
            }
        }
    }

    private void populateCorrectCornerOrientations() {
        for (int i = 0; i < 2187; i++) {
            List<Integer> orientation = Coordinates.getCornerOrientationFromIndex(i);

            if (affectedCornerOrientationPieces.stream().allMatch(piece -> orientation.get(piece) == 0)) {
                correctCornerOrientations.add(i);
            }
        }
    }

    public void initialize() {
        if (this.initialized) {
            return;
        }

        this.initialized = true;

        orientationMoves = new MoveTable(2048, Coordinates::orientationMove);
        permutationMoves = new MoveTable(NUM_EDGE_PERMUTATIONS, (index, move) -> Coordinates.permutationMove(index, move, affectedPermutationPieces));

        if (affectedCornerOrientationPieces != null) {
            cornerOrientationMoves = new MoveTable(2187, Coordinates::cornerOrientationMove);
        }

        if (affectedCornerPermutationPieces != null) {
            cornerPermutationMoves = new MoveTable(NUM_CORNER_PERMUTATIONS, (index, move) -> Coordinates.cornerPermutationMove(index, move, affectedCornerPermutationPieces));
        }

        if (affectedOrientationPieces != null) {
            populateCorrectOrientations();
        }

        if (affectedCornerOrientationPieces != null) {
            populateCorrectCornerOrientations();
        }

        pruneOrientation = new PruningTable(2048, orientationMoves, correctOrientations);
        prunePermutation = new PruningTable(NUM_EDGE_PERMUTATIONS, permutationMoves, DEFAULT_EDGE_PERMUTATION);

        if (cornerOrientationMoves != null) {
            pruneCornerOrientation = new PruningTable(2187, cornerOrientationMoves, correctCornerOrientations);
        }

        if (cornerPermutationMoves != null) {
            pruneCornerPermutation = new PruningTable(NUM_CORNER_PERMUTATIONS, cornerPermutationMoves, DEFAULT_CORNER_PERMUTATION);
        }
    }

    private boolean search(int orientation, int permutation, int cornerOrientation, int cornerPermutation, int depth, int lastMove, List<Integer> solution) {
        if (depth == 0) {
            if (affectedPermutationPieces != null && permutation != DEFAULT_EDGE_PERMUTATION) {
                return false;
            }

            if (affectedCornerPermutationPieces != null && cornerPermutation != DEFAULT_CORNER_PERMUTATION) {
                return false;
            }

            if (affectedOrientationPieces != null && !correctOrientations.contains(orientation)) {
                return false;
            }

            if (affectedCornerOrientationPieces != null && !correctCornerOrientations.contains(cornerOrientation)) {
                return false;
            }

            return true;
        }

        if ((pruneOrientation != null && pruneOrientation.getPruningValue(orientation) > depth)
                || (prunePermutation != null && prunePermutation.getPruningValue(permutation) > depth)
                || (pruneCornerOrientation != null && pruneCornerOrientation.getPruningValue(cornerOrientation) > depth)
                || (pruneCornerPermutation != null && pruneCornerPermutation.getPruningValue(cornerPermutation) > depth)) {
            return false;
        }

        for (int move = 0; move < 6; move += 1) {
            if (move != lastMove && move != lastMove - 3) {
                for (int pow = 0; pow < 3; pow += 1) {
                    int innerOrientation = orientationMoves.doMove(orientation, move * 3 + pow);
                    int innerPermutation = permutationMoves.doMove(permutation, move * 3 + pow);

                    // If any of the tables do not exist, we just pick 0 as a value - this does not matter,
                    // because if the tables are empty, the index will not be checked against at all.
                    int innerCornerOrientation = 0, innerCornerPermutation = 0;

                    if (cornerOrientationMoves != null) {
                        innerCornerOrientation = cornerOrientationMoves.doMove(cornerOrientation, move * 3 + pow);
                    }

                    if (cornerPermutationMoves != null) {
                        innerCornerPermutation = cornerPermutationMoves.doMove(cornerPermutation, move * 3 + pow);
                    }

                    boolean result = search(innerOrientation, innerPermutation, innerCornerOrientation, innerCornerPermutation, depth - 1, move, solution);

                    if (result) {
                        solution.add(move * 3 + pow);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public String solve(String scramble) {
        initialize();

        List<Integer> moves = Scrambles.parseScramble(scramble);

        int orientation = 0;
        int permutation = DEFAULT_EDGE_PERMUTATION;

        int cornerOrientation = 0;
        int cornerPermutation = DEFAULT_CORNER_PERMUTATION;

        for (Integer move : moves) {
            orientation = orientationMoves.doMove(orientation, move);
            permutation = permutationMoves.doMove(permutation, move);

            if (cornerOrientationMoves != null) {
                cornerOrientation = cornerOrientationMoves.doMove(cornerOrientation, move);
            }

            if (cornerPermutationMoves != null) {
                cornerPermutation = cornerPermutationMoves.doMove(cornerPermutation, move);
            }
        }

        List<Integer> solution = new ArrayList<Integer>();

        // Every cube is solvable with a depth of 20. However, such depths are too slow to ever end up solved.
        for (int depth = 0; depth < 20; depth += 1) {
            if (search(orientation, permutation, cornerOrientation, cornerPermutation, depth, -1, solution)) {
                break;
            }
        }

        Collections.reverse(solution);

        return Scrambles.formatMoveSequence(solution);
    }
}
