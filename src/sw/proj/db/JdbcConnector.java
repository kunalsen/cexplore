package sw.proj.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sw.proj.utility.PropertyManager;
/**
 * 
 * @author kunal
 *
 */
public class JdbcConnector {
	public static Connection getConnection() throws ClassNotFoundException,
			SQLException {
		System.out.println();
		Class.forName(PropertyManager.getPropertyValue("jdbc", "driver"));
		String url = PropertyManager.getPropertyValue("jdbc", "db_url");

		Connection conn = DriverManager.getConnection(url,
				PropertyManager.getPropertyValue("jdbc", "db_user"),
				PropertyManager.getPropertyValue("jdbc", "db_password"));
		return conn;

	}

	public static void closeConnection(Connection conn) throws SQLException {
		conn.close();

	}

	/**
	 * Test Method to be removed in production
	 * 
	 * @param args
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void main(String args[]) throws Exception {
		JdbcConnector connector = new JdbcConnector();
		Connection conn = connector.getConnection();
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("select count(*) from param");
		rs.next();

		System.out.println(rs.getInt(1));
		conn.close();
	}
}
