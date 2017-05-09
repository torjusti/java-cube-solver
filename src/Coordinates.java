import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Coordinates {
    public static List<Integer> getOrientationFromIndex(int index) {
        List<Integer> orientation = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        int parity = 0;

        for (int i = 10; i >= 0; i -= 1) {
            int ori = index % 2;
            orientation.set(i, ori);
            parity += ori;
            index /= 2;
        }

        orientation.set(11, (2 - parity % 2) % 2);

        return orientation;
    }

    public static int getIndexFromOrientation(List<Integer> orientation) {
        int sum = 0;

        for (int i = 0; i < 11; i += 1) {
            sum = 2 * sum + orientation.get(i);
        }

        return sum;
    }

    public static List<Integer> getPermutationFromIndex(int index, List<Integer> affectedPieces) {
        List<Integer> permutation = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        List<Integer> indexes = new ArrayList<Integer>();

        int base = 12;
        int factor = 12;

        for (int i = affectedPieces.size() - 2; i >= 0; i--) {
            factor -= 1;
            base *= factor;
        }

        for (int i = 0; i < affectedPieces.size() - 1; i++) {
            base /= factor;
            factor += 1;

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

    public static int getIndexFromPermutation(List<Integer> permutation, List<Integer> affectedPieces) {
        List<Integer> indexes = affectedPieces.stream().map(permutation::indexOf).collect(Collectors.toList());

        int base = 12;
        int factor = 12;

        int previous = indexes.get(indexes.size() - 1);

        for (int i = indexes.size() - 2; i >= 0; i--) {
            for (int j = indexes.size() - 1; j > i; j--) {
                if (indexes.get(i) > indexes.get(j)) {
                    indexes.set(i, indexes.get(i) - 1);
                }
            }

            previous += base * indexes.get(i);

            factor -= 1;
            base *= factor;
        }

        return previous;
    }

    public static int orientationMove(int index, int move) {
        List<Integer> orientation = getOrientationFromIndex(index);
        return getIndexFromOrientation(CubieCube.orientationMove(orientation, move));
    }

    public static int permutationMove(int index, int move, List<Integer> affectedPieces) {
        List<Integer> permutation = getPermutationFromIndex(index, affectedPieces);
        return getIndexFromPermutation(CubieCube.permutationMove(permutation, move), affectedPieces);
    }
}
