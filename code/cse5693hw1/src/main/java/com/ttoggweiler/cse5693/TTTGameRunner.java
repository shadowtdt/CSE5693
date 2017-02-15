package com.ttoggweiler.cse5693;

import com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame;
import com.ttoggweiler.cse5693.TicTacToe.board.BoardLoader;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import com.ttoggweiler.cse5693.TicTacToe.player.BasePlayer;
import com.ttoggweiler.cse5693.TicTacToe.player.CommandLinePlayer;
import com.ttoggweiler.cse5693.TicTacToe.player.MLPlayer;
import com.ttoggweiler.cse5693.TicTacToe.player.RandomPlayer;
import com.ttoggweiler.cse5693.TicTacToe.player.RulePlayer;
import com.ttoggweiler.cse5693.TicTacToe.player.SequentialPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
        MLPlayer mlp = null;
        if(args.length > 0) mlp = parseMainArgs(args);
        if(mlp != null)
        {
            System.out.println("Finished training, starting game with CommandLinePlayer Vs " + mlp.getName());
            playGames(mlp,new CommandLinePlayer(),3,-1,null);
        }

        //MLPlayer ai = new MLPlayer("AI");
        // MLPvsRand(3,100,null,ai);
        // winner = MLPvsMLP(3,100,null, winner);
        //playGames(new RulePlayer("R1"), new RulePlayer("R2"), 3, 100, null);
        //playGames(ai, new CommandLinePlayer(),3,50,null);
        //randomVsSequential(3, 100, null);
        //humanVsHuman(3,99,null);
        log.info("==== Tic-Tac-Toe Game Runner Terminating ====");
    }

    private static BasePlayer playGames(BasePlayer player1, BasePlayer player2, int boardSize, int iterations, Set<Move[]> initMoves)
    {
        if (initMoves == null) initMoves = new HashSet<>();
        if (boardSize < 3) boardSize = 3;

        BasePlayer tie = new RandomPlayer();
        tie.setName("Tie");

        HashMap<String, Integer> scoreChart = new HashMap<>(); // track win/lose/tie
        HashMap<String, Integer> traceMap = new HashMap<>(); // track number of identical games

        scoreChart.put(player1.getName(), 0);
        scoreChart.put(player2.getName(), 0);
        scoreChart.put(tie.getName(), 0);

        BasePlayer tmp;
        Iterator<Move[]> initItr = initMoves.iterator();
        int gameNumber;
        for ( gameNumber = 1; gameNumber != iterations; gameNumber++) {
            Move[] initMove = (initItr.hasNext()) ? initItr.next() : null;
            TicTacToeGame game = new TicTacToeGame(boardSize, player1, player2, initMove);
            game.startGame();

            List<Move> trace = game.getMoveManager().getMoves();
            String traceString = trace.toString();

            UUID winnerID = game.getBoardManager().findWinner().orElse(tie.getId());
            String winnerName;
            if (winnerID.equals(player1.getId())) winnerName = player1.getName();
            else if (winnerID.equals(player2.getId())) winnerName = player2.getName();
            else winnerName = tie.getName();

            int score = scoreChart.get(winnerName);
            int traceCount = traceMap.getOrDefault(traceString, 0);
            traceMap.put(traceString, traceCount + 1);
            scoreChart.put(winnerName, ++score);
            log.info("Game:{} Stats:{} Trace:{} #{}", gameNumber, scoreChart.toString(), traceString, traceCount);

            // Rotate first player
            tmp = player1;
            player1 = player2;
            player2 = tmp;

            if(!continuePlaying(player1,player2))
                break;
        }
        Float p1Ratio = (scoreChart.get(player1.getName()) + 0f) / iterations;
        Float p2Ratio = (scoreChart.get(player2.getName()) + 0f) / iterations;
        Float tieRatio = (scoreChart.get(tie.getName()) + 0f) / iterations;

        int p1Count = scoreChart.get(player1.getName());
        int p2Count = scoreChart.get(player2.getName());
        int tieCount = scoreChart.get(tie.getName());

        int uniqueGames = traceMap.size();
        Float uniqueRatio = (uniqueGames + 0f) / iterations;
        log.info("\n**** Stats for {} games, Unique: {} ({}%) ****\n{}: {} ({}%)\n{}: {} ({}%)\n{}: {} ({}%)\n"
                ,gameNumber , uniqueGames, uniqueRatio * 100
                , player1.getName(), p1Count, p1Ratio * 100
                , player2.getName(), p2Count, p2Ratio * 100
                , tie.getName(), tieCount, tieRatio * 100);

        //traceMap.keySet().forEach(log::warn);
        return (scoreChart.get(player1.getName()) > scoreChart.get(player2.getName())) ? player1 : player2;
    }

    private static boolean continuePlaying(BasePlayer p1, BasePlayer p2)
    {
        if(p1 instanceof CommandLinePlayer)
            return ((CommandLinePlayer) p1).continuePlayingGames();
        else if(p2 instanceof CommandLinePlayer)
            return ((CommandLinePlayer) p2).continuePlayingGames();
        return true;
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
        BasePlayer p2 = (ai == null) ? new MLPlayer("MLPvRand") : ai;
        return playGames(p1, p2, boardSize, iterations, initMoves);
    }

    private static BasePlayer MLPvsMLP(int boardSize, int iterations, Set<Move[]> initMoves, BasePlayer ai)
    {
        BasePlayer p1 = new MLPlayer("MLPvMLP");
        BasePlayer p2 = (ai == null) ? new MLPlayer("MLPvMLP2") : ai;
        return playGames(p1, p2, boardSize, iterations, initMoves);
    }

    private static void trainFromFile(MLPlayer mlp, String pathToFile)
    {
        if (mlp == null) mlp = new MLPlayer("MLP_withFileTeacher");
        BasePlayer filePlayer = new BasePlayer()
        {

            @Override
            public Move getNextMove(UUID gameId)
            {
                log.error("Game loaded from file did not have enough moves!");
                getGame(gameId).quitGame();
                return null;
            }

            @Override
            public void gameStarted(TicTacToeGame game)
            {

            }

            @Override
            public void gameEnded(UUID gameId, boolean winner)
            {

            }
        };
        filePlayer.setName("FileTeacher");
        try {
            Set<Move[]> games = BoardLoader.loadGamesFromFile(pathToFile);
            playGames(filePlayer, mlp, 3, games.size(), games);
        } catch (IOException e) {
            log.error("Error loading games from file!", e);
        }
    }

    private static MLPlayer parseMainArgs(String... args)
    {
        MLPlayer mlp = new MLPlayer();
        int iterations = 100;
        int boardSize = 3;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            log.debug("Parsing arg: " + arg);


            if (arg.contains("-r") || arg.toLowerCase().contains("-file")) // file teacher
            {
                if (i + 1 >= args.length) log.error("Found arg -file , but no path was found!");
                else {
                    trainFromFile(mlp, args[i + 1]);
                    i++;
                }
            } else if (arg.contains("-t") || arg.toLowerCase().contains("-teacher")) // Player teacher
            {
                String teacher = "Rule";
                if (i + 1 >= args.length) {
                    log.warn("Found arg -teacher , but no teacher was specified defaulting to Rule Based teacher!");
                } else {
                    teacher = args[i + 1];
                }
                teacherArgument(mlp, teacher, boardSize,iterations);
                i++;
            } else if (arg.contains("-i") || arg.toLowerCase().contains("-iterations")) // set iteration counts
            {
                if (i + 1 >= args.length) {
                    log.warn("Found arg -iterations , but no value, defaulting to {}!", iterations);
                } else {
                    try {
                        iterations = Integer.parseInt(args[i + 1]);
                        i++;
                    } catch (NumberFormatException e) {
                        log.error("Failed to parse iteration integer value, iterations set to: " + iterations, e);
                    }
                }
            }
            else if (arg.contains("-b") || arg.toLowerCase().contains("-board")) // set board size
            {
                if (i + 1 >= args.length) {
                    log.warn("Found arg -board , but no value, defaulting to {} board size!", iterations);
                } else {
                    try {
                        boardSize = Integer.parseInt(args[i + 1]);
                        i++;
                    } catch (NumberFormatException e) {
                        log.error("Failed to parse board size integer value, board size set to: " + iterations, e);
                    }
                }
            }
            else if (arg.contains("-s") || arg.toLowerCase().contains("-self")) // set play verse itself
            {
                mlp = (MLPlayer) playGames(new MLPlayer("MLP_1"),new MLPlayer("MLP_2"),boardSize,iterations,null);
            }
            else {
                log.warn("Argument {} invalid!",arg);
            }
        }

        return mlp;
    }

    private static void teacherArgument(MLPlayer mlp, String teacher,int boardSize, int iterations)
    {
        if (mlp == null) mlp = new MLPlayer();

        if (teacher.toLowerCase().contains("rule")) {
            log.info("Training with rule base Teacher");
            mlp.setName("MLP_withRuleTeacher");
            playGames(mlp, new RulePlayer("RuleTeacher"), boardSize, iterations, null);
        } else if (teacher.toLowerCase().contains("rand")) {
            log.info("Training with Random based Teacher");
            mlp.setName("MLP_withRandTeacher");
            playGames(mlp, new RandomPlayer("RandTeacher"), boardSize, iterations, null);
        } else if (teacher.toLowerCase().contains("sequ")) {
            log.info("Training with Sequential Teacher");
            mlp.setName("MLP_withSequTeacher");
            playGames(mlp, new SequentialPlayer("SequTeacher"), boardSize, iterations, null);
        } else {
            log.warn("Teacher {} not found, defaulting to RuleTeacher", teacher);
            mlp.setName("MLP_withRuleTeacher");
            playGames(mlp, new RulePlayer("RuleTeacher"), boardSize, iterations, null);
        }
    }
}
