package application.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import application.DailyBankState;
import application.control.ClientsManagement;
import application.control.EmpruntManagement;
import application.tools.AlertUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.Access_BD_Client;
import model.orm.Access_BD_CompteCourant;
import model.orm.Access_BD_Operation;

public class ClientsManagementViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Contrôleur de Dialogue associé à ClientsManagementController
    private ClientsManagement cmDialogController;

    // Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
    private Stage containingStage;

    // Données de la fenêtre
    private ObservableList<Client> oListClients;

    private Client selectedClient;

    // Manipulation de la fenêtre
    public void initContext(Stage _containingStage, ClientsManagement _cm, DailyBankState _dbstate) {
        this.cmDialogController = _cm;
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.configure();
    }

    private void configure() {
        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));

        this.oListClients = FXCollections.observableArrayList();
        this.lvClients.setItems(this.oListClients);
        this.lvClients.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvClients.getFocusModel().focus(-1);
        this.lvClients.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
        this.validateComponentState();

        lvClients.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                this.selectedClient = newValue;
                if (this.selectedClient.estInactif.equals("O")) {
                    this.btnDesactClient.getStyleClass().clear();
                    this.btnDesactClient.getStyleClass().addAll("btn-light-success");
                    this.btnDesactClient.setText("Activer client");
                    this.btnModifClient.setText("Consulter client");
                } else {
                    this.btnDesactClient.getStyleClass().clear();
                    this.btnDesactClient.getStyleClass().addAll("btn-light-warning");
                    this.btnDesactClient.setText("Désactiver client");
                    this.btnModifClient.setText("Modifier client");
                }
            }
        });
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
    private TextField txtNum;
    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private ListView<Client> lvClients;
    @FXML
    private Button btnDesactClient;
    @FXML
    private Button btnSuppClient;
    @FXML
    private Button btnModifClient;
    @FXML
    private Button btnComptesClient;
    @FXML
    private Button btnEmprunt;

    @FXML
    private void doCancel() {
        this.containingStage.close();
    }

    @FXML
    private void doRechercher() {
        int numCompte;
        try {
            String nc = this.txtNum.getText();
            if (nc.equals("")) {
                numCompte = -1;
            } else {
                numCompte = Integer.parseInt(nc);
                if (numCompte < 0) {
                    this.txtNum.setText("");
                    numCompte = -1;
                }
            }
        } catch (NumberFormatException nfe) {
            this.txtNum.setText("");
            numCompte = -1;
        }

        String debutNom = this.txtNom.getText();
        String debutPrenom = this.txtPrenom.getText();

        if (numCompte != -1) {
            this.txtNom.setText("");
            this.txtPrenom.setText("");
        } else {
            if (debutNom.equals("") && !debutPrenom.equals("")) {
                this.txtPrenom.setText("");
            }
        }

        // Recherche des clients en BD. cf. AccessClient > getClients(.)
        // numCompte != -1 => recherche sur numCompte
        // numCompte != -1 et debutNom non vide => recherche nom/prenom
        // numCompte != -1 et debutNom vide => recherche tous les clients
        ArrayList<Client> listeCli;
        listeCli = this.cmDialogController.getlisteComptes(numCompte, debutNom, debutPrenom);

        this.oListClients.clear();
        this.oListClients.addAll(listeCli);
        this.validateComponentState();
    }

    @FXML
    private void doComptesClient() {
        if (this.selectedClient != null && this.selectedClient.idNumCli != 0) {
            this.cmDialogController.gererComptesClient(this.selectedClient);
        }
    }

    @FXML
    private void doModifierClient() {
        if (this.selectedClient != null && this.selectedClient.idNumCli != 0) {
            Client result = this.cmDialogController.modifierClient(this.selectedClient);
            if (result != null) {
                this.oListClients.set(this.oListClients.indexOf(this.selectedClient), result);
            }
        }
    }

    /**
     * Cette méthode permet de désactiver un client sélectionné. Elle vérifie
     * d'abord si un client est sélectionné, récupère les comptes courants
     * associés à ce client, puis confirme la désactivation avec l'utilisateur.
     * Si la désactivation est confirmée et possible, elle met à jour le statut
     * du client. Si une erreur survient lors de l'une de ces étapes, une alerte
     * d'erreur est affichée.
     *
     * @author SHARIFI Daner
     */
    @FXML
    private void doDesactiverClient() {
        if (this.selectedClient != null && this.selectedClient.idNumCli != 0) {
            try {
                ArrayList<CompteCourant> listeCpt = new Access_BD_CompteCourant().getCompteCourants(this.selectedClient.idNumCli);
                boolean deactivationImpossible = this.selectedClient.estInactif.equals("N") && listeCpt.stream().anyMatch(compteCourant -> compteCourant.solde != 0);

                if (!deactivationImpossible && confirmDeactivation()) {
                    updateClientStatus();
                } else if (deactivationImpossible) {
                    displayErrorAlert("Impossible de désactiver le client, il possède des comptes non vides.");
                }
            } catch (Exception e) {
                displayErrorAlert("Une erreur est survenue lors de la récupération des comptes du client.");
            }
        }
    }

    /**
     * Cette méthode affiche une boîte de dialogue de confirmation de
     * désactivation du client à l'utilisateur. Elle renvoie true si
     * l'utilisateur confirme la désactivation, sinon elle renvoie false.
     *
     * @return true si l'utilisateur confirme la désactivation, sinon false
     * @author SHARIFI Daner
     */
    private boolean confirmDeactivation() {
        String desactiver = this.selectedClient.estInactif.equals("N") ? "désactiver" : "activer";
        return AlertUtilities.confirmYesCancel(containingStage, "Confirmation",
                "Confirmation de " + (this.selectedClient.estInactif.equals("N") ? "la désactivation" : "l'activation") + " du client",
                "Voulez-vous vraiment " + desactiver + " le client " + this.selectedClient.nom + " " + this.selectedClient.prenom + " ?",
                Alert.AlertType.CONFIRMATION);
    }

    /**
     * Cette méthode met à jour le statut du client dans la base de données
     * après confirmation de l'utilisateur. Elle modifie également le libellé du
     * bouton associé au client et affiche une alerte de réussite.
     *
     * @author SHARIFI Daner
     */
    private void updateClientStatus() {
        String desactiver = this.selectedClient.estInactif.equals("N") ? "désactiver" : "activer";
        try {
            Access_BD_Client ac = new Access_BD_Client();
            this.selectedClient.estInactif = this.selectedClient.estInactif.equals("N") ? "O" : "N";
            ac.updateClient(this.selectedClient);
            btnDesactClient.setText(this.selectedClient.estInactif.equals("N") ? "Désactiver client" : "Activer client");
            AlertUtilities.showAlert(containingStage, "Information", "Le client a été " + (this.selectedClient.estInactif.equals("N") ? "activé" : "désactivé") + " avec succès.", "", Alert.AlertType.INFORMATION);
            this.btnSuppClient.setDisable(this.selectedClient.estInactif.equals("N"));
            this.btnDesactClient.getStyleClass().clear();
            this.btnDesactClient.getStyleClass().addAll(this.selectedClient.estInactif.equals("N") ? "btn-light-warning" : "btn-light-success");
            this.btnModifClient.setText(this.selectedClient.estInactif.equals("N") ? "Modifier client" : "Consulter client");
        } catch (Exception e) {
            this.selectedClient.estInactif = this.selectedClient.estInactif.equals("N") ? "N" : "O";
            displayErrorAlert("Impossible de " + desactiver + " le client, contactez le développeur.");
        }
    }

    /**
     * Cette méthode affiche une alerte d'erreur avec un message spécifié.
     *
     * @param message Le message à afficher dans l'alerte d'erreur
     * @author SHARIFI Daner
     */
    private void displayErrorAlert(String message) {
        AlertUtilities.showAlert(containingStage, "Erreur", message, "", Alert.AlertType.WARNING);
    }

    @FXML
    private void doSupprimerClient() {
        if (this.selectedClient != null && this.selectedClient.idNumCli != 0 && this.selectedClient.estInactif.equals("O")) {
            int[] idComptes;
            try {
                idComptes = new Access_BD_CompteCourant().getCompteCourants(this.selectedClient.idNumCli).stream().mapToInt(compteCourant -> compteCourant.idNumCompte).toArray();
                HashMap<Integer, Date> dateDernierOperationChaqueCompte = new HashMap<Integer, Date>();
                for (int idCompte : idComptes) {
                    ArrayList<Operation> operations = new Access_BD_Operation().getOperations(idCompte);
                    for (Operation operation : operations) {
                        // if date is superior to the last date, we update it
                        if (!dateDernierOperationChaqueCompte.containsKey(idCompte) || operation.dateOp.compareTo(dateDernierOperationChaqueCompte.get(idCompte)) > 0) {
                            dateDernierOperationChaqueCompte.put(idCompte, operation.dateOp);
                        }
                    }
                }

                StringBuilder messageDateDerniereOperationParCompte = new StringBuilder();
                for (int idCompte : idComptes) {
                    if (dateDernierOperationChaqueCompte.containsKey(idCompte)) {
                        messageDateDerniereOperationParCompte.append("\n - Compte ").append(idCompte).append(" : ").append(dateDernierOperationChaqueCompte.get(idCompte));
                    }
                }

                boolean confirmed = AlertUtilities.confirmYesCancel(containingStage, "Suppression client",
                        "Voulez-vous vraiment supprimer ce client :",
                        "Nom : " + selectedClient.nom + "\nPrenom : " + selectedClient.prenom + "\nId : " + selectedClient.idNumCli
                        + "\nDate dernière opération par compte : " + messageDateDerniereOperationParCompte,
                        Alert.AlertType.ERROR);

                if (confirmed) {
                    try {
                        new Access_BD_Client().supprimerClient(selectedClient.idNumCli, idComptes);
                        this.oListClients.remove(selectedClient);
                        AlertUtilities.showAlert(containingStage, "Information", "Le client et ses comptes ont été supprimés avec succès.", "", Alert.AlertType.INFORMATION);
                    } catch (Exception e) {
                        AlertUtilities.showAlert(containingStage, "Erreur", "Impossible de supprimer le client, contactez le développeur.", "", Alert.AlertType.WARNING);
                    }
                }
            } catch (Exception e) {
                AlertUtilities.showAlert(containingStage, "Erreur", "Impossible de récupérer les comptes du client, contactez le développeur.", "", Alert.AlertType.WARNING);
            }
        }
    }

    @FXML
    private void doNouveauClient() {
        Client client;
        client = this.cmDialogController.nouveauClient();
        if (client != null) {
            this.oListClients.add(client);
        }
    }

    @FXML
    private void doEmprunt() {
        if (this.selectedClient != null) {
            EmpruntManagement em = new EmpruntManagement(containingStage, dailyBankState, selectedClient);
            em.doEmpruntManagementDialog();
        }
    }

    private void validateComponentState() {
        // Non implémenté => désactivé
        this.btnDesactClient.setDisable(true);
        this.btnEmprunt.setDisable(true);
        int selectedIndice = this.lvClients.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            this.btnModifClient.setDisable(false);
            this.btnDesactClient.setDisable(false);
            this.btnComptesClient.setDisable(false);
            this.btnSuppClient.setDisable(this.oListClients.get(selectedIndice).estInactif.equals("N"));
        } else {
            this.btnModifClient.setDisable(true);
            this.btnComptesClient.setDisable(true);
            this.btnDesactClient.setDisable(true);
        }

        if (!this.dailyBankState.isChefDAgence()) {
            this.btnDesactClient.setVisible(false);
            this.btnSuppClient.setVisible(false);
        }
    }
}
