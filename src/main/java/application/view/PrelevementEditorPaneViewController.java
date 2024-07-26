package application.view;

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
import model.data.CompteCourant;
import model.data.Prelevement;

public class PrelevementEditorPaneViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
    private Stage primaryStage;

    // Données de la fenêtre

    private EditionMode editionMode;
    private CompteCourant compteDuPrelev;
    private Prelevement prelevementEditer;
    private Prelevement prelevementResultat;

    // Manipulation de la fenêtre

    /**
     * Cette méthode permet d'initialiser le contexte de la fenêtre de dialogue de gestion d'un prélèvement.
     *
     * @param _containingStage La fenêtre physique contenant la scène
     * @param _dbstate L'état courant de l'application
     *
     * @author SHARIFI Daner
     */
    public void initContext(Stage _containingStage, DailyBankState _dbstate) {
        this.primaryStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.configure();
    }

    /**
     * Cette méthode permet de configurer les éléments de la fenêtre de dialogue de gestion d'un prélèvement.
     * Elle permet de gérer les événements de fermeture de la fenêtre, de focus sur les champs de saisie.
     *
     * @author SHARIFI Daner
     */
    private void configure() {
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

        this.txtDatePrelev.focusedProperty().addListener((t, o, n) -> this.focusDatePrelev(t, o, n));
        this.txtMontant.focusedProperty().addListener((t, o, n) -> this.focusMontantPrelev(t, o, n));
        this.txtBeneficiaire.focusedProperty().addListener((t,o,n) -> this.focusBeneficiaire(t,o,n));
    }

    /**
     * Cette méthode permet d'afficher la fenêtre de dialogue de gestion d'un prélèvement.
     * Elle permet de gérer les événements de création, de modification et de suppression d'un prélèvement.
     *
     * @param compte Le compte à qui appartient le prélèvement
     * @param prlv Le prélèvement concerné
     * @param mode Le mode d'édition à utiliser
     * @return Le prélèvement résultat
     *
     * @author SHARIFI Daner
     */
    public Prelevement displayDialog(CompteCourant compte, Prelevement prlv, EditionMode mode) {
        this.compteDuPrelev = compte;
        this.editionMode = mode;
        if (prlv == null) {
            this.prelevementEditer = new Prelevement(0, 0, this.compteDuPrelev.idNumCompte, 01, null);
        } else {
            this.prelevementEditer = new Prelevement(prlv);
        }
        this.prelevementResultat = null;
        this.txtIdprelev.setDisable(true);
        this.txtIdNumCompte.setDisable(true);
        switch (mode) {
            case CREATION:
                this.txtMontant.setDisable(false);
                this.txtDatePrelev.setDisable(false);
                this.txtBeneficiaire.setDisable(false);
                this.lblMessage.setText("Informations sur le nouveau prélèvement automatique");
                this.btnOk.setText("Ajouter");
                this.btnCancel.setText("Annuler");
                break;
            case MODIFICATION:
                this.txtMontant.setDisable(false);
                this.txtDatePrelev.setDisable(false);
                this.txtBeneficiaire.setDisable(false);
                this.lblMessage.setText("Modification du prélèvement");
                this.btnOk.setText("Modifier");
                this.btnCancel.setText("Annuler");
                break;
            case SUPPRESSION:
                this.txtMontant.setDisable(true);
                this.txtDatePrelev.setDisable(true);
                this.txtBeneficiaire.setDisable(true);
                this.errorSupp.setText("ATTENTION, vous êtes sur le point de supprimer le prélèvement suivant :");
                this.errorSupp.getStyleClass().add("alert-danger");
                this.lblMessage.setText("Suppression du prélèvement");
                this.btnOk.setText("Supprimer");
                this.btnOk.getStyleClass().remove("btn-primary");
                this.btnOk.getStyleClass().add("btn-danger");
                this.btnCancel.setText("Annuler");
                break;
        }

        // Paramétrages spécifiques pour les chefs d'agences
        if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
            // rien pour l'instant
        }

        // initialisation du contenu des champs
        this.txtIdprelev.setText("" + this.prelevementEditer.idNumPrelev);
        this.txtIdNumCompte.setText("" + this.prelevementEditer.idNumCompte);
        this.txtMontant.setText(""+this.prelevementEditer.debitPrelev);
        this.txtBeneficiaire.setText("" + this.prelevementEditer.beneficiaire);
        this.txtDatePrelev.setText(""+this.prelevementEditer.datePrelev);

        this.prelevementResultat = null;

        this.primaryStage.showAndWait();
        return this.prelevementResultat;
    }

    // Gestion du stage

    /**
     * Cette méthode permet de gérer l'événement de fermeture de la fenêtre de dialogue de gestion d'un prélèvement.
     *
     * @param e L'événement de fermeture de la fenêtre
     * @return null
     *
     * @author SHARIFI Daner
     */
    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    /**
     * Cette méthode permet de gérer l'événement de focus sur le champ de bénéficiaire en mettant à jour la valeur du bénéficiaire du prélèvement automatique.
     *
     * @param txtField Le champ de texte du bénéficiaire
     * @param oldPropertyValue La valeur précédente de la propriété "focused"
     * @param newPropertyValue La nouvelle valeur de la propriété "focused"
     * @return null
     *
     * @author SHARIFI Daner
     */
    private Object focusBeneficiaire(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
                                     boolean newPropertyValue) {
        if (oldPropertyValue) {
            try {
                String val;
                val = this.txtBeneficiaire.getText().trim();
                this.prelevementEditer.beneficiaire = val;
            } catch (NumberFormatException nfe) {
                this.txtBeneficiaire.setText("" + this.prelevementEditer.beneficiaire);
            }
        }
        return null;
    }

    /**
     * Gère l'événement de focus sur le champ de date prélèvement en mettant à jour la valeur du date occurence du prélèvement automatique.
     *
     * @param txtField Le champ de texte du découvert autorisé
     * @param oldPropertyValue La valeur précédente de la propriété "focused"
     * @param newPropertyValue La nouvelle valeur de la propriété "focused"
     * @return null
     */
    private Object focusDatePrelev(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
                                   boolean newPropertyValue) {
        if (oldPropertyValue) {
            try {
                int val;
                val = Integer.parseInt(this.txtDatePrelev.getText().trim());
                if (val < 0) {
                    throw new NumberFormatException();
                }
                this.prelevementEditer.datePrelev = val;
            } catch (NumberFormatException nfe) {
                this.txtDatePrelev.setText("" + this.prelevementEditer.datePrelev);
            }
        }
        return null;
    }

    /**
     * Gère l'événement de focus sur le champ de montant en mettant à jour la valeur du montant du prelevement automatique.
     *
     * @param txtField Le champ de texte du solde
     * @param oldPropertyValue La valeur précédente de la propriété "focused"
     * @param newPropertyValue La nouvelle valeur de la propriété "focused"
     * @return null
     */
    private Object focusMontantPrelev(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
                                      boolean newPropertyValue) {
        if (oldPropertyValue) {
            try {
                double val;
                val = Double.parseDouble(this.txtMontant.getText().trim());
                if (val < 0) {
                    throw new NumberFormatException();
                }
                this.prelevementEditer.debitPrelev = val;
            } catch (NumberFormatException nfe) {
                this.txtMontant.setText("" + this.prelevementEditer.debitPrelev);
            }
        }
        return null;
    }

    // Attributs de la scene + actions
    @FXML
    private Label lblMessage;
    @FXML
    private Label errorSupp;
    @FXML
    private TextField txtIdprelev;
    @FXML
    private Label errorIdprelev;
    @FXML
    private TextField txtMontant;
    @FXML
    private Label errorMontant;
    @FXML
    private TextField txtIdNumCompte;
    @FXML
    private Label errorIdNumCompte;
    @FXML
    private TextField txtBeneficiaire;
    @FXML
    private Label errorBeneficiaire;
    @FXML
    private TextField txtDatePrelev;
    @FXML
    private Label errorDatePrelev;
    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;



    /**
     * Gère l'action de l'annulation de l'opération en cours.
     * Réinitialise le compte résultat et ferme la fenêtre principale.
     */
    @FXML
    private void doCancel() {
        this.prelevementResultat = null;
        this.primaryStage.close();
    }



    /**
     * Gère l'action du bouton "Ajouter" en fonction du mode d'édition.
     * Vérifie la validité de la saisie, attribue le compte édité au compte résultat et ferme la fenêtre principale.
     */
    @FXML
    private void doAjouter() {
        switch (this.editionMode) {
            case CREATION:
                if (this.isSaisieValide()) {
                    this.prelevementResultat = this.prelevementEditer;
                    this.primaryStage.close();
                }
                break;
            case MODIFICATION:
                if (this.isSaisieValide()) {
                    this.prelevementResultat = this.prelevementEditer;
                    this.primaryStage.close();
                }
                break;
            case SUPPRESSION:
                this.prelevementResultat = this.prelevementEditer;
                this.primaryStage.close();
                break;
        }

    }

    /**
     * Vérifie si la saisie des champs est valide.
     *
     * @return true si la saisie est valide, false sinon
     *
     * @author SHARIFI Daner
     */
    private boolean isSaisieValide() {
        TextField[] textFields = {this.txtMontant, this.txtDatePrelev, this.txtBeneficiaire};
        Label[] errorLabels = {this.errorMontant, this.errorDatePrelev, this.errorBeneficiaire};
        String[] errorMessages = {"Montant du prélèvement", "Date du prélèvement", "Bénéficiaire du prélèvement"};

        boolean saisieValide, condition1, condition2, condition3;
        condition1 = FormValidation.handleIsTextFieldEmpty(textFields, errorLabels, errorMessages);
        condition2 = FormValidation.handleIsIntegerMinMax(
                new TextField[]{this.txtMontant, this.txtDatePrelev},
                new Label[]{this.errorMontant, this.errorDatePrelev},
                new int[]{1, 1},
                new int[]{999999, 99},
                new String[]{"Doit être entre 1 et 999,999", "Doit être entre 1 et 99"}
        );
        condition3 = FormValidation.handleIsTextFieldLengthInvalid(
                new TextField[]{this.txtBeneficiaire},
                new Label[]{this.errorBeneficiaire},
                new int[]{2},
                new int[]{50},
                new String[]{"Bénéficiaire du prélèvement"}
        );
        saisieValide = condition1 && condition2 && condition3;
        if (saisieValide) {
            this.prelevementEditer.beneficiaire = this.txtBeneficiaire.getText();
            this.prelevementEditer.datePrelev = Integer.parseInt(this.txtDatePrelev.getText());
            this.prelevementEditer.debitPrelev = Double.parseDouble(this.txtMontant.getText());
        }

        return saisieValide;
    }
}