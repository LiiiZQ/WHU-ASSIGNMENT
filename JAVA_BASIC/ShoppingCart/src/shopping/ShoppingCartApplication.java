package shopping;

import java.io.*;
import java.util.*;

/* DOCUMENT THIS CLASS */
public class ShoppingCartApplication {

	private static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	private static PrintWriter stdOut = new PrintWriter(System.out, true);
	private static PrintWriter stdErr = new PrintWriter(System.err, true);

	private ShoppingCart cart;

	/* DOCUMENT THIS PUBLIC METHOD */
	public static void main(String[] args) throws IOException {
		/**
		 * 运行程序
		 */
		ShoppingCartApplication application = new ShoppingCartApplication();
		application.run();
	}

	private void run() throws IOException {
		cart = new ShoppingCart();
		int choice = getChoice();
		while (choice != 0) {
			if (choice == 1) {
				cart.addProduct(readProduct());
			} else if (choice == 2) {
				stdOut.println(cart.toString());
			} else if (choice == 3) {
				stdOut.println(cart.getTotalValue());
			}
			choice = getChoice();
		}
	}

	private int getChoice() throws IOException {
		do {
			int input;
			try {
				stdErr.println();
				stdErr.print("[0]  Quit\n" + "[1]  Add Product\n" + "[2]  Display Products\n" + "[3]  Display Total\n"
						+ "choice>");
				stdErr.flush();
				input = Integer.parseInt(stdIn.readLine());
				if (0 <= input && 3 >= input) {
					return input;
				} else {
					stdErr.println("Invalid choice:  " + input);
				}
			} catch (NumberFormatException nfe) {
				stdErr.println(nfe);
			}
		} while (true);
	}

	private Product readProduct() throws IOException {
		final String DELIM = "_";
		String name = "";
		int quantity = 0;
		double price = 0.0;

		/* PLACE YOUR CODE HERE */
		do {
			try {
				// 提示并接收用户输入
				stdErr.print("product [name_qty_price]> ");
				stdErr.flush();
				String inputInfo = stdIn.readLine();
				StringTokenizer allInfo = new StringTokenizer(inputInfo, DELIM, false);
				// 检查用户输入1.输入是否多于或少于三个值 2.输入参数(合法整数？合法双精度？正数？)
				if (allInfo.countTokens() != 3) {
					stdErr.println("invalid input");
				} else {
					name = allInfo.nextToken();
					quantity = Integer.parseInt(allInfo.nextToken());
					price = Double.parseDouble(allInfo.nextToken());
					if (quantity <= 0 || price <= 0) {
						stdErr.println("invalid input");
					} else {
						//输入合法，接收输入
						Product p = new Product(name, quantity, price);
						return p;
					}
				}
			} catch (NumberFormatException nfe) {
				stdErr.println(nfe);
			}
		} while (true);
	}
}
