package application.view;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Optional;

import javax.swing.text.html.Option;

import application.DailyBankState;
import application.control.ClientsManagement;
import application.control.EmployeManagement;
import application.tools.AlertUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.Employe;

public class EmployeManagementViewController {
    	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à EmployeManagementViewController
	private EmployeManagement emDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage containingStage;

	// Données de la fenêtre
	private ObservableList<Employe> oEmployList;

    private void configure(){
        this.containingStage.setOnCloseRequest(e-> this.closeWindow(e));
    
        this.oEmployList = FXCollections.observableArrayList();
        this.EmployeListView.setItems(this.oEmployList);
        this.EmployeListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.EmployeListView.getFocusModel().focus(-1);
        this.EmployeListView.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
        this.validateComponentState();
    }

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, EmployeManagement _em, DailyBankState _dbstate) {
        System.out.println(_containingStage+" "+_em+" "+_dbstate);
        this.emDialogController = _em;
        this.containingStage = _containingStage;
        this.dailyBankState = _dbstate;
        this.configure();
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
    private TextField txtNum;

    @FXML
    private TextField txtNom;

    @FXML
    private TextField txtPrenom;

    @FXML
    private ListView<Employe> EmployeListView;

    @FXML
    private Button bModifEmploye;

    @FXML
    private Button bDesacEmploye;



    @FXML
	private void doCancel() {
		this.containingStage.close();
	}

    /**
     * Action sur le bouton nouveau employé
     *
     * @author SHARIFI Daner
     */
    @FXML
    private void actionNouveauEmploye() {
        Employe employe = this.emDialogController.nouveauEmploye();
        if (employe != null) {
            this.oEmployList.add(employe);
        }
    }


    /**
     * Action sur le bouton modifié employé
     *
     * @author ARGUELLES Alexian
     */
    @FXML
    private void actionModifierEmploye() {
        int selectedIndice = this.EmployeListView.getSelectionModel().getSelectedIndex();
        Employe em = this.emDialogController.modifEmploye(oEmployList.get(selectedIndice));
        if (em != null) {
            oEmployList.set(selectedIndice, em);
        }
    }

    @FXML
    private void actionSupprimer() {
        int selectedIndice = this.EmployeListView.getSelectionModel().getSelectedIndex();

        String title = "Suppression employé";
        String message = "Voulez-vous vraiment supprimer cet utilisateur :";
        String content = "Nom : " + oEmployList.get(selectedIndice).nom +
                "\nPrenom : " + oEmployList.get(selectedIndice).prenom +
                "\nRôle : " + oEmployList.get(selectedIndice).droitsAccess;

        boolean confirmed = AlertUtilities.confirmYesCancel(containingStage, title, message, content, AlertType.ERROR);

        if (confirmed) {
            this.emDialogController.supprimerEmploye(oEmployList.get(selectedIndice).idEmploye);
            oEmployList.remove(selectedIndice);
        }
    }


    /* Action sur le bouton rechercher employé
    *  @author ARGUELLES Alexian
    */
    @FXML
    private void actionRecherche() {
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

        ArrayList<Employe> listEmp;
        listEmp = this.emDialogController.getlisteEmployes(numCompte, debutNom, debutPrenom);

        this.oEmployList.clear();
        this.oEmployList.addAll(listEmp);
        this.validateComponentState();

    }


    @FXML
    private void validateComponentState() {
		this.bDesacEmploye.setDisable(true);
		int selectedIndice = this.EmployeListView.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0 && (oEmployList.get(selectedIndice).idEmploye != this.dailyBankState.getEmployeActuel().idEmploye)) {
			this.bModifEmploye.setDisable(false);
            this.bDesacEmploye.setDisable(false);
		} else {
			this.bModifEmploye.setDisable(true);
            this.bDesacEmploye.setDisable(true);
		}
	}
}
