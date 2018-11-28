import java.io.*;
import java.util.*;

/* DOCUMENT THIS CLASS */
public class ICarnegieInfoApplication {

	private static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	private static PrintWriter stdOut = new PrintWriter(System.out, true);
	private static PrintWriter stdErr = new PrintWriter(System.err, true);

	/* DOCUMENT THIS PUBLIC METHOD */
	public static void main(String[] args) throws IOException {

		ICarnegieInfo companyInfo = ICarnegieInfo.getInstance();

		int choice = getChoice();

		while (choice != 0) {

			if (choice == 1) {
				stdOut.println(companyInfo.getName());
			} else if (choice == 2) {
				stdOut.println(companyInfo.getAddress());
			} else if (choice == 3) {
				stdOut.println(companyInfo.getTelephone());
			} else if (choice == 4) {
				stdOut.println(companyInfo.getEmail());
			} else if (choice == 5) {
				stdOut.println(companyInfo.getUrl());
			}

			choice = getChoice();
		}
	}

	private static int getChoice() throws IOException {

		/* PLACE YOUR CODE HERE */
		do {
			int input;
			try {
				stdErr.println();
				stdErr.print("[0]  Quit\n" + "[1]  Display name\n" + "[2]  Display address\n" + "[3]  Display Telephone\n"
						+ "[4]  Display email\n" + "[5]  Display URL\n" + "choice>");
				stdErr.flush();
				input = Integer.parseInt(stdIn.readLine());
				if (0 <= input && 5 >= input) {
					return input;
				} else {
					stdErr.println("Invalid choice:  " + input);
				}
			} catch (NumberFormatException nfe) {
				stdErr.println(nfe);
			}
		} while (true);

		//return 0; /* CHANGE THIS STATEMENT AS NEEDED */
	}
}