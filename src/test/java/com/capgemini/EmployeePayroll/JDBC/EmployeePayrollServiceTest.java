package com.capgemini.EmployeePayroll.JDBC;

import java.util.Arrays;
import exception.PayrollSystemException;
import java.util.List;
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
	List<EmployeePayrollData> employeePayrollData=employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
	Assert.assertEquals(3,employeePayrollData.size());
	}
	
	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() throws PayrollSystemException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary("Amit",20000.0);
		boolean result=employeePayrollService.checkEmployeePayrollInSyncWithDB("Amit");
		Assert.assertTrue(result);
	}
}