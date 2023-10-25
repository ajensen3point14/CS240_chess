package chess;

import chess.pieces.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class myGame implements ChessGame {
    private myBoard board;
    private TeamColor teamTurn;
    public myGame() {
        this.board = new myBoard();
        this.teamTurn = TeamColor.WHITE; // White starts the game
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
        myBoard originalBoard = (myBoard) board.clone();
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return moves;
        }
        for (ChessMove move : piece.pieceMoves(board, startPosition)) {
            makeMove_internal(move);
            if (!isInCheck(piece.getTeamColor())) {
                moves.add(move);
            }
            setBoard((myBoard)originalBoard.clone());
        }
        // Castling
        if (piece.getPieceType() == ChessPiece.PieceType.KING && !piece.hasMoved()) {
            // kingside
            ChessPosition kingsideRookPos = new myPosition(startPosition.getRow(), 8);
            ChessPiece kingsideRook = board.getPiece(kingsideRookPos);
            if (kingsideRook != null &&
                    kingsideRook.getPieceType() == ChessPiece.PieceType.ROOK &&
                    !kingsideRook.hasMoved() &&
                    board.getPiece(new myPosition(startPosition.getRow(), 6)) == null &&
                    board.getPiece(new myPosition(startPosition.getRow(), 7)) == null) {
                // Check that the squares the king moves through are not under attack
                if (!isSquareUnderAttack(new myPosition(startPosition.getRow(), 6)) && !isSquareUnderAttack(new myPosition(startPosition.getRow(), 7))) {
                    // Add kingside castling move
                    moves.add(new myMove(startPosition, new myPosition(startPosition.getRow(), 7), null));
                }
            }
            // Queenside
            ChessPosition queensideRookPos = new myPosition(startPosition.getRow(), 1);
            ChessPiece queensideRook = board.getPiece(queensideRookPos);
            if (queensideRook != null &&
                    queensideRook.getPieceType() == ChessPiece.PieceType.ROOK &&
                    !queensideRook.hasMoved() &&
                    board.getPiece(new myPosition(startPosition.getRow(), 2)) == null &&
                    board.getPiece(new myPosition(startPosition.getRow(), 3)) == null &&
                    board.getPiece(new myPosition(startPosition.getRow(), 4)) == null) {
                // Check that the squares the king moves through are not under attack
                if (!isSquareUnderAttack(new myPosition(startPosition.getRow(), 4)) && !isSquareUnderAttack(new myPosition(startPosition.getRow(), 3))) {
                    // Add queenside castling move
                    moves.add(new myMove(startPosition, new myPosition(startPosition.getRow(), 3), null));
                }
            }
        }
        return moves;
    }

    // TODO: this seems like a lot of repeated code...
    private boolean isSquareUnderAttack(ChessPosition square) {
        TeamColor opponentTeam = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new myPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == opponentTeam) {
                    // Check if the piece can attack the specified square
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(square)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private void makeMove_internal(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        // move the piece, making the previous location null
        ChessPiece.PieceType newPieceType = move.getPromotionPiece();
        if (newPieceType != null) {
            ChessPiece promotedPiece = null;
            if (newPieceType == ChessPiece.PieceType.QUEEN) {
                promotedPiece = new myQueen(piece.getTeamColor());
            }else if (newPieceType == ChessPiece.PieceType.ROOK) {
                promotedPiece = new myRook(piece.getTeamColor());
            }else if (newPieceType == ChessPiece.PieceType.BISHOP) {
                promotedPiece = new myBishop(piece.getTeamColor());
            }else if (newPieceType == ChessPiece.PieceType.KNIGHT) {
                promotedPiece = new myKnight(piece.getTeamColor());
            }
            board.addPiece(end, promotedPiece);
        }else { board.addPiece(end, piece); }
        // "remove" piece from start position to complete the move
        board.addPiece(start, null);
        board.getPiece(end).setHasMoved();
    }

    private void toggleTeamTurn() {
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }
    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        if (piece != null && piece.getTeamColor() == teamTurn) {
            if (validMoves(start).contains(move)) {
                makeMove_internal(move);
                // TODO: Implement additional logic for handling castling, en passant
                toggleTeamTurn();
            } else {
                throw new InvalidMoveException("Invalid move.");
            }
        } else {
            throw new InvalidMoveException("Invalid piece or team's turn.");
        }
    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {
        List<ChessMove> moves = new ArrayList<>();
        myPosition kingPos = null;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                myPosition pos = new myPosition(row, col);
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
        myBoard originalBoard = (myBoard)board.clone();
        //if (teamTurn != teamColor) { return false; }
        if (!isInCheck(teamColor)) { return false; }
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                myPosition pos = new myPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                // if the piece is a good guy piece, see if it can move
                if (piece != null && piece.getTeamColor() == teamColor) {
                    for (ChessMove move : piece.pieceMoves(board, pos)) {
                        makeMove_internal(move);
                        boolean isCheck = isInCheck(piece.getTeamColor());
                        setBoard((myBoard)originalBoard.clone());
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
                myPosition pos = new myPosition(row, col);
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
    public void setBoard(ChessBoard board) {
        this.board = (myBoard) board;
    }

    @Override
    public ChessBoard getBoard() { return board; }
}
