package Munkres;

import static java.lang.Math.*;

import java.util.*;

public class Hungarian {

	//********************************//
	//METHODS FOR CONSOLE INPUT-OUTPUT//
	//********************************//

	public static int readInput(String prompt)	//Reads input,returns double.
	{
		Scanner in = new Scanner(System.in);
		System.out.print(prompt);
		int input = in.nextInt();
		return input;
	}
	public static void insertLines(int blancLines)	//Inserts <blancLine> lines.
	{
		for (int i=0; i<blancLines; i++)
		{System.out.println();}
	}

	public static double getTime(double start, double end) //Calculates elapsed time in seconds.
	{
		double total = (end - start)/1000000000.0; //Convert time in seconds
		return total;
	}
	public static void printTime(double time)	//Formats time output.
	{
		String timeElapsed = "";
		int days = (int)floor(time)/(24 * 3600);
		int hours = (int)floor(time%(24*3600))/(3600);
		int minutes = (int)floor((time%3600)/60);
		int seconds = (int)round(time%60);

		if (days > 0)
			timeElapsed = Integer.toString(days) + "d:";
		if (hours > 0)
			timeElapsed = timeElapsed + Integer.toString(hours) + "h:";
		if (minutes > 0)
			timeElapsed = timeElapsed + Integer.toString(minutes) + "m:";

		timeElapsed = timeElapsed + Integer.toString(seconds) + "s";
		System.out.print(timeElapsed);
	}

	//*******************************************//
	//METHODS THAT PERFORM ARRAY-PROCESSING TASKS//
	//*******************************************//

	public static void generateRandomArray	//Generates random 2-D array.
	(double[][] array, String randomMethod)	
	{
		Random generator = new Random();
		for (int i=0; i<array.length; i++)
		{
			for (int j=0; j<array[i].length; j++)
			{
				if (randomMethod.equals("random"))
				{array[i][j] = generator.nextDouble();}
				if (randomMethod.equals("gaussian"))
				{
					array[i][j] = generator.nextGaussian()/4;		//range length to 1.
					if (array[i][j] > 0.5) {array[i][j] = 0.5;}		//eliminate outliers.
					if (array[i][j] < -0.5) {array[i][j] = -0.5;}	//eliminate outliers.
					array[i][j] = array[i][j] + 0.5;				//make elements positive.
				}
			}
		}			
	}
	public static double findLargest		//Finds the largest element in a positive array.
	(double[][] array)
	//works for arrays where all values are >= 0.
	{
		double largest = 0;
		for (int i=0; i<array.length; i++)
		{
			for (int j=0; j<array[i].length; j++)
			{
				if (array[i][j] > largest)
				{
					largest = array[i][j];
				}
			}
		}

		return largest;
	}
	public static double[][] transpose		//Transposes a double[][] array.
	(double[][] array)	
	{
		double[][] transposedArray = new double[array[0].length][array.length];
		for (int i=0; i<transposedArray.length; i++)
		{
			for (int j=0; j<transposedArray[i].length; j++)
			{transposedArray[i][j] = array[j][i];}
		}
		return transposedArray;
	}
	public static double[][] copyOf			//Copies all elements of an array to a new array.
	(double[][] original)	
	{
		double[][] copy = new double[original.length][original[0].length];
		for (int i=0; i<original.length; i++)
		{
			//Need to do it this way, otherwise it copies only memory location
			System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
		}

		return copy;
	}

	//**********************************//
	//METHODS OF THE HUNGARIAN ALGORITHM//
	//**********************************//

	public static int[][] hgAlgorithm (double[][] array, String sumType)
	{
		double[][] cost = copyOf(array);	//Create the cost matrix.

		if (sumType.equalsIgnoreCase("max"))	//Then array is weight array. Must change to cost.
		{
			double maxWeight = findLargest(cost);

			for (int i=0; i<cost.length; i++)		//Generate cost by subtracting.
			{
				for (int j=0; j<cost[i].length; j++)
				{
					cost [i][j] = (maxWeight - cost [i][j]);
				}
			}
		}

		double maxCost = findLargest(cost);		//Find largest cost matrix element (needed for step 6).

		//Now we need to declare and initialize several other arrays that help in the calculations.
		//These are:
		//The mask array (dimensions same as cost).
		int[][] mask = new int[cost.length][cost[0].length];	

		//The row covering vector (dimension rowsOfCostx1).
		int[] rowCover = new int[cost.length];

		//The column covering vector (dimension colsOfCostx1).
		int[] colCover = new int[cost[0].length];

		//This next one is used to remember the position of a zero retrieved from step 4 that
		//needs to be passed to step 5. It has to do with the path array defined in step 5 and is
		//basically the hardest part of the algorithm to understand. To get an idea you need to read
		//what is happening inside steps 4 and 5.
		//This one should be defined here as an array and not as 2 independent variables
		//because in Java, simple variables that get sent to methods and change in the methods
		//DO NOT change in the main program whereas arrays DO.
		int[] zero_RC = new int[2];

		//Step number: its value guides the algorithm through the various steps.
		int step = 1;

		//When this changes to "true" the assignment would be completed.
		boolean done = false;	

		while (done == false)
		{ 
			switch (step)
			{
			case 1:
				step = hg_step1(step, cost);     
				break;

			case 2:
				step = hg_step2(step, cost, mask, rowCover, colCover);
				break;

			case 3:
				step = hg_step3(step, mask, colCover);
				break;

			case 4:
				step = hg_step4(step, cost, mask, rowCover, colCover, zero_RC);
				break;

			case 5:
				step = hg_step5(step, mask, rowCover, colCover, zero_RC);
				break;

			case 6:
				step = hg_step6(step, cost, rowCover, colCover, maxCost);

				break;

			case 7:
				//System.out.println("*********************************************");
				//System.out.println ("\nAll calculations completed!\nGetting out of main loop...\n");
				done=true;
				break;
			}
		}//end while

		int[][] assignment = new int[array.length][2];	//Create the returned array.
		for (int i=0; i<mask.length; i++)
		{
			for (int j=0; j<mask[i].length; j++)
			{
				if (mask[i][j] == 1)
				{
					assignment[i][0] = i;
					assignment[i][1] = j;
				}
			}
		}

		return assignment;

	}
	public static int hg_step1(int step, double[][] cost)
	{
		//What STEP 1 does:
		//For each row of the cost matrix, find the smallest element
		//and subtract it from from every other element in its row. 


		double minval;

		for (int i=0; i<cost.length; i++)	
		{									
			minval=cost[i][0];
			//First loop on columns finds the minimum value in a row
			for (int j=0; j<cost[i].length; j++)
			{
				if (minval>cost[i][j])
				{
					minval=cost[i][j];
				}
			}

			//Second loop on columns subtracts the minimum element on a row from
			//all other elements of the row.
			for (int j=0; j<cost[i].length; j++)
			{
				cost[i][j]=cost[i][j]-minval;
			}
		}
		insertLines(1);
		step=2;

		return step;
	}
	public static int hg_step2(int step, double[][] cost, int[][] mask, int[]rowCover, int[] colCover)
	{
		//What STEP 2 does:
		//As the calcs proceed, the cost matrix will start having many 0s. Some of these
		//we need to characterize as starred zeros and others as prime. This is exactly
		//the information that the mask matrix maintains. This matrix has the same dimensions
		//as the cost matrix. A 0 element in cost will have the same position in mask and its
		//value in mask will be 1 if the zero is starred, or 2 if the zero is primed.
		//Step 2 is concerned with finding and marking starred zeros only.
		//It checks every element of cost to see if it is a zero and if its row or column
		//are not covered (rowCover and colCover arrays). If these conditions are met,
		//the zero is starred so mask[i][j] becomes 1 for this zero element of cost.
		//In addition, the row i and the column j are covered (this covering is needed
		//internally in this step to work correctly). Before leaving step 2, the covers need
		//to be uncovered so as to help us count the amount of starred zeros in step 3.


		//Find starred zeros
		for (int i=0; i<cost.length; i++)
		{
			for (int j=0; j<cost[i].length; j++)
			{
				if ((cost[i][j]==0) && (colCover[j]==0) && (rowCover[i]==0))
				{
					mask[i][j]=1;
					colCover[j]=1;
					rowCover[i]=1;
				}
			}
		}

		//Reset the row and column vectors
		clearCovers(rowCover, colCover);

		step=3;

		return step;
	}
	public static int hg_step3(int step, int[][] mask, int[] colCover)
	{
		//What STEP 3 does:
		//Step 3 works on the mask matrix only. It examines each column and if
		//the column has a starred zero, then it covers it. If k columns are
		//covered, where k is min(rowsOfInputtedArray, colsOfInputtedArray),then
		//we got a complete set of unique assignments and we are done. It means
		//we have found and starred k independent zeros. However we can set
		//k = rowsOfInputtedArray because we have transposed the matrix already
		//so that the rows are less than the columns.
		//If k columns are not covered, then we go to Step 4.


		//Cover columns containing starred zeros
		for (int i=0; i<mask.length; i++)
		{
			for (int j=0; j<mask[i].length; j++)
			{
				if (mask[i][j] == 1)
				{
					colCover[j]=1;
				}
			}
		}

		//This block used only to print what columns are covered in increasing sequence
		for (int j=0; j<colCover.length; j++)
		{
			if (colCover[j] == 1)
			{
				//System.out.println("Column " + (j+1) + " had a starred zero and was covered.");
			}
		}

		//Count number of covered columns
		int count=0;
		for (int j=0; j<colCover.length; j++)
		{
			count=count+colCover[j];
		}
		//Branch execution to end or to Step 4
		if (count>=mask.length)	//Should be cost.length but ok, because mask has same dimensions.
		{
			step=7;
		}
		else
		{
			step=4;
		}

		return step;
	}
	public static int hg_step4(int step, double[][] cost, int[][] mask, int[] rowCover, int[] colCover, int[] zero_RC)
	{
		//What STEP 4 does:
		//Step 4 begins by checking for uncovered zeros in the cost matrix (i.e., finds a zero in
		//the cost matrix, then checks if its row and col are not covered in the row Cover and 
		//col Cover arrays respectively). If all these conditions are met then the uncovered zero
		//is primed (i.e., the corresponding element in the mask matrix is set to 2).
		//Then, step 4 checks to see if there is a starred zero in the row that contains the primed
		//zero (the one that was just primed). There are 2 alternatives:
		//a. If there is a starred zero, then cover this row and uncover the column containing
		//	 the starred zero. Repeat step 4 from the beginning until there are no uncovered
		//   zeros left in the cost matrix in which case we need to go to step 6.
		//b. If there is no starred zero in the row containing the primed zero then save the
		//   location of this primed zero (so that we can tell step 5 where the last discovered
		//   prime zero is) and go to step 5.


		int[] row_col = new int[2];	//Holds row and col of uncovered zero.
		boolean done = false;
		while (done == false)
		{
			row_col = findUncoveredZero(row_col, cost, rowCover, colCover);
			if (row_col[0] == -1)
			{
				//System.out.println("All zeros are covered. Going to step 6.\n");
				done = true;
				step = 6;
			}
			else
			{
				mask[row_col[0]][row_col[1]] = 2;	//Prime the found uncovered zero.


				//Now we try to find if there is a starred zero in the same row as 
				//the zero that we just primed.
				boolean starInRow = false;
				for (int j=0; j<mask[row_col[0]].length; j++)
				{
					if (mask[row_col[0]][j]==1)		//If there is a star in the same row...
					{
						starInRow = true;
						row_col[1] = j;		//remember its column.
					}
				}

				if (starInRow==true)
				{
					rowCover[row_col[0]] = 1;	//Cover the star's row.
					colCover[row_col[1]] = 0;	//Uncover its column.
				}
				else
				{
					done = true;
					step = 5;


					zero_RC[0] = row_col[0];	//Save row of primed zero.
					zero_RC[1] = row_col[1];	//Save column of primed zero.

				}
			}
		}
		return step;
	}
	public static int[] findUncoveredZero	//Aux 1 for hg_step4.
	(int[] row_col, double[][] cost, int[] rowCover, int[] colCover)
	{

		//The first row value that we define must not be one that refers to an index
		//of the cost matrix. It is only a check. If a meaningful row needs to be
		//returned it will be returned from the loop that follows. If there is no
		//such value we return -1 to tell step 4 that there exists no uncovered zero
		//and, hence, it needs to go to Step 6.
		row_col[0] = -1;	//Just a check value. Not a real index.
		row_col[1] = 0;

		int i = 0;
		boolean done = false;
		while (done == false)
		{
			int j = 0;
			while (j < cost[i].length)
			{
				if (cost[i][j]==0 && rowCover[i]==0 && colCover[j]==0)
				{
					row_col[0] = i;
					row_col[1] = j;
					done = true;
				}
				j = j+1;
			}//end inner while
			i=i+1;
			if (i >= cost.length)
			{
				done = true;
			}
		}//end outer while

		return row_col;
	}
	public static int hg_step5(int step, int[][] mask, int[] rowCover, int[] colCover, int[] zero_RC)
	{
		//What STEP 5 does:	
		//Step 5 basically is a version of an augmenting path algorith (for solving the
		//maximal matching problem). It discovers and sets augmenting paths, that is, paths
		//that increase the flow of a network (I think). Understanding augmenting paths etc
		//is a bit difficult and you need to only if you are a computer science major. Here,
		//I will just try to superficially explain how it works inside so that you understand
		//what is basically happening.
		//We want to construct a series of alternating primed and starred zeros as follows:
		//	a. Let path(i,j) represent the last uncovered primed zero found in Step 4. If you
		//     remember, we stored its coordinates in the zero_RC array.
		//	b. Let path(i+1,j) denote the starred zero in the column of path(i,j)(if any).
		//	   (When we say in the column, we mean in the column of the mask matrix).
		//	c. Let path(i+2,j) denote the primed zero in the row of path(i+1,j)
		//	   (there will always be one) (Again with row, we mean row of mask matrix).
		//	d. Continue until the series terminates at a primed zero that has no starred
		//	   zero in its column.
		//	e. Unstar each starred zero of the series and star each primed zero of the series.
		//	f. Erase all primes (if any other primes exist in the mask matrix).
		//	g. Uncover every row and column
		//	h. Return to step 3.
		//Clearly, some of the above steps, need to be decomposed into sub-methods. 


		int count = 0;	//Counts rows of the path matrix.

		//First we declare the path matrix. Since it holds positions of elements in the
		//mask matrix, its first column holds the row number and the second column holds
		//the column number. So this matrix is somethingx2. We can't be sure what exactly this
		//something will be equal to. It changes everytime. Therefore, we can either have the
		//path array dynamically grow when needed, or set it to something it cannot exceed.
		//We do the latter.

		int[][] path = new int[(mask[0].length*mask.length)][2];

		//Set the first row of path to the coordinates of the last uncovered primed zero.
		path[count][0] = zero_RC[0];	//Row of last prime.
		path[count][1] = zero_RC[1];	//Column of last prime.


		boolean done = false;
		while (done == false)
		{ 
			int r = findStarInCol(mask, path[count][1]);
			if (r>=0)
			{
				count = count+1;
				path[count][0] = r;					//Row of starred zero.
				path[count][1] = path[count-1][1];	//Column of starred zero.
			}
			else
			{

				done = true;
			}

			if (done == false)
			{
				int c = findPrimeInRow(mask, path[count][0]);
				count = count+1;
				path[count][0] = path [count-1][0];	//Row of primed zero.
				path[count][1] = c;					//Column of primed zero.
			}
		}//end while

		convertPath(mask, path, count);

		clearCovers(rowCover, colCover);

		erasePrimes(mask);

		step = 3;

		return step;

	}
	public static int findStarInCol	(int[][] mask, int col){		//Aux 1 for hg_step5.

		int r=-1;	//Again this is a check value.
		for (int i=0; i<mask.length; i++)
		{
			if (mask[i][col]==1)
			{
				r = i;
			}
		}

		return r;
	}
	public static int findPrimeInRow(int[][] mask, int row){	//Aux 2 for hg_step5.

		int c = -1;
		for (int j=0; j<mask[row].length; j++)
		{
			if (mask[row][j]==2)
			{
				c = j;
			}
		}
		return c;
	}
	public static void convertPath(int[][] mask, int[][] path, int count){		//Aux 3 for hg_step5.

		for (int i=0; i<=count; i++)
		{
			if (mask[(path[i][0])][(path[i][1])]==1)
			{
				mask[(path[i][0])][(path[i][1])] = 0;
			}
			else
			{
				mask[(path[i][0])][(path[i][1])] = 1;
			}
		}
	}
	public static void erasePrimes(int[][] mask){		//Aux 4 for hg_step5.

		int tempCount = 0;
		for (int i=0; i<mask.length; i++)
		{
			for (int j=0; j<mask[i].length; j++)
			{
				if (mask[i][j]==2)
				{
					tempCount = tempCount + 1;
					mask[i][j] = 0;
				}
			}
		}

	}
	public static void clearCovers			//Aux 5 for hg_step5 (also used from other parts).
	(int[] rowCover, int[] colCover)
	{
		for (int i=0; i<rowCover.length; i++)
		{
			rowCover[i] = 0;
		}
		for (int j=0; j<colCover.length; j++)
		{
			colCover[j] = 0;
		}
	}
	public static int hg_step6(int step, double[][] cost, int[] rowCover, int[] colCover, double maxCost)
	{
		//What STEP 6 does:
		//Step 6 does something similar to what step 1 did but not quite...
		//It finds the smallest uncovered value in the cost matrix. Then, it:
		//	a. Subtracts this value from every element that is on an uncovered column,
		//  b. Adds this value to every element that is on a covered row.
		//In essence it modifies the cost matrix so that step 4 can continue again.


		double minval = 0;
		minval = findSmallest(cost, rowCover, colCover, maxCost);

		int count1 = 0;
		for (int j=0; j<colCover.length; j++)
		{
			if (colCover[j]==0)
			{
				count1 = count1 + j;
			}
		}
		int count2 = 0;
		for (int i=0; i<rowCover.length; i++)
		{
			if (rowCover[i]==1)
			{
				count2 = count2 + i;
			}
		}			 

		for (int i=0; i<rowCover.length; i++)
		{
			for (int j=0; j<colCover.length; j++)
			{
				if (rowCover[i]==1)
				{
					cost[i][j] = cost[i][j] + minval;
				}
				if (colCover[j]==0)
				{
					cost[i][j] = cost[i][j] - minval;
				}
			}
		}


		step = 4;

		return step;
	}
	public static double findSmallest		//Aux 1 for hg_step6.
	(double[][] cost, int[] rowCover, int[] colCover, double maxCost)
	{
		double minval = maxCost;	//There cannot be a larger cost than this.

		//Now find the smallest uncovered value.
		for (int i=0; i<cost.length; i++)
		{
			for (int j=0; j<cost[i].length; j++)
			{
				if (rowCover[i]==0 && colCover[j]==0 && (minval > cost[i][j]))
				{
					minval = cost[i][j];
				}
			}
		}

		return minval;
	}

	public static double[][] generateRandomMatrix(int row, int col){

		double[][] rest = new double[row][col];
		Random rand = new Random();
		for(int i =0; i<row; i++){
			for(int k =0; k<col; k++){

				rest[i][k] = (double) rand.nextInt(100); 
			}
		}

		return rest;
	}

	public void deneme(double[][] cm) 
	{

		//If the intent is to find the assignment, that is, the combination of 
		//unique row-col pairs that minimizes the sum then leave "min" below for
		//sumType. If, instead you want to find the assignment that maximizes the
		//sum then change "min" to "max".
		String sumType = "min";
		//Hard-coded example. Change to enter your own matrix.
		/*double[][] array =
			{
				{31, 98, 93, 57, 16}, 
				{51, 39, 83, 34, 87}, 
				{86, 45, 92, 10, 0}, 
				{16, 93, 63, 67, 45}, 
				{27, 87, 34, 74, 51}
			};*/

		//Another example matrix

		/*double[][] array = 
			{
				{5, 86, 91, 0, 82},
				{54, 65, 48, 88, 92},
				{69, 86, 64, 63, 35},
				{13, 70, 82, 82, 11}
			};

		 */
//		Scanner s = new Scanner(System.in);
//		System.out.print("Row : ");
//		int row = s.nextInt();
//		System.out.print("\nColumn : ");
//		int col = s.nextInt();
		double[][] array = cm;

		if (array.length > array[0].length){ //Rectangular matrix
			array = transpose(array);
		}


		double startTime = System.nanoTime();	

		int[][] assignment = new int[array.length][2];
		assignment = hgAlgorithm(array, sumType);	//Call Hungarian algorithm.

		double endTime = System.nanoTime();

		String arrayType;						//Define this for printing purposes only.
		if (sumType.equalsIgnoreCase("min"))
		{
			arrayType = "cost";
		}
		else if (sumType.equalsIgnoreCase("max"))
		{
			arrayType = "weight";
		}
		else
		{
			arrayType = "";
		}

		double sum = 0;
		for (int i=0; i<assignment.length; i++)
		{
			//<COMMENT> to avoid printing the elements that make up the assignment
			System.out.printf("array(%d,%d) = %.1f\n", (assignment[i][0]+1), (assignment[i][1]+1),
					array[assignment[i][0]][assignment[i][1]]);
			sum = sum + array[assignment[i][0]][assignment[i][1]];
			//</COMMENT>
		}


		System.out.printf("\nThe %s is: %.1f\n", sumType, sum);


		double totalTime = getTime(startTime, endTime);
		System.out.print("Total time required: ");
		printTime(totalTime);
		insertLines(1);

	}

	

	public Hungarian(double[][] costMatrix){
		
deneme(costMatrix);
	}
}

