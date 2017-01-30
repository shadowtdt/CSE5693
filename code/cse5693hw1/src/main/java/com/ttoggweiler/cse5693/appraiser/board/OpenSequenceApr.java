package com.ttoggweiler.cse5693.appraiser.board;

import com.ttoggweiler.cse5693.TicTacToe.board.BoardManager;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import com.ttoggweiler.cse5693.TicTacToe.player.BasePlayer;
import com.ttoggweiler.cse5693.appraiser.BaseAppraiser;

import java.util.UUID;

/**
 * Appraises board based on the number of sequences that do not have an opponent occupant
 */
public class OpenSequenceApr extends BaseAppraiser<Move>
{
    public static final int OPEN_SEQUENCE_VALUE = 5;

    @Override
    public float appraise(Move move)
    {
        float value = 0f;
        UUID myPlayer = move.getPlayer();
        BoardManager bm = new BoardManager(move.getBoard());
        int length = bm.size()-1;
        
        for(int i = 0; i < length; i++){
            // Check horizontals
            if(bm.findMatchingInSequence(i,0,i,length ,true).filter(myPlayer::equals).isPresent())
                value += OPEN_SEQUENCE_VALUE;
            // Check verticals
            if(bm.findMatchingInSequence(0,i,length,i,true).filter(myPlayer::equals).isPresent())
                value += OPEN_SEQUENCE_VALUE;
        }

        // Check 0,0 -> N,N
        if(bm.findMatchingInSequence(0,0,length,length ,true).filter(myPlayer::equals).isPresent())
            value += OPEN_SEQUENCE_VALUE;
        // Check 0,N -> N,0
        if(bm.findMatchingInSequence(0,length,length,0,true).filter(myPlayer::equals).isPresent())
            value += OPEN_SEQUENCE_VALUE;

        return getWeight() * value;
    }
}
