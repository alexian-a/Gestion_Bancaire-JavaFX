package application.view;

import java.time.LocalDate;

import application.DailyBankState;
import application.control.DailyBankMainFrame;
import application.tools.AlertUtilities;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.AgenceBancaire;
import model.data.Employe;
import model.orm.Access_BD_Employe;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

/**
 * Controller JavaFX de la view dailybankmainframe.
 */
public class DailyBankMainFrameViewController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Contrôleur de Dialogue associé à DailyBankMainFrameController
    private DailyBankMainFrame dbmfDialogController;

    // Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
    private Stage containingStage;

    // Données de la fenêtre

    // Manipulation de la fenêtre

    /**
     * Initialisation du contrôleur de vue DailyBankMainFrameController.
     *
     * @param _containingStage Stage qui contient le fichier xml contrôlé par
     *                         DailyBankMainFrameController
     * @param _dbmf            Contrôleur de Dialogue qui réalisera les opérations
     *                         de navigation ou calcul
     * @param _dbstate         Etat courant de l'application
     */
    public void initContext(Stage _containingStage, DailyBankMainFrame _dbmf, DailyBankState _dbstate) {
        this.dbmfDialogController = _dbmf;
        this.dailyBankState = _dbstate;
        this.containingStage = _containingStage;
        this.configure();
        this.validateComponentState();
    }

    /**
     * Affichage de la fenêtre.
     */
    public void displayDialog() {
        this.containingStage.show();
        this.btnConn.requestFocus();
    }

    /*
     * Configuration de DailyBankMainFrameController. Fermeture par la croix,
     * bindings des boutons connexion/déconnexion.
     */
    private void configure() {
        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));
        this.btnConn.managedProperty().bind(this.btnConn.visibleProperty());
        this.btnDeconn.managedProperty().bind(this.btnDeconn.visibleProperty());
    }

    /*
     * Méthode de fermeture de la fenêtre par la croix.
     *
     * @param e Evénement associé (inutilisé pour le moment)
     *
     * @return null toujours (inutilisé)
     */
    private Object closeWindow(WindowEvent e) {
        this.doQuit();
        e.consume();
        return null;
    }

    @FXML
    private MenuItem mitemClient;
    @FXML
    private MenuItem mitemEmploye;
    @FXML
    private MenuItem mitemConnexion;
    @FXML
    private MenuItem mitemDeConnexion;
    @FXML
    private MenuItem mitemQuitter;
    @FXML
    private Button btnConn;
    @FXML
    private Button btnDeconn;

    // Actions

    /*
     * Action menu quitter. Demander une confirmation puis fermer la fenêtre (donc
     * l'application car fenêtre principale).
     */
    @FXML
    private void doQuit() {
        if (AlertUtilities.confirmYesCancel(this.containingStage, "Quitter l'application",
                "Êtes-vous sûr de vouloir quitter l'appli ?", null, AlertType.CONFIRMATION)) {
            this.quitterBD();
            this.containingStage.close();
        }
    }

    /*
     * Action menu aide. Affichage d'une alerte simplement avec information.
     */
    @FXML
    private void doActionAide() {
        String contenu = "DailyBank V2\nSAE 2.01 Développement\n\nDéveloppé par :\n   - Daner SHARIFI\n   - Alexian ARGUELLES\n   - Romy CHAUVIERE\nà l'IUT de Blagnac.\n\nAnnée universitaire 2023-2024";
        AlertUtilities.showAlert(this.containingStage, "Aide", contenu, "", AlertType.INFORMATION);
        this.btnConn.requestFocus();
    }

    /*
     * Action login. Demande au contrôleur de dialogue de lancer le login puis maj
     * de la fenêtre.
     */
    @FXML
    private void doLogin() {

        this.dbmfDialogController.loginDunEmploye();
        this.validateComponentState();
    }

    /*
     * Action déconnexion. Demande au contrôleur de dialogue de réaliser la
     * déconnexion puis maj de la fenêtre.
     */
    @FXML
    private void doDisconnect() {
        this.containingStage.setWidth(400);
        this.containingStage.setHeight(280);
        this.dbmfDialogController.deconnexionEmploye();
        this.validateComponentState();
        this.btnConn.requestFocus();
    }

    /*
     * Mise à jour de la fenêtre Les champs d'affichage de la banque et de l'employé
     * sont mis à jour. Les boutons de connexion/déconnexion et les menus sont mis à
     * jour. Si un employé est connecté : les champs sont remplis et les
     * boutons/menus activés, sauf connexion. Si aucun employé n'est connecté : les
     * champs sont vidés et les boutons/menus désactivés, sauf connexion.
     */
    private void validateComponentState() {
        Employe employee = this.dailyBankState.getEmployeActuel();
        AgenceBancaire currentAgency = this.dailyBankState.getAgenceActuelle();

        if (employee != null && currentAgency != null) {
            this.mitemEmploye.setDisable(!this.dailyBankState.isChefDAgence());
            this.mitemClient.setDisable(false);

            this.mainContentConnected();
            this.toggleConnectionVisibility(true);
        } else {
            this.mainContentNotConnected();
            this.toggleConnectionVisibility(false);
        }
    }

    private void toggleConnectionVisibility(boolean isConnected) {
        this.mitemConnexion.setVisible(!isConnected);
        this.mitemDeConnexion.setVisible(isConnected);
        this.btnConn.setVisible(!isConnected);
        this.btnDeconn.setVisible(isConnected);
    }

    private void mainContentNotConnected() {
        // clean main content
        this.mainContent.getChildren().clear();
        Label welcomeLabel = this.makeAlert("Bienvenue sur DailyBank !", new String[]{"connection-alert", "alert-info"}, 400);
        Label connLabel = this.makeAlert("Connectez-vous pour commencer.", new String[]{"connection-alert", "alert-info"}, 400);

        // make HBox
        VBox vbox = new VBox();
        vbox.getChildren().addAll(welcomeLabel, connLabel);
        vbox.setSpacing(10);

        // set containing stage height to needed for current content
        this.setDimensions(400, 280);
        this.mainContent.add(vbox, 0, 0);
    }

    private void mainContentConnected() {
        // Set containing stage dimensions
        this.setDimensions(700, 320);

        // Clean main content
        this.mainContent.getChildren().clear();

        // Make success alert for connected user
        Label welcomeLabel = this.makeAlert(
                "Bienvenue " + this.dailyBankState.getEmployeActuel().prenom + " !",
                new String[]{"connection-alert", "alert-success"},
                670
        );

        // Add avatar image
        ImageView avatarView = this.createImageView("images/avatar.png");

        // Add bank image
        ImageView bankView = this.createImageView("images/bank.png");

        // Create HBoxes for user and agency
        HBox userBox = new HBox();
        HBox agencyBox = new HBox();

        // Fetch chef information
        String chef = "";
        if (this.dailyBankState.isChefDAgence()) {
            chef = "Vous êtes chef d'agence";
        } else {
            Employe chefEmploye = this.fetchChefEmploye();
            chef = "Chef d'agence : " + chefEmploye.prenom + " " + chefEmploye.nom;
        }


        // Create labels for user information
        Label nameLabel = createLabel(
                this.dailyBankState.getEmployeActuel().prenom + " " + this.dailyBankState.getEmployeActuel().nom,
                "fz-20"
        );
        Label roleLabel = createLabel(
                this.dailyBankState.isChefDAgence() ? "Chef d'agence" : "Employé",
                "pill-primary"
        );

        // Create labels for agency information
        Label agencyName = this.createLabel(
                this.dailyBankState.getAgenceActuelle().nomAg,
                "fz-20"
        );
        Label chefAgency = this.createLabel(
                chef,
                "pill-primary"
        );

        // Add user and agency information to their respective VBoxes
        VBox userInfoBox = new VBox(nameLabel, roleLabel);
        userInfoBox.setSpacing(2);
        VBox agencyInfoBox = new VBox(agencyName, chefAgency);
        agencyInfoBox.setSpacing(2);

        // Set spacing and add views to user and agency HBoxes
        userBox.setSpacing(10);
        userBox.getChildren().addAll(avatarView, userInfoBox);
        agencyBox.setSpacing(10);
        agencyBox.getChildren().addAll(bankView, agencyInfoBox);

        // create grid for user and agency
        GridPane userAgencyGrid = new GridPane();
        userAgencyGrid.add(userBox, 0, 0);
        userAgencyGrid.add(agencyBox, 1, 0);
        userAgencyGrid.getColumnConstraints().add(new ColumnConstraints(300));
        userAgencyGrid.getColumnConstraints().add(new ColumnConstraints(400));

        // Create main VBox and add welcome label and user box
        VBox vbox = new VBox(welcomeLabel, userAgencyGrid);
        vbox.setSpacing(10);

        // Add main VBox to main content
        this.mainContent.add(vbox, 0, 0);
    }

    private ImageView createImageView(String path) {
        Image image = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(64);
        imageView.setFitWidth(64);
        return imageView;
    }

    private Label createLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private Employe fetchChefEmploye() {
        Employe chefE = null;
        try {
            chefE = new Access_BD_Employe().findEmployeByID(
                    this.dailyBankState.getAgenceActuelle().idEmployeChefAg + ""
            );
        } catch (DatabaseConnexionException | DataAccessException | RowNotFoundOrTooManyRowsException e) {
            e.printStackTrace();
        }
        return chefE;
    }


    private Label makeAlert(String message, String[] styleClasses, int width) {
        Label label = new Label(message);
        label.getStyleClass().addAll(styleClasses);
        label.setPrefWidth(width > 0 ? width : Integer.MAX_VALUE);
        return label;
    }

    private void setDimensions(int width, int height) {
        this.containingStage.setMinHeight(height);
        this.containingStage.setHeight(height);
        this.containingStage.setMaxHeight(height);
        this.containingStage.setMinWidth(width);
        this.containingStage.setWidth(width);
        this.containingStage.setMaxWidth(width);
    }

    @FXML
    private GridPane mainContent;

    /*
     * Action menu client. Demande au contrôleur de dialogue de lancer la gestion
     * client
     */
    @FXML
    private void doClientOption() {
        this.dbmfDialogController.gestionClients();
    }

    /*
     * Action menu Employé. Not Yet Implemented. Pour le moment : une alerte
     * d'information
     */
    @FXML
    private void doEmployeOption() {
        this.dbmfDialogController.gestionEmploye();
    }

    /*
     * Se déconnecter de la bd oracle. Demande au contrôleur de dialogue de se
     * déconnecter.
     */
    private void quitterBD() {
        this.dbmfDialogController.deconnexionEmploye();
    }
}
