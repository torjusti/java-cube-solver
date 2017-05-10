import java.util.*;

public class Search {
    private boolean initialized = false;

    private MoveTable orientationMoves;
    private MoveTable permutationMoves;

    private PruningTable pruneOrientation;
    private PruningTable prunePermutation;

    private List<Integer> affectedPermutationPieces;
    private List<Integer> affectedOrientationPieces;

    private Set<Integer> correctOrientations = new HashSet<Integer>();

    private int NUM_PERMUTATIONS;
    private int DEFAULT_PERMUTATION;

    public Search(List<Integer> affectedPermutationPieces, List<Integer> affectedOrientationPieces) {
        this.affectedPermutationPieces = affectedPermutationPieces;
        this.affectedOrientationPieces = affectedOrientationPieces;

        NUM_PERMUTATIONS = Tools.factorial(12) / Tools.factorial(12 - affectedPermutationPieces.size());
        DEFAULT_PERMUTATION = Coordinates.getIndexFromPermutation(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), affectedPermutationPieces);
    }

    private void populateCorrectOrientations() {
        for (int i = 0; i < 2048; i++) {
            List<Integer> orientation = Coordinates.getOrientationFromIndex(i);

            if (affectedOrientationPieces.stream().allMatch(piece -> orientation.get(piece) == 0)) {
                correctOrientations.add(i);
            }
        }
    }

    public void initialize() {
        if (this.initialized) {
            return;
        }

        this.initialized = true;

        orientationMoves = new MoveTable(2048, Coordinates::orientationMove);
        permutationMoves = new MoveTable(NUM_PERMUTATIONS, (index, move) -> Coordinates.permutationMove(index, move, affectedPermutationPieces));

        if (affectedOrientationPieces != null) {
            populateCorrectOrientations();
        }

        pruneOrientation = new PruningTable(2048, orientationMoves, correctOrientations);
        prunePermutation = new PruningTable(NUM_PERMUTATIONS, permutationMoves, DEFAULT_PERMUTATION);
    }

    private boolean search(int orientation, int permutation, int depth, int lastMove, List<Integer> solution) {
        if (depth == 0) {
            if (permutation != DEFAULT_PERMUTATION) {
                return false;
            }

            if (affectedOrientationPieces != null) {
                return correctOrientations.contains(orientation);
            }

            return orientation == 0;
        }

        if (pruneOrientation.getPruningValue(orientation) > depth || prunePermutation.getPruningValue(permutation) > depth) {
            return false;
        }

        for (int move = 0; move < 6; move += 1) {
            if (move != lastMove && move != lastMove - 3) {
                for (int pow = 0; pow < 3; pow += 1) {
                    int innerOrientation = orientationMoves.doMove(orientation, move * 3 + pow);
                    int innerPermutation = permutationMoves.doMove(permutation, move * 3 + pow);

                    boolean result = search(innerOrientation, innerPermutation, depth - 1, move, solution);

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
        int permutation = DEFAULT_PERMUTATION;

        for (Integer move : moves) {
            orientation = orientationMoves.doMove(orientation, move);
            permutation = permutationMoves.doMove(permutation, move);
        }

        List<Integer> solution = new ArrayList<Integer>();

        // Every cube is solvable with a depth of 20. However, such depths are too slow to ever end up solved.
        for (int depth = 0; depth < 20; depth += 1) {
            if (search(orientation, permutation, depth, -1, solution)) {
                break;
            }
        }

        Collections.reverse(solution);

        return Scrambles.formatMoveSequence(solution);
    }
}
