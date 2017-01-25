package com.ttoggweiler.cse5693.TicTacToe.board;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reads in an array of coordinates and translates them into an array of moves
 * that can be loaded into {@link com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame} game
 */
public class BoardLoader
{
    /* Example game string
    [ (0:1), (1:2) ,(2:2) ]  // single
    0:1, 1:2, 2:3

     */
    public static Move[] loadFromString(String boardString)throws IOException
    {
        if(boardString == null || boardString.isEmpty())throw new NullPointerException("Unable to load board from a null string");
        return parseMovesArray(boardString);
    }

    public static Set<Move[]> loadFromPath(Path path)throws IOException
    {
        if(path == null)throw new NullPointerException("Unable to load board from a null path");
        return Files.lines(path)
                .map(BoardLoader::parseMovesArray)
                .collect(Collectors.toSet());
    }

    public static Set<Move[]> loadFromFile(String path)throws IOException
    {
        if(path== null)throw new NullPointerException("Unable to load board from a null file path string");
        return loadFromPath(Paths.get(path));
    }

    private static  Move[] parseMovesArray(String moveArrayString)
    {
        if(moveArrayString == null || moveArrayString.isEmpty())
            throw new NullPointerException("Unable to parse move array from null or empty string");
        String[] moves = moveArrayString.split(",");
        Move[] parsedMoves = new Move[moves.length];

        int maxValue = 0;
        for(int i = 0; i < moves.length; i++)
        {
            String[] coordinates = moves[i].split(":");
            if(coordinates.length != 2)
                throw new IllegalArgumentException("Incorrect number of coordinates found in: " + moveArrayString);
            int xCoord = Integer.parseInt(coordinates[0]);
            int yCoord = Integer.parseInt(coordinates[1]);
            if(xCoord > maxValue)maxValue = xCoord;
            if(yCoord > maxValue)maxValue = yCoord;
            parsedMoves[i] = new Move(xCoord,yCoord);
        }
        return parsedMoves;
    }
}
