package com.ttoggweiler.cse5693.TicTacToe.player;

import com.ttoggweiler.cse5693.TicTacToe.TicTacToeGame;
import com.ttoggweiler.cse5693.TicTacToe.board.BoardManager;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by ttoggweiler on 1/31/17.
 */
public class RulePlayer extends BasePlayer
{
    public RulePlayer(String name)
    {
        setName(name);
    }

    public RulePlayer()
    {
        this("Rule");
    }

    @Override
    public Move getNextMove(UUID gameId)
    {
        UUID opponent = Arrays.stream(getGame(gameId).getPlayers())
                .filter(p -> !p.getId().equals(getId()))
                .findAny().get().getId();

        BoardManager bm = getGame(gameId).getBoardManager();

        Move m = findWinningMoveForPlayer(getId(),bm) // find winning move for my player
                .orElse(findWinningMoveForPlayer(opponent,bm) // find win blocking move
                        .orElse(getCenterMove(bm) // get center space
                                .orElse(getCenterMove(bm) // get corner space
                                        .orElse(getOpenSequenceMove(getId(),bm) // get open seq for my player
                                                .orElse(getOpenSequenceMove(opponent,bm) // block open seq for opponent
                                                        .orElse(SequentialPlayer.nextSequentialMove(bm))))))); // next sequential move


        m.setPlayer(getId());
        return m;
    }

    @Override
    public void gameStarted(TicTacToeGame game)
    {

    }

    @Override
    public void gameEnded(UUID gameId, boolean winner)
    {

    }

    private static Optional<Move> findWinningMoveForPlayer(UUID player, BoardManager bm)
    {
        if (bm.findWinner().isPresent()) return Optional.empty(); //Board already has winner

        int length = bm.size() - 1;
        UUID[][] board = bm.getCurrentBoard();

        for (int i = 0; i <= length; i++)
            for (int j = 0; j <= length; j++)
                if (board[i][j] == null) // for all empty spaces
                {
                    board[i][j] = player; // set space to my id
                    Move m = new Move(player, i, j); // create a move for this board state
                    if (new BoardManager(board).findWinner().isPresent()) // check if this creates a winning board
                        return Optional.of(m); // return winning move
                    else
                        board[i][j] = null; // reset board for next loop
                }
        return Optional.empty();
    }

    private static Optional<Move> getCenterMove(BoardManager bm)
    {
        int radius = bm.size()/2;
        if(!bm.isOccupied(radius,radius))
            return Optional.of(new Move(radius,radius));
        else
            return Optional.empty();
    }

    private static Optional<Move> getCornerMove(BoardManager bm)
    {
        int length = bm.size() -1;
        if(!bm.isOccupied(0,0)) return Optional.of(new Move(0,0));
        else if(!bm.isOccupied(length,length)) return Optional.of(new Move(length,length));
        else if(!bm.isOccupied(0,length)) return Optional.of(new Move(0,length));
        else if(!bm.isOccupied(length,0)) return Optional.of(new Move(length,0));
        else return Optional.empty();
    }

    private static Optional<Move> getOpenSequenceMove(UUID player, BoardManager bm)
    {
        int length = bm.size() - 1;

        for (int i = 0; i <= length; i++) {
            if(bm.findMatchingInSequence(0, i, length, i, 1, true)
                    .filter(player::equals).isPresent())
                return getMoveInCol(i,bm);
            if(bm.findMatchingInSequence(i, 0, i, length, 1, true)
                    .filter(player::equals).isPresent())
                return getMoveInRow(i,bm);
        }
        return Optional.empty();
    }

    private static Optional<Move> getMoveInRow(int row, BoardManager bm)
    {
        for (int i = 0; i < bm.size(); i++) {
            if(!bm.isOccupied(row,i))return Optional.of(new Move(row,i));
        }
        return Optional.empty();
    }

    private static Optional<Move> getMoveInCol(int col, BoardManager bm)
    {
        for (int i = 0; i < bm.size(); i++) {
            if(!bm.isOccupied(i,col))return Optional.of(new Move(i,col));
        }
        return Optional.empty();
    }

}
