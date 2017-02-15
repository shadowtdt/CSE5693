package com.ttoggweiler.cse5693.TicTacToe.player;

import com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;
import java.util.UUID;

/**
 * This player gets input from the user through terminal prompts
 */
public class CommandLinePlayer extends BasePlayer
{

    private static final Logger log = LoggerFactory.getLogger(CommandLinePlayer.class);

    private Scanner keyboard = new Scanner(System.in);

    public CommandLinePlayer()
    {
        this(null);
    }

    public CommandLinePlayer(String name)
    {
        if(name == null || name.trim().isEmpty()) name = getNameFromUser();
        this.setName(name);
    }

    @Override
    public Move getNextMove(UUID gameId)
    {
        System.out.println(getGame(gameId).getBoardManager().getPrettyBoardString(getId()));
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

    private String getNameFromUser()
    {
        while(true)
        {
            System.out.println("Please provide player name:");
            String input = keyboard.nextLine();
            if (input == null || input.trim().isEmpty()) continue;
            return input;
        }
    }

    private Move getMoveFromUser(TicTacToeGame game)
    {
        boolean valid = false;
        UUID startingPlayer = game.getMoveManager().findMoveForIndex(0)
                .map(Move :: getPlayer).orElse(game.whoseTurnIsIt());

        Move m = null;
        while (!valid) {
            System.out.println("Player "+getName()+"'s turn, input next move:");
            String input = keyboard.nextLine();
            if (input == null || input.trim().isEmpty()) continue;

            if (input.contains(",")) {
                String[] coords = input.split(",");
                if (coords.length != 2) System.err.println("Incorrect number of coordinates found in: " + input);
                int rCoord = Integer.parseInt(coords[0].trim());
                int cCoord = Integer.parseInt(coords[1].trim());
                m = new Move(getId(),rCoord,cCoord);
                valid = game.getMoveManager().isMoveValid(m);

                if(!valid)
                {
                    System.err.println(("Invalid move: "+m.getRejectionCause().toString()));
                }
            } else {
                switch (input.trim().toLowerCase()) {
                    case "print":
                    case "p":
                        System.out.println(game.getBoardManager().getPrettyBoardString(startingPlayer));
                        break;
                    case "quit":
                    case "q":
                        game.quitGame();
                        valid = true;
                    case "help":
                    case "h":
                        printCmds();
                        break;
                    default:
                        System.err.println("Unknown command received!");
                        printCmds();
                        break;
                }
            }
        }
        return m;
    }

    public boolean continuePlayingGames()
    {
        while (true) {
            System.out.println("Would you like to continue playing games? Y | N");
            String input = keyboard.nextLine();
            if (input == null || input.trim().isEmpty()){}
            else if (input.toLowerCase().contains("y")) return true;
            else if (input.toLowerCase().contains("n")) return false;
        }
    }

    private static void printCmds()
    {
        System.out.println("======== Commands ========");
        System.out.println("Make Move:      row,col  ie: 0,0 = Top Left");
        System.out.println("Quit game:      quit (q)");
        System.out.println("Print board:    print (p)");
        System.out.println("help:           help (h)");
    }


}
