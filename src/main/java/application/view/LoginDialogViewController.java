package application.view;

import application.DailyBankState;
import application.control.LoginDialog;
import application.tools.FormValidation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;

/**
 * Controller JavaFX de la view logindialog.
 *
 */
public class LoginDialogViewController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à LoginDialogController
	private LoginDialog ldDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage containingStage;

	// Données de la fenêtre

	// Manipulation de la fenêtre

	/**
	 * Initialisation du contrôleur de vue LoginDialogController.
	 *
	 * @param _containingStage Stage qui contient le fichier xml contrôlé par
	 *                         LoginDialogController
	 * @param _ld              Contrôleur de Dialogue qui réalisera les opérations
	 *                         de navigation ou calcul
	 * @param _dbstate         Etat courant de l'application
	 */
	public void initContext(Stage _containingStage, LoginDialog _ld, DailyBankState _dbstate) {
		this.containingStage = _containingStage;
		this.ldDialogController = _ld;
		this.dailyBankState = _dbstate;
		this.configure();
	}

	/**
	 * Affichage de la fenêtre.
	 */
	public void displayDialog() {
		this.containingStage.showAndWait();
	}

	/*
	 * Configuration de LoginDialogController. Fermeture par la croix.
	 */
	private void configure() {
		this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	/*
	 * Méthode de fermeture de la fenêtre par la croix.
	 *
	 * @param e Evénement associé (inutilisé pour le moment)
	 *
	 * @return null toujours (inutilisé)
	 */
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene
	@FXML
	private TextField txtLogin;
	@FXML
	private Label errorLogin;
	@FXML
	private PasswordField txtPassword;
	@FXML
	private Label errorPassword;
	@FXML
	private Label lblMessage;

	// Actions

	/*
	 * Action quitter (annuler le login et fermer la fenêtre)
	 */
	@FXML
	private void doCancel() {
		this.ldDialogController.annulerLogin();
		this.containingStage.close();
	}

	/*
	 * Action login.
	 *
	 * Vérifier que login/password non vides. Lancer la recherche par le contrôleur
	 * de dialogue Si employé trouvé : fermer la fenêtre (sinon continuer)
	 */
	@FXML
	private void doOK() {
		TextField[] champs = { this.txtLogin, this.txtPassword };
		Label[] erreurs = { this.errorLogin, this.errorPassword };
		String[] messages = { "Login", "Mot de passe" };

		if (FormValidation.handleIsTextFieldEmpty(champs, erreurs, messages)) {
			String login = this.txtLogin.getText().trim();
			String password = this.txtPassword.getText().trim();

			Employe e;
			e = this.ldDialogController.chercherEmployeParLogin(login, password);
			if (e == null) {
				FormValidation.setFieldNeutral(this.txtPassword, this.errorPassword);
				FormValidation.setFieldError(this.txtPassword, this.errorPassword, "");
				FormValidation.setFieldNeutral(this.txtLogin, this.errorLogin);
				FormValidation.setFieldError(this.txtLogin, this.errorLogin, "Login ou mot de passe incorrect.");
			} else {
				this.containingStage.close();
			}
		}
	}
}
