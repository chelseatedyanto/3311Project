package ca.utm.utoronto.assignment2.ThreeMusketeers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

import java.util.List;

public class BoardPanel extends GridPane implements EventHandler<ActionEvent> {

    private final View view;
    private final Board board;
    private Cell selectedCell = null;

    /**
     * Constructs a new GridPane that contains a Cell for each position in the board
     *
     * Contains default alignment and styles which can be modified
     * @param view
     * @param board
     */
    public BoardPanel(View view, Board board) {
        this.view = view;
        this.board = board;

        // Can modify styling
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #181a1b;");
        int size = 550;
        this.setPrefSize(size, size);
        this.setMinSize(size, size);
        this.setMaxSize(size, size);

        setupBoard();
        updateCells();
    }


    /**
     * Setup the BoardPanel with Cells
     */
    private void setupBoard(){ // TODO
        for (int i=0; i<board.size; i++) {
            for (int j=0; j<board.size; j++) {
                Cell cell=board.getCell(new Coordinate(i, j));
                this.add(cell, i, j);
                cell.setOnAction(this);
            }
        }
    }

    /**
     * Updates the BoardPanel to represent the board with the latest information
     *
     * If it's a computer move: disable all cells and disable all game controls in view
     *
     * If it's a human player turn and they are picking a piece to move:
     *      - disable all cells
     *      - enable cells containing valid pieces that the player can move
     * If it's a human player turn and they have picked a piece to move:
     *      - disable all cells
     *      - enable cells containing other valid pieces the player can move
     *      - enable cells containing the possible destinations for the currently selected piece
     *
     * If the game is over:
     *      - update view.messageLabel with the winner ('MUSKETEER' or 'GUARD')
     *      - disable all cells
     */
    protected void updateCells(){ // TODO
        if (board.isGameOver()) {
            view.setMessageLabel("Winner: " + board.getWinner().getType());
            disableAllCells();
        } else {
            Piece.Type currentTurn = board.getTurn();
            boolean isHuman = view.model.isHumanTurn();
            List<Cell> validCells = board.getPossibleCells();
            for (int i=0; i<board.size; i++) {
                for (int j=0; j<board.size; j++) {
                    Cell cell = board.getCell(new Coordinate(i, j));
                    cell.setDefaultColor();
                    boolean isValidPiece = ((validCells.contains(cell)) && (cell.getPiece() != null) && (cell.getPiece().getType()==currentTurn));
                    if (isHuman) {
                        if (selectedCell==null){
                            cell.setDisable(!isValidPiece);
                            if (isValidPiece){
                                cell.setOptionsColor();
                            }
                        } else {
                            //when piece is selected, enable the cell and possible destinations
                            boolean isDest = board.getPossibleDestinations(selectedCell).contains(cell) || cell.equals(selectedCell);
                            cell.setDisable(!isDest);
                            if (isDest) {
                                cell.setOptionsColor();
                            }
                        }
                    } else {
                        cell.setDisable(true); //disable all cells when its computer's turn
                    }
                }
            }
        }
    }

    /**
     * Handles Cell clicks and updates the board accordingly
     * When a Cell gets clicked the following must be handled:
     *  - If it's a valid piece that the player can move, select the piece and update the board
     *  - If it's a destination for a selected piece to move, perform the move and update the board
     * @param actionEvent
     */
    @Override
    public void handle(ActionEvent actionEvent) { // TODO
        Cell cell = (Cell) actionEvent.getSource(); //get clicked cell
        if (view.model.isHumanTurn()) {
            if (board.getPossibleCells().contains(cell)) {
                System.out.println("Valid piece selected"); //if cell has a valid piece that can be moved
                disableAllCells();
                view.model.setCurrentCell(cell);
                List<Cell> dest = board.getPossibleDestinations(cell); //possible places that the selected cells can move to

                for (int i=0; i<dest.size(); i++) {
                    Cell d=dest.get(i);
                    System.out.println("Enabling the destination: " + d.getCoordinate());
                    d.setDisable(false);
                    d.setAgentToColor(); //change the color of the possible destination cells
                }
                cell.setAgentFromColor(); //selected cell highlighted
                view.setMessageLabel("Move "+cell.getPiece().getType()+". Select your destination.");
            }
            else if (board.getPossibleDestinations(view.model.getCurrentCell()).contains(cell) && view.model.getCurrentCell() != null) {
                System.out.println("The selected move is valid.");
                Move move = new Move(view.model.getCurrentCell(), cell); //make the move
                view.model.move(move);
                updateCells(); //update the board
                view.runMove();
            }
            else {
                System.out.println("Click is invalid, no action taken.");
            }
        }
    }
    //function to disable all cells
    public void disableAllCells() {
        for (int i=0; i< board.size; i++) {
            for (int j=0; j<board.size; j++) {
                Cell cell = board.getCell(new Coordinate(i, j));
                cell.setDisable(true);
            }
        }
    }
}
