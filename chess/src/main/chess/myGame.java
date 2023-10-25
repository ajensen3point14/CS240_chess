package chess;

import chess.*;

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
        /*
        Helper method that filters out moves from piece moves that put you into or leave you in check
         */
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
        return moves;
    }

    private void makeMove_internal(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        // move the piece, making the previous location null
        board.addPiece(end, piece);
        board.addPiece(start, null);
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
                // TODO: Implement additional logic for handling captures, castling, en passant
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
        if (teamTurn != teamColor) { return false; }
        if (isInCheck(teamColor)) {
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
