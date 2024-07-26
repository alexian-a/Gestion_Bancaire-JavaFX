package application.control;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.view.DailyBankMainFrameViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.data.Operation;
import model.data.Prelevement;
import model.orm.Access_BD_Operation;
import model.orm.Access_BD_Prelevement;
import model.orm.LogToDatabase;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;

/**
 * Classe de controleur de Dialogue de la fenêtre principale.
 *
 */

public class DailyBankMainFrame extends Application {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Stage de la fenêtre principale construite par DailyBankMainFrame
	private Stage dbmfStage;

	/**
	 * Méthode de démarrage (JavaFX).
	 */
	@Override
	public void start(Stage primaryStage) {

		this.dbmfStage = primaryStage;

		try {

			// Création de l'état courant de l'application
			this.dailyBankState = new DailyBankState();
			this.dailyBankState.setEmployeActuel(null);

			// Chargement du source fxml
			FXMLLoader loader = new FXMLLoader(
					DailyBankMainFrameViewController.class.getResource("dailybankmainframe.fxml"));
			BorderPane root = loader.load();

			// Paramétrage du Stage : feuille de style, titre
			Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.dbmfStage.setScene(scene);
			this.dbmfStage.setTitle("Fenêtre Principale");

			// checkPrelevement(); Prélèvement automatique n'est pas fini

			/*
			 * // En mise au point : // Forcer une connexion existante pour rentrer dans
			 * l'appli en mode connecté
			 * 
			 * try { Employe e; Access_BD_Employe ae = new Access_BD_Employe();
			 * 
			 * e = ae.getEmploye("Tuff", "Lejeune");
			 * 
			 * if (e == null) { System.out.println("\n\nPB DE CONNEXION\n\n"); } else {
			 * this.dailyBankState.setEmployeActuel(e); } } catch
			 * (DatabaseConnexionException e) { ExceptionDialog ed = new
			 * ExceptionDialog(this.dbmfStage, this.dailyBankState, e);
			 * ed.doExceptionDialog(); this.dailyBankState.setEmployeActuel(null); } catch
			 * (ApplicationException ae) { ExceptionDialog ed = new
			 * ExceptionDialog(this.dbmfStage, this.dailyBankState, ae);
			 * ed.doExceptionDialog(); this.dailyBankState.setEmployeActuel(null); }
			 * 
			 * if (this.dailyBankState.getEmployeActuel() != null) {
			 * this.dailyBankState.setEmployeActuel(this.dailyBankState.getEmployeActuel());
			 * }
			 * 
			 */

			// Récupération du contrôleur et initialisation (stage, contrôleur de dialogue,
			// état courant)
			DailyBankMainFrameViewController dbmfViewController = loader.getController();
			dbmfViewController.initContext(this.dbmfStage, this, this.dailyBankState);

			dbmfViewController.displayDialog();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Méthode principale de lancement de l'application.
	 */
	public static void runApp() {
		Application.launch();
	}

	/**
	 * Réaliser la déconnexion de l'application.
	 */
	public void deconnexionEmploye() {
		this.dailyBankState.setEmployeActuel(null);
		try {
			LogToDatabase.closeConnexion();
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.dbmfStage, this.dailyBankState, e);
			ed.doExceptionDialog();
		}
	}

	/**
	 * Lancer la connexion de l'utilisateur (login/mdp employé).
	 */
	public void loginDunEmploye() {
		LoginDialog ld = new LoginDialog(this.dbmfStage, this.dailyBankState);
		ld.doLoginDialog();
	}

	/**
	 * Lancer la gestion des clients (liste des clients).
	 */
	public void gestionClients() {
		ClientsManagement cm = new ClientsManagement(this.dbmfStage, this.dailyBankState);
		cm.doClientManagementDialog();
	}

	/**
	 * Lancer la gestion des clients (liste des clients).
	 * @author ARGUELLES Alexian
	 */
	public void gestionEmploye() {
		EmployeManagement em = new EmployeManagement(this.dbmfStage, this.dailyBankState);
		em.doEmployeManagementDialog();

	}

	private void checkPrelevement() {
		Access_BD_Prelevement ap = new Access_BD_Prelevement();
		Access_BD_Operation op = new Access_BD_Operation();

		ArrayList<Prelevement> arrayAp = new ArrayList<Prelevement>();
		ArrayList<Operation> arrayOp = new ArrayList<Operation>();

			try {

				LocalDate today = LocalDate.now();
				int jour = today.getDayOfMonth();

				arrayAp = ap.getPrelevementsByDate(jour);

				for (int i =0; i<arrayAp.size();i++) {
					System.out.println(arrayAp.get(i));
				}

			} catch (DataAccessException | DatabaseConnexionException e) {
				e.printStackTrace();
			}
		
			try {
				arrayOp = op.getOperationsToday();
				for (int i =0; i<arrayOp.size();i++) {
					System.out.println(arrayOp.get(i));
				}
			} catch (DataAccessException | DatabaseConnexionException e) {
				e.printStackTrace();
			}

			for(int i=0; i<arrayAp.size(); i++) {
				Boolean check = false;
				for(int j=0; j<arrayOp.size(); j++) {
					if ((arrayOp.get(j).idNumCompte == arrayAp.get(i).idNumCompte) && (arrayOp.get(j).montant  == arrayAp.get(i).debitPrelev)) {
						check = true;
						break;
					}
				}

				if (!check) {
					try {
						op.insertDebit(arrayAp.get(i).idNumCompte, arrayAp.get(i).debitPrelev, "Prélèvement automatique");
						check = true;
						System.out.println("Insertion de l'operation :\n"+"Prélèvement : "+arrayAp.get(i).beneficiaire+" - "+arrayAp.get(i).idNumPrelev);
					} catch (DatabaseConnexionException | ManagementRuleViolation | DataAccessException e) {
						e.printStackTrace();
					}
				}
			}

	}

}
