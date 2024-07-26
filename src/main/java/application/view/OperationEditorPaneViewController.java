package application.view;

import java.util.Locale;

import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.ConstantesIHM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.CompteCourant;
import model.data.Operation;

public class OperationEditorPaneViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
    private Stage containingStage;

    // Données de la fenêtre
    private CategorieOperation categorieOperation;
    private CompteCourant compteEdite;
    private Operation operationResultat;

    // Manipulation de la fenêtre
    public void initContext(Stage _containingStage, DailyBankState _dbstate) {
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.configure();
    }

    private void configure() {
        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));
    }

    public Operation displayDialog(CompteCourant cpte, CategorieOperation mode) {
        this.categorieOperation = mode;
        this.compteEdite = cpte;

        String info;
        ObservableList<String> listTypesOpesPossibles = FXCollections.observableArrayList();

        switch (mode) {

            case DEBIT:
                this.containingStage.setHeight(320);
                info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
                        + String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
                        + String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
                this.lblMessage.setText(info);

                this.btnOk.setText("Effectuer Débit");
                this.btnCancel.setText("Annuler Débit");

                listTypesOpesPossibles.addAll(ConstantesIHM.OPERATIONS_DEBIT_GUICHET);

                this.cbTypeOpe.setItems(listTypesOpesPossibles);
                this.cbTypeOpe.getSelectionModel().select(0);
                break;
            case CREDIT:
                this.containingStage.setHeight(320);
                info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
                        + String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
                        + String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
                this.lblMessage.setText(info);

                this.btnOk.setText("Effectuer Crédit");
                this.btnCancel.setText("Annuler Crédit");

                listTypesOpesPossibles.addAll(ConstantesIHM.OPERATIONS_CREDIT_GUICHET);

                this.cbTypeOpe.setItems(listTypesOpesPossibles);
                this.cbTypeOpe.getSelectionModel().select(0);
                break;
            case VIREMENT:
                this.containingStage.setHeight(350);
                info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
                        + String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
                        + String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
                this.lblMessage.setText(info);

                this.btnOk.setText("Effectuer Virement");
                this.btnCancel.setText("Annuler Virement");

                this.txtCompte = new TextField();
                this.txtCompte.getStyleClass().add("input");
                Label labelCmpDestinataire = new Label("Compte destinataire");
                labelCmpDestinataire.setAlignment(Pos.CENTER_RIGHT);
                labelCmpDestinataire.setMaxWidth(Double.MAX_VALUE);
                labelCmpDestinataire.setPadding(new Insets(0, 20, 0, 0));
                labelCmpDestinataire.getStyleClass().add("fz-16");

                this.gridPane.addRow(2, labelCmpDestinataire);
                this.gridPane.addRow(2, txtCompte);

                this.btnOk.setText("Effectuer Transfert");
                this.btnCancel.setText("Annuler Transfert");

                listTypesOpesPossibles.addAll(ConstantesIHM.OPERATIONS_VIREMENT_GUICHET);

                this.cbTypeOpe.setItems(listTypesOpesPossibles);
                this.cbTypeOpe.getSelectionModel().select(0);
                break;
        }

        this.operationResultat = null;
        this.cbTypeOpe.requestFocus();

        this.containingStage.showAndWait();
        return this.operationResultat;
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
    private Label lblMontant;
    @FXML
    private ComboBox<String> cbTypeOpe;
    @FXML
    private TextField txtMontant;
    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;
    @FXML
    private GridPane gridPane;

    private TextField txtCompte;

    @FXML
    private void doCancel() {
        this.operationResultat = null;
        this.containingStage.close();
    }

    @FXML
    private void doAjouter() {
        double montant;
        String info;
        String typeOp;
        switch (this.categorieOperation) {
            case DEBIT:
                // règles de validation d'un débit :
                // - le montant doit être un nombre valide
                // - et si l'utilisateur n'est pas chef d'agence,
                // - le débit ne doit pas amener le compte en dessous de son découvert autorisé

                this.txtMontant.getStyleClass().remove("borderred");
                this.lblMontant.getStyleClass().remove("borderred");
                this.lblMessage.getStyleClass().remove("borderred");
                info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
                        + String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
                        + String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
                this.lblMessage.setText(info);

                try {
                    montant = Double.parseDouble(this.txtMontant.getText().trim());
                    if (montant <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException nfe) {
                    this.txtMontant.getStyleClass().add("borderred");
                    this.lblMontant.getStyleClass().add("borderred");
                    this.txtMontant.requestFocus();
                    return;
                }
                // Paramétrages spécifiques pour les chefs d'agences
                if (!ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
                    if (this.compteEdite.solde - montant < this.compteEdite.debitAutorise) {
                        typeOp = this.cbTypeOpe.getValue();
                        this.operationResultat = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp);
                        this.containingStage.close();
                    } else {
                        info = "Dépassement du découvert ! - Cpt. : " + this.compteEdite.idNumCompte + "  "
                                + String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
                                + String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
                        this.lblMessage.setText(info);
                        this.txtMontant.getStyleClass().add("borderred");
                        this.lblMontant.getStyleClass().add("borderred");
                        this.lblMessage.getStyleClass().add("borderred");
                        this.txtMontant.requestFocus();
                        return;
                    }
                }
                typeOp = this.cbTypeOpe.getValue();
                this.operationResultat = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp);
                this.containingStage.close();
                break;
            case CREDIT:
                // règles de validation d'un débit :
                // - le montant doit être un nombre valide

                this.txtMontant.getStyleClass().remove("borderred");
                this.lblMontant.getStyleClass().remove("borderred");
                this.lblMessage.getStyleClass().remove("borderred");
                info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
                        + String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
                        + String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
                this.lblMessage.setText(info);

                try {
                    montant = Double.parseDouble(this.txtMontant.getText().trim());
                    if (montant <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException nfe) {
                    this.txtMontant.getStyleClass().add("borderred");
                    this.lblMontant.getStyleClass().add("borderred");
                    this.txtMontant.requestFocus();
                    return;
                }
                typeOp = this.cbTypeOpe.getValue();
                this.operationResultat = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp);
                this.containingStage.close();
                break;
            case VIREMENT:
                this.txtMontant.getStyleClass().remove("borderred");
                this.lblMontant.getStyleClass().remove("borderred");
                this.lblMessage.getStyleClass().remove("borderred");
                info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
                        + String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
                        + String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
                this.lblMessage.setText(info);

                try {
                    montant = Double.parseDouble(this.txtMontant.getText().trim());
                    if (montant <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException nfe) {
                    this.txtMontant.getStyleClass().add("borderred");
                    this.lblMontant.getStyleClass().add("borderred");
                    this.txtMontant.requestFocus();
                    return;
                }
                typeOp = this.cbTypeOpe.getValue();
                this.operationResultat = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp, Integer.parseInt(this.txtCompte.getText()));
                this.containingStage.close();
                break;
        }
    }
}
