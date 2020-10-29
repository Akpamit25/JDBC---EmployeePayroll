package databaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.sql.Driver;

public class ConnectingToDB {
	public static void main(String[] args) {
		String jdbcURL = "jdbc:mysql://localhost:3306/payrollservice";
		String userName = "root";
		String password = "training_capg";
		Connection connection;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver Loaded");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cannot find driver in classpath !", e);
		}
		listDrivers();
		try{
			System.out.println("connecting to database: " + jdbcURL);
			connection = DriverManager.getConnection(jdbcURL, userName, password);
			System.out.println("connection successful !!!! " + connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void listDrivers() {
		Enumeration<Driver> driverList = DriverManager.getDrivers();
		while (driverList.hasMoreElements()) {
			Driver driverClass = (Driver) driverList.nextElement();
			System.out.println(" " + driverClass.getClass().getName());
		}
	}
}
