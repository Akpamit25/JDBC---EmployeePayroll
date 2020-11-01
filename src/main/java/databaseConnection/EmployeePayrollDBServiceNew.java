package databaseConnection;

import java.sql.*;
import java.time.LocalDate;
import com.capgemini.EmployeePayroll.JDBC.*;
import exception.PayrollSystemException;

public class EmployeePayrollDBServiceNew {
	private static EmployeePayrollDBServiceNew employeePayrollDBServiceNew;
	private EmployeePayrollDBServiceNew() {
	}

	public static EmployeePayrollDBServiceNew getInstance() {
		if (employeePayrollDBServiceNew == null) {
			employeePayrollDBServiceNew = new EmployeePayrollDBServiceNew();
		}
		return employeePayrollDBServiceNew;
	}
	
	public EmployeePayrollData addEmployeeToPayroll(String name, double salary, LocalDate start, char gender)
			throws PayrollSystemException {
		int employeeId = -1;
		Connection connection = null;
		EmployeePayrollData employeePayrollData = null;
		try {
			connection = EmployeePayrollDBService.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format("INSERT INTO employeepayroll (employeename,gender,basic_pay,startdate)" + "VALUES('%s','%s','%s','%s')", name,
										gender, salary, Date.valueOf(start));
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet result = statement.getGeneratedKeys();
				if (result.next())
					employeeId = result.getInt(1);
			}
			if (rowAffected == 0)
				throw new PayrollSystemException("insert into employee table unsuccessful !!!",
													PayrollSystemException.ExceptionType.INSERT_EXCEPTION);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new PayrollSystemException("insert into employee table unsuccessful !!!",
													PayrollSystemException.ExceptionType.INSERT_EXCEPTION);
		} catch (PayrollSystemException e) {
			System.out.println(e);
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try (Statement statement = connection.createStatement()) {
			double deductions = salary * 0.2;
			double taxable_pay = salary - deductions;
			double tax = taxable_pay * 0.1;
			double net_pay = salary - tax;
			String sql = String.format("INSERT INTO payroll_details (employee_id,basic_pay,deductions,taxable_pay,tax,net_pay)" + ""
							+ "VALUES('%s','%s','%s','%s','%s','%s')",
					employeeId, salary, deductions, taxable_pay, tax, net_pay);
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 0)
				throw new PayrollSystemException("insert into payroll table unsuccessful !!!",
															PayrollSystemException.ExceptionType.INSERT_EXCEPTION);
		} catch (PayrollSystemException e1) {
			System.out.println(e1);
			try {
				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new PayrollSystemException("insert into payroll table unsuccessful !!!",
														PayrollSystemException.ExceptionType.INSERT_EXCEPTION);
		}
		try (Statement statement = connection.createStatement()) {
			int dept_id = 105;
			String dept_name = "Finance";
			String sql = String.format("INSERT INTO department (department_id,department_name) VALUES('%s','%s')", dept_id,
					dept_name);
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 0)
				throw new PayrollSystemException("insert into department table unsuccessful !!!",
															PayrollSystemException.ExceptionType.INSERT_EXCEPTION);
		} catch (PayrollSystemException e1) {
			System.out.println(e1);
			try {
				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new PayrollSystemException("insert into department table unsuccessful !!!",
														PayrollSystemException.ExceptionType.INSERT_EXCEPTION);
		}
		try (Statement statement = connection.createStatement()) {
			int dept_id1 = 105;
			String sql1 = String.format("INSERT INTO employee_department (employee_id,department_id) VALUES('%s','%s')", employeeId, dept_id1);
			statement.executeUpdate(sql1);
			int dept_id = 101;
			String sql = String.format("INSERT INTO employee_department (employee_id,department_id) VALUES('%s','%s')", employeeId, dept_id);
			int rowAffected1 = statement.executeUpdate(sql);
			if (rowAffected1 == 1) {
				employeePayrollData = new EmployeePayrollData(employeeId, name, salary, start);
			}
			if (rowAffected1 == 0)
				throw new PayrollSystemException("insert into employee_department table unsuccessful !!!",
															PayrollSystemException.ExceptionType.INSERT_EXCEPTION);
		} catch (PayrollSystemException e1) {
			System.out.println(e1);
			try {
				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new PayrollSystemException("insert into employee_department table unsuccessful !!!",
																PayrollSystemException.ExceptionType.INSERT_EXCEPTION);
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return employeePayrollData;
	}//
}