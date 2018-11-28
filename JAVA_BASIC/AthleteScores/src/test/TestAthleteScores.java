package test;
import java.io.PrintWriter;

public class TestAthleteScores {
	/* Standard output stream */
	private static PrintWriter  stdOut = new  PrintWriter(System.out, true);

	/* Standard error stream */
	private static PrintWriter  stdErr = new  PrintWriter(System.err, true);

	/**
	 * Displays a message in the standard error stream if the value specified
	 * by parameter <code>condition<code> is <code>false</code>.
	 *
	 * @param message  the error message.
	 * @param condition  the test condition.
	 */
	public static void assertTrue(String message, boolean condition) {

		if (! condition) {
			stdErr.print("** Test failure ");
			stdErr.println(message);
		}
	}

	/**
	 * Test driver for class <code>AthleteSocres</code>.
	 *
	 * @param args  not used.
	 */
	public static void  main(String[] args)  {

		stdOut.println("");
		stdOut.println("Testing class Product...");

		String name = "C001";
		double scoreOne = 11.0;
		double scoreTwo = 12.0;
		double scoreThree = 13.0;
		double newScoreOne = 11.5;
		double newScoreTwo = 12.5;
		double newScoreThree = 13.5;

		// Test accessors
		AthleteScores score = new AthleteScores(name, scoreOne, scoreTwo, scoreThree);

		assertTrue("1: testing method getName",
		           name.equals(score.getName()));
		assertTrue("2: testing method getScoreOne",
		           scoreOne == score.getScoreOne());
		assertTrue("3: testing method getScoreTwo",
		           scoreTwo == score.getScoreTwo());
		assertTrue("4: testing method getScoreThree",
		           scoreThree == score.getScoreThree());
		
		// Test mutator
		score = new AthleteScores(name, scoreOne, scoreTwo, scoreThree);
		score.setScoreOne(newScoreOne);
		score.setScoreTwo(newScoreTwo);
		score.setScoreThree(newScoreThree);	
		
		assertTrue("5: testing method setScoreOne",
				newScoreOne == score.getScoreOne());
		assertTrue("6: testing method setScoreTwo",
				newScoreTwo == score.getScoreTwo());
		assertTrue("7: testing method setScoreThree",
				newScoreThree == score.getScoreThree());
		
		//Test method getMinimum
		double MiniScoreOne = 3;
		double MiniScoreTwo = 4;
		double MiniScoreThree = 5;
		
		AthleteScores scoreONE = new AthleteScores(name, MiniScoreOne, scoreTwo, scoreThree);
		AthleteScores scoreTWO = new AthleteScores(name, scoreOne, MiniScoreTwo, scoreThree);
		AthleteScores scoreTHREE = new AthleteScores(name, scoreOne, scoreTwo, MiniScoreThree);

		assertTrue("8: testing method getMinimum",
				MiniScoreOne == scoreONE.getMinimum());
		assertTrue("9: testing method getMinimum",
		        MiniScoreTwo == scoreTWO.getMinimum());
		assertTrue("10: testing method getMinimum",
		        MiniScoreThree == scoreTHREE.getMinimum());
		
		// Test method equals
		String name_1 = "Oliver";
		double scoreOne_1 = 11.4;
		double scoreTwo_1 = 12.4;
		double scoreThree_1 = 13.4;

		String name_2 = "Timber";
		double scoreOne_2 = 11.6;
		double scoreTwo_2 = 12.6;
		double scoreThree_2 = 13.6;

		scoreONE = new AthleteScores(name_1, scoreOne_1, scoreTwo_1, scoreThree_1);
		scoreTWO = new AthleteScores(name_1, scoreOne_2, scoreTwo_2, scoreThree_2);
		scoreTHREE = new AthleteScores(name_2, scoreOne_2, scoreTwo_2, scoreThree_2);
		
		assertTrue("11: testing method equals",
		           scoreONE.equals(scoreTWO));
		assertTrue("12: testing method equals",
		           !scoreONE.equals(scoreTHREE));
		assertTrue("13: testing method equals",
		           !scoreONE.equals("AthleteScores"));

		// Test method toString
		score = new AthleteScores(name, scoreOne, scoreTwo, scoreThree);
		String result = name + "," + scoreOne + "," + scoreTwo + "," + scoreThree;
		assertTrue("14: testing method toString",
		           result.equals(score.toString()));

		stdOut.println("done");
	}
}
