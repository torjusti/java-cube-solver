public class Tools {
    /**
     * Computes the factorial n!.
     */
    public static int factorial(int n) {
        if (n == 1) {
            return 1;
        }

        return n * factorial(n - 1);
    }
}
