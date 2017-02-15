package com.ttoggweiler.cse5693.TicTacToe;

import com.ttoggweiler.cse5693.TicTacToe.board.BoardManager;
import com.ttoggweiler.cse5693.TicTacToe.board.MoveManager;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import com.ttoggweiler.cse5693.TicTacToe.player.BasePlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages the iteraction between player and moveManager
 * tracks the turn and determines winner
 */
public class TicTacToeGame
{
    private Logger log = LoggerFactory.getLogger(TicTacToeGame.class);

    private UUID id = UUID.randomUUID();
    private long creationTime = System.currentTimeMillis();

    private UUID winningPlayer = null;
    //private boolean isMinMoveThresholdMet = false;
    private boolean gameStarted = false;
    private boolean gameEnded = false;

    private MoveManager moveManager;
    private BoardManager boardManager;

    private BasePlayer player1;
    private BasePlayer player2;
    private BasePlayer currentTurn;

    /**
     * Creates a tic tac toe game for the give size with the given players
     * Constructs a moveManager of dimensions size X size
     * Calls, {@link BasePlayer#getNextMove(UUID)} on the players on their turn
     * @param size dimensions of the game moveManager
     * @param player1 starting player
     * @param player2 player who goes second
     */
    public TicTacToeGame(int size, BasePlayer player1, BasePlayer player2)
    {
        validatePlayers(player1, player2);
        boardManager = new BoardManager(size);
        moveManager = new MoveManager(getId(), boardManager);
        this.player1 = player1;
        this.player2 = player2;
        this.currentTurn = this.player1;
    }

    /**
     * Creates a tic tac toe game for the give size with the given players, and initializes with the given Move array
     * Constructs a moveManager of dimensions size X size
     * Calls, {@link BasePlayer#getNextMove(UUID)} on the players on their turn
     * @param size dimensions of the game moveManager
     * @param player1 starting player
     * @param player2 player who goes second
     * @param initMoves the Moves to initialize the game with
     */
    public TicTacToeGame(int size, BasePlayer player1, BasePlayer player2, Move... initMoves)
    {
        this(size, player1, player2);
        if (initMoves != null) {
            for (Move m : initMoves) {
                m.setPlayer(currentTurn.getId());
                moveManager.makeMove(m);
                rotatePlayers();
            }
        }
    }

    /**
     * Creates a traditional tic tac toe game with a 3x3 moveManager and the given players
     * Calls, {@link BasePlayer#getNextMove(UUID)} on the players on their turn
     * @param player1 starting player
     * @param player2 player who goes second
     */
    public TicTacToeGame(BasePlayer player1, BasePlayer player2)
    {
        this(3, player1, player2);
    }

    /**
     * The Unique id for this game
     * @return the game id
     */
    public UUID getId()
    {
        return id;
    }

    /**
     * The time in system milli when the game was created
     * @return milliseconds of the systemTime when game was created
     */
    public long getCreationTime()
    {
        return creationTime;
    }

    /**
     * Gets the current moveManager
     * @return the game moveManager
     */
    public MoveManager getMoveManager()
    {
        return this.moveManager;
    }

    /**
     * Gets the current moveManager
     * @return the game moveManager
     */
    public BoardManager getBoardManager()
    {
        return this.boardManager;
    }

    /**
     * Gets the player whos turn it is
     * @return player who will make the next move
     */
    public UUID whoseTurnIsIt()
    {
        return this.currentTurn.getId();
    }

    /**
     * Gets the players for the game
     * @return players of the game
     */
    public BasePlayer[] getPlayers()
    {
        return new BasePlayer[]{player1, player2};
    }


    public void startGame()
    {
        assert !gameStarted;
        gameStarted = true;

        log.info("** Starting TicTacToe game ID: {} **",id.toString());
        log.info("Player1 (X): {}", player1.getName());
        log.info("Player2 (O): {}", player2.getName());
        if (moveManager.getCurrentMoveIndex() != 0) {
            log.info("Loaded {} moves.", moveManager.getCurrentMoveIndex() + 1);
            log.info(boardManager.getPrettyBoardString(player1.getId()));
        }
        player1.gameStart(this);
        player2.gameStart(this);
        while (!boardManager.findWinner().isPresent() && !boardManager.getState().equals(BoardManager.BoardState.FULL) && !gameEnded) // while no winner and non-full moveManager
        {
            try {
                // assert previous and current turn are not the same player
                if (moveManager.findLastMove().isPresent())
                    assert moveManager.findLastMove().get().getPlayer() != currentTurn.getId();

                Move nextMove = currentTurn.getNextMove(getId());
                if (nextMove == null) continue;
                if (gameEnded) break; // Check to see if game ended while waiting for move

                log.debug("{} making move {}", getPlayerForId(nextMove.getPlayer()).getName(), Arrays.toString(nextMove.getMove()));
                moveManager.makeMove(nextMove);
                if (!nextMove.wasAccepted()) log.error("Invalid move submitted");
                log.debug(boardManager.getPrettyBoardString(player1.getId()));
                rotatePlayers();
            } catch (Exception e) {
                log.error(currentTurn.getName() + "'s last move was invalid, repeating turn.", e);
            }
        }
        endGame();
    }

    public void quitGame()
    {
        log.info("** Quiting TicTacToe game **");
        endGame();
    }

    private void endGame()
    {
        if (gameEnded) return;
        gameEnded = true;

        log.info("** TicTacToe game end ID: {} **",id.toString());
        String board = boardManager.getPrettyBoardString(player1.getId());

        winningPlayer = boardManager.findWinner().orElse(null);
        if (winningPlayer != null) {
            log.info("Winner: {}" + board, getPlayerForId(winningPlayer).getName());
        } else if (boardManager.getState().equals(BoardManager.BoardState.FULL)) {
            log.info("Tie: {} and {}" + board, player1.getName(), player2.getName());
        } else {
            log.info("Premature game ending.");
            return;
        }

        player1.gameEnded(this.getId(), winningPlayer == player1.getId());
        player2.gameEnded(this.getId(), winningPlayer == player2.getId());
    }
    /* victory checking */
//    /**
//     * Returns the moveManager winner if there is one
//     * this check is run every move, quick fails when there have not been enough moves to have a winner
//     * returns a known winner, otherwise only checks most recent move's row/col/dig for winning sequence
//     * @return game winner, empty optional otherwise
//     */
//    public Optional<UUID> findWinner()
//    {
//        if (winningPlayer != null) return Optional.of(winningPlayer);
//
//        if (!minMoveThresholdMet()) return Optional.empty(); // Not enough moves for there to be a winner
//
//        //Reduce search space by only checking the previous move's effected row/col/diagonal
//        if (!checkLastMoveHorizontal())
//            if (!checkLastMoveVertical())
//                if (!checkLastMoveDiagonal())
//                    return Optional.empty(); // if all checks fail there is no winner
//
//        return Optional.of(winningPlayer); // there must be a winner to get here
//    }
//
//    /**
//     * Reduce winning search space by first checking if there have been enough moves for a winner
//     * @return
//     */
//    private boolean minMoveThresholdMet()
//    {
//        if (!isMinMoveThresholdMet) {
//            isMinMoveThresholdMet = ((boardManager.size() * 2) - 1) <= moveManager.getCurrentMoveIndex();
//        }
//        return isMinMoveThresholdMet;
//    }
//
//    /**
//     * Checks the horizontal of the last move played for a winning state
//     * @return Optional winning player if one is found
//     */
//    private boolean checkLastMoveHorizontal()
//    {
//        Move lastMove = moveManager.findLastMove().orElseThrow(() ->
//                new IllegalStateException("Horizontal check should always be preceded by the Min-Move-Threshold check."));
//        int row = lastMove.getMove()[0];
//        boardManager.findMatchingInSequence(row, 0, row, boardManager.size() - 1, 3, false)
//                .ifPresent(player -> winningPlayer = player);
//        return winningPlayer != null;
//    }
//
//    /**
//     * Checks the vertical of the last move played for a winning state
//     * @return Optional winning player if one is found
//     */
//    private boolean checkLastMoveVertical()
//    {
//        Move lastMove = moveManager.findLastMove().orElseThrow(() ->
//                new IllegalStateException("Vertical check should always be preceded by the Min-Move-Threshold check."));
//        int col = lastMove.getMove()[1];
//        boardManager.findMatchingInSequence(0, col, boardManager.size() - 1, col, 3, false)
//                .ifPresent(player -> winningPlayer = player);
//        return winningPlayer != null;
//    }
//
//    /**
//     * Checks the diagonal of the last move played for a winning state
//     * @return Optional winning player if one is found
//     */
//    private boolean checkLastMoveDiagonal()
//    {
//        Move lastMove = moveManager.findLastMove().orElseThrow(() ->
//                new IllegalStateException("Diagonal check should always be preceded by the Min-Move-Threshold check."));
//        int row = lastMove.getMove()[0];
//        int col = lastMove.getMove()[1];
//        int size = boardManager.size() - 1;
//        // Quick fail if the last move was not on a diagonal
//        if (row - col == 0) //  0,0 -> N,N diagonal
//            boardManager.findMatchingInSequence(0, 0, size, size, 3, false)
//                    .ifPresent(moves -> winningPlayer = moves);
//        else if (row + col == boardManager.size() - 1) // 0,N -> N,0 diagonal ||
//            boardManager.findMatchingInSequence(0, size, size, 0, 3, false)
//                    .ifPresent(moves -> winningPlayer = moves);
//
//        return winningPlayer != null;
//    }

    /**
     * Checks for null values and sets names for those players who do not have one set
     * @param players the players to validate
     */
    private void validatePlayers(BasePlayer... players)
    {
        for (BasePlayer player : players) {
            if (player == null) throw new NullPointerException("One or more of the provided players is null");
        }
    }

    private BasePlayer rotatePlayers()
    {
        if (currentTurn == null) currentTurn = player1; // if null assume it is player1's turn
        else moveManager.findLastMove().ifPresent(m -> // get last move
                currentTurn = (m.getPlayer().equals(player1.getId()) ? // compare previous move with player1 id
                        player2 : player1));// it matching, player2's turn, else it is player1's
        return currentTurn;
    }

    private BasePlayer getPlayerForId(UUID id)
    {
        if (id == null) throw new NullPointerException("Failed to get player for null ID");
        if (player1.getId().equals(id)) return player1;
        else if (player2.getId().equals(id)) return player2;
        else
            throw new IllegalArgumentException("No player with ID: " + id.toString() + " exists for game " + getId().toString());

    }

}
