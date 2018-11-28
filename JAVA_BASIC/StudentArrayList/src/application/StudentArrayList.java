package application;
import java.util.*;

import javax.sound.sampled.LineListener;

/**
 * This class contains methods to process array lists of {@link Student}
 * objects.
 *
 * @author  autor name
 * @version  1.0.0
 * @see  Student
 * @see  ArrayList
 */
public class  StudentArrayList  {

	/**
	 * Returns an array list with three elements.
	 *
	 * @param first  a <code>Student</code> object.
	 * @param second  a <code>Student</code> object.
	 * @param third  a <code>Student</code> object.
	 * @return an array list with the objects <code>first</code>,
	 *           <code>second</code>, and <code>third</code>
	 */
	public static ArrayList<Student> makeArrayList(
		Student  first,
		Student  second,
		Student  third)  {

		ArrayList<Student> it = new ArrayList<Student>();
		it.add(first);
		it.add(second);
		it.add(third);

		return it;
	}

	/**
	 * Returns an array list with the same elements of the specified array
	 * arranged in the same order.
	 *
	 * @param array  an array with <code>Student</code> objects .
	 * @return an array list with the same elements of the specified array
	 *         arranged in the same order
	 */
	public static ArrayList<Student> makeArrayListFromArray(Student[] array) {
		int len = array.length;
		ArrayList<Student> it = new ArrayList<Student>();
		for(int i = 0; i < len; i++)
			it.add(array[i]);
		
		return it; // REMOVE; USED SO THIS FILE COMPILES
	}

	/**
	 * Returns <code>true</code> if the specified array list contains a
	 * student whose id matches the specified ID.
	 *
	 * @param arrayList  an array list of <code>Student</code> objects.
	 * @param id  a student ID.
	 * @return  <code>true</code> if the specified array list contains a
	 *          student whose ID matches the specified ID;
	 *          <code>false</code> otherwise.
	 */
	public static boolean hasStudent(
		ArrayList<Student>  arrayList,
		int  id)  {
		int len = arrayList.size();
		for(int i = 0; i < len; i++){
			if(arrayList.get(i).getId() == id)
				return true;
		}

		return false; 
	}

	/**
	 * Returns the number of students in the specified array list whose
	 * grade is greater than or equal to the specified grade.
	 *
	 * @param arrayList  an array list of <code>Student</code> objects.
	 * @param grade  a grade.
	 * @return  the number of students in the specified array list whose
	 *          grade is greater than or equal to the specified grade.
	 */
	public static int countGradeGreaterOrEqual(
		ArrayList<Student> arrayList,
		int grade)  {
		int icount = 0;
		int len = arrayList.size();
		for(int i = 0; i < len; i++){
			if(arrayList.get(i).getGrade() >= grade)
				icount++;
		}

		return icount; 
	}

	/**
	 * Returns the smallest grade of the students in the specified array list.
	 * <p>
	 * This method assumes that the array list is not empty.
	 *
	 * @param arrayList  an array list of <code>Student</code> objects.
	 * @return  the smallest grade of the students in the specified array list.
	 */
	public static int getMinGrade(ArrayList<Student> arrayList)  {
		int mini = 0;
		int len = arrayList.size();
		for(int i = 0; i < len; i++){
			if(mini > arrayList.get(i).getGrade())
				mini = arrayList.get(i).getGrade();
		}
		return mini; // REMOVE; USED SO THIS FILE COMPILES
	}

	/**
	 * Returns the average grade of the students in the specified array list.
	 *
	 * @param arrayList  an array list of <code>Student</code> objects.
	 * @return  the average grade of the students in the specified array list.
	 */
	public static double getGradeAverage(ArrayList<Student>  arrayList)  {
		int total = 0;
		double len = arrayList.size();
		for(int i = 0; i < len; i++){
			total += arrayList.get(i).getGrade();
		}
		double aver = total / len;
		return aver;
	}

	/**
	 * Removes all students in the specified array list whose grade
	 * is less than the specified grade.
	 *
	 * @param arrayList  an array list of <code>Student</code> objects.
	 * @param grade  a grade.
	 */
	public static void removeGradeLess(
		ArrayList<Student>  arrayList,
		int  grade)  {
		int mini = getMinGrade(arrayList);
		double len = arrayList.size();
		for(int i = 0; i < len; i++){
			if (arrayList.get(i).getGrade() == mini)
				arrayList.remove(i);
			i--;
			len--;
		}
	}

	/**
	 * Returns the string representation of the objects in the specified
	 * array list.
	 * <p>
	 * A new line character ( \n ) should separate the string
	 * representations of the objects. For example:
	 * </p>
	 * <pre>
	 * Student[328,Galileo Galilei,80]\nStudent[123,Albert Einstein,100]
	 * </pre>
	 * <p>
	 * Note that the string does <i>not</i> end with a new line character ( \n )
	 * </p>
	 *
	 * @param arrayList  an array list of <code>Student</code> objects.
	 * @return  the string representation of the objects in the specified
	 *          array list.
	 */
	public static String displayAll(ArrayList<Student>  arrayList)  {
		int len = arrayList.size();
		if(len == 0) return "";
		
		String it = arrayList.get(0).toString(); 
		for(int i = 1; i < len; i++){
			it += "\n" + arrayList.get(i).toString();
		}
		return it;
	}
}