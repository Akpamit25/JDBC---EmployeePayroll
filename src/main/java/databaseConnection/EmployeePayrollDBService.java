package databaseConnection;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import com.capgemini.EmployeePayroll.JDBC.*;

import exception.PayrollSystemException;

public class EmployeePayrollDBService {
	private PreparedStatement employeePayrollDataStatement;
	private static EmployeePayrollDBService employeePayrollDBService;

	private EmployeePayrollDBService() {
	}

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null) {
			employeePayrollDBService = new EmployeePayrollDBService();
		}
		return employeePayrollDBService;
	}

	public static Connection getConnection() throws SQLException {
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
		String sql = "SELECT e.id,e.employeename,e.startdate,e.gender,e.basic_pay, d.department_name from employeepayroll e inner join "
				+ "employee_department ed on  e.id= ed.employee_id inner join department d on ed.department_id=d.department_id; ";
		return this.getEmployeePayrollDataUsingQuery(sql);
	}

	private List<EmployeePayrollData> getEmployeePayrollDataUsingQuery(String sql) {
		List<EmployeePayrollData> employeePayrollList=null;
		try (Connection connection = EmployeePayrollDBService.getConnection();){
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet result=preparedStatement.executeQuery(sql);
			employeePayrollList=this.getEmployeePayrollData(result);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	public int updateEmployeeData(String name, double salary) {
		return this.updateEmployeeDataUsingPreparedStatement(name, salary);
	}

	public int updateEmployeeDataUsingPreparedStatement(String name, double salary) {
		try (Connection connection = EmployeePayrollDBService.getConnection();) {
			String sql = "update employeepayroll set basic_pay=? where employeename=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setDouble(1, salary);
			preparedStatement.setString(2, name);
			int status = preparedStatement.executeUpdate();
			return status;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<EmployeePayrollData> getEmployeePayrollData(String name) {
		List<EmployeePayrollData> employeeParollList = null;
		if (this.employeePayrollDataStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			employeeParollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeeParollList;
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet result) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		List<String> departmentName=new ArrayList<>();
		try {
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("employeename");
				double Salary = result.getDouble("basic_pay");
				LocalDate startDate = result.getDate("startdate").toLocalDate();
				char gender=result.getString("gender").charAt(0);
				String dept=result.getString("department_name");
				departmentName.add(dept);
				String[] deptArray=new String[departmentName.size()];
				employeePayrollList.add(new EmployeePayrollData(id, name, Salary, startDate,gender,departmentName.toArray(deptArray)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private void prepareStatementForEmployeeData() {
		try {
			Connection connection = EmployeePayrollDBService.getConnection();
			String sql = "SELECT e.id,e.employeename,e.startdate,e.gender,e.basic_pay, d.department_name from employeepayroll e inner join "
					+ "employee_department ed on e.id=ed.employee_id inner join department d on ed.department_id=d.department_id WHERE employeename=?";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<EmployeePayrollData> getEmployeeForDateRange(LocalDate startDateTime, LocalDate endDateTime) {
		String sql = String.format("SELECT e.id,e.employeename,e.startdate,e.gender,e.basic_pay, d.department_name from employeepayroll e inner join "
				+ "employee_department ed on e.id=ed.employee_id inner join department d on ed.department_id=d.department_id where startdate between '%s' AND '%s';",
				Date.valueOf(startDateTime), Date.valueOf(endDateTime));
		return this.getEmployeePayrollDataUsingQuery(sql);
	}

	public Map<String, Double> getAverageSalaryByGender() {
		String sql = "SELECT gender,AVG(basic_pay) as avg_salary FROM employeepayroll group by gender;";
		Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
		try (Connection connection = EmployeePayrollDBService.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				String gender = result.getString("gender");
				double salary = result.getDouble("avg_salary");
				genderToAverageSalaryMap.put(gender, salary);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return genderToAverageSalaryMap;
	}
	public List<EmployeePayrollData> readActiveEmployeeData() {
		String sql = "SELECT e.id,e.employeename,e.startdate,e.gender,e.basic_pay, d.department_name from employeepayroll e inner join "
				+ "employee_department ed on e.id=ed.employee_id inner join department d on ed.department_id=d.department_id where is_active=true; ";
		return this.getEmployeePayrollDataUsingQuery(sql);
	}
}