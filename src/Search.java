import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Search {
    private boolean initialized = false;

    private List<List<Integer>> orientationMoves;
    private List<List<Integer>> permutationMoves;

    private byte[] pruneOrientation;
    private byte[] prunePermutation;

    private List<Integer> affectedPermutationPieces;
    private List<Integer> affectedOrientationPieces;

    private List<Integer> correctOrientations = new ArrayList<Integer>();

    private int NUM_PERMUTATIONS;
    private int DEFAULT_PERMUTATION;

    public Search(List<Integer> affectedPermutationPieces, List<Integer> affectedOrientationPieces) {
        this.affectedPermutationPieces = affectedPermutationPieces;
        this.affectedOrientationPieces = affectedOrientationPieces;

        NUM_PERMUTATIONS = Tools.factorial(12) / Tools.factorial(12 - affectedPermutationPieces.size());
        DEFAULT_PERMUTATION = Coordinates.getIndexFromPermutation(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), affectedPermutationPieces);
    }

    private static List<List<Integer>> createMoveTable(int size, CoordinateMove doMove) {
        List<List<Integer>> table = new ArrayList<List<Integer>>();

        for (int i = 0; i < size; i += 1) {
            table.add(new ArrayList<Integer>());

            for (int move = 0; move < 6; move += 1) {
                for (int pow = 0; pow < 3; pow += 1) {
                    table.get(i).add(doMove.apply(i, move * 3 + pow));
                }
            }
        }

        return table;
    }

    static void setPruning(byte[] table, int index, byte value) {
        if ((index & 1) == 0) {
            table[index / 2] &= 0xf0 | value;
        } else {
            table[index / 2] &= 0x0f | (value << 4);
        }
    }

    static byte getPruning(byte[] table, int index) {
        if ((index & 1) == 0) {
            return (byte)(table[index / 2] & 0x0f);
        } else {
            return (byte)((table[index / 2] & 0xf0) >>> 4);
        }
    }

    private static byte[] computePruningTable(int size, List<List<Integer>> doMove, int defaultPosition, List<Integer> solvedIndexes) {
        byte[] table = new byte[size /  2];

        for (int i = 0; i < size / 2; i += 1) {
            table[i] = -1;
        }

        setPruning(table, defaultPosition, (byte) 0);

        int done = 1;
        int depth = 0;

        while (depth != size) {
            for (int index = 0; index < size; index++) {
                if (getPruning(table, index) != depth) {
                    continue;
                }

                for (int move = 0; move < 18; move++) {
                    int position = doMove.get(index).get(move);

                    if (getPruning(table, position) == 0x0f) {
                        if (solvedIndexes != null && Collections.binarySearch(solvedIndexes, position) >= 0) {
                            setPruning(table, position, (byte) 0);
                        } else {
                            setPruning(table, position, (byte) depth);
                        }

                        done++;
                    }
                }
            }

            depth++;
        }

        return table;
    }

    private void populateCorrectOrientations() {
        for (int i = 0; i < 2048; i++) {
            List<Integer> orientation = Coordinates.getOrientationFromIndex(i);

            boolean allSolved = true;

            for (Integer piece : affectedOrientationPieces) {
                if (orientation.get(piece) != 0) {
                    allSolved = false;
                    break;
                }
            }

            if (allSolved) {
                correctOrientations.add(i);
            }
        }
    }

    private void initialize() {
        orientationMoves = createMoveTable(2048, Coordinates::orientationMove);
        permutationMoves = createMoveTable(NUM_PERMUTATIONS, (index, move) -> Coordinates.permutationMove(index, move, affectedPermutationPieces));

        if (affectedOrientationPieces != null) {
            populateCorrectOrientations();
        }

        pruneOrientation = computePruningTable(2048, orientationMoves, 0, correctOrientations);
        prunePermutation = computePruningTable(NUM_PERMUTATIONS, permutationMoves, DEFAULT_PERMUTATION, null);
    }

    private boolean search(int orientation, int permutation, int depth, int lastMove, List<Integer> solution) {
        if (depth == 0) {
            if (affectedOrientationPieces != null) {
                return permutation == DEFAULT_PERMUTATION && Collections.binarySearch(correctOrientations, orientation) >= 0;
            }

            return permutation == DEFAULT_PERMUTATION && orientation == 0;
        }

        if (getPruning(prunePermutation, permutation) > depth) {
            return false;
        }

        if (getPruning(pruneOrientation, orientation) > depth) {
            return false;
        }

        for (int move = 0; move < 6; move += 1) {
            if (move != lastMove && move != lastMove - 3) {
                for (int pow = 0; pow < 3; pow += 1) {
                    int innerOrientation = orientationMoves.get(orientation).get(move * 3 + pow);
                    int innerPermutation = permutationMoves.get(permutation).get(move * 3 + pow);

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
        if (!initialized) {
            initialize();
            initialized = true;
        }

        System.out.println("running!!");

        List<Integer> moves = Scrambles.parseScramble(scramble);

        int orientation = 0;
        int permutation = DEFAULT_PERMUTATION;

        for (Integer move : moves) {
            orientation = orientationMoves.get(orientation).get(move);
            permutation = permutationMoves.get(permutation).get(move);
        }

        List<Integer> solution = new ArrayList<Integer>();

        for (int depth = 0; depth < 9; depth += 1) {
            if (search(orientation, permutation, depth, -1, solution)) {
                break;
            }
        }

        Collections.reverse(solution);

        return Scrambles.formatMoveSequence(solution);
    }
}
