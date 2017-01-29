package com.ttoggweiler.cse5693.appraiser.board;

import com.ttoggweiler.cse5693.TicTacToe.board.BoardManager;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import com.ttoggweiler.cse5693.TicTacToe.player.BasePlayer;
import com.ttoggweiler.cse5693.appraiser.AggregateAppraiser;
import com.ttoggweiler.cse5693.appraiser.BaseAppraiser;

import java.util.Optional;
import java.util.UUID;

/**
 * Board evaluator that uses sub-appraisers to give a board an approximate value
 * the sub-appraisers are features of the board
 */
public class BoardAppraiser extends AggregateAppraiser<Move>
{

    public static final int LOST_GAME_VALUE = -100;
    public static final int WON_GAME_VALUE = 100;
    public static final int TIE_GAME_VALUE = 10;

    @Override
    public float appraise(Move input)
    {
        // Check if board has a winner
        BoardManager bm = new BoardManager(input.getBoard());
        Optional<UUID> winner = bm.findWinner();
        if (bm.findWinner().isPresent()) {
            if (winner.get().equals(input.getPlayer()))
                return WON_GAME_VALUE;
            else
                return LOST_GAME_VALUE;
        } else if (bm.getState().equals(BoardManager.BoardState.FULL)) {
            return TIE_GAME_VALUE;
        } else { // Otherwise summation the sub-appraisers
            float value = 0f;
            for (BaseAppraiser<Move> appraiser : getSubAppraisers())
                value += appraiser.appraise(input);
            return getWeight() + value;
        }
    }

}
