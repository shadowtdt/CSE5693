package com.ttoggweiler.cse5693.TicTacToe.board;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reads in an array of coordinates and translates them into an array of moves
 * that can be loaded into {@link com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame} game
 */
public class BoardLoader
{
    /*
    Example game string  "0:1, 1:2, 2:3"
    Example games file, separated by newlines
    0:1, 0:2, 2:2
    0:2, 1:1, 1:2
    0:0, 1:0, 2:2
    1:1, 1:2, 2:0
    2:0, 1:2, 2:1
     */

    /**
     * Parses a single game from the provided string
     * @param boardString string to parse moves from
     * @return array of moves that were parsed
     */
    public static Move[] loadMovesFromString(String boardString)
    {
        if (boardString == null || boardString.isEmpty())
            throw new NullPointerException("Unable to load board from a null string");
        return parseMovesArray(boardString);
    }

    /**
     * Loads and parses games from provided Path
     * @param path path of the file to load
     * @return A set of games made up of an array of moves
     * @throws IOException when files does not exist or is not readable
     */
    public static Set<Move[]> loadGamesFromPath(Path path) throws IOException
    {
        if (path == null) throw new NullPointerException("Unable to load board from a null path");
        return Files.lines(path)
                .map(BoardLoader::parseMovesArray)
                .collect(Collectors.toSet());
    }

    /**
     * Loads and parses games from provided file path
     * @param path path to file to load
     * @return A set of games made up of an array of moves
     * @throws IOException when files does not exist or is not readable
     */
    public static Set<Move[]> loadGamesFromFile(String path) throws IOException
    {
        if (path == null) throw new NullPointerException("Unable to load board from a null file path string");
        return loadGamesFromPath(Paths.get(path));
    }

    /**
     * Parses the provided string into a move array
     * assumes moves are comma separated and coordinates are colan separated
     * Performs some basic max moves checks
     * @param moveArrayString the string of moves to parse
     * @return an array of moves parsed from the string
     */
    private static Move[] parseMovesArray(String moveArrayString)
    {
        if (moveArrayString == null || moveArrayString.isEmpty())
            throw new NullPointerException("Unable to parse move array from null or empty string");

        String[] moves = moveArrayString.split(",");
        Move[] parsedMoves = new Move[moves.length];

        int maxValue = 0;
        for (int i = 0; i < moves.length; i++) {
            String[] coordinates = moves[i].split(":");
            if (coordinates.length != 2)
                throw new IllegalArgumentException("Incorrect number of coordinates found in: " + moveArrayString);
            int xCoord = Integer.parseInt(coordinates[0].trim());
            int yCoord = Integer.parseInt(coordinates[1].trim());
            if (xCoord > maxValue) maxValue = xCoord;
            if (yCoord > maxValue) maxValue = yCoord;
            parsedMoves[i] = new Move(xCoord, yCoord);
        }
        if (moves.length > (++maxValue * 2))
            throw new IllegalArgumentException("More moves that spaces detected in: " + moveArrayString);
        return parsedMoves;
    }

    public static void main(String... args)
    {
        Logger log = LoggerFactory.getLogger(BoardLoader.class);
        String sampleBoard = "0:0,0:1,0:2";
        log.info("Loading board: {}", sampleBoard);
        Move[] moves = BoardLoader.loadMovesFromString(sampleBoard);
        log.info("Loaded {} Moves: ", moves.length);
        Arrays.stream(moves)
                .map(Move::toString)
                .forEach(log::info);


        String fileToLoad = "/inputFiles/boardSet1.txt";
        log.info("Loading file: {}", fileToLoad);

        try {
            Set<Move[]> games = BoardLoader.loadGamesFromFile(BoardLoader.class.getResource(fileToLoad).getPath());
            log.info("Loaded {} Games: ", games.size());
            int i = 0;
            games.forEach(game -> {
                log.info("GameStart");
                Arrays.stream(game)
                        .map(Move::toString)
                        .forEach(log::info);
            });
        } catch (IOException e) {
            log.error("Failed loading games from file.", e);
        }


    }
}
