import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Search {
    private boolean initialized = false;

    private List<List<Integer>> orientationMoves;
    private List<List<Integer>> permutationMoves;

    private List<Integer> pruneOrientation;
    private List<Integer> prunePermutation;

    private List<Integer> affectedPermutationPieces;
    private int NUM_PERMUTATIONS;
    private int DEFAULT_PERMUTATION;

    public Search(List<Integer> affectedPermutationPieces) {
        this.affectedPermutationPieces = affectedPermutationPieces;

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

    private static int getShift(int index) {
        return (index % 8) << 2;
    }

    private static int getPruningValue(List<Integer> table, int index) {
        return table.get(index >> 3) & (0xF << getShift(index)) >>> getShift(index);
    }

    private static void setPruningValue(List<Integer> table, int index, int value) {
        table.set(index >> 3, (table.get(index >> 3) & ~(0xF << getShift(index))) | (value << getShift(index)));
    }

    private static List<Integer> computePruningTable(int size, List<List<Integer>> doMove) {
        List<Integer> table = new ArrayList<Integer>();

        for (int i = 0; i <= Math.ceil(size / 8); i += 1) {
            table.add(0xF);
        }

        setPruningValue(table, 0, 0);

        int depth = 0;

        while (true) {
            int count = 0;

            for (int index = 0; index < size; index += 1) {
                if (getPruningValue(table, index) != depth) {
                    continue;
                }

                for (int move = 0; move < 6; move += 1) {
                    for (int pow = 0; pow < 3; pow += 1) {
                        int position = doMove.get(index).get(move * 3 + pow);

                        if (getPruningValue(table, position) == 0xF) {
                            setPruningValue(table, position, depth);
                            count += 1;
                        }
                    }
                }
            }

            // We assume the table is finished when we go for an entire depth
            // without adding any values to the pruning table.
            if (count == 0) {
                break;
            }

            depth += 1;
        }

        return table;
    }

    private void initialize() {
        orientationMoves = createMoveTable(2048, Coordinates::orientationMove);
        permutationMoves = createMoveTable(NUM_PERMUTATIONS, (index, move) -> Coordinates.permutationMove(index, move, affectedPermutationPieces));

        pruneOrientation = computePruningTable(2048, orientationMoves);
        prunePermutation = computePruningTable(NUM_PERMUTATIONS, permutationMoves);
    }

    private boolean search(int orientation, int permutation, int depth, int lastMove, List<Integer> solution) {
        if (depth == 0) {
            return permutation == DEFAULT_PERMUTATION && orientation == 0;
        }

        if (getPruningValue(pruneOrientation, orientation) > depth || getPruningValue(prunePermutation, permutation) > depth) {
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
