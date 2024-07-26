package application.view;

import application.DailyBankState;
import application.control.EmpruntManagement;
import application.control.EmpruntTableau;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.Emprunt;

public class EmpruntManagementViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Contrôleur de Dialogue associé à OperationsManagementController
    private EmpruntManagement emDialogController;

    // Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
    private Stage containingStage;

    // Données de la fenêtre
    private Client clientDuCompte;

    // Manipulation de la fenêtre
    public void initContext(Stage _containingStage, EmpruntManagement _em, DailyBankState _dbstate, Client client) {
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.emDialogController = _em;
        this.clientDuCompte = client;
        this.configure();
    }

    private void configure() {
        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));
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

    @FXML
    private Button btnSimuler;
    @FXML
    private Button btnFermer;
    @FXML
    private TextField txtCapital;
    @FXML
    private TextField txtDuree;
    @FXML
    private TextField txtTauxInteret;

    @FXML
    private void doSimuler() {

        int duree = Integer.parseInt(txtDuree.getText());
        double montant = Double.parseDouble(txtCapital.getText());
        double tauxInteret = Double.parseDouble(txtTauxInteret.getText());
        int idClient = this.clientDuCompte.idNumCli;
        this.emDialogController.enregistrerEmprunt(montant, duree, tauxInteret, idClient);

        Emprunt ep = new Emprunt(duree, montant, tauxInteret, idClient);
        EmpruntTableau et = new EmpruntTableau(containingStage, dailyBankState, ep);
        et.doEmpruntTableauDialog();
    }

    @FXML
    private void doCancel() {
        this.containingStage.close();
    }
}
