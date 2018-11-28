package file;

import java.io.*;
import java.util.*;

/**
 * Tests the class <code>EmployeeFileIO</code>
 *
 * @author iCarnegie
 * @version 1.0.0
 * @see EmployeeFileIO
 * @see Employee
 */
public class  TestEmployeeFileIO  {

	/* Standard output stream */
	private static PrintWriter  stdOut = new  PrintWriter(System.out, true);

	/* Standard error stream */
	private static PrintWriter  stdErr = new  PrintWriter(System.err, true);

	/* Variables that contains the test objects */
	private Employee  first, second, third, fourth;
	private ArrayList<Employee> empty, employees;
	private static String fileNameEmpty = "empty.txt";
	private static String fileNameEmployees = "employees.txt";

	/**
	 * Tests methods of class {@link EmployeeFileIO}
	 *
	 * @param args  not used.
	 * @throws IOException  if an I/O error occurs.
	 */
	public static void main (String args[]) throws IOException {

		stdOut.println("");
		stdOut.println("Testing class EmployeeFileIO...");

		TestEmployeeFileIO tester = new TestEmployeeFileIO();

		if (tester.testRead() & tester.testWrite()) {
			stdOut.println("All tests passed");
		}
		stdOut.println("");
	}

	/**
	 * Displays a message in the standard error stream if the value specified
	 * by parameter <code>condition<code> is <code>false</code>.
	 *
	 * @param message  the error message.
	 * @param condition  the test condition.
	 * @return the value of <code>condition</code>
	 */
	public static boolean assertTrue(String message, boolean condition) {

		if (!condition) {
			stdErr.print("** Test failure ");
			stdErr.println(message);

			return false;
		} else {

			return true;
		}

	}

	/**
	 * Displays a message in the standard error stream.
	 *
	 * @param message  the error message.
	 * @return <code>false</code>;
	 */
	public static boolean fail(String message) {

		stdErr.print("** Test failure ");
		stdErr.println(message);

		return false;
	}

	/* Assign the initial value to the test variables */
	private void setUp() throws IOException {

		first = new Employee(100, "Employee One", 1000.0);
		second = new Employee(300, "Employee Two", 3000.0);
		third = new Employee(400, "Employee Three", 4000.0);
		fourth = new Employee(200, "Employee Four", 2000.0);

		empty = new ArrayList<Employee>();
		employees = new ArrayList<Employee>();

		employees.add(first);
		employees.add(second);
		employees.add(third);
		employees.add(fourth);
	}

	/**
	 * Tests the method <code>read</code>.
	 *
	 * @return <code>true</code> if all test passed; otherwise returns
	 *         <code>false</code>.
	 * @throws IOException  if an I/O error occurs.
	 */
	public boolean testRead() throws IOException {

		setUp();

		boolean test = true;

		try {
			// Testing an empty file
			ArrayList<Employee> resultEmpty =
				EmployeeFileIO.read(fileNameEmpty);

			test = test && assertTrue(
				"1, testing method read with an empty file",
				resultEmpty instanceof ArrayList);

			test = test && assertTrue(
				"2, testing method read with an empty file",
				resultEmpty.size() == 0);

			// Testing a not empty file
			ArrayList<Employee> result = EmployeeFileIO.read(fileNameEmployees);

			test = test && assertTrue("3, testing method read",
			                          result instanceof ArrayList);
			test = test && assertTrue("4, testing method read",
			                          result.size() == 4);
			test = test && assertTrue("5, testing method read",
			                          TestHelper.equals(employees, result));
		} catch (Exception e) {
			test = test && fail("6, testing method read: " + e.getMessage());
		}

		return test;
	}

	/**
	 * Tests the method <code>write</code>.
	 *
	 * @return <code>true</code> if all test passed; otherwise returns
	 *         <code>false</code>.
	 * @throws IOException  if an I/O error occurs.
	 */
	public boolean testWrite() throws IOException  {

		setUp();

		boolean test = true;

		try {
			String fileNameOne = "testWriteOne.txt";
			String fileNameTwo = "testWriteTwo.txt";

			(new File(fileNameOne)).delete();
			(new File(fileNameTwo)).delete();


			// Testing an empty employees
			EmployeeFileIO.write(fileNameOne, empty);

			test = test && assertTrue("1, testing method write",
					TestHelper.equals(fileNameEmpty, fileNameOne));

			// Testing a not empty file
			EmployeeFileIO.write(fileNameTwo, employees);

			test = test && assertTrue("2, testing method write",
					TestHelper.equals(fileNameEmployees, fileNameTwo));
		} catch (Exception e) {
			test = test && fail("3, testing method write: " + e.getMessage());
		}

		return test;
	}
}