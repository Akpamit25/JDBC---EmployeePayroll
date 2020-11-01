package com.capgemini.EmployeePayroll.JDBC;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import exception.PayrollSystemException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import com.capgemini.EmployeePayroll.JDBC.EmployeePayrollService.IOService;

public class EmployeePayrollServiceTest {
	
	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEnteries() {
		EmployeePayrollData[] arrayOfEmps= {
				new EmployeePayrollData(1,"Amit Kumar", 530000.0),
				new EmployeePayrollData(2,"Ankit Pal", 320000.0),
				new EmployeePayrollData(3,"Abhijeet", 700000.0)
		};
		EmployeePayrollService employeePayrollService;
		employeePayrollService=new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		employeePayrollService.writeEmployeePayrollData(IOService.FILE_IO);
		long entries=employeePayrollService.countEntries(IOService.FILE_IO);
		Assert.assertEquals(3,entries);	
	}
	
	@Test
	public void givenFileOnReadingFileShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> entries = employeePayrollService.readPayrollData(IOService.FILE_IO);
	}
	
	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Assert.assertEquals(6, employeePayrollData.size());
	}
	
	@Test
	public void givenNewSalaryForEmployee_WhenUpdatedUsingPreparedStatement_ShouldSyncWithDB()
			throws PayrollSystemException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary("Amit", 300000.0);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Amit");
		Assert.assertTrue(result);
	}

	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate endDate = LocalDate.now();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService
				.readEmployeePayrollForDateRange(IOService.DB_IO, startDate, endDate);
		Assert.assertEquals(6, employeePayrollData.size());
	}

	@Test
	public void findSumAverageMinMaxCount_ofEmployees_ShouldMatchEmployeeCount() throws PayrollSystemException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> genderToAverageSalaryMap = employeePayrollService.getAvgSalary(IOService.DB_IO);
		Double avgSalaryMale = 113333.33333333333;
		Assert.assertEquals(avgSalaryMale, genderToAverageSalaryMap.get("M"));
		Double avgSalaryFemale = 20000.0;
		Assert.assertEquals(avgSalaryFemale, genderToAverageSalaryMap.get("F"));
	}

	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() throws PayrollSystemException, SQLException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeToPayroll("Mandy",50000.0,LocalDate.now(),'M');
		boolean result=employeePayrollService.checkEmployeePayrollInSyncWithDB("Mandy");
		Assert.assertTrue(result);		
	}
}