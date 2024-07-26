package model.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.data.Employe;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;
import oracle.net.aso.l;
import oracle.net.aso.q;

/**
 * Classe d'accès aux Employe en BD Oracle.
 */
public class Access_BD_Employe {

    public Access_BD_Employe() {
    }

    /**
     * Recherche d'un employé par son login / mot de passe.
     *
     * @param login    login de l'employé recherché
     * @param password mot de passe donné
     * @return un Employe ou null si non trouvé
     * @throws RowNotFoundOrTooManyRowsException La requête renvoie plus de 1 ligne
     * @throws DataAccessException               Erreur d'accès aux données (requête
     *                                           mal formée ou autre)
     * @throws DatabaseConnexionException        Erreur de connexion
     *
     * @author SHARIFI Daner
     */
    public Employe getEmploye(String login, String password)
            throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {

        Employe employeTrouve;

        try {
            Connection con = LogToDatabase.getConnexion();
            String query = "SELECT * FROM Employe WHERE" + " login = ?" + " AND motPasse = ?";

            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, login);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            System.err.println(query);

            if (rs.next()) {
                int idEmployeTrouve = rs.getInt("idEmploye");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String droitsAccess = rs.getString("droitsAccess");
                String loginTROUVE = rs.getString("login");
                String motPasseTROUVE = rs.getString("motPasse");
                int idAgEmploye = rs.getInt("idAg");

                employeTrouve = new Employe(idEmployeTrouve, nom, prenom, droitsAccess, loginTROUVE, motPasseTROUVE,
                        idAgEmploye);
            } else {
                rs.close();
                pst.close();
                // Non trouvé
                return null;
            }

            if (rs.next()) {
                // Trouvé plus de 1 ... bizarre ...
                rs.close();
                pst.close();
                throw new RowNotFoundOrTooManyRowsException(Table.Employe, Order.SELECT,
                        "Recherche anormale (en trouve au moins 2)", null, 2);
            }
            rs.close();
            pst.close();
            return employeTrouve;
        } catch (SQLException e) {
            throw new DataAccessException(Table.Employe, Order.SELECT, "Erreur accès", e);
        }
    }

    /**
     * Recherche d'un employé par son login.
     *
     * @param login login de l'employé recherché
     * @return un Employe ou null si non trouvé
     * @throws RowNotFoundOrTooManyRowsException La requête renvoie plus de 1 ligne
     * @throws DataAccessException               Erreur d'accès aux données (requête
     *                                           mal formée ou autre)
     * @throws DatabaseConnexionException        Erreur de connexion
     *
     * @author SHARIFI Daner
     */
    public Employe findEmployeByLogin(String login)
            throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
        Employe employeTrouve;

        try {
            Connection con = LogToDatabase.getConnexion();
            String query = "SELECT idEmploye, nom, prenom, droitsAccess, login, idAg FROM Employe WHERE" + " login = ?";

            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, login);

            ResultSet rs = pst.executeQuery();

            System.err.println(query);

            if (rs.next()) {
                int idEmployeTrouve = rs.getInt("idEmploye");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String droitsAccess = rs.getString("droitsAccess");
                String loginTROUVE = rs.getString("login");
                int idAgEmploye = rs.getInt("idAg");

                employeTrouve = new Employe(idEmployeTrouve, nom, prenom, droitsAccess, loginTROUVE, "caché",
                        idAgEmploye);
            } else {
                rs.close();
                pst.close();
                // Non trouvé
                return null;
            }

            if (rs.next()) {
                // Trouvé plus de 1 ... bizarre ...
                rs.close();
                pst.close();
                throw new RowNotFoundOrTooManyRowsException(Table.Employe, Order.SELECT,
                        "Recherche anormale (en trouve au moins 2)", null, 2);
            }
            rs.close();
            pst.close();
            return employeTrouve;
        } catch (SQLException e) {
            throw new DataAccessException(Table.Employe, Order.SELECT, "Erreur accès", e);
        }
    }

    /**
     * Recherche d'un employé par son login.
     *
     * @param id id de l'employé recherché
     * @return un Employe ou null si non trouvé
     * @throws RowNotFoundOrTooManyRowsException La requête renvoie plus de 1 ligne
     * @throws DataAccessException               Erreur d'accès aux données (requête
     *                                           mal formée ou autre)
     * @throws DatabaseConnexionException        Erreur de connexion
     *
     * @author SHARIFI Daner
     */
    public Employe findEmployeByID(String id)
            throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
        Employe employeTrouve;

        try {
            Connection con = LogToDatabase.getConnexion();
            String query = "SELECT idEmploye, nom, prenom, droitsAccess, login, idAg FROM Employe WHERE" + " idEmploye = ?";

            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, id);

            ResultSet rs = pst.executeQuery();

            System.err.println(query);

            if (rs.next()) {
                int idEmployeTrouve = rs.getInt("idEmploye");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String droitsAccess = rs.getString("droitsAccess");
                String loginTROUVE = rs.getString("login");
                int idAgEmploye = rs.getInt("idAg");

                employeTrouve = new Employe(idEmployeTrouve, nom, prenom, droitsAccess, loginTROUVE, "caché",
                        idAgEmploye);
            } else {
                rs.close();
                pst.close();
                // Non trouvé
                return null;
            }

            if (rs.next()) {
                // Trouvé plus de 1 ... bizarre ...
                rs.close();
                pst.close();
                throw new RowNotFoundOrTooManyRowsException(Table.Employe, Order.SELECT,
                        "Recherche anormale (en trouve au moins 2)", null, 2);
            }
            rs.close();
            pst.close();
            return employeTrouve;
        } catch (SQLException e) {
            throw new DataAccessException(Table.Employe, Order.SELECT, "Erreur accès", e);
        }
    }

    /* Recup la liste de tous les employés
     * @author ARGUELLES Alexian
     */
    public ArrayList<Employe> getsEmployes(int idAg, int idEmploye, String debutNom, String debutPrenom)
            throws DataAccessException, DatabaseConnexionException {
        ArrayList<Employe> alResult = new ArrayList<>();

        try {
            Connection con = LogToDatabase.getConnexion();

            PreparedStatement pst;

            String query;
            query = "SELECT * FROM Employe WHERE idAg = ?";
            if (idEmploye != -1) {
                query += " AND idEmploye = ?";
                query += "ORDER BY nom";
                pst = con.prepareStatement(query);
                pst.setInt(1, idAg);
                pst.setInt(2, idEmploye);
            } else if (!debutNom.equals("")) {
                debutNom = debutNom.toUpperCase() + "%";
                debutPrenom = debutPrenom.toUpperCase() + "%";
                query += " AND UPPER(nom) LIKE ?" + "AND UPPER(prenom) LIKE ?";
                query += " ORDER BY nom";
                pst = con.prepareStatement(query);
                pst.setInt(1, idAg);
                pst.setString(2, debutNom);
                pst.setString(3, debutPrenom);
            } else {
                query += "ORDER BY nom";
                pst = con.prepareStatement(query);
                pst.setInt(1, idAg);
            }

            System.err.println(query + " nom : " + debutNom + " prenom : " + debutPrenom + "#");

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int ridEmploye = rs.getInt("idEmploye");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String droitsAccess = rs.getString("droitsAccess");
                String login = rs.getString("login");
                String motPasse = rs.getString("motPasse");
                int ridAg = rs.getInt("idAg");
                alResult.add(
                        new Employe(ridEmploye, nom, prenom, droitsAccess, login, motPasse, ridAg)
                );
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            throw new DataAccessException(Table.Employe, Order.SELECT, "Erreur accès", e);
        }

        return alResult;
    }

    /**
     * Insertion d'un employé dans la base de données.
     *
     * @param employe Employe à insérer
     * @throws RowNotFoundOrTooManyRowsException La requête renvoie plus de 1 ligne
     * @throws DataAccessException               Erreur d'accès aux données (requête
     *                                           mal formée ou autre)
     * @throws DatabaseConnexionException        Erreur de connexion
     *
     * @author SHARIFI Daner
     */
    public void insertEmploye(Employe employe)
            throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
        try {

            Connection con = LogToDatabase.getConnexion();

            String query = "INSERT INTO EMPLOYE VALUES (seq_id_employe.NEXTVAL, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, employe.nom);
            pst.setString(2, employe.prenom);
            pst.setString(3, employe.droitsAccess);
            pst.setString(4, employe.login);
            pst.setString(5, employe.motPasse);
            pst.setInt(6, employe.idAg);

            System.err.println(query);

            int result = pst.executeUpdate();
            pst.close();

            if (result != 1) {
                con.rollback();
                throw new RowNotFoundOrTooManyRowsException(Table.Employe, Order.INSERT,
                        "Insert anormal (insert de moins ou plus d'une ligne)", null, result);
            }

            query = "SELECT seq_id_employe.CURRVAL from DUAL";

            System.err.println(query);
            PreparedStatement pst2 = con.prepareStatement(query);

            ResultSet rs = pst2.executeQuery();
            rs.next();
            int numEmpBase = rs.getInt(1);

            con.commit();
            rs.close();
            pst2.close();

            employe.idEmploye = numEmpBase;
        } catch (SQLException e) {
            throw new DataAccessException(Table.Employe, Order.INSERT, "Erreur accès", e);
        }
    }

    public void supprimerEmploye(int idEmploye)
        throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
            try {
                Connection con = LogToDatabase.getConnexion();

                String query = "DELETE FROM Employe WHERE idEmploye = ?";
                PreparedStatement pst = con.prepareStatement(query);

                pst.setInt(1, idEmploye);

                System.err.println(query);
                int result = pst.executeUpdate();
                pst.close();

                if (result != 1) {
                    con.rollback();
                    throw new RowNotFoundOrTooManyRowsException(Table.Employe, Order.DELETE,
                            "Problème sur le delete, la ligne n'a pas était trouvé", null, result);
                }

                con.commit();
                pst.close();

            } catch (SQLException e) {
                throw new DataAccessException(Table.Employe, Order.DELETE, "Erreur accès", e);
            }
    }

    public void modifierEmploye(Employe em) 
        throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
            try {
                Connection con = LogToDatabase.getConnexion();
                String query = "UPDATE Employe SET nom = ?, prenom = ?, droitsAccess = ?, login = ?, motPasse = ?, idAg = ? WHERE idEmploye = ?";
                PreparedStatement pst = con.prepareStatement(query);


                pst.setString(1, em.nom);
                pst.setString(2, em.prenom);
                pst.setString(3, em.droitsAccess);
                pst.setString(4, em.login);
                pst.setString(5, em.motPasse);
                pst.setInt(6, em.idAg);
                pst.setInt(7, em.idEmploye);

                System.err.println(query);

                int result = pst.executeUpdate();
                pst.close();

                if (result != 1) {
                    con.rollback();
                    throw new RowNotFoundOrTooManyRowsException(Table.Employe, Order.UPDATE,
                            "Problème sur le update de la table Employe", null, result);
                }

                con.commit();
                pst.close();

            } catch (SQLException e) {
                throw new DataAccessException(Table.Employe, Order.UPDATE, "Erreur accès", e);
            }
    }
}

