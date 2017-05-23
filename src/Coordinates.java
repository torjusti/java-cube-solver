import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Coordinates {
    /**
     * Computes an unique index in the range from 0 to but not up to
     * the maximum number of unique flips of the pieces.
     * Thus, this function is a bijection, however there is no guaranteed logical
     * connection between the indexes and the orientation.
     */
    public static int getIndexFromOrientation(List<Integer> pieces, int flipCount) {
        int sum = 0;

        for (int i = 0; i < pieces.size() - 1; i++) {
            sum = flipCount * sum + pieces.get(i);
        }

        return sum;
    }

    public static int getIndexFromCornerOrientation(List<Integer> corners) {
        // Corners can be twisted in 3 ways.
        return getIndexFromOrientation(corners, 3);
    }

    public static int getIndexFromEdgeOrientation(List<Integer> edges) {
        // Edges may be flipped or not.
       return getIndexFromOrientation(edges, 2);
    }

    private static List<Integer> getOrientationFromIndex(int index, int numPieces, int numFlips) {
        List<Integer> orientation = new ArrayList<>(Collections.nCopies(numPieces, 0));

        int parity = 0;

        for (int i = numPieces - 2; i >= 0; i--) {
            int ori = index % numFlips;
            orientation.set(i, ori);
            parity += ori;
            index /= numFlips;
        }

        // The flip of the last piece is uniquely determined by the flip of the other pieces.
        orientation.set(numPieces - 1, (numFlips - parity % numFlips) % numFlips);

        return orientation;
    }

    public static List<Integer> getCornerOrientationFromIndex(int index) {
        return getOrientationFromIndex(index, 8, 3);
    }

    public static List<Integer> getEdgeOrientationFromIndex(int index) {
        return getOrientationFromIndex(index, 12, 2);
    }

    /**
     * Retrieves the unique permutation of the affected pieces in a list of given length corresponding to the given index.
     */
    private static List<Integer> getPermutationFromIndex(int index, List<Integer> affectedPieces, int size) {
        List<Integer> permutation = new ArrayList<>(Collections.nCopies(size, 0));
        List<Integer> indexes = new ArrayList<Integer>();

        if (affectedPieces.size() == 1) {
            permutation.set(index, affectedPieces.get(0));

            return permutation;
        }

        int factor = 1 + size - affectedPieces.size();

        int base = size;

        for (int i = affectedPieces.size() - 2; i >= 0; i--) {
            base *= factor + i;
        }

        for (int i = 0; i < affectedPieces.size() - 1; i++) {
            base /= factor + i;

            int value = index / base;
            int rest = index % base;

            indexes.add(value);

            if (i == affectedPieces.size() - 2) {
                indexes.add(rest);
            }

            index -= base * value;
        }

        for (int i = 0; i < indexes.size() - 1; i++) {
            for (int j = i + 1; j < indexes.size(); j++) {
                if (indexes.get(i) >= indexes.get(j)) {
                    indexes.set(i, indexes.get(i) + 1);
                }
            }
        }

        for (int i = 0; i < affectedPieces.size(); i++) {
            permutation.set(indexes.get(i), affectedPieces.get(i));
        }

        return permutation;
    }

    public static List<Integer> getEdgePermutationFromIndex(int index, List<Integer> affectedPieces) {
        return getPermutationFromIndex(index, affectedPieces, 12);
    }

    public static List<Integer> getCornerPermutationFromIndex(int index, List<Integer> affectedPieces) {
        return getPermutationFromIndex(index, affectedPieces, 8);
    }

    /**
     * This function is a bijection which will map the given permutation to an unique number.
     * The range of the numbers depends on the affected pieces - the number will be an unique
     * number in the range from 0 up but not unto the number of ways the affected pieces
     * may be permuted in a list of the given length. The function is identical for both edges and corners.
     */
    public static int getIndexFromPermutation(List<Integer> permutation, List<Integer> affectedPieces) {
        List<Integer> indexes = affectedPieces.stream().map(permutation::indexOf).collect(Collectors.toList());

        if (affectedPieces.size() == 1) {
            return permutation.indexOf(affectedPieces.get(0));
        }

        int base = permutation.size();

        int previous = indexes.get(indexes.size() - 1);

        for (int i = indexes.size() - 2; i >= 0; i--) {
            for (int j = indexes.size() - 1; j > i; j--) {
                if (indexes.get(i) > indexes.get(j)) {
                    indexes.set(i, indexes.get(i) - 1);
                }
            }

            previous += base * indexes.get(i);

            base *= 1 + permutation.size() - indexes.size() + i;
        }

        return previous;
    }

    /**
     * Returns the new orientation index after performing a move.
     */
    public static int orientationMove(int index, int move) {
        List<Integer> orientation = getEdgeOrientationFromIndex(index);
        return getIndexFromEdgeOrientation(CubieCube.orientationMove(orientation, move));
    }

    /**
     * Returns the new permutation index after performing a move.
     */
    public static int permutationMove(int index, int move, List<Integer> affectedPieces) {
        List<Integer> permutation = getEdgePermutationFromIndex(index, affectedPieces);
        return getIndexFromPermutation(CubieCube.permutationMove(permutation, move), affectedPieces);
    }

    public static int cornerOrientationMove(int index, int move) {
        List<Integer> orientation = getCornerOrientationFromIndex(index);
        return getIndexFromCornerOrientation(CubieCube.cornerOrientationMove(orientation, move));
    }

    public static int cornerPermutationMove(int index, int move, List<Integer> affectedPieces) {
        List<Integer> permutation = getCornerPermutationFromIndex(index, affectedPieces);
        return getIndexFromPermutation(CubieCube.cornerPermutationMove(permutation, move), affectedPieces);
    }
}
