package test;
/**
 * This class maintains the name of an athlete and three scores.
 *
 * @author  iCarnegie
 * @version  1.0.0
 */

public class AthleteScores  {

	/* Athlete name */
	private String  name;

	/* Score one */
	private double  scoreOne;

	/* Score two */
	private double  scoreTwo;

	/* Score three */
	private double  scoreThree;

	/**
	 * Constructs an <code>AthleteScores</code> object.
	 *
	 * @param initialName  a string with the name of the athlete.
	 * @param initialScoreOne  a double that represents the first score of
	 *                         the athlete.
	 * @param initialScoreTwo  a double that represents the second score of
	 *                         the athlete.
	 * @param initialScoreThree  a double that represents the third score of
	 *                         the athlete.
	 */
	public  AthleteScores(String initialName, double initialScoreOne,
	                      double initialScoreTwo, double initialScoreThree) {

		this.name = initialName;
		this.scoreOne = initialScoreOne;
		this.scoreTwo = initialScoreTwo;
		this.scoreThree = initialScoreThree;
	}

	/**
	 * Returns the name of this athlete.
	 *
	 * @return  the name of this athlete.
	 */
	public String getName() {

		return this.name;
	}

	/**
	 * Returns the first score of this athlete.
	 *
	 * @return  the first score of this athlete.
	 */
	public double getScoreOne() {

		return this.scoreOne;
	}

	/**
	 * Returns the second score of this athlete.
	 *
	 * @return  the second score of this athlete.
	 */
	public double getScoreTwo() {

		return this.scoreTwo;
	}

	/**
	 * Returns the third score of this athlete.
	 *
	 * @return  the third score of this athlete.
	 */
	public double getScoreThree() {

		return this.scoreThree;
	}

	/**
	 * Modifies the first score of this athlete.
	 *
	 * @param newScore  a double with the new score.
	 */
	public void setScoreOne(double newScore) {

		this.scoreOne = newScore;
	}

	/**
	 * Modifies the second score of this athlete.
	 *
	 * @param newScore  a double with the new score.
	 */
	public void setScoreTwo(double newScore) {

		this.scoreTwo = newScore;
	}

	/**
	 * Modifies the third score of this athlete.
	 *
	 * @param newScore  a double with the new score.
	 */
	public void setScoreThree(double newScore) {

		this.scoreThree = newScore;
	}

	/**
	 * Returns the smallest of this athlete's three scores.
	 *
	 * @result  the smallest of this athlete's three scores.
	 */
	public double getMinimum() {

		if ((scoreOne < scoreTwo) && (scoreOne < scoreThree)) {

			return scoreOne;

		} else if ((scoreTwo < scoreOne) && (scoreTwo < scoreThree)) {

			return scoreTwo;

		} else {

			return scoreThree;

		}
	}

	/**
	 * Two <code>AthleteScores</code> objects are equal if their names
	 * are equal (overrides the {@link Object#equals(Object)} method).
	 *
	 * @param object  object with which this <code>AthleteScores</code>
	 *                is to be compared.
	 * @return  <code>true</code> if the argument is an
	 *          <code>AthleteScores</code> object <i>and</i> the name of
	 *          the argument is equal to the name of this
	 *          <code>AthleteScores</code>; <code>false</code> otherwise.
	 */
	public boolean  equals(Object  object)  {

		return (object instanceof AthleteScores)
		        && ((AthleteScores) object).getName().equals(getName());
	}

	/**
	 * Returns the string representation of this <code>AthleteScores</code>
	 * object (overrides the {@link Object#toString()} method).
	 *
	 * @return  the string representation of this <code>AthleteScores</code>
	 *          object.
	 */
	public String  toString()  {

		return  getName() + "," + getScoreOne() + "," + getScoreTwo()
		        + "," + getScoreThree();
	}
}