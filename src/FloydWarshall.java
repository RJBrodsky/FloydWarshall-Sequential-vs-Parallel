/**
 * @author Ryan Brodsky
 * ICS440 Parallel Programming
 * Program #4
 * 8/6/2019
 */
import java.util.Random; //Used when generating the matrix
import java.util.concurrent.ExecutorService; // used for thread pools
import java.util.concurrent.Executors; //used for thread pools
import java.util.concurrent.TimeUnit; // time unit for the while loop in executeMultiThread method

/**
 * This is my floyd warshall class. This class generates a matrix of size "dim"
 * it then calls the execute method which performs the floyd warshall algorithm in sequential time
 * After that it calls the executeMultiThread method which performs the same thing but in parallel time
 * The main method is currently set to loop 10 times, this is so i can test 1 - 10 threads in one run. *
 */
public class FloydWarshall {
	
    private static final int I = Integer.MAX_VALUE; // Infinity
    public static final int dim = 5000; // Size of Matrix
    private static double fill = 0.3; // fill variable for matrix generation
    private static int maxDistance = 100; //distance variable for matrix generation
    private static int adjacencyMatrix[][] = new int[dim][dim];
    private static int d[][] = new int[dim][dim];
	
    private static int numberThreads = 1; // Number of threads here
	private static int secondMatrix[][] = new int[dim][dim]; // Second Copy of Matrix for Multi-Threaded version

	
    /*
     * Generate a randomized matrix to use for the algorithm.
     */
    private static void generateMatrix() {
        Random random = new Random();
        for (int i = 0; i < dim; i++)	{
            for (int j = 0; j < dim; j++)	{
                if (i != j)
                    adjacencyMatrix[i][j] = I;
            }
        }
        for (int i = 0; i < dim * dim * fill; i++)	{
            adjacencyMatrix[random.nextInt(dim)][random.nextInt(dim)] = random.nextInt(maxDistance + 1);
        }
    }
    
    /*
     * Execute Floyd Warshall on adjacencyMatrix.
     */
    private static void execute() {
    	for (int i = 0; i < dim; i++) {
    		for (int j = 0; j < dim; j++)	{
    			d[i][j] = adjacencyMatrix[i][j];
    			if (i == j)	{
    				d[i][j] = 0;
    			}
    		}
    	}
    	for (int k = 0; k < dim; k++) {
    		for (int i = 0; i < dim; i++) {
    			for (int j = 0; j < dim; j++) {
    				if (d[i][k] == I || d[k][j] == I) {
    					continue;
    				} else if (d[i][j] > d[i][k] + d[k][j]) {
    					d[i][j] = d[i][k] + d[k][j];
    				}
    			}
    		}
    	}
    }
        
    /*
     * Execute Floyd Warshall on adjacencyMatrix using thread pools.
     */
    private static void executeMultiThread() throws InterruptedException {

    	ExecutorService threadPool = Executors.newFixedThreadPool(numberThreads); //Creates a Thread Pool of size Number of Threads

    	/**
    	 * This first loop constructs the first matrix
    	 */
    	for (int i = 0; i < dim; i++) {
    		for (int j = 0; j < dim; j++) {
    			secondMatrix[i][j] = adjacencyMatrix[i][j];
    			if (i == j) {
    				secondMatrix[i][j] = 0;
    			}
    		}
    	}

    	/**
    	 * This loop uses a thread pool to complete Floyd Warshall algorithm
    	 * Each thread will execute the runnable Parallel Task, Solving the matrix in parallel 
    	 */
    	for (int k = 0; k < dim; k++) {
    		for (int i = 0; i < dim; i++) {
    			ParallelTask nextTask = new ParallelTask(secondMatrix, k, i);
    			threadPool.execute(nextTask); // Takes a thread from the pool and executes the runnable parallel Task
    		}
    	}

    	threadPool.shutdown(); //shutdown thread pool

    	/**
    	 * The while loop waits until all the threads are done working. 
    	 */
    	while (!threadPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS))	{ 
    	}
    }
  
    /*
     * Print matrix[dim][dim]
     */
    private static void print(int matrix[][]) {
    	for (int i = 0; i < dim; i++) {
    		for (int j = 0; j < dim; j++) {
    			if (matrix[i][j] == I) {
    				System.out.print("I" + " ");
    			} else {
    				System.out.print(matrix[i][j] + " ");
    			}
    		}
    		System.out.println();
    	}
    	System.out.println();
    }
    
    /*
     * Compare two matrices, matrix1[dim][dim] and matrix2[dim][dim] and
     * print whether they are equivalent.
     */
    private static void compare (int matrix1[][], int matrix2[][]) {
    	for (int i = 0; i < dim; i++) {
    		for (int j = 0; j < dim; j++) {
    			if (matrix1[i][j] != matrix2[i][j])	{
    				System.out.println("Comparison failed");
    			}
    		}
    	}
    	System.out.println("Comparison succeeded");
    }
        
    /**
     * This is my main method. I Kept the original code and added my multi thread code below,
     * this way on run time it will print out both sequential and parallel times
     * @param args
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException {

    	long start, end; // start and end variables for the timers
    	generateMatrix(); // generates the matrix
    	//start = System.nanoTime(); // starts timer
    	//execute(); // calls execute method for single threaded version
    	//end = System.nanoTime(); // ends timer
    	//System.out.println("********* Sequential Time Floyd Warshall ********");
    	//System.out.println(numberThreads + " Thread  time consumed: " + (double)(end - start) / 1000000000); // print message
    	//System.out.println(); //prints blank line

    	/**
    	 * This will loop 10 times each time it increase the number of threads
    	 * This is so i can test 1 - 10 threads in one run.
    	 */
    	System.out.println("********* Parallel Time Floyd Warshall **********");
    	for(int i = 0; i < 9; i++)	{
    		numberThreads += 1; // increments the number of threads
    		start = System.nanoTime(); // starts timer
    		executeMultiThread(); // calls execute for multi thread method
    		end = System.nanoTime(); // ends timer
    		System.out.println(numberThreads + " Threads time consumed: " + (double)(end - start) / 1000000000); // print message
    		compare(d, secondMatrix); // compares to make sure there equal
    		System.out.println(); // prints blank line
    	}
    }
}