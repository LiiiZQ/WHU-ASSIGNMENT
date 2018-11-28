import java.io.*;
import java.util.*;

public class SecondsCalculator {
	private static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	private static PrintWriter stdOut = new PrintWriter(System.out, true);
	private static PrintWriter stdErr = new PrintWriter(System.err, true);

	public static void main(String[] args) throws IOException {
		// TODO write your own code here.
		SecondsCalculator calculator = new SecondsCalculator();
		calculator.run();
	}

	private void run() throws IOException {
		final String DELIM = ":";
		int hour = 0;
		int minute = 0;
		int second = 0;
		int total = 0;
		// 提示并接收用户输入
		do {
			try {
				stdErr.println();
				stdErr.print("time [hour:minutes:seconds]> ");
				stdErr.flush();

				String inputTime = stdIn.readLine();
				StringTokenizer time = new StringTokenizer(inputTime, DELIM, false);
				// 检查用户输入1.是否为三个数？2.是否为整数？3.是否大于零？4.是否在范围内？
				if (time.countTokens() != 3) {
					stdErr.println("invalid input");
				} else {
					hour = Integer.parseInt(time.nextToken());
					minute = Integer.parseInt(time.nextToken());
					second = Integer.parseInt(time.nextToken());
					if ((hour < 0 || hour > 23) || (minute < 0 || minute > 59) || (second < 0 || second > 59)) {
						stdErr.println("invalid input");
					} else {
						total = hour * 3600 + minute * 60 + second;
						stdErr.println("The number of seconds is: " + total);
						return;
					}
				}
			} catch (NumberFormatException nfe) {
				stdErr.println(nfe);
			}
		} while (true);

	}
}
