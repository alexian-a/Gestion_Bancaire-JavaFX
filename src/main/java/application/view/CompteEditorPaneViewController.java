package application.view;

import java.util.Locale;

import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import application.tools.FormValidation;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;

public class CompteEditorPaneViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
    private Stage containingStage;

    // Données de la fenêtre
    private EditionMode editionMode;
    private Client clientDuCompte;
    private CompteCourant compteEdite;
    private CompteCourant compteResultat;

    // Manipulation de la fenêtre
    public void initContext(Stage _containingStage, DailyBankState _dbstate) {
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.configure();
    }

    private void configure() {
        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));

        this.txtDecAutorise.focusedProperty().addListener((t, o, n) -> this.focusDecouvert(t, o, n));
        this.txtSolde.focusedProperty().addListener((t, o, n) -> this.focusSolde(t, o, n));
    }

    public CompteCourant displayDialog(Client client, CompteCourant cpte, EditionMode mode) {
        this.clientDuCompte = client;
        this.editionMode = mode;
        if (cpte == null) {
            this.compteEdite = new CompteCourant(0, 200, 0, "N", this.clientDuCompte.idNumCli);
        } else {
            this.compteEdite = new CompteCourant(cpte);
        }
        this.compteResultat = null;
        this.txtIdclient.setDisable(true);
        this.txtIdAgence.setDisable(true);
        this.txtIdNumCompte.setDisable(true);
        switch (mode) {
            case CREATION:
                this.txtDecAutorise.setDisable(false);
                this.txtSolde.setDisable(false);
                this.lblMessage.setText("Informations sur le nouveau compte");
                this.lblSolde.setText("Solde (premier dépôt)");
                this.btnOk.setText("Ajouter");
                this.btnCancel.setText("Annuler");
                break;
            case MODIFICATION:
                AlertUtilities.showAlert(this.containingStage, "Non implémenté", "Modif de compte n'est pas implémenté", null,
                        AlertType.ERROR);
                return null;
            // break;
            case SUPPRESSION:
                AlertUtilities.showAlert(this.containingStage, "Non implémenté", "Suppression de compte n'est pas implémenté",
                        null, AlertType.ERROR);
                return null;
            // break;
        }

        // Paramétrages spécifiques pour les chefs d'agences
        if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
            // rien pour l'instant
        }

        // initialisation du contenu des champs
        this.txtIdclient.setText("" + this.compteEdite.idNumCli);
        this.txtIdNumCompte.setText("" + this.compteEdite.idNumCompte);
        this.txtIdAgence.setText("" + this.dailyBankState.getEmployeActuel().idAg);
        this.txtDecAutorise.setText("" + this.compteEdite.debitAutorise);
        this.txtSolde.setText(String.format(Locale.ENGLISH, "%10.02f", this.compteEdite.solde).trim());

        this.compteResultat = null;

        this.containingStage.showAndWait();
        return this.compteResultat;
    }

    // Gestion du stage
    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    private Object focusDecouvert(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
                                  boolean newPropertyValue) {
        if (oldPropertyValue) {
            try {
                int val;
                val = Integer.parseInt(this.txtDecAutorise.getText().trim());
                if (val < 0) {
                    throw new NumberFormatException();
                }
                this.compteEdite.debitAutorise = val;
            } catch (NumberFormatException nfe) {
                this.txtDecAutorise.setText("" + this.compteEdite.debitAutorise);
            }
        }
        return null;
    }

    private Object focusSolde(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
                              boolean newPropertyValue) {
        if (oldPropertyValue) {
            try {
                double val;
                val = Double.parseDouble(this.txtSolde.getText().trim());
                if (val < 0) {
                    throw new NumberFormatException();
                }
                this.compteEdite.solde = val;
            } catch (NumberFormatException nfe) {
                this.txtSolde.setText(String.format(Locale.ENGLISH, "%10.02f", this.compteEdite.solde).trim());
            }
        }
        this.txtSolde.setText(String.format(Locale.ENGLISH, "%10.02f", this.compteEdite.solde).trim());
        return null;
    }

    // Attributs de la scene + actions
    @FXML
    private Label lblMessage;
    @FXML
    private Label lblSolde;
    @FXML
    private TextField txtIdclient;
    @FXML
    private TextField txtIdAgence;
    @FXML
    private TextField txtIdNumCompte;
    @FXML
    private TextField txtDecAutorise;
    @FXML
    private Label errorDecAutorise;
    @FXML
    private TextField txtSolde;
    @FXML
    private Label errorSolde;
    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;

    @FXML
    private void doCancel() {
        this.compteResultat = null;
        this.containingStage.close();
    }

    @FXML
    private void doAjouter() {
        switch (this.editionMode) {
            case CREATION:
                if (this.isSaisieValide()) {
                    this.compteResultat = this.compteEdite;
                    this.containingStage.close();
                }
                break;
            case MODIFICATION:
                if (this.isSaisieValide()) {
                    this.compteResultat = this.compteEdite;
                    this.containingStage.close();
                }
                break;
            case SUPPRESSION:
                this.compteResultat = this.compteEdite;
                this.containingStage.close();
                break;
        }

    }

    /**
     * Vérifie si la saisie pour la création d'un compte est valide
     *
     * @return true si la saisie est valide, false sinon
     *
     * @author SHARIFI Daner
     */
    private boolean isSaisieValide() {
        boolean isSaisieValide;

        isSaisieValide = FormValidation.handleIsTextFieldEmpty(
                new TextField[] { this.txtDecAutorise, this.txtSolde },
                new Label[] { this.errorDecAutorise, this.errorSolde },
                new String[] { "Découvert autorisé", "Solde" }
        );

        if (this.editionMode == EditionMode.CREATION) {
            isSaisieValide = isSaisieValide && FormValidation.handleIsIntegerMinMax(
                    new TextField[] { this.txtSolde },
                    new Label[] { this.errorSolde },
                    new int[] { 50 },
                    new int[] {},
                    new String[] { "Dépôt initial doit être au moins de 50 euros." }
            );
        }

        return isSaisieValide;
    }
}
