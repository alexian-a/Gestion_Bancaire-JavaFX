package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.EmpruntTableauViewController;
import application.view.OperationEditorPaneViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Emprunt;

public class EmpruntTableau {

    private Stage etStage;
    private EmpruntTableauViewController etViewController;
    private Emprunt emprunt;

    public EmpruntTableau(Stage _parentStage, DailyBankState _dbstate, Emprunt emprunt) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    OperationEditorPaneViewController.class.getResource("emprunttableau.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, 500 + 20, 250 + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.emprunt = emprunt;
            this.etStage = new Stage();
            this.etStage.initModality(Modality.WINDOW_MODAL);
            this.etStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.etStage);
            this.etStage.setScene(scene);

            this.etViewController = loader.getController();
            this.etViewController.initContext(this.etStage, this, _dbstate, emprunt);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doEmpruntTableauDialog() {
        this.etViewController.displayDialog();
        this.etViewController.creerTableau(emprunt);

    }
}
