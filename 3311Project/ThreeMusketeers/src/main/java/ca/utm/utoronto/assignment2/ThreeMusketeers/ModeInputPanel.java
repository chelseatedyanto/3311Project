package ca.utm.utoronto.assignment2.ThreeMusketeers;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.Objects;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModeInputPanel extends GridPane {
    private final View view;

    /**
     * Constructs a new GridPane with the main menu of the game, to select a game mode and load saved boards
     * @param view
     */
    public ModeInputPanel(View view) {
        this.view = view;
        this.setAlignment(Pos.CENTER);
        this.setVgap(10);

        view.setMessageLabel("Main Menu");

        createModeButtons();
        createListView();
    }

    /**
     * Creates the Game mode buttons
     */
    private void createModeButtons(){
        for (ThreeMusketeers.GameMode mode: ThreeMusketeers.GameMode.values()) {
            Button button = new Button(mode.getGameModeLabel());
            button.setId(mode.getGameModeLabel().replaceAll(" ", "")); // DO NOT MODIFY ID

            // Default styles which can be modified
            button.setPrefSize(500, 100);
            button.setFont(new Font(20));
            button.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
            button.setOnAction(e -> this.view.setGameMode(mode));

            this.add(button, 0, this.getRowCount());
        }
    }

    /**
     * Creates the ListView to select a board to load
     */
    private void createListView(){
        Label selectBoardLabel = new Label(String.format("Current board: %s", view.model.getBoardFile().getName()));
        selectBoardLabel.setId("CurrentBoard"); // DO NOT MODIFY ID

        ListView<String> boardsList = new ListView<>();
        boardsList.setId("BoardsList");  // DO NOT MODIFY ID

        boardsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        int starterIndex = getFiles(boardsList);
        boardsList.getSelectionModel().select(starterIndex);

        Button selectBoardButton = new Button("Change board");
        selectBoardButton.setId("ChangeBoard"); // DO NOT MODIFY ID

        selectBoardButton.setOnAction(e -> selectBoard(selectBoardLabel, boardsList));

        VBox selectBoardBox = new VBox(10, selectBoardLabel, boardsList, selectBoardButton);

        // Default styles which can be modified

        boardsList.setPrefHeight(100);

        selectBoardLabel.setStyle("-fx-text-fill: #e8e6e3");
        selectBoardLabel.setFont(new Font(16));

        selectBoardButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
        selectBoardButton.setPrefSize(200, 50);
        selectBoardButton.setFont(new Font(16));

        selectBoardBox.setAlignment(Pos.CENTER);

        this.add(selectBoardBox, 0, this.getRowCount());
    }

    /**
     * Gets all the text files from the boards directory and puts them into the given listView
     * @param listView ListView to update
     * @return the index in the listView of Starter.txt
     */
    private int getFiles(ListView<String> listView) { // TODO
        File folder = new File("boards");
        int starterFileIndex = -1;

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt")); //get all txt files from board
            if (files != null) {
                for (int i=0; i<files.length; i++) {
                    File file = files[i];
                    listView.getItems().add(file.getName()); //add file name to list view
                    if (file.getName().equals("Starter.txt")) {
                        starterFileIndex = i; //to know the location of Starter.txt
                    }
                }
            }
        }
        return starterFileIndex;
    }

    /**
     * Loads the board file selected in the boardsList and updates the selectBoardLabel with the name of the new Board file
     * @param selectBoardLabel a message Label to update which board is currently selected
     * @param boardsList a ListView populated with boards to load
     */
    private void selectBoard(Label selectBoardLabel, ListView<String> boardsList) { // TODO
        String selectedBoardName = boardsList.getSelectionModel().getSelectedItem();
        if (selectedBoardName != null) {
            File boardFile = new File("Boards", selectedBoardName);
            view.model.setBoard(boardFile);
            selectBoardLabel.setText(String.format("Current board: %s", boardFile.getName()));
        }
    }
}
