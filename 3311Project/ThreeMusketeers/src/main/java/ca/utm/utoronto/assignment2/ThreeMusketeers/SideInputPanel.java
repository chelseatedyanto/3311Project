package ca.utm.utoronto.assignment2.ThreeMusketeers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class SideInputPanel extends GridPane implements EventHandler<ActionEvent> {
    private final View view;

    /**
     * Constructs a new GridPane with 2 buttons to select what side the human player wants to play as
     * @param view
     */
    public SideInputPanel(View view) {
        this.view = view;
        this.setAlignment(Pos.CENTER);
        this.setVgap(10);

        view.setMessageLabel("Select your side");

        for (Piece.Type pieceType: Piece.Type.values()) {
            String label = pieceType.getType();

            Button button = new Button(label);
            button.setId(label); // DO NOT MODIFY ID

            // Default styles which can be modified
            button.setPrefSize(500, 100);
            button.setFont(new Font(20));
            button.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");

            button.setOnAction(this);

            this.add(button, 0, this.getRowCount());
        }
    }

    /**
     * Handles click of human player side options ('MUSKETEER' or 'GUARD')
     * Calls view.setSide with the appropriate Piece.Type selected
     * @param actionEvent
     */
    @Override
    public void handle(ActionEvent actionEvent) { // TODO
        Button button = (Button) actionEvent.getSource(); //button that was clicked
        String buttonLabel = button.getText();
        Piece.Type sideType;
        if (buttonLabel.equals("MUSKETEER")) {
            sideType = Piece.Type.MUSKETEER; //set sideType to Musketeer if button text is "MUSKETEER"
        } else if (buttonLabel.equals("GUARD")) {
            sideType = Piece.Type.GUARD; //set sideType to Guard if button text is "GUARD"
        } else {
            return;
        }
        view.setSide(sideType);
    }
}
