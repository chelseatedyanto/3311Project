package ca.utm.utoronto.assignment2.ThreeMusketeers;

import javafx.animation.PauseTransition;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class View {

    ThreeMusketeers model;
    Stage stage;
    BorderPane borderPane;

    ThreeMusketeers.GameMode gameMode;

    Label messageLabel = new Label("");
    Label gameModeLabel = new Label("");
    BoardPanel boardPanel;
    Button undoButton, saveButton, restartButton;
    TextField saveFileNameTextField;
    Label saveFileErrorLabel;

    // must use these strings to update saveFileErrorLabel when saving a board
    static String saveFileSuccess = "Saved board";
    static String saveFileExistsError = "Error: File already exists";
    static String saveFileNotTxtError = "Error: File must end with .txt";

    public View(ThreeMusketeers model, Stage stage) {
        this.model = model;
        this.stage = stage;
        initUI();
    }

    /**
     * Initializes the UI and shows the main menu
     *
     * Contains default alignment and styles which can be modified
     */
    private void initUI() {
        borderPane = new BorderPane();

        // DO NOT MODIFY IDs
        borderPane.setId("BorderPane");  // DO NOT MODIFY ID
        gameModeLabel.setId("GameModeLabel");  // DO NOT MODIFY ID
        messageLabel.setId("MessageLabel"); // DO NOT MODIFY ID

        var threeMusketeersLabel = new Label("Three Musketeers");

        // Default styles which can be modified

        borderPane.setStyle("-fx-background-color: #121212;");

        threeMusketeersLabel.setFont(new Font(30));
        threeMusketeersLabel.setStyle("-fx-text-fill: #e8e6e3");

        gameModeLabel.setText("");
        gameModeLabel.setFont(new Font(20));
        gameModeLabel.setStyle("-fx-text-fill: #e8e6e3");

        messageLabel.setFont(new Font(20));
        messageLabel.setStyle("-fx-text-fill: #e8e6e3");

        VBox labels = new VBox(threeMusketeersLabel, gameModeLabel);
        labels.setAlignment(Pos.TOP_CENTER);
        borderPane.setTop(labels);

        showMainMenu();

        var scene = new Scene(borderPane, 800, 800);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Updates the view to show the Main menu
     */
    private void showMainMenu(){
        ModeInputPanel modeInputPanel = new ModeInputPanel(this);

        VBox vBox = new VBox(20, messageLabel, modeInputPanel);
        vBox.setAlignment(Pos.CENTER);

        borderPane.setCenter(vBox);
        borderPane.setBottom(null);
    }

    /**
     * Updates the view to show the BoardPanel and game controls
     */
    private void showBoard() {
        boardPanel = new BoardPanel(this, model.getBoard());
        undoButton = new Button("Undo move");
        undoButton.setId("UndoButton");   // DO NOT MODIFY ID
        undoButton.setPrefSize(150, 50);
        undoButton.setFont(new Font(12));
        undoButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        undoButton.setOnAction(e -> undo());
        setUndoButton();

        saveButton = new Button("Save board");
        saveButton.setId("SaveButton");  // DO NOT MODIFY ID
        saveButton.setPrefSize(150, 50);
        saveButton.setFont(new Font(12));
        saveButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        saveButton.setOnAction(e -> saveBoard());

        String boardName = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ".txt";
        saveFileNameTextField = new TextField(boardName);
        saveFileNameTextField.setId("SaveFileNameTextField");  // DO NOT MODIFY ID
        saveFileNameTextField.setStyle("-fx-background-color: #181a1b; -fx-text-fill: white;");

        saveFileErrorLabel.setId("SaveFileErrorLabel");  // DO NOT MODIFY ID
        saveFileErrorLabel = new Label("");
        saveFileErrorLabel.setStyle("-fx-text-fill: #e8e6e3;");

        restartButton = new Button("New game");
        restartButton.setId("RestartButton");  // DO NOT MODIFY ID
        restartButton.setPrefSize(150, 50);
        restartButton.setFont(new Font(12));
        restartButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        restartButton.setOnAction(e -> restart());

        GridPane controls = new GridPane();
        controls.addRow(0, undoButton, restartButton);
        controls.addRow(1, saveFileNameTextField, saveButton);
        controls.add(saveFileErrorLabel, 0, 2, 2, 1);
        controls.setHgap(20);
        controls.setVgap(20);
        controls.setAlignment(Pos.BOTTOM_CENTER);
        GridPane.setHalignment(saveFileErrorLabel, HPos.CENTER);

        VBox vBox = new VBox(20, messageLabel, boardPanel, controls);
        vBox.setAlignment(Pos.CENTER);
        borderPane.setCenter(vBox);
        if (!(model.getCurrentAgent() instanceof HumanAgent)) {
            runMove();
        } else {
            messageLabel.setText(String.format("[%s turn] Select a piece", model.getBoard().getTurn().getType()));
        }
    }

    /**
     * Updates the messageLabel to the given String
     * @param messageLabel String to use for the text of the messageLabel
     */
    protected void setMessageLabel(String messageLabel) {
        this.messageLabel.setText(messageLabel);
    }

    /**
     * Handles running a move for both Human and Computer agents
     * messageLabel must always contain 'MUSKETEER' or 'GUARD'
     * Updates the view after performing the move, the board can be updated by calling BoardPanel.updateCells
     * Checks if the game is over and updates the view accordingly
     * On game over, messageLabel must contain the winner ('MUSKETEER' or 'GUARD') and all Cells must be disabled
     *
     * All Cells and Buttons must be disabled while a computer moves
     *
     */
    protected void runMove() { // TODO
        Button undoBtn = undoButton;
        Button saveBtn = saveButton;
        Button restartBtn = restartButton;
        undoBtn.setDisable(true);
        saveBtn.setDisable(true);
        restartBtn.setDisable(true);
        boardPanel.disableAllCells();

        //check for game over
        if (model.getBoard().isGameOver()) {
            String winner = model.getBoard().getWinner().getType().toString();
            setMessageLabel(String.format("%s WINS!", winner));
        } else {
            Agent activeAgent = model.getCurrentAgent();
            System.out.println("Current Agent: " + activeAgent.getClass().getSimpleName());
            //checks if its a human player
            if (activeAgent instanceof HumanAgent) {
                String turnMessage = String.format("%s turn, select a piece", model.getBoard().getTurn().getType().toString());
                setMessageLabel(turnMessage); //ask human player to select a piece
                boardPanel.updateCells();
                //enable the buttons again after move
                undoBtn.setDisable(false);
                saveBtn.setDisable(false);
                restartBtn.setDisable(false);
            } else {
                System.out.println("Computer is making a move...");
                Move computerMove = activeAgent.getMove(); //get computer's move
                model.move(computerMove); //execute
                System.out.println("Computer moved from " + computerMove.fromCell.getCoordinate() + " to " + computerMove.toCell.getCoordinate());
                boardPanel.updateCells();
                runMove();
            }
        }
        setUndoButton();
    }

    /**
     *  Enables or disables the undo button depending on if there are moves to undo
     */
    protected void setUndoButton() { // TODO
        undoButton.setDisable(model.getMovesSize()==0);
    }


    /**
     * Sets the GameMode to the given mode
     * Shows the SideSelector (Not needed for Human vs Human) or the BoardPanel accordingly
     * @param mode the selected GameMode
     */
    protected void setGameMode(ThreeMusketeers.GameMode mode) { // TODO
        if (saveFileErrorLabel==null) {
            saveFileErrorLabel = new Label("");
            saveFileErrorLabel.setId("SaveFileErrorLabel");
        }
        this.gameMode= mode;
        switch (mode) {
            case Human:
                model.selectMode(mode, Piece.Type.MUSKETEER);
                showBoard();
                break;
            case HumanRandom:
            case HumanGreedy:
                showSideSelector();
                break;
        }
    }

    /**
     * Handles setting the correct agents based on the selected GameMode and the player's piece type by
     * calling model.selectMode
     * Shows the BoardPanel once the side and mode are selected
     * @param sideType the selected Piece Type for the human player in Human vs Computer games
     */
    protected void setSide(Piece.Type sideType) { // TODO
        model.selectMode(gameMode, sideType);
        showBoard();
    }


    /**
     * Handler for the Undo button
     * Undoes the latest move
     */
    private void undo() { // TODO
        model.undoMove();
        boardPanel.updateCells();
        setUndoButton();
    }

    /**
     * Handler for the Save Board button
     * Saves the current board state to a text file
     * Uses saveFileNameTextField to get user input for the name of the file (must end with ".txt")
     * Contains error handling to make sure the file does not already exist and the input ends with ".txt"
     * Updates saveFileErrorLabel with the appropriate message
     *
     * Must use saveFileSuccess, saveFileExistsError, or saveFileNotTxtError to set as the text of saveFileErrorLabel
     */
    private void saveBoard() { // TODO
        String filename = saveFileNameTextField.getText().trim();
        if (filename.length()==0) { // If no input is provided
            saveFileErrorLabel.setText("Filename cannot be empty."); //error for empty filename
            return;
        }
        if (!filename.endsWith(".txt")) { //check if its a txt file
            saveFileErrorLabel.setText(saveFileNotTxtError);
            return;
        }
        File file = new File(filename);
        if (file.exists()) {
            saveFileErrorLabel.setText(saveFileExistsError); //error message for existing file name
            return;
        }
        model.getBoard().saveBoard(file); //save board
        saveFileErrorLabel.setText(saveFileSuccess);
    }
    /**
     * The handler for the New Game button
     * Restarts the game and updates the view accordingly
     * A new game is created and the main menu must be shown again
     */
    private void restart() { // TODO
        model.restart();
        showMainMenu();
    }
    /**
     * Updates the view to show the side selector
     */
    private void showSideSelector() {
        VBox vBox = new VBox(20, messageLabel, new SideInputPanel(this));
        vBox.setAlignment(Pos.CENTER);
        borderPane.setCenter(vBox);
    }
}
