import java.util.ArrayList;
import java.util.List;

public class MoveTable {
    private List<List<Integer>> table;

    public MoveTable(int size, CoordinateMove doMove) {
        createMoveTable(size, doMove);
    }

    private void createMoveTable(int size, CoordinateMove doMove) {
        table = new ArrayList<List<Integer>>();

        for (int i = 0; i < size; i += 1) {
            table.add(new ArrayList<Integer>());

            for (int move = 0; move < 6; move += 1) {
                for (int pow = 0; pow < 3; pow += 1) {
                    table.get(i).add(doMove.apply(i, move * 3 + pow));
                }
            }
        }
    }

    public int doMove(int index, int move) {
        return table.get(index).get(move);
    }
}
