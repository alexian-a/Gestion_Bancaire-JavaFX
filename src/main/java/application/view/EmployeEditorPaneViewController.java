package application.view;

import application.DailyBankState;
import application.control.ExceptionDialog;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import application.tools.FormValidation;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;
import model.orm.Access_BD_Employe;
import model.orm.exception.*;

public class EmployeEditorPaneViewController {

    // état courant de l'application
    private DailyBankState dailyBankState;

    // Fenêtre physique où est la scène contenant le fichier xml contrôlé par this
    private Stage containingStage;

    // Données de la fenêtre

    private Employe employeEdite;
    private Employe employeResultat;
    private EditionMode Emode;

    // Manipulation de la fenêtre

    /**
     * Initialisation du contexte de la fenêtre
     *
     * @param _containingStage la fenêtre physique contenant la scène
     * @param _dbstate         l'état courant de l'application
     * @author SHARIFI Daner
     */
    public void initContext(Stage _containingStage, DailyBankState _dbstate) {
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.configure();
    }

    /**
     * Configuration de la fenêtre
     *
     * @author SHARIFI Daner
     */
    private void configure() {
        this.containingStage.setOnCloseRequest(this::closeWindow);
        this.droitsAccess.setItems(FXCollections.observableArrayList("chefAgence", "guichetier"));
    }

    /**
     * Affichage de la fenêtre de dialogue : découpage en plusieurs sous méthodes pour plus de clarté
     *
     * @param employe l'employé à afficher
     * @param mode    le mode d'édition
     * @return l'employé modifié ou null si l'opération est annulée
     * @author SHARIFI Daner
     */
    public Employe displayDialog(Employe employe, EditionMode mode) {
        this.initializeEmploye(employe);
        this.configureDialog(mode);

        this.initializeFields();
        this.setDroitsAccessField();

        this.containingStage.showAndWait();
        return this.employeResultat;
    }

    /**
     * Initialisation de l'employé
     *
     * @param employe l'employé à initialiser
     * @author SHARIFI Daner
     */
    private void initializeEmploye(Employe employe) {
        if (employe == null) {
            this.employeEdite = new Employe(0, "", "", "", "", "", 0);
        } else {
            this.employeEdite = new Employe(employe);
        }
        this.employeEdite.idAg = this.dailyBankState.getAgenceActuelle().idAg;
        this.employeResultat = null;
    }

    /**
     * Configuration de la fenêtre en fonction du mode d'édition
     *
     * @param mode le mode d'édition
     * @author SHARIFI Daner / ARGUELLES Alexian
     */
    private void configureDialog(EditionMode mode) {
        this.Emode = mode;
        switch (mode) {
            case CREATION:
                this.lblMessage.setText("Informations sur le nouveau employé");
                this.butOk.setText("Ajouter");
                this.butCancel.setText("Annuler");
                break;
            case MODIFICATION:
                this.lblMessage.setText("Informations employé");
                this.butOk.setText("Modifier");
                this.butCancel.setText("Annuler");
                break;
        }
    }

    /**
     * Initialisation des champs de saisie
     *
     * @author SHARIFI Daner
     */
    private void initializeFields() {
        this.txtIdEmp.setText(String.valueOf(this.employeEdite.idEmploye));
        this.txtNom.setText(this.employeEdite.nom);
        this.txtPrenom.setText(this.employeEdite.prenom);
        this.txtLogin.setText(this.employeEdite.login);
        this.txtMdp.setText(this.employeEdite.motPasse);
        this.idAgence.setText(this.dailyBankState.getAgenceActuelle().nomAg);
    }

    /**
     * Initialisation du champ de droits d'accès
     * Si l'employé a déjà des droits, on les sélectionne, sinon on sélectionne guichetier
     *
     * @author SHARIFI Daner
     */
    private void setDroitsAccessField() {
        if (!this.employeEdite.droitsAccess.isEmpty()) {
            boolean trouve = false;
            for (int i = 0; i < this.droitsAccess.getItems().size() && !trouve; i++) {
                if (this.droitsAccess.getItems().get(i).toString().equals(this.employeEdite.droitsAccess)) {
                    this.droitsAccess.getSelectionModel().select(i);
                    trouve = true;
                }
            }
        } else {
            this.droitsAccess.getSelectionModel().select(1);
        }
    }


    /**
     * Fermeture de la fenêtre
     *
     * @param e l'événement de fermeture
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
    private Label lblMessage;
    @FXML
    private TextField txtIdEmp;
    @FXML
    private TextField txtNom;
    @FXML
    private Label errorNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private Label errorPrenom;
    @FXML
    private ChoiceBox droitsAccess;
    @FXML
    private Label errorDroitsAccess;
    @FXML
    private TextField txtLogin;
    @FXML
    private Label errorLogin;
    @FXML
    private TextField txtMdp;
    @FXML
    private Label errorMdp;
    @FXML
    private TextField idAgence;
    @FXML
    private Button butOk;
    @FXML
    private Button butCancel;

    /**
     * Action sur le bouton Annuler
     *
     * @author SHARIFI Daner
     */
    @FXML
    private void doCancel() {
        this.employeResultat = null;
        this.containingStage.close();
    }

    /**
     * Action sur le bouton Ajouter / Modifier
     *
     * @author SHARIFI Daner
     * @author ARGUELLES Alexian
     */
    @FXML
    private void doAjouter() {
        if (this.isSaisieValide() && this.isLoginUnique()) {
            this.employeResultat = this.employeEdite;
            this.containingStage.close();
        }
    }

    /**
     * Vérifie si le login est unique
     *
     * @return true si le login est unique, false sinon
     * @author SHARIFI Daner
     */
    private boolean isLoginUnique() {
        boolean loginUnique = true;
        try {
            Employe employe = (new Access_BD_Employe()).findEmployeByLogin(this.employeEdite.login);
            if (employe != null && Integer.parseInt(this.txtIdEmp.getText()) != employe.idEmploye) {
                FormValidation.setFieldError(this.txtLogin, this.errorLogin, "Login déjà utilisé.");
                loginUnique = false;
            }
        } catch (DatabaseConnexionException e) {
            ExceptionDialog ed = new ExceptionDialog(this.containingStage, this.dailyBankState, e);
            ed.doExceptionDialog();
            this.containingStage.close();
            loginUnique = false;
        } catch (ApplicationException ae) {
            ExceptionDialog ed = new ExceptionDialog(this.containingStage, this.dailyBankState, ae);
            ed.doExceptionDialog();
            loginUnique = false;
        }
        return loginUnique;
    }

    /**
     * Vérifie si la saisie est valide version optimisée
     *
     * @return true si la saisie est valide, false sinon
     * @author SHARIFI Daner
     */
    private boolean isSaisieValide() {

        TextField[] textFields = {this.txtNom, this.txtPrenom, this.txtLogin, this.txtMdp};
        Label[] labels = {this.errorNom, this.errorPrenom, this.errorLogin, this.errorMdp};
        String[] errorMessages = {"Nom", "Prénom", "Login", "Mot de passe"};
        int[] minLengthVal = {2, 2, 4, 6};
        int[] maxLengthVal = {25, 15, 8, 15};

        boolean isValid = FormValidation.handleIsTextFieldEmpty(textFields, labels, errorMessages);
        isValid = isValid && FormValidation.handleIsTextFieldLengthInvalid(textFields, labels, minLengthVal, maxLengthVal, errorMessages);

        if (isValid) {
            this.employeEdite.nom = this.txtNom.getText().trim();
            this.employeEdite.prenom = this.txtPrenom.getText().trim();
            this.employeEdite.droitsAccess = this.droitsAccess.getValue().toString();
            this.employeEdite.login = this.txtLogin.getText().trim();
            this.employeEdite.motPasse = this.txtMdp.getText().trim();
        }

        return isValid;
    }
}