/**
 * @author Ryan Brodsky
 * ICS440 Parallel Programming
 * Program #4
 * 8/6/2019
 */
public class ParallelTask implements Runnable {

	private static int secondMatrix[][]; // matrix variable
	private int y, x;
	private static final int value = Integer.MAX_VALUE; // value variable

	/**
	 * This constructs a parallel task, it takes the matrix and two integers as parameters.
	 * @param secondMatrix
	 * @param x
	 * @param y
	 */
	public ParallelTask(int secondMatrix[][], int x, int y) {
		ParallelTask.secondMatrix = secondMatrix;
		this.x = x;
		this.y = y;
	}

	/**
	 * This is my run method. It is very similar to the execute method. except for this 
	 * will allow me to run in in parallel with multiple threads.
	 */
	public void run() {
		for (int i = 0; i < FloydWarshall.dim; i++) {
			if (secondMatrix[y][x] == value || secondMatrix[x][i] == value) {
				continue;
			} else if (secondMatrix[y][i] > secondMatrix[y][x] + secondMatrix[x][i]) {
				secondMatrix[y][i] = secondMatrix[y][x] + secondMatrix[x][i];
			}
		}
	}
}