package com.ttoggweiler.cse5693.experience;

import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import com.ttoggweiler.cse5693.appraiser.BaseAppraiser;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Translates a move history list and an appraiser to training experience, Vtrain,
 * This is done by setting the value for the given move to its successors appraised value.
 *
 * Vt(b) <- V~(Successor(b))
 */
public class TicTacToeCritic
{
    public static Map<Move,Float> critique(List<Move> moves, BaseAppraiser<Move> appraiser)
    {
        if(moves == null || moves.isEmpty() || appraiser == null)
            throw new NullPointerException("Critic received null/empty moves list or a null appraiser");

        int size = moves.size() - 1;
        Move lastMove = moves.get(size);
        float lastMoveApr = appraiser.appraise(lastMove);

        if(lastMoveApr != 100 && lastMoveApr != -100 && lastMoveApr!= 10)
            throw new IllegalArgumentException("Critic received a non-winning game trace!");

        Map<Move,Float> vTrain = new HashMap<>();
        ListIterator<Move> current = moves.listIterator(moves.size() - 2); // c(n-1)
        ListIterator<Move> succussor = moves.listIterator(moves.size() - 1);// s(n)

        vTrain.put(current.previous(),lastMoveApr);// c(n-2)
        while(current.hasPrevious())
        {
//            current.previous(); // c(n-3)
//            succussor.previous(); // s(n-1)
            vTrain.put(current.previous(),appraiser.appraise(succussor.previous())); // c(n-4) & s(n-2)..
        }
        return vTrain;
    }
}
