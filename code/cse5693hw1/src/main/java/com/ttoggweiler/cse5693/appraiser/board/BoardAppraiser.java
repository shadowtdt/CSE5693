package com.ttoggweiler.cse5693.appraiser.board;

import com.ttoggweiler.cse5693.TicTacToe.board.BoardManager;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;
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

    public static final Float LOST_GAME_VALUE = -100f;
    public static final Float WON_GAME_VALUE = 100f;
    public static final Float TIE_GAME_VALUE = 10f;

    @Override
    public Float appraise(Move input)
    {
        // Check if board has a winner
        BoardManager bm = new BoardManager(input.getBoard());
        Optional<UUID> winner = bm.findWinner();
        Float value = 0f;
        if (bm.findWinner().isPresent()) {
            if (winner.get().equals(input.getPlayer()))
                value = WON_GAME_VALUE;
            else
                value = LOST_GAME_VALUE;
        } else if (bm.getState().equals(BoardManager.BoardState.FULL)) {
            value = TIE_GAME_VALUE;
        } else { // Otherwise summation of the sub-appraisers
            for (BaseAppraiser<Move> appraiser : getSubAppraisers())
                value += appraiser.appraise(input);
            value = getWeight() + value;
        }
        if(value < LOST_GAME_VALUE) value = LOST_GAME_VALUE;
        if(value > WON_GAME_VALUE) value = WON_GAME_VALUE;
        input.setEstimatedValue(value);
        return value;
    }

}
