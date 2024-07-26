package application.view;

import java.util.ArrayList;
import java.util.Date;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.control.PrelevementsManagement;
import application.tools.AlertUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.data.Prelevement;

public class ComptesManagementViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Contrôleur de Dialogue associé à ComptesManagementController
    private ComptesManagement cmDialogController;

    // Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
    private Stage containingStage;

    // Données de la fenêtre
    private Client clientDesComptes;
    private ObservableList<CompteCourant> oListCompteCourant;

    // Manipulation de la fenêtre
    public void initContext(Stage _containingStage, ComptesManagement _cm, DailyBankState _dbstate, Client client) {
        this.cmDialogController = _cm;
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.clientDesComptes = client;
        this.configure();
    }

    private void configure() {
        String info;

        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));

        this.oListCompteCourant = FXCollections.observableArrayList();
        this.lvComptes.setItems(this.oListCompteCourant);
        this.lvComptes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvComptes.getFocusModel().focus(-1);
        this.lvComptes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

        info = this.clientDesComptes.nom + "  " + this.clientDesComptes.prenom + "  (id : "
                + this.clientDesComptes.idNumCli + ")";
        this.lblInfosClient.setText(info);

        this.loadList();
        this.validateComponentState();

        if (this.clientDesComptes.estInactif.equals("O")) {
            this.btnAjouterCompte.setDisable(true);
        }
    }

    public void displayDialog() {
        this.containingStage.showAndWait();
    }

    // Gestion du stage
    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    // Attributs de la scene + actions
    @FXML
    private Label lblInfosClient;
    @FXML
    private ListView<CompteCourant> lvComptes;
    @FXML
    private Button btnVoirOpes;
    @FXML
    private Button btnVoirPrelevements;
    //@FXML
    //private Button btnModifierCompte;
    @FXML
    private Button btnSupprCompte;
    @FXML
    private Button btnCloturerCompte;
    @FXML
    private Button btnRelevPDF;
    @FXML
    private Button btnAjouterCompte;

    @FXML
    private void createPDF() {
        int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            PrelevementsManagement pm = new PrelevementsManagement(this.containingStage, this.dailyBankState, this.oListCompteCourant.get(selectedIndice), this.clientDesComptes);
            CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);

            ButtonType yesButton = new ButtonType("Global", ButtonBar.ButtonData.YES);
            ButtonType cancelButton = new ButtonType("Mois", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType[] buttons = new ButtonType[]{yesButton, cancelButton};
            String[] classes = new String[]{"btn-success", "btn-danger"};
            ButtonType result = AlertUtilities.showAlertWithCustomButtons(
                containingStage,
                "Relevez PDF",
                "Voulez-vous faire un relevez pour le mois ou global ?",
                "",
                AlertType.INFORMATION,
                buttons,
                classes
            );

            ArrayList<Operation> op = this.cmDialogController.operationsDunCompte(cpt.idNumCompte);

            Date date = new Date();

            if (result.getText().equals("Mois")) {
                for(int i=0; i< op.size(); i++) {
                    if (op.get(i).dateOp.getMonth() != date.getMonth()) {
                        op.remove(i);
                        i--;
                    }
                }
            }
            
            this.cmDialogController.genererPDF(this.clientDesComptes.prenom, this.clientDesComptes.nom, cpt, op);
        }
    }

    @FXML
    private void doCancel() {
        this.containingStage.close();
    }

    @FXML
    private void doVoirOperations() {
        int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
            this.cmDialogController.gererOperationsDUnCompte(cpt);
        }
        this.loadList();
        this.validateComponentState();
    }

    @FXML
    private void doVoirPrelevements() {
        int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            PrelevementsManagement pm = new PrelevementsManagement(this.containingStage, this.dailyBankState, this.oListCompteCourant.get(selectedIndice), this.clientDesComptes);
            pm.doPrelevementsManagementDialog();
        }
    }

    @FXML
    private void doModifierCompte() {
    }

    @FXML
    private void doSupprimerCompte() {
        int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            this.oListCompteCourant.remove(selectedIndice);
        }
    }

    @FXML
    private void doNouveauCompte() {
        CompteCourant compte;
        compte = this.cmDialogController.creerNouveauCompte();
        if (compte != null) {
            this.oListCompteCourant.add(compte);
        }
    }

    @FXML
    private void doCloturerCompte() {
        CompteCourant cc = this.lvComptes.getSelectionModel().getSelectedItem();
        if (cc != null && cc.estCloture.equals("N")) {
            if (this.cmDialogController.cloturerCompte(cc)) {
                this.btnCloturerCompte.setText("Supprimer compte");
                this.loadList();
            }
        } else if (cc != null && cc.estCloture.equals("O")) {
            boolean confirmSupp = AlertUtilities.confirmYesCancel(
                    this.containingStage,
                    "Supprimer le compte",
                    "Voulez-vous vraiment supprimer le compte N°" + cc.idNumCompte + " ?",
                    "",
                    Alert.AlertType.ERROR
            );
            if (confirmSupp) {
                this.cmDialogController.supprimerCompte(cc);
                this.oListCompteCourant.remove(cc);
                this.btnCloturerCompte.setText("Clôturer compte");
            }
        }
    }

    private void loadList() {
        ArrayList<CompteCourant> listeCpt;
        listeCpt = this.cmDialogController.getComptesDunClient();
        this.oListCompteCourant.clear();
        this.oListCompteCourant.addAll(listeCpt);
    }

    private void validateComponentState() {
        int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            this.btnVoirOpes.setDisable(false);
            this.btnVoirPrelevements.setDisable(false);
            this.btnRelevPDF.setDisable(false);
            if (this.clientDesComptes.estInactif.equals("O")) {
                this.btnCloturerCompte.setDisable(true);
            } else if (this.oListCompteCourant.get(selectedIndice).estCloture.equals("O")) {
                this.btnCloturerCompte.setDisable(false);
                this.btnCloturerCompte.setText("Supprimer compte");
            } else {
                this.btnCloturerCompte.setDisable(false);
                this.btnCloturerCompte.setText("Clôturer compte");
            }

        } else {
            this.btnVoirOpes.setDisable(true);
            this.btnVoirPrelevements.setDisable(true);
            this.btnRelevPDF.setDisable(true);
            this.btnCloturerCompte.setDisable(true);
        }

    }
}
