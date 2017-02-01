package com.ttoggweiler.cse5693.learner;

import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import com.ttoggweiler.cse5693.appraiser.BaseAppraiser;
import com.ttoggweiler.cse5693.appraiser.board.BoardAppraiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ListIterator;

/**
 * Improves the appraiser using the training data output by the critic
 */
public class TTTExpGeneralizer
{

    public static final Float ADJUSTMENT_MODIFIER = 0.1F;
    private static Logger log = LoggerFactory.getLogger(TTTExpGeneralizer.class);


    public static void updateAppraiser(List<Move> moves, BoardAppraiser appraiser)
    {
        ListIterator<Move> moveItr = moves.listIterator(moves.size());
        while(moveItr.hasPrevious())
        {
            Move m = moveItr.previous();
            if(m.getTrainingValue() != null)
            {
                if(m.getEstimatedValue() == null)m.setEstimatedValue(appraiser.appraise(m));

                for(BaseAppraiser apr : appraiser.getSubAppraisers())
                {
                    Float diff = (m.getTrainingValue() - m.getEstimatedValue());
                    Float newWeight =  apr.getWeight() + ((ADJUSTMENT_MODIFIER * diff) * (apr.appraise(m)/apr.getWeight()));
                    log.debug("apt:{}\tT:{} E:{} D:{} W:{} -> {}",apr.getClass().getSimpleName(), m.getTrainingValue(),m.getEstimatedValue(),diff,apr.getWeight(),newWeight);
                    if(newWeight == 0)
                        newWeight = 0.00001f;
                    apr.setWeight(newWeight);
                }
            }
        }
    }
}