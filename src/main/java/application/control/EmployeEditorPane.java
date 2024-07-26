package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.EmployeEditorPaneViewController;
import application.view.EmployeManagementViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;

public class EmployeEditorPane {

	private Stage eepStage;
	private EmployeEditorPaneViewController eepViewController;
	private DailyBankState dailyBankState;

	/**
	 * Constructor de la classe EmployeEditorPane
	 *
	 * @param _parentStage Le stage parent de la fenêtre de dialogue
	 * @param _dbstate L'état de l'application
	 *
	 * @see DailyBankState
	 * @see EmployeEditorPaneViewController
	 *
	 * @author SHARIFI Daner
	 */
	public EmployeEditorPane(Stage _parentStage, DailyBankState _dbstate) {
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(EmployeManagementViewController.class.getResource("employeeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.eepStage = new Stage();
			this.eepStage.initModality(Modality.WINDOW_MODAL);
			this.eepStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.eepStage);
			this.eepStage.setScene(scene);
			this.eepStage.setTitle("Gestion d'un client");
			this.eepStage.setResizable(false);

			this.eepViewController = loader.getController();
			this.eepViewController.initContext(this.eepStage, this.dailyBankState);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Affiche la fenêtre de dialogue de gestion d'un employé
	 *
	 * @param employe L'employé à gérer
	 * @param em Le mode d'édition
	 * @return L'employé modifié
	 *
	 * @see Employe
	 * @see EditionMode
	 *
	 * @author SHARIFI Daner
	 */
	public Employe doEmployeEditorDialog(Employe employe, EditionMode em) {
		return this.eepViewController.displayDialog(employe, em);
	}
}