import java.util.ArrayList;
import java.util.List;

public class Scrambles {
    // Returns true if we are able to parse this sequence, false if not.
    public static boolean validateSequence(String sequence) {
        if (sequence.trim().isEmpty()) {
            return false;
        }

        for (String part : sequence.trim().split( " ")) {
            if (part.trim().isEmpty()) {
                continue;
            }

            if (!part.matches("^[FRUBLD]{1}[2']?$")) {
                return false;
            }
        }

        return true;
    }

    public static List<Integer> parseScramble(String scramble) {
        List<Integer> moves = new ArrayList<>();

        for (String move : scramble.trim().split(" ")) {
            if (move.trim().isEmpty()) {
                continue;
            }

            List<Integer> movePair = new ArrayList<Integer>();

            int moveNum = "FRUBLD".indexOf(move.charAt(0));
            int pow = 0;

            if (move.length() == 2) {
                if (move.charAt(1) == '2') {
                    pow = 1;
                } else if (move.charAt(1) == '\'') {
                    pow = 2;
                }
            }

            moves.add(moveNum * 3 + pow);
        }

        return moves;
    }

    public static String formatMoveSequence(List<Integer> moves) {
        String sequence = "";

        for (Integer move : moves) {
            sequence += " ";
            sequence += "FRUBLD".charAt(move / 3);

            switch (move % 3) {
                case 1:
                    sequence += "2";
                    break;
                case 2:
                    sequence += "'";
                    break;
            }
        }

        return sequence;
    }
}
