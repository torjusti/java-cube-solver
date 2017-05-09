import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class PruningTable {
    private byte[] table;

    public PruningTable(int size, MoveTable moveTable, int defaultPosition) {
        this(size, moveTable, new HashSet(Arrays.asList(defaultPosition)));
    }

    public PruningTable(int size, MoveTable moveTable, Collection<Integer> solvedIndexes) {
        computePruningTable(size, moveTable, solvedIndexes);
    }

    private void setPruningValue(int index, byte value) {
        if ((index & 1) == 0) {
            table[index / 2] &= 0xf0 | value;
        } else {
            table[index / 2] &= 0x0f | (value << 4);
        }
    }

    public byte getPruningValue(int index) {
        if ((index & 1) == 0) {
            return (byte) (table[index / 2] & 0x0f);
        } else {
            return (byte) ((table[index / 2] & 0xf0) >>> 4);
        }
    }

    private void computePruningTable(int size, MoveTable moveTable, Collection<Integer> solvedIndexes) {
        table = new byte[size /  2];

        for (int i = 0; i < size / 2; i += 1) {
            table[i] = -1;
        }

        int done = 0;

        for (Integer index : solvedIndexes) {
            setPruningValue(index, (byte) 0);
            done++;
        }

        int depth = 0;

        while (depth != size) {
            for (int index = 0; index < size; index++) {
                if (getPruningValue(index) != depth) {
                    continue;
                }

                for (int move = 0; move < 18; move++) {
                    int position = moveTable.doMove(index, move);

                    if (getPruningValue(position) == 0x0f) {
                        setPruningValue(position, (byte)(depth + 1));
                        done++;
                    }
                }
            }

            depth++;
        }
    }
}
