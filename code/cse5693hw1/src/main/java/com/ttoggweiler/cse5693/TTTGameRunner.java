package com.ttoggweiler.cse5693;

import com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import com.ttoggweiler.cse5693.TicTacToe.player.BasePlayer;
import com.ttoggweiler.cse5693.TicTacToe.player.CommandLinePlayer;
import com.ttoggweiler.cse5693.TicTacToe.player.RandomPlayer;
import com.ttoggweiler.cse5693.TicTacToe.player.SequentialPlayer;
import com.ttoggweiler.cse5693.appraiser.BaseAppraiser;
import com.ttoggweiler.cse5693.appraiser.board.BoardAppraiser;
import com.ttoggweiler.cse5693.appraiser.board.BoardCornerApr;
import com.ttoggweiler.cse5693.appraiser.board.OpenSequenceApr;
import com.ttoggweiler.cse5693.experience.TicTacToeCritic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Runs the tick-tack-toe game with the specified configuration
 */
public class TTTGameRunner
{
    private static Logger log = LoggerFactory.getLogger(TTTGameRunner.class);

    public static void main(String... args)
    {
        log.info("==== Tic-Tac-Toe Game Runner ====");

        randomVsSequential(3, 100, null);
        //humanVsHuman(3,1,null);
    }

    public static BasePlayer playGames(BasePlayer player1, BasePlayer player2, int boardSize, int iterations, Set<Move[]> initMoves)
    {
        if (initMoves == null) initMoves = new HashSet<>();
        if (boardSize < 3) boardSize = 3;
        if (iterations < 1) iterations = 1;

        BasePlayer tie = new RandomPlayer();
        tie.setName("Tie");

        HashMap<String, Integer> scoreChart = new HashMap<>();
        scoreChart.put(player1.getName(), 0);
        scoreChart.put(player2.getName(), 0);
        scoreChart.put(tie.getName(), 0);

        Iterator<Move[]> initItr = initMoves.iterator();
        for (int i = 0; i < iterations; i++) {
            Move[] initMove = (initItr.hasNext()) ? initItr.next() : null;
            TicTacToeGame game = new TicTacToeGame(boardSize, player1, player2, initMove);
            game.startGame();

            evaluateGame(game); // Appraiser

            UUID winnerID = game.findWinner().orElse(tie.getId());
            String winnerName = (player1.getId().equals(winnerID) ? player1.getName() : player2.getName());
            int score = scoreChart.get(winnerName);
            scoreChart.put(winnerName, ++score);
            log.warn("Game {} Stats {}:", i, scoreChart.toString());
        }
        return (scoreChart.get(player1.getName()) > scoreChart.get(player2.getName())) ? player1 : player2;
    }

    public static BasePlayer randomVsSequential(int boardSize, int iterations, Set<Move[]> initMoves)
    {
        BasePlayer p1 = new RandomPlayer();
        BasePlayer p2 = new SequentialPlayer();

        p1.setName("Rand");
        p2.setName("Sequ");

        return playGames(p1, p2, boardSize, iterations, initMoves);
    }

    public static BasePlayer humanVsHuman(int boardSize, int iterations, Set<Move[]> initMoves)
    {
        BasePlayer p1 = new CommandLinePlayer();
        BasePlayer p2 = new CommandLinePlayer();
        return playGames(p1, p2, boardSize, iterations, initMoves);
    }

    public static void evaluateGame(TicTacToeGame game)
    {
        BoardAppraiser bApr = new BoardAppraiser();
        bApr.addSubAppraiser(new BoardCornerApr());
        bApr.addSubAppraiser(new OpenSequenceApr());

        bApr.initilizeAllWeights(2);

        List<Move> trace = game.getMoveManager().getMoves();
        TicTacToeCritic.critique(trace, bApr);

        for(Move m : trace)
            log.debug("{} Train: {}",Arrays.toString(m.getMove()),m.getTrainingValue());

    }


}
