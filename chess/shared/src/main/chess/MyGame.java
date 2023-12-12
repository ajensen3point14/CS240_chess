package chess;

import chess.pieces.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyGame implements ChessGame {
    private MyBoard board;
    private TeamColor teamTurn;
    private ChessMove lastMove = null;
    private boolean gameOver = false;

    public MyGame() {
        this.board = new MyBoard();
        // White starts the game
        this.teamTurn = TeamColor.WHITE;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    @Override
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    @Override
    public void setTeamTurn(TeamColor team) { teamTurn = team; }

    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // Helper method that filters out moves from piece moves that put you into or leave you in check
        List<ChessMove> moves = new ArrayList<>();
        MyBoard originalBoard = (MyBoard) board.clone();
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return moves;
        }
        for (ChessMove move : piece.pieceMoves(board, startPosition)) {
            makeMove_internal(move);
            if (!isInCheck(piece.getTeamColor())) {
                moves.add(move);
            }
            setBoard((MyBoard)originalBoard.clone());
        }

        // Castling
        if (piece.getPieceType() == ChessPiece.PieceType.KING && !piece.hasMoved() && !isInCheck(piece.getTeamColor())) {
            TeamColor opponentTeam = piece.getTeamColor() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
            // Kingside
            ChessPosition kingsideRookPos = new MyPosition(startPosition.getRow(), 8);
            ChessPiece kingsideRook = board.getPiece(kingsideRookPos);
            if (kingsideRook != null &&
                    kingsideRook.getPieceType() == ChessPiece.PieceType.ROOK &&
                    kingsideRook.getTeamColor() == piece.getTeamColor() &&
                    !kingsideRook.hasMoved() &&
                    board.getPiece(new MyPosition(startPosition.getRow(), 6)) == null &&
                    board.getPiece(new MyPosition(startPosition.getRow(), 7)) == null) {
                // Check that the squares the king moves through are not under attack
                if (isSquareSafe(new MyPosition(startPosition.getRow(), 6), opponentTeam) && isSquareSafe(new MyPosition(startPosition.getRow(), 7), opponentTeam)) {
                    // Add kingside castling move
                    moves.add(new MyMove(startPosition, new MyPosition(startPosition.getRow(), 7), null));

                }
            }
            // Queenside
            ChessPosition queensideRookPos = new MyPosition(startPosition.getRow(), 1);
            ChessPiece queensideRook = board.getPiece(queensideRookPos);
            if (queensideRook != null &&
                    queensideRook.getPieceType() == ChessPiece.PieceType.ROOK &&
                    queensideRook.getTeamColor() == piece.getTeamColor() &&
                    !queensideRook.hasMoved() &&
                    board.getPiece(new MyPosition(startPosition.getRow(), 2)) == null &&
                    board.getPiece(new MyPosition(startPosition.getRow(), 3)) == null &&
                    board.getPiece(new MyPosition(startPosition.getRow(), 4)) == null) {
                // Check that the squares the king moves through are not under attack
                if (isSquareSafe(new MyPosition(startPosition.getRow(), 4), opponentTeam) && isSquareSafe(new MyPosition(startPosition.getRow(), 3), opponentTeam)) {
                    // Add queenside castling move
                    moves.add(new MyMove(startPosition, new MyPosition(startPosition.getRow(), 3), null));
                }
            }
        }

        // En passant
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN
                && lastMove != null
                && lastMove.getEndPosition().getRow() == startPosition.getRow()
                && Math.abs(lastMove.getEndPosition().getRow() - lastMove.getStartPosition().getRow()) == 2
                && Math.abs(lastMove.getEndPosition().getColumn() - startPosition.getColumn()) == 1) {
            ChessPiece lastMovedPiece = board.getPiece(lastMove.getEndPosition());
            if (lastMovedPiece.getPieceType() == ChessPiece.PieceType.PAWN
                    && lastMovedPiece.getTeamColor() != piece.getTeamColor()) {
                int verticalDirection = piece.getTeamColor() == TeamColor.WHITE ? 1 : -1;
                MyPosition newPos = new MyPosition(startPosition.getRow() + verticalDirection, lastMove.getEndPosition().getColumn());
                moves.add(new MyMove(startPosition, newPos, null));
            }
        }
        return moves;
    }

    private boolean isSquareSafe(ChessPosition square, TeamColor opponentTeam) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new MyPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == opponentTeam) {
                    // Check if the piece can attack the specified square
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(square)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    // Extracted method to make move_internal easier to read
    private static ChessPiece getPromotedChessPiece(ChessPiece.PieceType newPieceType, ChessPiece piece) {
        ChessPiece promotedPiece = null;
        if (newPieceType == ChessPiece.PieceType.QUEEN) {
            promotedPiece = new MyQueen(piece.getTeamColor());
        }else if (newPieceType == ChessPiece.PieceType.ROOK) {
            promotedPiece = new MyRook(piece.getTeamColor());
        }else if (newPieceType == ChessPiece.PieceType.BISHOP) {
            promotedPiece = new MyBishop(piece.getTeamColor());
        }else if (newPieceType == ChessPiece.PieceType.KNIGHT) {
            promotedPiece = new MyKnight(piece.getTeamColor());
        }
        return promotedPiece;
    }

    private void toggleTeamTurn() {
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    // Makes a temporary move to check validity
    private void makeMove_internal(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        // move the piece
        ChessPiece.PieceType newPieceType = move.getPromotionPiece();
        if (newPieceType != null) {
            ChessPiece promotedPiece = getPromotedChessPiece(newPieceType, piece);
            board.addPiece(end, promotedPiece);
        }else { board.addPiece(end, piece); }
        // "remove" piece from start position to complete the move
        board.addPiece(start, null);
    }
    // Actual moves
    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        if (piece != null && piece.getTeamColor() == teamTurn) {
            Collection<ChessMove> tmp = validMoves(start);
            if (tmp.contains(move)) {
                // check if castling kingside
                if (piece.getPieceType() == ChessPiece.PieceType.KING && end.getColumn() - start.getColumn() > 1) {
                    ChessPosition kingRookStart = new MyPosition(start.getRow(), 8);
                    ChessPosition kingRookEnd = new MyPosition(start.getRow(), end.getColumn() - 1);
                    // Move the rook
                    makeMove_internal(new MyMove(kingRookStart, kingRookEnd, null));
                    board.getPiece(kingRookEnd).bumpMoveCt();
                }
                // check if castling queenside
                if (piece.getPieceType() == ChessPiece.PieceType.KING && end.getColumn() - start.getColumn() < -1) {
                    ChessPosition kingRookStart = new MyPosition(start.getRow(), 1);
                    ChessPosition kingRookEnd = new MyPosition(start.getRow(), end.getColumn() + 1);
                    // Move the rook
                    makeMove_internal(new MyMove(kingRookStart, kingRookEnd, null));
                    board.getPiece(kingRookEnd).bumpMoveCt();
                }
                // En passant
                if (start.getColumn() != end.getColumn() && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                    // remove the enemy pawn
                    MyPosition tempPos = new MyPosition(start.getRow(), end.getColumn());
                    board.addPiece(tempPos, null);
                }
                makeMove_internal(move);
                board.getPiece(end).bumpMoveCt();
                toggleTeamTurn();
            } else {
                throw new InvalidMoveException("Invalid move.");
            }
        } else {
            throw new InvalidMoveException("Invalid piece or team's turn.");
        }
        // track the last move made
        lastMove = move;
    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {
        List<ChessMove> moves = new ArrayList<>();
        MyPosition kingPos = null;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                MyPosition pos = new MyPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                // if the king is found, record the king's position
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING
                        && piece.getTeamColor() == teamColor) {
                    kingPos = pos;
                }
                // if the piece is an enemy piece, add it to the moves list
                if (piece != null && piece.getTeamColor() != teamColor) {
                    moves.addAll(piece.pieceMoves(board, pos));
                }
            }
        }
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(kingPos)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        MyBoard originalBoard = (MyBoard)board.clone();
        if (!isInCheck(teamColor)) { return false; }
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                MyPosition pos = new MyPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                // if the piece is a good guy piece, see if it can move
                if (piece != null && piece.getTeamColor() == teamColor) {
                    for (ChessMove move : piece.pieceMoves(board, pos)) {
                        makeMove_internal(move);
                        boolean isCheck = isInCheck(piece.getTeamColor());
                        setBoard((MyBoard)originalBoard.clone());
                        if (!isCheck) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                MyPosition pos = new MyPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                // if the piece is a good guy piece, see if it can move
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(pos).isEmpty()) { return false; }
                }
            }
        }
        return true;
    }

    @Override
    public void setBoard(ChessBoard board) { this.board = (MyBoard) board; }

    @Override
    public ChessBoard getBoard() { return board; }
}
