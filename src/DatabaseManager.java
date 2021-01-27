import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class DatabaseManager {
	private static DatabaseManager instance;

	public static DatabaseManager getInstance() {
		if (instance != null) {
			return instance;
		}
		instance = new DatabaseManager();
		return instance;
	}

	public Connection getDatabaseConnection(final int port, final String database) throws SQLException {
		System.out.println(String.format("[mysql]: Connecting to MySQL:%s - %s", port, database));
		return DriverManager.getConnection(
				String.format("jdbc:mysql://localhost:%s/%s?serverTimezone=UTC&useSSL=false", port, database), "root",
				"bichl601");
	}

	public void releaseConnection(final Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	public void insertIntoDatabase(final Connection connection, LocalDate date, double closeValue, String aktie)
			throws SQLException {
		String createTable = "CREATE TABLE IF NOT EXISTS " + aktie
				+ " ( Datum varchar(50), close varchar(10), PRIMARY KEY(Datum));";
		try (PreparedStatement statement = connection.prepareStatement(createTable)) {
			statement.execute(createTable);
			statement.executeUpdate();
		}
		final String insertInto = "insert ignore into " + aktie + " (Datum, close) values (?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(insertInto)) {
			statement.setString(1, date.toString());
			statement.setDouble(2, closeValue);
			statement.executeUpdate();
		}
	}

	public static void insertAVG(final Connection connection, String aktie, double avg, LocalDate date)
			throws SQLException {
		String createTable = "CREATE TABLE IF NOT EXISTS " + aktie + "AVG (AVG varchar(50), Datum varchar(50))";
		try (PreparedStatement statement = connection.prepareStatement(createTable)) {
			statement.execute(createTable);
			statement.executeUpdate();
		}
		final String insertInto = "replace into " + aktie + "AVG (AVG,Datum) values (?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(insertInto)) {
			statement.setDouble(1, avg);
			statement.setString(2, date.toString());
			statement.executeUpdate();
		}
	}
	//Bessere 200er Schritt "berechnung" mit einem Select befehl
	public static void selectAvg(String aktie, final Connection connection, ArrayList<Double> closeValue,
			ArrayList<LocalDate> date) {
		for (int i = 0; i < date.size(); i++) {
			String select = "with temp as (select close from " + aktie + " where datum <= '" + date.get(i)
					+ "' order by datum desc limit 200) select avg(close) from temp";
			try {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(select);

				rs.next();

				closeValue.add(rs.getDouble(1));

			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static void selectALL(String aktie, final Connection connection, ArrayList<Double> closeValue,
			ArrayList<LocalDate> date) {
		String selectFrom = "SELECT * FROM " + aktie + " ORDER BY DATUM ASC";

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(selectFrom);

			while (rs.next()) {
				date.add(LocalDate.parse(rs.getString("Datum")));
				closeValue.add(rs.getDouble("close"));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

}