package com.capgemini.EmployeePayroll.JDBC;

import java.time.LocalDate;
import java.util.*;
import databaseConnection.*;
import exception.PayrollSystemException;


public class EmployeePayrollService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayrollData> employeePayrollList;
	private Map<String, Double> genderToAverageSalaryMap;
	private EmployeePayrollDBService employeePayrollDBService;
	private EmployeePayrollDBServiceNew employeePayrollDBServiceNew;

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}

	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
		employeePayrollDBServiceNew=EmployeePayrollDBServiceNew.getInstance();
	}

	public static void main(String[] args) {
		System.out.println("Welcome to Employee Payroll Service");
		ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		EmployeePayrollService employeePayroll = new EmployeePayrollService(employeePayrollList);
		Scanner consoleInputReader = new Scanner(System.in);
		employeePayroll.readEmployeePayrollData(consoleInputReader);
		employeePayroll.writeEmployeePayrollData(IOService.CONSOLE_IO);
	}

	private void readEmployeePayrollData(Scanner consoleInputReader) {
		System.out.println("Enter Employee ID: ");
		int id = consoleInputReader.nextInt();
		System.out.println("Enter Employee Name ");
		String name = consoleInputReader.next();
		System.out.println("Enter Employee Salary");
		Double salary = consoleInputReader.nextDouble();
		employeePayrollList.add(new EmployeePayrollData(id, name, salary));
	}

	public void writeEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO))
			System.out.println("Employee Payroll Data " + employeePayrollList);
		else if (ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().writeData(employeePayrollList);
		}
	}

	public void printData(IOService fileIo) {
		if (fileIo.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().printData();
		}
	}

	public long countEntries(IOService fileIo) {
		if (fileIo.equals(IOService.FILE_IO)) {
			return new EmployeePayrollFileIOService().countEntries();
		}
		return 0;
	}

	public List<EmployeePayrollData> readPayrollData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			this.employeePayrollList = new EmployeePayrollFileIOService().readData();
		return employeePayrollList;
	}

	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.DB_IO))
			this.employeePayrollList = employeePayrollDBService.readData();
		return this.employeePayrollList;
	}

	public void updateEmployeeSalary(String name, double salary) throws PayrollSystemException {
		try {
			int result = employeePayrollDBService.updateEmployeeData(name, salary);
			if (result == 0) {
				throw new PayrollSystemException("no rows updated",
						PayrollSystemException.ExceptionType.UPDATE_FILE_EXCEPTION);
			}
			EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
			if (employeePayrollData != null)
				employeePayrollData.salary = salary;
		} catch (PayrollSystemException e) {
			System.out.println(e);
		}
	}

	private EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream().filter(empPayrollDataItem -> empPayrollDataItem.name.equals(name))
				.findFirst().orElse(null);
	}

	public boolean checkEmployeePayrollInSyncWithDB(String name) {
		List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}

	public List<EmployeePayrollData> readEmployeePayrollForDateRange(IOService ioService, LocalDate startDate,
			LocalDate endDate) {
		if (ioService.equals(IOService.DB_IO))
			this.employeePayrollList = employeePayrollDBService.getEmployeeForDateRange(startDate, endDate);
		return employeePayrollList;
	}

	public Map<String, Double> getAvgSalary(IOService ioService) throws PayrollSystemException {
		try {
			if (ioService.equals(IOService.DB_IO))
				this.genderToAverageSalaryMap = employeePayrollDBService.getAverageSalaryByGender();
			if (genderToAverageSalaryMap.isEmpty()) {
				throw new PayrollSystemException("no data retrieved",
						PayrollSystemException.ExceptionType.RETRIEVE_EXCEPTION);
			}
		} catch (PayrollSystemException e) {
			System.out.println(e);
		}
		return genderToAverageSalaryMap;
	}

	public void addEmployeeToPayroll(String name, double salary, LocalDate joiningDate, char gender)
			throws PayrollSystemException {
		employeePayrollList.add(employeePayrollDBServiceNew.addEmployeeToPayroll(name, salary, joiningDate, gender));
	}
	public int removeEmployeeFromPayroll(String name, IOService ioService) {
		int employeeCount=0;
		if (ioService.equals(IOService.DB_IO))
			employeeCount=employeePayrollDBServiceNew.removeEmployee(name);
		return employeeCount;
	}

	public List<EmployeePayrollData> readActiveEmployeePayrollData(IOService ioService) {
		if (ioService.equals(IOService.DB_IO))
			this.employeePayrollList = employeePayrollDBService.readActiveEmployeeData();
		return this.employeePayrollList;
	}
}