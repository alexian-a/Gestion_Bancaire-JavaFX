package model.data;

public class Emprunt {

    private double montant;
    private double tauxInteret;
    private int duree;
    private int idNumClient;

    public Emprunt(int duree, double montant, double tauxInteret, int idNumClient) {
        this.duree = duree;
        this.montant = montant;
        this.tauxInteret = tauxInteret;
        this.idNumClient = idNumClient;
    }

    public double getMontant() {
        return montant;
    }

    public double getTauxInteret() {
        return tauxInteret;
    }

    public int getDuree() {
        return duree;
    }

    public int getIdNumClient() {
        return idNumClient;
    }

}
