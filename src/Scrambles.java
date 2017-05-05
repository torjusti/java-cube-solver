import java.util.ArrayList;
import java.util.List;

public class Scrambles {
    public static List<Integer> parseScramble(String scramble) {
        List<Integer> moves = new ArrayList<>();

        for (String move : scramble.trim().split(" ")) {
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