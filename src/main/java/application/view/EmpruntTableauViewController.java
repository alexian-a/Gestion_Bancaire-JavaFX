package application.view;

import application.DailyBankState;
import application.control.EmpruntTableau;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Emprunt;

public class EmpruntTableauViewController {

    private EmpruntTableau etDialogController;
    private Stage containingStage;
    private DailyBankState dailyBankState;
    private Emprunt empruntCourant;

    public void initContext(Stage _containingStage, EmpruntTableau _et, DailyBankState _dbstate, Emprunt emprunt) {
        this.containingStage = _containingStage;
        this.etDialogController = _et;
        this.dailyBankState = _dbstate;
        this.empruntCourant = emprunt;
        this.configure();
    }

    private void configure() {
        this.containingStage.setOnCloseRequest(e -> this.closeWindow(e));
    }

    public void displayDialog() {
        this.creerTableau(empruntCourant);
        this.containingStage.showAndWait();
    }

    private Object closeWindow(WindowEvent e) {
        e.consume();
        return null;
    }

    @FXML
    GridPane tableau;
    @FXML
    Button btnFermer;

    @FXML
    public void creerTableau(Emprunt emprunt) {
        this.tableau = new GridPane();

        double capitalDebut = emprunt.getMontant();
        double tauxInteretMensuel = emprunt.getTauxInteret() / 12;
        int dureeMois = emprunt.getDuree() * 12; // En mois

        double interetsMensuels = capitalDebut * tauxInteretMensuel;
        double mensualite = capitalDebut * (tauxInteretMensuel / (1 - Math.pow(1 + tauxInteretMensuel, dureeMois * -1)));
        double principal = mensualite - interetsMensuels;
        double capitalRestant = capitalDebut - principal;

        for (int i = 1; i <= dureeMois; i++) {

            Label labelMois = new Label(Integer.toString(i));
            Label labelMontant = new Label(String.format("%.2f", capitalRestant));
            Label labelInteret = new Label(String.format("%.2f", tauxInteretMensuel));
            Label labelMensualite = new Label(String.format("%.2f", mensualite));
            Label labelRemboursement = new Label(String.format("%.2f", principal));

            tableau.add(labelMois, 0, i);
            tableau.add(labelMontant, 1, i);
            tableau.add(labelInteret, 2, i);
            tableau.add(labelRemboursement, 3, i);
            tableau.add(labelMensualite, 4, i);

            capitalDebut = capitalRestant;
            interetsMensuels = capitalDebut * tauxInteretMensuel;
            principal = mensualite - interetsMensuels;
            capitalRestant = capitalDebut - principal;

        }

        ScrollPane scrollPane = new ScrollPane(tableau);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        BorderPane root = new BorderPane();
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 600, 400);
        containingStage.setScene(scene);
        containingStage.setTitle("Tableau d'Amortissement");
    }

    @FXML
    private void doCancel() {
        this.containingStage.close();
    }
}
