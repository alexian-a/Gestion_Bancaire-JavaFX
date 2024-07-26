package application.control;
import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.PrelevementsManagementViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;
import model.orm.Access_BD_Prelevement;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.Table;

public class PrelevementsManagement {

    private Stage primaryStage;
    private PrelevementsManagementViewController pmcViewController;
    private DailyBankState dailyBankState;
    private CompteCourant prelevDesComptes;

    /**
     * Constructeur de la classe de PrelevementsManagement
     * Cette classe permet de gérer l'affichage d'un dialogue de gestion des prelevements
     *
     * @param _parentStage Fenêtre parente
     * @param _dbstate Etat courant de l'application
     * @param compte le compte courant
     *
     * @author SHARIFI Daner
     */
    public PrelevementsManagement(Stage _parentStage, DailyBankState _dbstate, CompteCourant compte, Client client) {

        this.prelevDesComptes = compte;
        this.dailyBankState = _dbstate;
        try {
            FXMLLoader loader = new FXMLLoader(PrelevementsManagementViewController.class.getResource("prelevementsmanagement.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Gestion des prélèvements");
            this.primaryStage.setResizable(false);

            this.pmcViewController = loader.getController();
            this.pmcViewController.initContext(this.primaryStage, this, _dbstate, compte, client);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet d'afficher le dialogue de gestion des prelevements
     *
     * @author SHARIFI Daner
     */
    public void doPrelevementsManagementDialog() {
        this.pmcViewController.displayDialog();
    }

    /**
     * Permet de récupérer la liste des prelevements d'un compte
     *
     * @author SHARIFI Daner
     */
    public ArrayList<Prelevement> getPrelevementDunCompte() {
        ArrayList<Prelevement> listePrelev = new ArrayList<>();

        try {
            Access_BD_Prelevement ap = new Access_BD_Prelevement();
            listePrelev = ap.getPrelevements(this.prelevDesComptes.idNumCompte);
        } catch (DatabaseConnexionException e) {
            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
            ed.doExceptionDialog();
            this.primaryStage.close();
            listePrelev = new ArrayList<>();
        } catch (ApplicationException ae) {
            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
            ed.doExceptionDialog();
            listePrelev = new ArrayList<>();
        }
        return listePrelev;
    }

    /**
     * Permet de créer un nouveau prelevement et de l'ajouter à la base de données
     *
     * @author SHARIFI Daner
     */
    public Prelevement creerNouveauPrelev() {
        Prelevement prelev;
        PrelevementEditorPane cep = new PrelevementEditorPane(this.primaryStage, this.dailyBankState);
        prelev = cep.doPrelevementEditorDialog(this.prelevDesComptes, null, EditionMode.CREATION);
        if (prelev != null) {
            try {
                Access_BD_Prelevement acP= new Access_BD_Prelevement();
                acP.insertPrelevement(prelev);
                acP.updatePrelevement(prelev);
                if (Math.random() < -1) {
                    throw new ApplicationException(Table.PrelevementAutomatique, Order.INSERT, "todo : test exceptions", null);
                }
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                this.primaryStage.close();
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
            }
        }
        return prelev;
    }

    /**
     * Permet de modifier un prelevement existant
     *
     * @param prelev le prelevement que l'on souhaite modifier
     * @return le prelevement modifié
     *
     * @author SHARIFI Daner
     */
    public Prelevement modifierPrelevement(Prelevement prelev) {
        PrelevementEditorPane cep = new PrelevementEditorPane(this.primaryStage, this.dailyBankState);
        Prelevement result = cep.doPrelevementEditorDialog(this.prelevDesComptes,prelev, EditionMode.MODIFICATION);
        if (result != null) {
            try {
                Access_BD_Prelevement ac = new Access_BD_Prelevement();
                ac.updatePrelevement(result);
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                result = null;
                this.primaryStage.close();
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
                result = null;
            }
        }
        return result;
    }

    /**
     * Permet de supprimer un prelevement existant
     *
     * @param prelev le prelevement que l'on souhaite supprimer
     * @return le prelevement supprimé
     *
     * @author SHARIFI Daner
     */
    public Prelevement supprimerPrelevement(Prelevement prelev) {
        PrelevementEditorPane pep = new PrelevementEditorPane(this.primaryStage, this.dailyBankState);
        Prelevement result = pep.doPrelevementEditorDialog(this.prelevDesComptes,prelev, EditionMode.SUPPRESSION);
        if (result != null) {
            try {
                Access_BD_Prelevement ap = new Access_BD_Prelevement();
                ap.deletePrelevement(prelev);
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                result = null;
                this.primaryStage.close();
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
                result = null;
            }
        }
        return result;
    }
}