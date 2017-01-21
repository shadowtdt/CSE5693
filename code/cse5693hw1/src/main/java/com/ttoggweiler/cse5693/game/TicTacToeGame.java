package com.ttoggweiler.cse5693.game;

import com.ttoggweiler.cse5693.board.Board;
import com.ttoggweiler.cse5693.board.Move;
import com.ttoggweiler.cse5693.player.BasePlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages the iteraction between player and board
 * tracks the turn and determines winner
 */
public class TicTacToeGame
{
    private UUID id = UUID.randomUUID();

    private List<Move> winningMoves = null;
    private boolean isMinMoveThresholdMet = false;
    private Board board;
    private BasePlayer player1;
    private BasePlayer player2;

    public TicTacToeGame(int size, BasePlayer player1, BasePlayer player2)
    {
        board = new Board(3);
    }

    public TicTacToeGame(BasePlayer player1, BasePlayer player2)
    {
        new TicTacToeGame(3,player1,player2);
    }

    /**
     * The Unique id for this game
     * @return the game id
     */
    public UUID getId()
    {
        return id;
    }

    void submitMove(Move move)
    {
        findWinner();
    }

    public Optional<BasePlayer> findWinner()
    {
        if(winningMoves != null && winningMoves.size() == board.size())
            return Optional.of(winningMoves.get(0).getPlayer());

        if(!checkMinMoveThreshold())return Optional.empty(); // Not enough moves for there to be a winner

        //Reduce search space by only checking the previous moves effected row/col/diagonal
        if(!checkLastMoveDiagonal())
            if(!checkLastMoveHorizontal())
                if(!checkLastMoveVertical())
                    return Optional.empty(); // if all checks fail there is no winner

        return Optional.of(winningMoves.get(0).getPlayer()); // there must be a winner to get here
    }

    /* victory checking */

    /**
     * Reduce winning search space by first checking if there have been enough moves for a winner
     * @return
     */
    private boolean checkMinMoveThreshold()
    {
        if (!isMinMoveThresholdMet) {
            isMinMoveThresholdMet = ((board.size() * 2) - 1) >= board.getCurrentMoveIndex();
        }
        return isMinMoveThresholdMet;
    }

    /**
     * Checks the horizontal of the last move played for a winning state
     * @return Optional winning player if one is found
     */
    private boolean checkLastMoveHorizontal()
    {
        Move lastMove = board.findLastMove().orElseThrow(() ->
                new IllegalStateException("Horizontal check should always be preceded by the Min-Move-Threshold check."));
        int row = lastMove.getMove()[0];
        board.findMatchingSequence(row,0, row,board.size()).ifPresent(moves -> winningMoves = moves);
        return winningMoves != null;
    }

    /**
     * Checks the vertical of the last move played for a winning state
     * @return Optional winning player if one is found
     */
    private boolean checkLastMoveVertical()
    {
        Move lastMove = board.findLastMove().orElseThrow(() ->
                new IllegalStateException("Vertical check should always be preceded by the Min-Move-Threshold check."));
        int col = lastMove.getMove()[1];
        board.findMatchingSequence(0,col,board.size(),col).ifPresent(moves -> winningMoves = moves);
        return winningMoves != null;
    }

    /**
     * Checks the diagonal of the last move played for a winning state
     * @return Optional winning player if one is found
     */
    private boolean checkLastMoveDiagonal()
    {
        Move lastMove = board.findLastMove().orElseThrow(() ->
                new IllegalStateException("Diagonal check should always be preceded by the Min-Move-Threshold check."));
        int row = lastMove.getMove()[0];
        int col = lastMove.getMove()[1];

        // Quick fail if the last move was not on a diagonal
        if(col-row == 0) // 0,0 -> N,N diagonal
            board.findMatchingSequence(0,0,board.size(),board.size()).ifPresent(moves -> winningMoves = moves);
        else if(row+col == board.size()) // 0,N -> N,0 diagonal
            board.findMatchingSequence(0,board.size(),board.size(),0).ifPresent(moves -> winningMoves = moves);

        return winningMoves != null;
    }
}
