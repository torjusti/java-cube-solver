import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Defines moves by how they affect the orientation and permutation vectors.
 */
public class CubieCube {
    /**
     * We store moves as a 2D list describing which pieces the move cycles clockwise.
     * Pieces: UR, UF, UL, UB, DR, DF, DL, DB, FR, FL, BL, BR.
     */
    private static List<List<Integer>> moves = new ArrayList<List<Integer>>();

    static {
        moves.add(Arrays.asList(1, 8, 5, 9)); // F
        moves.add(Arrays.asList(0, 11, 4, 8)); // R
        moves.add(Arrays.asList(1, 2, 3, 0)); // U
        moves.add(Arrays.asList(3, 10, 7, 11)); // B
        moves.add(Arrays.asList(2, 9, 6, 10)); // L
        moves.add(Arrays.asList(5, 4, 7, 6)); // D
    }

    /**
     * Shuffles the items in a list to the right by one, such that
     * element A goes to B, B to C, C to D and D back to A.
     * This helps us compute the result cube after a move is applied,
     * because all moves permute pieces to the right by one in a circular fashion.
     */
    static List<Integer> rotateRight(List<Integer> edges, int a, int b, int c, int d) {
        List<Integer> updatedPieces = new ArrayList<>(edges);
        updatedPieces.set(b, edges.get(a));
        updatedPieces.set(c, edges.get(b));
        updatedPieces.set(d, edges.get(c));
        updatedPieces.set(a, edges.get(d));
        return updatedPieces;
    }

    /**
     * Returns the updated permutation vector after applying a move.
     */
    public static List<Integer> permutationMove(List<Integer> edges, int moveIndex) {
        List<Integer> move = moves.get(moveIndex / 3);
        int pow = moveIndex % 3;

        List<Integer> permuted = edges;

        for (int i = 0; i <= pow; i += 1) {
            permuted = rotateRight(permuted, move.get(0), move.get(1), move.get(2), move.get(3));
        }

        return permuted;
    }

    /**
     * Returns the updated permutation vector after applying a move.
     */
    public static List<Integer> orientationMove(List<Integer> edges, int moveIndex) {
        List<Integer> move = moves.get(moveIndex / 3);
        int pow = moveIndex % 3;

        List<Integer> permuted = permutationMove(edges, moveIndex);

        // F and B moves affect the orientation, but only if it is only a single slice move in either direction.
        if ((moveIndex / 3 == 0 || moveIndex / 3 == 3) && pow % 2 == 0) {
            permuted.set(move.get(0), (permuted.get(move.get(0)) + 1) % 2);
            permuted.set(move.get(1), (permuted.get(move.get(1)) + 1) % 2);
            permuted.set(move.get(2), (permuted.get(move.get(2)) + 1) % 2);
            permuted.set(move.get(3), (permuted.get(move.get(3)) + 1) % 2);
        }

        return permuted;
    }
}
