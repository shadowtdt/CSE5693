package com.ttoggweiler.cse5693;

import com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import com.ttoggweiler.cse5693.TicTacToe.player.BasePlayer;
import com.ttoggweiler.cse5693.TicTacToe.player.CommandLinePlayer;
import com.ttoggweiler.cse5693.TicTacToe.player.MLPlayer;
import com.ttoggweiler.cse5693.TicTacToe.player.RandomPlayer;
import com.ttoggweiler.cse5693.TicTacToe.player.RulePlayer;
import com.ttoggweiler.cse5693.TicTacToe.player.SequentialPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
        BasePlayer winner = null;

        //winner = MLPvsRand(3,100,null,winner);
        //winner = MLPvsMLP(3,100,null, winner);
        playGames(new RulePlayer(), new MLPlayer(),3,100,null);
        //randomVsSequential(3, 100, null);
        //humanVsHuman(3,99,null);
    }

    private static BasePlayer playGames(BasePlayer player1, BasePlayer player2, int boardSize, int iterations, Set<Move[]> initMoves)
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

        BasePlayer tmp;
        Iterator<Move[]> initItr = initMoves.iterator();
        for (int i = 0; i < iterations; i++) {
            Move[] initMove = (initItr.hasNext()) ? initItr.next() : null;
            TicTacToeGame game = new TicTacToeGame(boardSize, player1, player2, initMove);
            game.startGame();

            List<Move> trace = game.getMoveManager().getMoves();

            UUID winnerID = game.getBoardManager().findWinner().orElse(tie.getId());
            String winnerName;
            if(winnerID.equals(player1.getId()))winnerName = player1.getName();
            else if(winnerID.equals(player2.getId()))winnerName = player2.getName();
            else winnerName = tie.getName();

            int score = scoreChart.get(winnerName);
            scoreChart.put(winnerName, ++score);
            log.warn("Game {} Stats {}:", i, scoreChart.toString());

            // Rotate first player
            tmp = player1;
            player1 = player2;
            player2 = tmp;
        }
        return (scoreChart.get(player1.getName()) > scoreChart.get(player2.getName())) ? player1 : player2;
    }

    private static BasePlayer randomVsSequential(int boardSize, int iterations, Set<Move[]> initMoves)
    {
        BasePlayer p1 = new RandomPlayer();
        BasePlayer p2 = new SequentialPlayer();
        return playGames(p1, p2, boardSize, iterations, initMoves);
    }

    private static BasePlayer humanVsHuman(int boardSize, int iterations, Set<Move[]> initMoves)
    {
        BasePlayer p1 = new CommandLinePlayer();
        BasePlayer p2 = new CommandLinePlayer();
        return playGames(p1, p2, boardSize, iterations, initMoves);
    }

    private static BasePlayer MLPvsRand(int boardSize, int iterations, Set<Move[]> initMoves, BasePlayer ai)
    {
        BasePlayer p1 = new RandomPlayer();
        BasePlayer p2 =  (ai == null)? new MLPlayer("MLPvRand"):ai;
        return playGames(p1, p2, boardSize, iterations, initMoves);
    }

    private static BasePlayer MLPvsMLP(int boardSize, int iterations, Set<Move[]> initMoves, BasePlayer ai)
    {
        BasePlayer p1 = new MLPlayer("MLPvMLP");
        BasePlayer p2 = (ai == null)? new MLPlayer("MLPvMLP2"):ai;
        return playGames(p1, p2, boardSize, iterations, initMoves);
    }
}
