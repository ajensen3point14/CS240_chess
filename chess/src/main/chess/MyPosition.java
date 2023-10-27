package chess;

import java.util.Objects;

public class MyPosition implements ChessPosition {
    private int row = 0;
    private int col = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyPosition that = (MyPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public String toString() {
        return "myPosition{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    public MyPosition(int new_row, int new_col) {
        row = new_row;
        col = new_col;
    }
    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return col;
    }

    public void setPos(int newRow, int newCol) {
        row = newRow;
        col = newCol;
    }
    public boolean isValidPosition(int newRow, int newCol) {
        return (newRow >= 1 && newRow <= 8) && (newCol >= 1 && newCol <= 8);
    }
}
