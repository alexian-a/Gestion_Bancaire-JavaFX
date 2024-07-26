package application.view;

import java.util.ArrayList;

import application.DailyBankState;
import application.control.PrelevementsManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;


public class PrelevementsManagementViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Contrôleur de Dialogue associé à PrelevementsManagement
    private PrelevementsManagement cmDialogController;

    // Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
    private Stage primaryStage;

    // Données de la fenêtre
    private Client clientDuCompte;
    private CompteCourant prelevDuCompte;
    private ObservableList<Prelevement> oListPrelevement;

    // Choix de l'emplacement du fichier à enregistrer
    FileChooser fileChooser = new FileChooser();

    // Manipulation de la fenêtre

    /**
     * Initialise le contexte de la fenêtre de gestion des prelevements.
     *
     * @param _containingStage Fenêtre contenant la scène
     * @param _cm              Contrôleur de dialogue associé à PrelevementsManagement
     * @param _dbstate         Etat courant de l'application
     * @param compte           Compte courant à gérer
     * @param client           Client du compte
     * @author SHARIFI Daner
     */
    public void initContext(Stage _containingStage, PrelevementsManagement _cm, DailyBankState _dbstate, CompteCourant compte, Client client) {
        this.cmDialogController = _cm;
        this.primaryStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.prelevDuCompte = compte;
        this.clientDuCompte = client;
        this.configure();
    }


    /**
     * Configure la fenêtre de gestion des prelevements.
     * Initialise les composants de la fenêtre et configure les actions associées.
     * Charge la liste des prelevements du compte et met à jour l'état des composants.
     *
     * @author SHARIFI Daner
     */
    private void configure() {
        String info;

        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

        this.oListPrelevement = FXCollections.observableArrayList();
        this.lvPrelevement.setItems(this.oListPrelevement);
        this.lvPrelevement.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvPrelevement.getFocusModel().focus(-1);
        this.lvPrelevement.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

        info = this.prelevDuCompte.idNumCompte + "  " + "  (id : "
                + this.prelevDuCompte.idNumCli + ")";
        this.lblInfosCompte.setText(info);

        this.loadList();
        this.validateComponentState();
    }

    /**
     * Affiche la fenêtre de gestion des comptes.
     *
     * @author SHARIFI Daner
     */
    public void displayDialog() {
        this.primaryStage.showAndWait();
    }


    /**
     * Ferme la fenêtre de gestion des prelevements.
     *
     * @param e Evénement de fermeture de la fenêtre
     * @return null
     * @author SHARIFI Daner
     */
    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    // Attributs de la scene + actions

    @FXML
    private Label lblInfosCompte;
    @FXML
    private ListView<Prelevement> lvPrelevement;
    @FXML
    private Button btnModifierPrelev;
    @FXML
    private Button btnSupprPrelev;
    @FXML
    private Button btnAjoutPrelev;

    /**
     * Annule et ferme la fenêtre de gestion des prelevements.
     *
     * @author SHARIFI Daner
     */
    @FXML
    private void doCancel() {
        this.primaryStage.close();
    }

    //Modification d'un prelevement

    /**
     * Modifie un prelevement sélectionné.
     *
     * @author SHARIFI Daner
     */
    @FXML
    private void doModifierPrelev() {
        int selectedIndice = this.lvPrelevement.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            Prelevement prelevMod = this.oListPrelevement.get(selectedIndice);
            Prelevement result = this.cmDialogController.modifierPrelevement(prelevMod);
            if (result != null) {
                this.oListPrelevement.set(selectedIndice, result);
            }
        }
        this.loadList();
        this.validateComponentState();
    }

    //Suppression d'un prelevement

    /**
     * Supprime un prelevement sélectionné
     *
     * @author SHARIFI Daner
     */
    @FXML
    private void doSupprimerPrelev() {
        int selectedItem = this.lvPrelevement.getSelectionModel().getSelectedIndex();
        if (selectedItem >= 0) {
            Prelevement prelev = this.oListPrelevement.get(selectedItem);
            this.cmDialogController.supprimerPrelevement(prelev);
        }
        this.loadList();
        this.validateComponentState();
    }

    //Ajout d'un nouveau prelevement

    /**
     * Crée un nouveau prelevement.
     * Ajoute le prelevement créé à la liste des prelevements du compte.
     * Met à jour la liste des prelevements du compte.
     * Met à jour l'état des composants.
     *
     * @author SHARIFI Daner
     */
    @FXML
    private void doNouveauPrelev() {
        Prelevement prelev;
        prelev = this.cmDialogController.creerNouveauPrelev();
        if (prelev != null) {
            this.oListPrelevement.add(prelev);
        }
    }


    /**
     * Charge la liste des prelevements du cmopte et l'affiche dans la vue.
     *
     * @author SHARIFI Daner
     */
    private void loadList() {
        ArrayList<Prelevement> listePrelev;
        listePrelev = this.cmDialogController.getPrelevementDunCompte();
        this.oListPrelevement.clear();
        this.oListPrelevement.addAll(listePrelev);
    }

    /**
     * Valide l'état des composants de la fenêtre.
     * Active ou désactive les boutons de modification et de suppression de prelevement.
     * Met à jour l'état des composants.
     *
     * @author SHARIFI Daner
     */
    private void validateComponentState() {
        int selectedIndice = this.lvPrelevement.getSelectionModel().getSelectedIndex();
        System.out.println(this.clientDuCompte.estInactif);
        if (selectedIndice >= 0) {
            this.btnSupprPrelev.setDisable(this.clientDuCompte.estInactif.equals("O") || this.prelevDuCompte.estCloture.equals("O"));
            this.btnModifierPrelev.setDisable(this.clientDuCompte.estInactif.equals("O") || this.prelevDuCompte.estCloture.equals("O"));

        } else {
            this.btnModifierPrelev.setDisable(true);
            this.btnSupprPrelev.setDisable(true);
        }

        this.btnAjoutPrelev.setDisable(this.clientDuCompte.estInactif.equals("O") || this.prelevDuCompte.estCloture.equals("O"));
    }
}