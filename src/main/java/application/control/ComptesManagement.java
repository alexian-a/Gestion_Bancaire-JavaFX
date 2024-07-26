package application.control;

import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ComptesManagementViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Cell;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.data.Prelevement;
import model.orm.Access_BD_CompteCourant;
import model.orm.Access_BD_Operation;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Table;

public class ComptesManagement {

    private Stage cmStage;
    private ComptesManagementViewController cmViewController;
    private DailyBankState dailyBankState;
    private Client clientDesComptes;

    public ComptesManagement(Stage _parentStage, DailyBankState _dbstate, Client client) {

        this.clientDesComptes = client;
        this.dailyBankState = _dbstate;
        try {
            FXMLLoader loader = new FXMLLoader(ComptesManagementViewController.class.getResource("comptesmanagement.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.cmStage = new Stage();
            this.cmStage.initModality(Modality.WINDOW_MODAL);
            this.cmStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.cmStage);
            this.cmStage.setScene(scene);
            this.cmStage.setTitle("Gestion des comptes");
            this.cmStage.setResizable(false);

            this.cmViewController = loader.getController();
            this.cmViewController.initContext(this.cmStage, this, _dbstate, client);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doComptesManagementDialog() {
        this.cmViewController.displayDialog();
    }

    public void gererOperationsDUnCompte(CompteCourant cpt) {
        OperationsManagement om = new OperationsManagement(this.cmStage, this.dailyBankState,
                this.clientDesComptes, cpt);
        om.doOperationsManagementDialog();
    }

    public void genererPDF(String prenom, String nom, CompteCourant cpt, ArrayList<Operation> op) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer le relevé de compte");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Documents", "pdf"));
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String dest = fileChooser.getSelectedFile().getAbsolutePath();
            if (!dest.toLowerCase().endsWith(".pdf")) {
                dest += ".pdf";
            }



            ArrayList<String[]> opArray = new ArrayList<>();

            Double solde = 0.0;

            for (int i = 0; i < op.size(); i++) {
                String methode;
                if (op.get(i).idTypeOp.equals("Retrait Espèces") || op.get(i).idTypeOp.equals("Dépôt Espèces")) {
                    methode = "Espèces";
                } else if (op.get(i).idTypeOp.equals("Dépôt Chèque")) {
                    methode = "Chèque";
                } else {
                    methode = "Prélèvement";
                }

                if (op.get(i).montant < 0) {
                    opArray.add(new String[]{op.get(i).dateOp.toString(), methode, "", String.valueOf(op.get(i).montant)});
                } else {
                    opArray.add(new String[]{op.get(i).dateOp.toString(), methode, String.valueOf(op.get(i).montant), ""});
                }

                solde += op.get(i).montant;
            }

            String[][] operations = new String[opArray.size()][4];
            for (int i = 0; i < opArray.size(); i++) {
                operations[i] = opArray.get(i);
            }

            try {
                Document document = new Document(PageSize.A4, 36, 36, 54, 54);
                PdfWriter.getInstance(document, new FileOutputStream(dest));
                document.open();

                Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
                Paragraph title = new Paragraph("RELEVÉ DE COMPTE", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                document.add(new Paragraph("\n"));

                Font infoFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
                Font infoFont2 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
                Paragraph clientInfo = new Paragraph();

                clientInfo.add(new Phrase("Agence : ", infoFont2));
                clientInfo.add(new Phrase(this.dailyBankState.getAgenceActuelle().nomAg+"\n", infoFont));

                clientInfo.add(new Phrase("Adresse agence : ", infoFont2));
                clientInfo.add(new Phrase(this.dailyBankState.getAgenceActuelle().adressePostaleAg+"\n", infoFont));

                clientInfo.add(new Phrase("Client : ",infoFont2));
                clientInfo.add(new Phrase(nom+" "+prenom+" - "+cpt.idNumCli+"\n",infoFont));

                clientInfo.add(new Phrase("Numréro de compte : ", infoFont2));
                clientInfo.add(new Phrase(cpt.idNumCompte+"", infoFont));
                document.add(clientInfo);


                Paragraph oper = new Paragraph("Opérations", infoFont);
                oper.setAlignment(Element.ALIGN_CENTER);
                document.add(oper);

                document.add(new Paragraph("\n"));


                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.setWidths(new int[]{2, 3, 2, 2});


                Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

                BaseColor headerColor = new BaseColor(245, 245, 220);

                PdfPCell headerCell;

                headerCell = new PdfPCell(new Phrase("Date", headerFont));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setBackgroundColor(headerColor);
                headerCell.setBorderWidth(0.5f);
                table.addCell(headerCell);

                headerCell = new PdfPCell(new Phrase("Type", headerFont));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setBackgroundColor(headerColor);
                headerCell.setBorderWidth(0.5f);
                table.addCell(headerCell);

                headerCell = new PdfPCell(new Phrase("Crédit (€)", headerFont));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setBackgroundColor(headerColor);
                headerCell.setBorderWidth(0.5f);
                table.addCell(headerCell);

                headerCell = new PdfPCell(new Phrase("Débit (€)", headerFont));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setBackgroundColor(headerColor);
                headerCell.setBorderWidth(0.5f);
                table.addCell(headerCell);

                // Add rows to the table
                for (int i = 0; i < operations.length; i++) {
                    for (String field : operations[i]) {
                        PdfPCell cell = new PdfPCell(new Phrase(field, new Font(Font.FontFamily.HELVETICA, 10)));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorderWidth(0.5f);
                        if (i % 2 == 0) {
                            cell.setBackgroundColor(BaseColor.WHITE);
                        } else {
                            cell.setBackgroundColor(BaseColor.WHITE);
                        }
                        table.addCell(cell);
                    }
                }

                // Add the table to the document
                document.add(table);

                // Add a space
                document.add(new Paragraph("\n"));

                // Add the summary section
                PdfPTable summaryTable = new PdfPTable(2);
                summaryTable.setWidthPercentage(50);
                summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

                PdfPCell summaryCell;

                summaryCell = new PdfPCell(new Phrase("Solde final", headerFont));
                summaryCell.setBorderWidth(0.5f);
                summaryCell.setBackgroundColor(headerColor);
                summaryTable.addCell(summaryCell);

                summaryCell = new PdfPCell(new Phrase(solde+"", headerFont));
                summaryCell.setBorderWidth(0.5f);
                summaryCell.setBackgroundColor(headerColor);
                summaryTable.addCell(summaryCell);

                document.add(summaryTable);

                document.close();

                System.out.println("PDF created successfully.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Operation> operationsDunCompte(int numCmpt) {
        ArrayList<Operation> op = new ArrayList<>();

        try {
            Access_BD_Operation ao = new Access_BD_Operation();
            op = ao.getOperations(numCmpt);
        } catch (DatabaseConnexionException e) {
            ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, e);
            ed.doExceptionDialog();
        } catch (ApplicationException ae) {
            ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, ae);
            ed.doExceptionDialog();
        }
        return op;
    }

    public boolean cloturerCompte(CompteCourant cc) {
        if (cc != null) {
            try {
                Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
                acc.clotureCompteCourant(cc);
                return true;
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                this.cmStage.close();
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
            }
        }
        return false;
    }

    public void supprimerCompte(CompteCourant cc) {
        if (cc != null) {
            try {
                Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
                acc.supprimerCompteCourant(cc.idNumCompte);
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                this.cmStage.close();
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
            }
        }
    }

    public CompteCourant creerNouveauCompte() {
        CompteCourant compte;
        CompteEditorPane cep = new CompteEditorPane(this.cmStage, this.dailyBankState);
        compte = cep.doCompteEditorDialog(this.clientDesComptes, null, EditionMode.CREATION);
        if (compte != null) {
            try {
                Access_BD_CompteCourant acCC = new Access_BD_CompteCourant();
                acCC.insertCompte(compte);
                acCC.updateCompteCourant(compte);
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                this.cmStage.close();
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
            }
        }
        return compte;
    }

    public ArrayList<CompteCourant> getComptesDunClient() {
        ArrayList<CompteCourant> listeCpt = new ArrayList<>();

        try {
            Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
            listeCpt = acc.getCompteCourants(this.clientDesComptes.idNumCli);
        } catch (DatabaseConnexionException e) {
            ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, e);
            ed.doExceptionDialog();
            this.cmStage.close();
            listeCpt = new ArrayList<>();
        } catch (ApplicationException ae) {
            ExceptionDialog ed = new ExceptionDialog(this.cmStage, this.dailyBankState, ae);
            ed.doExceptionDialog();
            listeCpt = new ArrayList<>();
        }
        return listeCpt;
    }
}
