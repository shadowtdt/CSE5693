package com.ttoggweiler.cse5693.appraiser.board;

import com.ttoggweiler.cse5693.TicTacToe.board.BoardManager;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import com.ttoggweiler.cse5693.appraiser.BaseAppraiser;

import java.util.UUID;

/**
 * Appraises board based on the number of winning sequences that do not have an opponent occupant
 */
public class SequenceApr extends BaseAppraiser<Move>
{
    public static final int DEFAULT_SEQUENCE_VALUE = 1;
    public static final int DEFAULT_OPPONENT_SEQUENCE_VALUE = 0;
    public static final int DEFAULT_MIN_MATCHING = 1;

    private int sequenceValue;
    private int opponentSequenceValue;
    private int minMatching;
    private UUID myPlayer;

    public SequenceApr()
    {
        this(DEFAULT_SEQUENCE_VALUE, DEFAULT_OPPONENT_SEQUENCE_VALUE,DEFAULT_MIN_MATCHING);
    }

    public SequenceApr(int sequenceValue, int opponentSequenceValue, int minMatching)
    {
        this.sequenceValue = sequenceValue;
        this.opponentSequenceValue = opponentSequenceValue;
        this.minMatching = minMatching;
    }

    @Override
    public Float appraise(Move move)
    {
        float value = 0f;
        myPlayer = move.getPlayer();
        BoardManager bm = new BoardManager(move.getBoard());
        int length = bm.size()-1;
        
        for(int i = 0; i <= length; i++){
            // Check verticals
            value += bm.findMatchingInSequence(0,i,length,i,minMatching,true)
                    .map(this::toValue).orElse(0);
            // Check horizontals
            value += bm.findMatchingInSequence(i,0,i,length, minMatching,true)
                    .map(this::toValue).orElse(0);
        }

        // Check 0,0 -> N,N
        value += bm.findMatchingInSequence(0,0,length,length,minMatching ,true)
                .map(this::toValue).orElse(0);
        // Check 0,N -> N,0
        value += bm.findMatchingInSequence(0,length,length,0,minMatching,true)
                .map(this::toValue).orElse(0);

        return getWeight() * value;
    }

    private int toValue(UUID id)
    {
        return id.equals(myPlayer) ? sequenceValue : opponentSequenceValue;
    }
}
