package chess;

import java.util.Objects;

public class MyMove implements ChessMove {
    ChessPosition startPos;
    ChessPosition endPos;
    ChessPiece.PieceType promotion;
    public MyMove(ChessPosition pos1, ChessPosition pos2, ChessPiece.PieceType new_promotion) {
        startPos = pos1;
        endPos = pos2;
        promotion = new_promotion;
    }


    @Override
    public ChessPosition getStartPosition() {
        return startPos;
    }

    @Override
    public ChessPosition getEndPosition() {
        return endPos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyMove myMove = (MyMove) o;
        return Objects.equals(startPos, myMove.startPos)
                && Objects.equals(endPos, myMove.endPos)
                && promotion == myMove.promotion;
    }

    @Override
    public String toString() {
        return "myMove{" +
                "startPos=" + startPos +
                ", endPos=" + endPos +
                ", promotion=" + promotion +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPos, endPos, promotion);
    }

    @Override
    public ChessPiece.PieceType getPromotionPiece() {
        return promotion;
    }
}
