package chess;

/**
 * Represents a single square position on a chess board
 * 
 * Note: You can add to this interface, but you should not alter the existing
 * methods.
 */
public interface ChessPosition {
    static boolean isValidPosition(int newRow, int newCol) {
        // TODO: implement? Is this necessary?
        return true;
    }

    /**
     * @return which row this position is in
     *         1 codes for the bottom row
     */
    int getRow();

    /**
     * @return which column this position is in
     *         1 codes for the left row
     */
    int getColumn();
}
