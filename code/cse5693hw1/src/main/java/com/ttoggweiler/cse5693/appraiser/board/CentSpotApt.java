package com.ttoggweiler.cse5693.appraiser.board;

import com.ttoggweiler.cse5693.TicTacToe.board.BoardManager;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import com.ttoggweiler.cse5693.appraiser.BaseAppraiser;

import java.util.UUID;

/**
 * Created by ttoggweiler on 1/31/17.
 */
public class CentSpotApt extends BaseAppraiser<Move>
{
    public static final int DEFAULT_CENTER_VALUE = 1;
    public static final int DEFAULT_OPPONENT_CENTER_VALUE = 0;

    private int centerValue;
    private int opponentCenterValue;
    private UUID myPlayer;

    public CentSpotApt()
    {
        centerValue = DEFAULT_CENTER_VALUE;
        opponentCenterValue = DEFAULT_OPPONENT_CENTER_VALUE;
    }

    public CentSpotApt(int centerValue, int opponentCenterValue)
    {
        this.centerValue = centerValue;
        this.opponentCenterValue = opponentCenterValue;
    }

    @Override
    public Float appraise(Move move)
    {
        myPlayer = move.getPlayer();
        BoardManager bm = new BoardManager(move.getBoard());
        int length = bm.size()-1;


        return bm.findPlayer(length/2,length/2) // get player in center
                .map(p -> (p.equals(myPlayer)? centerValue:opponentCenterValue) * getWeight()) // map to value * weight
                .orElse(0f); // if not occupant, 0
    }
}
