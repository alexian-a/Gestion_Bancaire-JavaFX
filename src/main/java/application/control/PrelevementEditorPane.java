package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.PrelevementEditorPaneViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.CompteCourant;
import model.data.Prelevement;

public class PrelevementEditorPane {

    private Stage primaryStage;
    private PrelevementEditorPaneViewController pepcViewController;

    /**
     * Constructeur de la classe de PrelevementEditorPane
     * Cette classe permet de gérer l'affichage d'un dialogue de gestion des prelevements.
     *
     * @param _parentStage Fenêtre parente
     * @param _dbstate    Etat courant de l'application
     *
     * @author SHARIFI Daner
     */
    public PrelevementEditorPane(Stage _parentStage, DailyBankState _dbstate) {
        try {
            FXMLLoader loader = new FXMLLoader(PrelevementEditorPaneViewController.class.getResource("prelevementeditorpane.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Gestion d'un prélèvement");
            this.primaryStage.setResizable(false);

            this.pepcViewController = loader.getController();
            this.pepcViewController.initContext(this.primaryStage, _dbstate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cette méthode permet d'afficher le dialogue de gestion d'un prelevement.
     * Que ce soit pour une création, une modification, une suppression ou une consultation
     *
     * @param compte le compte a qui appartient le prelevement
     * @param prlv le prelevement concerné
     * @param em le mode d'édition qui sera utilisé
     *
     * @return le prelevement
     *
     * @author SHARIFI Daner
     */
    public Prelevement doPrelevementEditorDialog(CompteCourant compte, Prelevement prlv, EditionMode em) {
        return this.pepcViewController.displayDialog(compte, prlv, em);
    }
}
