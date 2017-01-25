package com.ttoggweiler.cse5693.TicTacToe;

import com.ttoggweiler.cse5693.TicTacToe.board.Board;
import com.ttoggweiler.cse5693.TicTacToe.board.Move;
import com.ttoggweiler.cse5693.TicTacToe.player.BasePlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages the iteraction between player and board
 * tracks the turn and determines winner
 */
public class TicTacToeGame
{
    private Logger log = LoggerFactory.getLogger(TicTacToeGame.class);

    private UUID id = UUID.randomUUID();
    private long creationTime = System.currentTimeMillis();

    private List<Move> winningMoves = null;
    private boolean isMinMoveThresholdMet = false;
    private boolean gameStarted = false;

    private Board board;
    private BasePlayer player1;
    private BasePlayer player2;
    private BasePlayer currentTurn;

    /**
     * Creates a tic tac toe game for the give size with the given players
     * Constructs a board of dimensions size X size
     * Calls, {@link BasePlayer#getNextMove(Board)} on the players on their turn
     * @param size dimensions of the game board
     * @param player1 starting player
     * @param player2 player who goes second
     */
    public TicTacToeGame(int size, BasePlayer player1, BasePlayer player2)
    {
        validatePlayers(player1, player2);
        board = new Board(getId(), size);
        this.player1 = player1;
        this.player2 = player2;
        this.currentTurn = this.player1;
    }

    /**
     * Creates a tic tac toe game for the give size with the given players, and initializes with the given Move array
     * Constructs a board of dimensions size X size
     * Calls, {@link BasePlayer#getNextMove(Board)} on the players on their turn
     * @param size dimensions of the game board
     * @param player1 starting player
     * @param player2 player who goes second
     * @param initMoves the Moves to initialize the game with
     */
    public TicTacToeGame(int size, BasePlayer player1, BasePlayer player2, Move... initMoves)
    {
        // TODO: ttoggweiler 1/21/17
        //this(size,player1,player2);
        // play moves
        // start normal game
        throw new NullPointerException("This constructor has not been implemented yet");
    }

    /**
     * Creates a traditional tic tac toe game with a 3x3 board and the given players
     * Calls, {@link BasePlayer#getNextMove(Board)} on the players on their turn
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
     * Gets the current board
     * @return the game board
     */
    public Board getBoard()
    {
        return this.board;
    }

    /**
     * Gets the player whos turn it is
     * @return player who will make the next move
     */
    public BasePlayer whoseTurnIsIt()
    {
        return this.currentTurn;
    }

    /**
     * Gets the players for the game
     * @return players of the game
     */
    public BasePlayer[] getPlayers()
    {
        return new BasePlayer[]{player1, player2};
    }

    /**
     * Returns the board winner if there is one
     * this check is run every move, quick fails when there have not been enough moves to have a winner
     * returns a known winner, otherwise only checks most recent move's row/col/dig for winning sequence
     * @return game winner, empty optional otherwise
     */
    public Optional<BasePlayer> findWinner()
    {
        if (winningMoves != null && winningMoves.size() == board.size())
            return Optional.of(winningMoves.get(0).getPlayer()); // Known winner

        if (!minMoveThresholdMet()) return Optional.empty(); // Not enough moves for there to be a winner

        //Reduce search space by only checking the previous move's effected row/col/diagonal
        if (!checkLastMoveHorizontal())
            if (!checkLastMoveDiagonal())
                if (!checkLastMoveVertical())
                    return Optional.empty(); // if all checks fail there is no winner

        return Optional.of(winningMoves.get(0).getPlayer()); // there must be a winner to get here
    }

    public void start()
    {
        assert !gameStarted;
        gameStarted = true;

        log.info("** Starting TicTacToe game **");
        log.info("Game ID: {}", id.toString());
        log.info("Player1: {}", player1.getName());
        log.info("Player2: {}", player2.getName());
        while (!findWinner().isPresent() && !board.getState().equals(Board.BoardState.FULL)) // while no winner and non-full board
        {
            try {
                // assert previous and current turn are not the same player
                if (board.findLastMove().isPresent()) assert board.findLastMove().get().getPlayer() != currentTurn;
                Move nextMove = currentTurn.getNextMove(this.board);
                log.debug("{} making move {}", nextMove.getPlayer().getName(), Arrays.toString(nextMove.getMove()));
                board.makeMove(nextMove);
                log.debug(getBoardString());
                currentTurn = (currentTurn == player1) ? player2 : player1; // rotate players
            } catch (Exception e) {
                log.error(currentTurn.getName() + "'s last move was invalid, repeating turn.", e);
            }
        }
        endGame();
    }

    private void endGame()
    {
        log.info("** Ending TicTacToe game **");
        log.info("Game ID: {}", id.toString());
        if (winningMoves != null) {
            log.info("Winner: {}", winningMoves.get(0).getPlayer().getName());
        } else if (board.getState().equals(Board.BoardState.FULL)) {
            log.info("Tie between players {} and {}", player1.getName(), player2.getName());
        } else assert false;
    }
    /* victory checking */

    /**
     * Reduce winning search space by first checking if there have been enough moves for a winner
     * @return
     */
    private boolean minMoveThresholdMet()
    {
        if (!isMinMoveThresholdMet) {
            isMinMoveThresholdMet = ((board.size() * 2) - 1) <= board.getCurrentMoveIndex();
        }
        return isMinMoveThresholdMet;
    }

    /**
     * Checks the horizontal of the last move played for a winning state
     * @return Optional winning player if one is found
     */
    private boolean checkLastMoveHorizontal()
    {
        Move lastMove = board.findLastMove().orElseThrow(() ->
                new IllegalStateException("Horizontal check should always be preceded by the Min-Move-Threshold check."));
        int row = lastMove.getMove()[0];
        board.findMatchingSequence(row, 0, row, board.size() - 1).ifPresent(moves -> winningMoves = moves);
        return winningMoves != null;
    }

    /**
     * Checks the vertical of the last move played for a winning state
     * @return Optional winning player if one is found
     */
    private boolean checkLastMoveVertical()
    {
        Move lastMove = board.findLastMove().orElseThrow(() ->
                new IllegalStateException("Vertical check should always be preceded by the Min-Move-Threshold check."));
        int col = lastMove.getMove()[1];
        board.findMatchingSequence(0, col, board.size() - 1, col).ifPresent(moves -> winningMoves = moves);
        return winningMoves != null;
    }

    /**
     * Checks the diagonal of the last move played for a winning state
     * @return Optional winning player if one is found
     */
    private boolean checkLastMoveDiagonal()
    {
        Move lastMove = board.findLastMove().orElseThrow(() ->
                new IllegalStateException("Diagonal check should always be preceded by the Min-Move-Threshold check."));
        int row = lastMove.getMove()[0];
        int col = lastMove.getMove()[1];

        // Quick fail if the last move was not on a diagonal
        if (col - row == 0 || row + col == board.size() - 1) // 0,N -> N,0 diagonal ||  0,0 -> N,N diagonal
        {
            board.findMatchingSequence(0, 0, board.size() - 1, board.size() - 1).ifPresent(moves -> winningMoves = moves);
            board.findMatchingSequence(0, board.size() - 1, board.size() - 1, 0).ifPresent(moves -> winningMoves = moves);
        }

        return winningMoves != null;
    }

    private String getBoardString()
    {
        String boardStr = "\n\n";
        for (int i = 0; i <= this.board.size() - 1; i++) {
            for (int j = 0; j <= this.board.size() - 1; j++) {
                boardStr += (j > 0) ? "| " : "";
                if (board.findPlayer(i, j).isPresent())
                    boardStr += (board.findPlayer(i, j).get().equals(player1)) ? "O " : "X ";
                else boardStr += "  ";
            }
            if (i != this.board.size() - 1) boardStr += "\n----------\n";
        }
        return boardStr;
    }

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

}