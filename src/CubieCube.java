import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Defines moves by how they affect the orientation and permutation vectors.
 */
public class CubieCube {
    /**
     * We store moves as a 2D list describing which pieces the move cycles clockwise.
     * Pieces: UR, UF, UL, UB, DR, DF, DL, DB, FR, FL, BL, BR.
     */
    public static List<List<Integer>> edgeMoves = new ArrayList<List<Integer>>();

    static {
        edgeMoves.add(Arrays.asList(1, 8, 5, 9)); // F
        edgeMoves.add(Arrays.asList(0, 11, 4, 8)); // R
        edgeMoves.add(Arrays.asList(1, 2, 3, 0)); // U
        edgeMoves.add(Arrays.asList(3, 10, 7, 11)); // B
        edgeMoves.add(Arrays.asList(2, 9, 6, 10)); // L
        edgeMoves.add(Arrays.asList(5, 4, 7, 6)); // D
    }

    /**
     * Moves for edges are stored in the same way as edges.
     * Pieces: URF, UFL, ULB, UBR, DFR, DLF, DBL, DRB
     */
    public static List<List<Integer>> cornerPermutationMoves = new ArrayList<List<Integer>>();

    static {
        cornerPermutationMoves.add(Arrays.asList(1, 5, 2, 3, 0, 4, 6, 7)); // F
        cornerPermutationMoves.add(Arrays.asList(4, 1, 2, 0, 7, 5, 6, 3)); // R
        cornerPermutationMoves.add(Arrays.asList(3, 0, 1, 2, 4, 5, 6, 7)); // U
        cornerPermutationMoves.add(Arrays.asList(0, 1, 3, 7, 4, 5, 2, 6)); // B
        cornerPermutationMoves.add(Arrays.asList(0, 2, 6, 3, 4, 1, 5, 7)); // L
        cornerPermutationMoves.add(Arrays.asList(0, 1, 2, 3, 5, 6, 7, 4)); // D
    }

    public static List<List<Integer>> cornerOrientationMoves = new ArrayList<List<Integer>>();

    static {
        cornerOrientationMoves.add(Arrays.asList(1, 2, 0, 0, 2, 1, 0, 0)); // F
        cornerOrientationMoves.add(Arrays.asList(2, 0, 0, 1, 1, 0, 0, 2)); // R
        cornerOrientationMoves.add(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0)); // U
        cornerOrientationMoves.add(Arrays.asList(0, 0, 1, 2, 0, 0, 2, 1)); // B
        cornerOrientationMoves.add(Arrays.asList(0, 1, 2, 0, 0, 2, 1, 0)); // L
        cornerOrientationMoves.add(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0)); // D
    }

    /**
     * Shuffles the items in a list to the right by one.
     * This helps us compute the result cube after a move is applied, as
     * all moves permute pieces to the right by one in a circular fashion.
     */
    static List<Integer> rotateRight(List<Integer> edges, int... elems) {
        List<Integer> updatedPieces = new ArrayList<>(edges);

        updatedPieces.set(elems[0], edges.get(elems[elems.length - 1]));

        for (int i = 1; i < elems.length; i++) {
            updatedPieces.set(elems[i], edges.get(elems[i - 1]));
        }

        return updatedPieces;
    }

    /**
     * Returns the updated permutation vector after applying a move.
     */
    public static List<Integer> permutationMove(List<Integer> pieces, int moveIndex) {
        List<Integer> move = edgeMoves.get(moveIndex / 3);
        int pow = moveIndex % 3;

        List<Integer> permuted = pieces;

        for (int i = 0; i <= pow; i += 1) {
            permuted = rotateRight(permuted, move.get(0), move.get(1), move.get(2), move.get(3));
        }

        return permuted;
    }

    /**
     * Returns the updated permutation vector after applying a move.
     */
    public static List<Integer> orientationMove(List<Integer> pieces, int moveIndex) {
        List<Integer> move = edgeMoves.get(moveIndex / 3);
        int pow = moveIndex % 3;

        List<Integer> permuted = permutationMove(pieces, moveIndex);

        // F and B moves affect the orientation, but only if it is only a single slice move in either direction.
        if ((moveIndex / 3 == 0 || moveIndex / 3 == 3) && pow % 2 == 0) {
            permuted.set(move.get(0), (permuted.get(move.get(0)) + 1) % 2);
            permuted.set(move.get(1), (permuted.get(move.get(1)) + 1) % 2);
            permuted.set(move.get(2), (permuted.get(move.get(2)) + 1) % 2);
            permuted.set(move.get(3), (permuted.get(move.get(3)) + 1) % 2);
        }

        return permuted;
    }

    public static List<Integer> cornerPermutationMove(List<Integer> pieces, int moveIndex) {
        List<Integer> move = cornerPermutationMoves.get(moveIndex / 3);
        int pow = moveIndex % 3;

        List<Integer> permuted = new ArrayList<>(pieces);

        for (int i = 0; i <= pow; i++) {
            List<Integer> round = new ArrayList<>(permuted);

            for (int j = 0; j < 8; j++) {
                int from = move.get(j);
                permuted.set(j, round.get(from));
            }
        }

        return permuted;
    }

    public static List<Integer> cornerOrientationMove(List<Integer> pieces, int moveIndex) {
        int move = moveIndex / 3;
        int pow = moveIndex % 3;

        List<Integer> oriented = new ArrayList<>(pieces);

        for (int i = 0; i <= pow; i++) {
            List<Integer> round = new ArrayList<>(oriented);

            for (int j = 0; j < 8; j++) {
                int from = cornerPermutationMoves.get(move).get(j);
                oriented.set(j, (round.get(from) + cornerOrientationMoves.get(move).get(j)) % 3);
            }
        }

        return oriented;
    }
}
