package application.tools;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Utilitaire pour afficher une fenêtre de message ou de confirmation.
 */
public class AlertUtilities {

    public static boolean confirmYesCancel(Stage _fen, String _title, String _message, String _content, AlertType _at) {
        Alert alert = buildAlert(_fen, _title, _message, _content, _at);
        Optional<ButtonType> option = alert.showAndWait();
        System.out.println(option.get());
        return option.get().getButtonData() == ButtonBar.ButtonData.YES;
    }

    public static void showAlert(Stage _fen, String _title, String _message, String _content, AlertType _at) {
        Alert alert = buildAlert(_fen, _title, _message, _content, _at);
        alert.showAndWait();
    }

    private static Alert buildAlert(Stage _fen, String _title, String _message, String _content, AlertType _at) {
        if (_at == null) {
            _at = AlertType.INFORMATION;
        }
        Alert alert = new Alert(_at);
        alert.initOwner(_fen);
        alert.setTitle(_title);
        alert.setHeaderText(_message);
        alert.setContentText(_content);

        if (_at == AlertType.INFORMATION || _at == AlertType.WARNING) {
            ButtonType buttonYes = new ButtonType("OK", ButtonBar.ButtonData.YES);

            alert.getButtonTypes().setAll(buttonYes);
            alert.getDialogPane().lookupButton(buttonYes).getStyleClass().add("btn-success");
        } else {
            ButtonType buttonYes = new ButtonType("Confirmer", ButtonBar.ButtonData.YES);
            ButtonType buttonCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonYes, buttonCancel);
            alert.getDialogPane().lookupButton(buttonYes).getStyleClass().add("btn-success");
            alert.getDialogPane().lookupButton(buttonCancel).getStyleClass().add("btn-danger");
        }


        StackPane stackPane = new StackPane();
        Label contentLabel = new Label(alert.getContentText());
        contentLabel.getStyleClass().add("fz-14");
        stackPane.getChildren().add(contentLabel);
        alert.getDialogPane().setContent(stackPane);

        StackPane headerPane = new StackPane();
        Label headerLabel = new Label(alert.getHeaderText());

        if (_at == AlertType.ERROR || _at == AlertType.WARNING) {
            headerLabel.getStyleClass().add("alert-danger");
        } else {
            headerLabel.getStyleClass().add("alert-info");
        }

        headerLabel.getStyleClass().add("fz-16");
        headerPane.getChildren().add(headerLabel);
        alert.getDialogPane().setHeader(headerPane);

        alert.getDialogPane().getStyleClass().add("alert-container");

        return alert;
    }

    // create alert with custom buttons with custom classes return button type clicked
    public static ButtonType showAlertWithCustomButtons(Stage _fen, String _title, String _message, String _content, AlertType _at, ButtonType[] buttons, String[] classes) {
        Alert alert = buildAlert(_fen, _title, _message, _content, _at);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(buttons);

        for (int i = 0; i < buttons.length; i++) {
            alert.getDialogPane().lookupButton(buttons[i]).getStyleClass().add(classes[i]);
        }

        Optional<ButtonType> option = alert.showAndWait();
        return option.get();
    }
}
