package model.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.data.Prelevement;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

/**
 * Classe d'accès aux Prelevement en BD Oracle.
 * Fournit des méthodes pour interagir avec les prélèvements automatiques dans la base de données.
 *
 * @author SHARIFI Daner
 */
public class Access_BD_Prelevement {

	/**
	 * Constructeur par défaut.
	 */
	public Access_BD_Prelevement() {
	}

	/**
	 * Récupère la liste des prélèvements automatiques pour un compte donné.
	 * @author ARGUELLES Alexian
	 */
	public ArrayList<Prelevement> getPrelevementsByDate(int date)
			throws DataAccessException, DatabaseConnexionException {

		ArrayList<Prelevement> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM PrelevementAutomatique where dateRecurrente = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, date);
			System.err.println(query);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				double debitPrelev = rs.getInt("montant");
				int datePrelev = rs.getInt("dateRecurrente");
				String beneficiaire = rs.getString("beneficiaire");
				int idPrelev = rs.getInt("idPrelev");
				int idNumCompte = rs.getInt("idNumCompte");

				alResult.add(new Prelevement(idPrelev, debitPrelev, idNumCompte, datePrelev, beneficiaire));
			}

			rs.close();
			pst.close();
		
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.SELECT, "Erreur accès", e);
		}

		return alResult;
	}


	/**
	 * Récupère la liste des prélèvements automatiques pour un compte donné.
	 *
	 * @param idNumCompte L'identifiant du compte.
	 * @return Une liste de prélèvements automatiques.
	 * @throws DataAccessException Si une erreur d'accès aux données se produit.
	 * @throws DatabaseConnexionException Si une erreur de connexion à la base de données se produit.
	 *
	 * @author SHARIFI Daner
	 */
	public ArrayList<Prelevement> getPrelevements(int idNumCompte)
			throws DataAccessException, DatabaseConnexionException {

		ArrayList<Prelevement> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM PrelevementAutomatique where idNumCompte = ?";
			query += " ORDER BY idNumCompte";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCompte);
			System.err.println(query);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				double debitPrelev = rs.getInt("montant");
				int datePrelev = rs.getInt("dateRecurrente");
				String beneficiaire = rs.getString("beneficiaire");
				int idPrelev = rs.getInt("idPrelev");

				alResult.add(new Prelevement(idPrelev, debitPrelev, idNumCompte, datePrelev, beneficiaire));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.SELECT, "Erreur accès", e);
		}

		return alResult;
	}

	/**
	 * Récupère un prélèvement automatique par son identifiant.
	 *
	 * @param idNumPrelev L'identifiant du prélèvement.
	 * @return Le prélèvement automatique correspondant.
	 * @throws RowNotFoundOrTooManyRowsException Si aucun ou plusieurs prélèvements sont trouvés.
	 * @throws DataAccessException Si une erreur d'accès aux données se produit.
	 * @throws DatabaseConnexionException Si une erreur de connexion à la base de données se produit.
	 *
	 * @author SHARIFI Daner
	 */
	public Prelevement getPrelevement(int idNumPrelev)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			Prelevement pr;

			Connection con = LogToDatabase.getConnexion();

			String query = "SELECT * FROM PRELEVEMENTAUTOMATIQUE where idPrelev = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumPrelev);

			System.err.println(query);

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				int idNumPrelevTROUVE = rs.getInt("idPrelev");
				double debitPrelev = rs.getInt("montant");
				int datePrelev = rs.getInt("dateRecurrente");
				String beneficiaire = rs.getString("beneficiaire");
				int idNumCompte = rs.getInt("idNumCompte");

				pr = new Prelevement(idNumPrelevTROUVE, debitPrelev, idNumCompte, datePrelev, beneficiaire);
			} else {
				rs.close();
				pst.close();
				return null;
			}

			if (rs.next()) {
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.SELECT,
						"Recherche anormale (en trouve au moins 2)", null, 2);
			}
			rs.close();
			pst.close();
			return pr;
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.SELECT, "Erreur accès", e);
		}
	}

	/**
	 * Insère un nouveau prélèvement automatique dans la base de données.
	 *
	 * @param prelev Le prélèvement automatique à insérer.
	 * @throws RowNotFoundOrTooManyRowsException Si l'insertion est anormale (moins ou plus d'une ligne).
	 * @throws DataAccessException Si une erreur d'accès aux données se produit.
	 * @throws DatabaseConnexionException Si une erreur de connexion à la base de données se produit.
	 *
	 * @author SHARIFI Daner
	 */
	public void insertPrelevement(Prelevement prelev)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "INSERT INTO PRELEVEMENTAUTOMATIQUE VALUES (seq_id_prelevAuto.NEXTVAL, ?, ?, ?, ?)";
			PreparedStatement pst = con.prepareStatement(query);
			pst.setDouble(1, prelev.debitPrelev);
			pst.setInt(2, prelev.datePrelev);
			pst.setString(3, prelev.beneficiaire);
			pst.setInt(4, prelev.idNumCompte);
			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.INSERT,
						"Insert anormal (insert de moins ou plus d'une ligne)", null, result);
			}

			query = "SELECT seq_id_prelevAuto.CURRVAL from DUAL";

			System.err.println(query);
			PreparedStatement pst4 = con.prepareStatement(query);

			ResultSet rs = pst4.executeQuery();
			rs.next();
			int numPrelevBase = rs.getInt(1);

			con.commit();
			rs.close();
			pst4.close();

			prelev.idNumPrelev = numPrelevBase;
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.INSERT, "Erreur accès", e);
		}
	}

	/**
	 * Supprime un prélèvement automatique de la base de données.
	 *
	 * @param prelev Le prélèvement automatique à supprimer.
	 * @throws RowNotFoundOrTooManyRowsException Si la suppression est anormale (moins ou plus d'une ligne).
	 * @throws DataAccessException Si une erreur d'accès aux données se produit.
	 * @throws DatabaseConnexionException Si une erreur de connexion à la base de données se produit.
	 *
	 * @author SHARIFI Daner
	 */
	public void deletePrelevement(Prelevement prelev) throws RowNotFoundOrTooManyRowsException, DataAccessException,
			DatabaseConnexionException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "DELETE FROM PrelevementAutomatique WHERE idPrelev = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, prelev.idNumPrelev);
			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.DELETE,
						"Delete anormal (delete de moins ou plus d'une ligne)", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.DELETE, "Erreur accès", e);
		}
	}

	/**
	 * Met à jour un prélèvement automatique dans la base de données.
	 *
	 * @param prelev Le prélèvement automatique à mettre à jour.
	 * @throws RowNotFoundOrTooManyRowsException Si la mise à jour est anormale (moins ou plus d'une ligne).
	 * @throws DataAccessException Si une erreur d'accès aux données se produit.
	 * @throws DatabaseConnexionException Si une erreur de connexion à la base de données se produit.
	 * @throws ManagementRuleViolation Si une règle de gestion est violée (par exemple, montant négatif).
	 *
	 * @author SHARIFI Daner
	 */
	public void updatePrelevement(Prelevement prelev) throws RowNotFoundOrTooManyRowsException, DataAccessException,
			DatabaseConnexionException, ManagementRuleViolation {
		try {

			if (prelev.debitPrelev <= 0) {
				throw new ManagementRuleViolation(Table.PrelevementAutomatique, Order.UPDATE,
						"Erreur de règle de gestion : impossible de faire un prélèvement négatif ou égal à 0", null);
			}
			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE PrelevementAutomatique SET montant = ?, dateRecurrente = ?, beneficiaire = ? WHERE idPrelev = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setDouble(1, prelev.debitPrelev);
			pst.setInt(2, prelev.datePrelev);
			pst.setString(3, prelev.beneficiaire);
			pst.setInt(4, prelev.idNumPrelev);
			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.UPDATE,
						"Update anormal (update de moins ou plus d'une ligne)", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.UPDATE, "Erreur accès", e);
		}
	}
}
