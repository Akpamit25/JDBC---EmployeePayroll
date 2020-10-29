package databaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.capgemini.EmployeePayroll.JDBC.*;

public class EmployeePayrollDBService {
	
	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payrollservice";
		String userName = "root";
		String password = "training_capg";
		Connection connection;
		System.out.println("connecting to database: " + jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("connection successful !!!! " + connection);
		return connection;
	}

	public List<EmployeePayrollData> readData() {
		String sql="SELECT * FROM employeepayroll; ";
		List<EmployeePayrollData> employeePayrollList=new ArrayList<>();
		try (Connection connection=this.getConnection();){
			Statement statement=connection.createStatement();
			ResultSet result=statement.executeQuery(sql);
			while(result.next()) {
				int id =result.getInt("id");
				String name=result.getString("employeename");
				double Salary=result.getDouble("salary");
				LocalDate startDate=result.getDate("startdate").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id,name,Salary,startDate));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}
}