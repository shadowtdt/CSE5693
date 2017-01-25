package com.ttoggweiler.cse5693.TicTacToe.player;

import com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

/**
 * This player gets input from the user through terminal prompts
 */
public class CommandLinePlayer extends BasePlayer
{
    private static final Logger log = LoggerFactory.getLogger(CommandLinePlayer.class);

    private Scanner keyboard = new Scanner(System.in);


    @Override
    public Move getNextMove(UUID gameId)
    {
        return getMoveFromUser(getGame(gameId));
    }

    @Override
    public void gameStarted(TicTacToeGame game)
    {
        games.put(game.getId(),game);
        printCmds();
    }

    @Override
    public void gameEnded(UUID gameId, boolean winner)
    {

    }

    private Move getMoveFromUser(TicTacToeGame game)
    {
        boolean valid = false;
        Move m = null;
        while (!valid) {

            log.info("Player {} turn, input next move:", getName());
            String input = keyboard.nextLine();
            if (input == null || input.trim().isEmpty()) continue;

            if (input.contains(",")) {
                String[] coords = input.split(",");
                if (coords.length != 2) log.warn("Incorrect number of coordinates found in: {}" , input);
                int rCoord = Integer.parseInt(coords[0].trim());
                int cCoord = Integer.parseInt(coords[1].trim());
                m = new Move(this,rCoord,cCoord);
                valid = game.getBoard().isMoveValid(m);
            } else {
                switch (input.trim().toLowerCase()) {
                    case "print":
                    case "p":
                        log.info(game.getBoard().getPrettyBoardString());
                        break;
                    case "quit":
                    case "q":
                        game.quit();
                        valid = true;
                    case "help":
                    case "h":
                        printCmds();
                        break;
                }
            }
        }
        return m;
    }

    private static void printCmds()
    {
        log.info("====== Commands ======");
        log.info("Move:        x,y");
        log.info("Quit game:   quit (q)");
        log.info("Print board: print (p)");
        log.info("help:        help (h)");
    }

}