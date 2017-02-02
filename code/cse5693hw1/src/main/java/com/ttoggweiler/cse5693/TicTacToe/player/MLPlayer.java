package com.ttoggweiler.cse5693.TicTacToe.player;

import com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame;
import com.ttoggweiler.cse5693.TicTacToe.board.BoardManager;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;
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
 * Created by ttoggweiler on 1/30/17.
 */
public class MLPlayer extends BasePlayer
{
    public static final Float DEFAULT_WEIGHT_VALUE = 2f;
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
        setName(name);
        if(apr == null) {
            bApr = new BoardAppraiser();
            bApr.addSubAppraiser(new CornerCtApr(1,0));
            bApr.addSubAppraiser(new CornerCtApr(0,1));

            bApr.addSubAppraiser(new SequenceApr(1,0,2));
            bApr.addSubAppraiser(new SequenceApr(0,1,2));

            bApr.addSubAppraiser(new SequenceApr(1,0,1));
            bApr.addSubAppraiser(new SequenceApr(0,1,1));

            bApr.addSubAppraiser(new CentSpotApt(0,1));
            bApr.addSubAppraiser(new CentSpotApt(1,0));

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

    private static void evaluateGame(List<Move> moves, BoardAppraiser bApr)
    {
        TTTCritic.generateTrainingValues(moves, bApr);
        for(Move m : moves)
            log.debug("{} Train: {}", Arrays.toString(m.getMove()),m.getTrainingValue());
    }

    private static void learnFromTrace(List<Move> trace,BoardAppraiser bApr)
    {
        HashMap<String, Float> previousWeights = new HashMap<>();
        bApr.getSubAppraisers().forEach(apr ->
                previousWeights.put(apr.getClass().getSimpleName(),apr.getWeight()));

        TTTExpGeneralizer.updateAppraiser(trace,bApr);

        bApr.getSubAppraisers().forEach(apr -> log.info("{}: {} --> {}"
                , apr.getClass().getSimpleName()
                ,previousWeights.get(apr.getClass().getSimpleName())
                ,apr.getWeight()));
    }
}
