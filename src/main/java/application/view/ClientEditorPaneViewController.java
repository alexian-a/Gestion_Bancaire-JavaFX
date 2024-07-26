package application.view;

import java.util.ArrayList;
import java.util.regex.Pattern;

import application.DailyBankState;
import application.control.ExceptionDialog;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import application.tools.FormValidation;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.Access_BD_CompteCourant;
import model.orm.exception.ApplicationException;
import model.orm.exception.Order;
import model.orm.exception.Table;

public class ClientEditorPaneViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
    private Stage containingStage;

    // Données de la fenêtre

    private Client clientEdite;
    private EditionMode editionMode;
    private Client clientResultat;

    // Manipulation de la fenêtre

    public void initContext(Stage _containingStage, DailyBankState _dbstate) {
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.configure();
    }

    private void configure() {
        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));
    }

    public Client displayDialog(Client client, EditionMode mode) {
        this.editionMode = mode;
        if (client == null) {
            this.clientEdite = new Client(0, "", "", "", "", "", "N", this.dailyBankState.getEmployeActuel().idAg);
        } else {
            this.clientEdite = new Client(client);
        }
        this.clientResultat = null;
        switch (mode) {
            case CREATION:
                this.txtIdcli.setDisable(true);
                this.txtNom.setDisable(false);
                this.txtPrenom.setDisable(false);
                this.txtTel.setDisable(false);
                this.txtMail.setDisable(false);
                this.rbActif.setSelected(true);
                this.rbInactif.setSelected(false);
                if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
                    this.rbActif.setDisable(false);
                    this.rbInactif.setDisable(false);
                } else {
                    this.rbActif.setDisable(true);
                    this.rbInactif.setDisable(true);
                }
                this.lblMessage.setText("Informations sur le nouveau client");
                this.butOk.setText("Ajouter");
                this.butCancel.setText("Annuler");
                break;
            case MODIFICATION:
                this.txtIdcli.setDisable(true);
                this.txtNom.setDisable(false);
                this.txtPrenom.setDisable(false);
                this.txtTel.setDisable(false);
                this.txtMail.setDisable(false);
                this.rbActif.setSelected(true);
                this.rbInactif.setSelected(false);
                if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
                    this.rbActif.setDisable(false);
                    this.rbInactif.setDisable(false);
                } else {
                    this.rbActif.setDisable(true);
                    this.rbInactif.setDisable(true);
                }
                this.lblMessage.setText("Informations client");
                this.butOk.setText("Modifier");
                this.butCancel.setText("Annuler");
                break;
            case SUPPRESSION:
                // ce mode n'est pas utilisé pour les Clients :
                // la suppression d'un client n'existe pas il faut que le chef d'agence
                // bascule son état "Actif" à "Inactif"
                ApplicationException ae = new ApplicationException(Table.NONE, Order.OTHER, "SUPPRESSION CLIENT NON PREVUE",
                        null);
                ExceptionDialog ed = new ExceptionDialog(this.containingStage, this.dailyBankState, ae);
                ed.doExceptionDialog();

                break;
        }
        if (client != null && client.estInactif.equals(ConstantesIHM.CLIENT_INACTIF)) {
            this.txtMail.setDisable(true);
            this.txtTel.setDisable(true);
            this.txtNom.setDisable(true);
            this.txtPrenom.setDisable(true);
            this.txtAdr.setDisable(true);
            this.rbActif.setDisable(true);
            this.rbInactif.setDisable(true);
            this.butOk.setDisable(true);
        }
        // initialisation du contenu des champs
        this.txtIdcli.setText("" + this.clientEdite.idNumCli);
        this.txtNom.setText(this.clientEdite.nom);
        this.txtPrenom.setText(this.clientEdite.prenom);
        this.txtAdr.setText(this.clientEdite.adressePostale);
        this.txtMail.setText(this.clientEdite.email);
        this.txtTel.setText(this.clientEdite.telephone);

        this.rbInactif.setSelected(ConstantesIHM.estInactif(this.clientEdite));

        this.clientResultat = null;

        this.containingStage.showAndWait();
        return this.clientResultat;
    }

    // Gestion du stage
    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    // Attributs de la scene + actions

    @FXML
    private Label lblMessage;
    @FXML
    private TextField txtIdcli;
    @FXML
    private TextField txtNom;
    @FXML
    private Label errorNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private Label errorPreom;
    @FXML
    private TextField txtAdr;
    @FXML
    private Label errorAdr;
    @FXML
    private TextField txtTel;
    @FXML
    private Label errorTel;
    @FXML
    private TextField txtMail;
    @FXML
    private Label errorMail;
    @FXML
    private RadioButton rbActif;
    @FXML
    private RadioButton rbInactif;
    @FXML
    private ToggleGroup actifInactif;
    @FXML
    private Button butOk;
    @FXML
    private Button butCancel;

    @FXML
    private void doCancel() {
        this.clientResultat = null;
        this.containingStage.close();
    }

    @FXML
    private void doAjouter() {
        switch (this.editionMode) {
            case CREATION:
                if (this.isSaisieValide()) {
                    this.clientResultat = this.clientEdite;
                    this.containingStage.close();
                }
                break;
            case MODIFICATION:
                if (this.isSaisieValide()) {
                    this.clientResultat = this.clientEdite;
                    this.containingStage.close();
                }
                break;
            case SUPPRESSION:
                this.clientResultat = this.clientEdite;
                this.containingStage.close();
                break;
        }

    }

    private boolean isSaisieValide() {
        // Définir le statut du client en fonction du bouton radio sélectionné.
        if (this.rbActif.isSelected()) {
            this.clientEdite.estInactif = ConstantesIHM.CLIENT_ACTIF;
        } else {
            this.clientEdite.estInactif = ConstantesIHM.CLIENT_INACTIF;
        }

        // Valider les champs de texte pour les valeurs vides et les contraintes de longueur.
        TextField[] textFields = {this.txtNom, this.txtPrenom, this.txtAdr};
        Label[] labelsErreur = {this.errorNom, this.errorPreom, this.errorAdr};
        String[] messagesErreur = {"Nom", "Prénom", "Adresse"};

        boolean saisieValide = FormValidation.handleIsTextFieldEmpty(textFields, labelsErreur, messagesErreur);

        saisieValide = saisieValide && FormValidation.handleIsTextFieldLengthInvalid(
                textFields, labelsErreur,
                new int[]{2, 2, 8},
                new int[]{25, 15, 50},
                new String[]{
                        "Nom doit contenir entre 2 et 25 caractères.",
                        "Prénom doit contenir entre 2 et 15 caractères.",
                        "Adresse doit contenir entre 8 et 50 caractères."
                }
        );

        // Valider le champ email pour un format d'email valide et une longueur maximale.
        String regexEmail = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (!Pattern.matches(regexEmail, this.txtMail.getText().trim()) || this.txtMail.getText().trim().length() > 20) {
            FormValidation.setFieldError(this.txtMail, this.errorMail, "Mail n'est pas valide.");
            this.txtMail.requestFocus();
            saisieValide = false;
        } else {
            FormValidation.setFieldValid(this.txtMail, this.errorMail);
        }

        // Valider le champ numéro de téléphone pour un format valide et une longueur exacte.
        String regexTel = "(0)[1-9][0-9]{8}";
        if (!Pattern.matches(regexTel, this.txtTel.getText().trim()) || this.txtTel.getText().trim().length() != 10) {
            FormValidation.setFieldError(this.txtTel, this.errorTel, "Téléphone n'est pas valide.");
            this.txtTel.requestFocus();
            saisieValide = false;
        } else {
            FormValidation.setFieldValid(this.txtTel, this.errorTel);
        }

        // Gérer le processus de désactivation du client et mettre à jour les informations du client.
        if (saisieValide) {
            if (this.clientEdite.estInactif.equals(ConstantesIHM.CLIENT_INACTIF)) {
                try {
                    ArrayList<CompteCourant> listeCpt = new Access_BD_CompteCourant().getCompteCourants(this.clientEdite.idNumCli);
                    boolean desactivationImpossible = listeCpt.stream().anyMatch(compteCourant -> compteCourant.solde != 0);

                    if (desactivationImpossible) {
                        AlertUtilities.showAlert(containingStage, "Erreur",
                                "Impossible de désactiver le client car il possède au moins un compte avec un solde non nul.",
                                "", Alert.AlertType.WARNING);
                        saisieValide = false;
                    } else {
                        mettreAJourInfoClient();
                    }
                } catch (Exception e) {
                    AlertUtilities.showAlert(containingStage, "Erreur",
                            "Une erreur est survenue lors de la récupération des comptes du client.",
                            "", Alert.AlertType.WARNING);
                    saisieValide = false;
                }
            } else {
                mettreAJourInfoClient();
            }
        }

        return saisieValide;
    }

    /**
     * Mettre à jour les informations du client à partir des champs de texte.
     */
    private void mettreAJourInfoClient() {
        this.clientEdite.nom = this.txtNom.getText().trim();
        this.clientEdite.prenom = this.txtPrenom.getText().trim();
        this.clientEdite.adressePostale = this.txtAdr.getText().trim();
        this.clientEdite.telephone = this.txtTel.getText().trim();
        this.clientEdite.email = this.txtMail.getText().trim();
    }

}
