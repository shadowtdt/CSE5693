package com.ttoggweiler.cse5693.appraiser.board;

import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import com.ttoggweiler.cse5693.appraiser.AggregateAppraiser;
import com.ttoggweiler.cse5693.appraiser.BaseAppraiser;

/**
 * Board evaluator that uses sub-appraisers to
 */
public class BoardAppraiser extends AggregateAppraiser<Move>
{
    @Override
    public float appraise(Move input)
    {
        Float value = 0f;
        for(BaseAppraiser<Move> appraiser : getSubAppraisers())
        {
            value += appraiser.appraise(input);
        }
        return getWeight() + value;
    }
}
