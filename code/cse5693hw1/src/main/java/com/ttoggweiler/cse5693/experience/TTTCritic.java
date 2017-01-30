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
public class TTTCritic
{
    public static void critique(List<Move> moves, BaseAppraiser<Move> appraiser)
    {
        if(moves == null || moves.isEmpty() || appraiser == null)
            throw new NullPointerException("Critic received null/empty moves list or a null appraiser");

        int size = moves.size() - 1;
        Move lastMove = moves.get(size);
        float lastMoveApr = appraiser.appraise(lastMove);
        lastMove.setTrainingValue(lastMoveApr);

        // Set last move -1/-2 based on the winning moves value
        if(lastMoveApr == 100){
            moves.get(size-1).setTrainingValue(-100);// set losing value for winningMove - 1
            moves.get(size-2).setTrainingValue(100);// set training value for winningMove - 2
        }
        else if(lastMoveApr == 10){
            moves.get(size-1).setTrainingValue(10);// set tying value for lastMove - 1
            moves.get(size-2).setTrainingValue(10);// set tying value for lastMove - 2
        }
        else throw new IllegalArgumentException("Critic received a game trace that did not end with a winner or a tie, unable to determine training values for moves!");

        ListIterator<Move> successor = moves.listIterator(size);
        ListIterator<Move> current = moves.listIterator(size - 2);


        while(current.hasPrevious())
            current.previous().setTrainingValue(appraiser.appraise(successor.previous()));
    }
}
