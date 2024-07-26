package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.EmployeManagementViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;
import model.orm.Access_BD_Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class EmployeManagement {
    
    private Stage cmStage;
	private DailyBankState dailyBankState;
	private EmployeManagementViewController emController;

    public EmployeManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(EmployeManagementViewController.class.getResource("employesmanagement.fxml"));
			BorderPane root = loader.load();


			Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.cmStage = new Stage();
			this.cmStage.initModality(Modality.WINDOW_MODAL);
			this.cmStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.cmStage);
			this.cmStage.setScene(scene);
			this.cmStage.setTitle("Gestion des employés");
			this.cmStage.setResizable(false);

			this.emController = loader.getController();
			this.emController.initContext(this.cmStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public void doEmployeManagementDialog() {
		this.emController.displayDialog();
	}

    public ArrayList<Employe> getlisteEmployes(int _numCompte, String _debutNom, String _debutPrenom){
        ArrayList<Employe> listeEmp = new ArrayList<>();
        try {
            Access_BD_Employe ae = new Access_BD_Employe();
            listeEmp = ae.getsEmployes(this.dailyBankState.getEmployeActuel().idAg, _numCompte, _debutNom, _debutPrenom);

        } catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.cmStage.close();
			listeEmp = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeEmp = new ArrayList<>();
        }
        return listeEmp;
    }

	public boolean supprimerEmploye(int id) {
		try {
			Access_BD_Employe ae = new Access_BD_Employe();
			ae.supprimerEmploye(id);
			return true;
		} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.cmStage.close();
		} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
		}
		return false;
	}

	/**
	 * Méthode pour créer un nouvel employé
	 *
	 * @return Employe : l'employé créé
	 *
	 * @see Employe
	 * @see EmployeEditorPane
	 * @see EditionMode
	 * @see Access_BD_Employe
	 * @see DatabaseConnexionException
	 * @see ApplicationException
	 *
	 * @author SHARIFI Daner
	 */
	public Employe nouveauEmploye() {
		Employe employe;
		EmployeEditorPane eep = new EmployeEditorPane(this.cmStage, this.dailyBankState);
		employe = eep.doEmployeEditorDialog(null, EditionMode.CREATION);
		if (employe != null) {
			try {
				Access_BD_Employe ac = new Access_BD_Employe();

				ac.insertEmploye(employe);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.cmStage.close();
				employe = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				employe = null;
			}
		}
		return employe;
	}


	public Employe modifEmploye(Employe em) {
		EmployeEditorPane eep = new EmployeEditorPane(this.cmStage, this.dailyBankState);
		Employe employe;
		employe = eep.doEmployeEditorDialog(em, EditionMode.MODIFICATION);
		if (employe != null) {
			try {
				Access_BD_Employe ac = new Access_BD_Employe();
				ac.modifierEmploye(employe);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.cmStage.close();
				employe = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				employe = null;
			}
		}
		return employe;
	}

}
