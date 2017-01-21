package com.ttoggweiler.cse5693;

import com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame;
import com.ttoggweiler.cse5693.TicTacToe.player.BasePlayer;
import com.ttoggweiler.cse5693.TicTacToe.player.RandomPlayer;
import com.ttoggweiler.cse5693.TicTacToe.player.SequentialPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Runs the tick-tack-toe game with the specified configuration
 */
public class TTTGameRunner
{
    private static Logger log = LoggerFactory.getLogger(TTTGameRunner.class);

    public static void main(String... args)
    {
        log.info("==== Tic-Tac-Toe Game Runner ====");

        BasePlayer p1 = new RandomPlayer();
        BasePlayer p2 = new SequentialPlayer();
        BasePlayer tie = new RandomPlayer();

        tie.setName("Tie");
        p1.setName("Rand");
        p2.setName("Sequ");

        HashMap<String, Integer> scoreChart = new HashMap<>();
        scoreChart.put(p1.getName(), 0);
        scoreChart.put(p2.getName(), 0);
        scoreChart.put(tie.getName(), 0);
        int games = 0;
        while (true) {
            TicTacToeGame game = new TicTacToeGame(3, p1, p2);
            game.start();
            games++;
            String result = game.findWinner().orElse(tie).getName();
            int score = scoreChart.get(result);
            scoreChart.put(result,++score);
            if(games %100000 == 0)
                log.warn("Games: "+games+" Stats {}", scoreChart.toString());
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                log.error("Sleep between games was interrupted", e);
//            }
        }
    }


}
