package application.control;

import java.sql.Date;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.EmpruntManagementViewController;
import application.view.OperationsManagementViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.orm.Access_BD_Operation;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;

public class EmpruntManagement {

    private Stage emStage;
    private DailyBankState dailyBankState;
    private EmpruntManagementViewController emViewController;
    private Client clientDuCompte;

    public EmpruntManagement(Stage _parentStage, DailyBankState _dbstate, Client client) {

        this.clientDuCompte = client;

        this.dailyBankState = _dbstate;

        try {
            FXMLLoader loader = new FXMLLoader(
                    OperationsManagementViewController.class.getResource("empruntmanagement.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, 900 + 20, 350 + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.emStage = new Stage();
            this.emStage.initModality(Modality.WINDOW_MODAL);
            this.emStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.emStage);
            this.emStage.setScene(scene);
            this.emStage.setTitle("Gestion des emprunts");
            this.emStage.setResizable(false);

            this.emViewController = loader.getController();
            this.emViewController.initContext(this.emStage, this, _dbstate, client);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doEmpruntManagementDialog() {
        this.emViewController.displayDialog();
    }

    public void enregistrerEmprunt(double capital, int duree, double tauxInteret, int idClient) {
        Access_BD_Operation ao = new Access_BD_Operation();
        Date dateEmp = new Date(System.currentTimeMillis());
        try {
            ao.insertEmprunt(idClient, tauxInteret, capital, duree, dateEmp, idClient);
        } catch (DatabaseConnexionException e) {
            System.out.println("Erreur de connexion à la base de données");
            e.printStackTrace();
        } catch (ManagementRuleViolation e) {
            e.printStackTrace();
        } catch (DataAccessException e) {
            System.out.println("Erreur d'accès aux données");
            e.printStackTrace();
        }

    }
}
