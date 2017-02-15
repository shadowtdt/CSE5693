package com.ttoggweiler.cse5693.TicTacToe.player;

import com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame;
import com.ttoggweiler.cse5693.TicTacToe.board.BoardManager;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import com.ttoggweiler.cse5693.appraiser.BaseAppraiser;
import com.ttoggweiler.cse5693.appraiser.board.BoardAppraiser;
import com.ttoggweiler.cse5693.appraiser.board.CentSpotApt;
import com.ttoggweiler.cse5693.appraiser.board.CornerCtApr;
import com.ttoggweiler.cse5693.appraiser.board.SequenceApr;
import com.ttoggweiler.cse5693.experience.TTTCritic;
import com.ttoggweiler.cse5693.learner.TTTExpGeneralizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This player Learns to make make moves based on the value of all the possible boards
 */
public class MLPlayer extends BasePlayer
{
    public static final Float DEFAULT_WEIGHT_VALUE = 0.1f;
    private static Logger log = LoggerFactory.getLogger(MLPlayer.class);

    private BoardAppraiser bApr;

    public MLPlayer()
    {
        this("MLP",null);
    }

    public MLPlayer(String name)
    {
        this(name,null);
    }

    public MLPlayer(BoardAppraiser apr)
    {
        this("MLP",apr);
    }

    public MLPlayer(String name, BoardAppraiser apr)
    {
        /*
        Here we are init the feature set that will be used to give boards values
         */
        setName(name);
        if(apr == null) {
            bApr = new BoardAppraiser();

            CornerCtApr mCorners = new CornerCtApr(1,0);
            mCorners.setName("Corners");
            bApr.addSubAppraiser(mCorners);

            CornerCtApr oppCorners = new CornerCtApr(0,1);
            oppCorners.setName("OppCorners");
            bApr.addSubAppraiser(oppCorners);

            SequenceApr myTwoRow = new SequenceApr(1,0,2);
            myTwoRow.setName("TwoInRow");
            bApr.addSubAppraiser(myTwoRow);

            SequenceApr oppTwoRow =new SequenceApr(0,1,2);
            oppTwoRow.setName("OppTwoInRow");
            bApr.addSubAppraiser(oppTwoRow);

            SequenceApr myOneInRow =new SequenceApr(1,0,1);
            myOneInRow.setName("OneInRow");
            bApr.addSubAppraiser(myOneInRow);

            SequenceApr oppOneInRow =new SequenceApr(0,1,1);
            oppOneInRow.setName("OppOneInRow");
            bApr.addSubAppraiser(oppOneInRow);

            CentSpotApt myCenter = new CentSpotApt(1,0);
            myCenter.setName("Center");
            bApr.addSubAppraiser(myCenter);

            CentSpotApt oppCenter = new CentSpotApt(0,1);
            oppCenter.setName("OppCenter");
            bApr.addSubAppraiser(oppCenter);


            bApr.initilizeAllWeights(DEFAULT_WEIGHT_VALUE);
        }
    }

    @Override
    public Move getNextMove(UUID gameId)
    {
        return getMoveForHighestValuedBoard(gameId);
    }

    @Override
    public void gameStarted(TicTacToeGame game)
    {

    }

    @Override
    public void gameEnded(UUID gameId, boolean winner)
    {
        evaluateGame(getGame(gameId).getMoveManager().getMoves(),bApr);
        learnFromTrace(getGame(gameId).getMoveManager().getMoves(),bApr);
    }

    private Move getMoveForHighestValuedBoard(UUID gameId)
    {
        BoardManager bm = getGame(gameId).getBoardManager();
        UUID[][] board = bm.getCurrentBoard();

        List<Move> openMoves = new ArrayList<>();
        for(int i = 0; i < bm.size(); i++)
            for(int j = 0; j < bm.size(); j++)
                if(board[i][j] == null) // for all empty spaces
                {
                    board[i][j] = getId(); // set space to my id
                    Move m = new Move(getId(),i,j); // create a move for this board state
                    m.setBoard(board);
                    bApr.appraise(m);// estimate the value of the board
                    openMoves.add(m);
                    board[i][j] = null; // reset board for next loop
                }

        assert !openMoves.isEmpty() : "No valid moves found while searching for the best board";

        openMoves.sort(Comparator.comparingDouble(Move::getEstimatedValue));
        openMoves.forEach(m-> log.debug("Move:{}, Value:{}", Arrays.toString(m.getMove()),m.getEstimatedValue()));

        if(openMoves.size() == 1)return openMoves.get(0);
        Move oneOfBest = null;
        try {
            oneOfBest = openMoves.stream().filter(m ->
                    m.getEstimatedValue() >= openMoves.get(openMoves.size()-1).getEstimatedValue())
                    .sorted(Comparator.comparing(Move::getId))
                    .findFirst().get();
        } catch (Exception e) {
            log.error("should never happen", e);
        }
        return oneOfBest;//openMoves.get(openMoves.size()-1);
    }

    /**
     * Evaluates a game trace and assigns training values to each move
     * @param moves
     * @param bApr
     */
    private static void evaluateGame(List<Move> moves, BoardAppraiser bApr)
    {
        TTTCritic.generateTrainingValues(moves, bApr);
        for(Move m : moves)
            log.debug("{} Train Value: {}", Arrays.toString(m.getMove()),m.getTrainingValue());
    }

    /**
     * Using the training values generated by {@link #evaluateGame(List, BoardAppraiser)}
     * this method will update the weights on the given appraiser
     * @param trace
     * @param bApr
     */
    private static void learnFromTrace(List<Move> trace,BoardAppraiser bApr)
    {
        HashMap<String, Float> previousWeights = new HashMap<>();
        bApr.getSubAppraisers().forEach(apr ->
                previousWeights.put(apr.getName(),apr.getWeight()));

        log.info("Updating weights based on training data");
        TTTExpGeneralizer.updateAppraiser(trace,bApr);

        bApr.getSubAppraisers().stream().sorted(Comparator.comparing(BaseAppraiser::getName))
                .forEach(apr -> log.info("{}: {} --> {}"
                , apr.getName()
                ,previousWeights.get(apr.getName())
                ,apr.getWeight()));
    }

}
