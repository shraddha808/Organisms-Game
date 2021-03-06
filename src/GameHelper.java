import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * This class has methods that the main game calls in order to conduct the games
 * It implements the various rules of the games
 * 
 * @author ShadyJ
 *
 */
public class GameHelper {

	private Cell[][] grid;
	private ArrayList<Point> boxes;
	private HashMap<Player, IndividualOrganismData> individualData;
	ArrayList<Player> players;
	private HashMap<Player, Integer> keyMap;
	private int height;
	private int width;

	/**
	 * This is the constructor for the class.
	 * 
	 * @param players
	 *            - the ArrayList of players
	 * @param boxes
	 *            - the ArrayList of available points on the grid
	 * @param individualData
	 *            - the HashMap that maps player type to organism data
	 * @param grid
	 *            - 2D array that represents the grid for the game
	 * @param keyMap
	 *            - the HashMap that maps the players to its key/ID
	 */
	public GameHelper(ArrayList<Player> players, ArrayList<Point> boxes,
			HashMap<Player, IndividualOrganismData> individualData, Cell[][] grid, HashMap<Player, Integer> keyMap) {

		this.grid = grid;
		this.players = players;
		this.boxes = boxes;
		this.individualData = individualData;
		this.keyMap = keyMap;
	}

	/**
	 * This method randomly places the original places on the grid at the
	 * beginning of the game
	 * 
	 * @return retValue - 0 if placed successfully, otherwise -1 to indicate
	 *         that the grid is covered completely.
	 */
	public int placePlayersOnGrid() {
		final int STARTENERGY = 500;
		int retValue = 0;
		// boolean found = false;

		if (boxes.isEmpty()) {
			// do something if all the boxes are occupied
			retValue = -1;
		} else {
			for (Player plays : players) {

				Point box = boxes.get(0);

				IndividualOrganismData individual = new IndividualOrganismData(STARTENERGY, plays, box,
						keyMap.get(plays));

				// allIndividuals.add(individual);
				individualData.put(plays, individual);

				grid[box.getX()][box.getY()].setOccupancy(plays);

				boxes.remove(box);

			}
		}
		return retValue;
	}

	/**
	 * This method generates the grid for the game
	 * 
	 * @param width
	 *            - the width of the grid
	 * @param height
	 *            - the height of the grid
	 */
	public void generateGrid(int width, int height) {
		this.height = height;
		this.width = width;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Point point = new Point(i, j);
				boxes.add(point);
				grid[i][j] = new Cell(point);
			}
		}

	}

	/**
	 * This method takes care of the random/spontaneous appearance and doubling
	 * of food on each cell
	 * 
	 * @param currentCell
	 *            - the cell the game is currently in
	 * @param p
	 *            - the probability of food spontaneously appearing on an empty
	 *            cell
	 * @param q-
	 *            the probability of food doubling
	 * @return the cell
	 */
	public Cell changeFoodInCell(Cell currentCell, double p, double q) {
		final int MAX_FOOD = 50;
		Random rand = new Random();
		Cell cell = currentCell;
		boolean occupied = cell.isOccupied();
		int foodUnits = cell.getFoodUnits();
		// System.out.println("food: "+foodUnits);
		if (foodUnits >= MAX_FOOD) {

			// do nothing
		}
		// if there is no food or organism on the cell, 1 unit of food appears
		// with probablity = p
		else {
			if (occupied == false && foodUnits == 0) {
				boolean val = rand.nextInt((int) (1 / p)) == 0;
				if (val == true) {
					cell.changeFood(1);
				}
			}
			// if there is no organism on the cell, but there is food, each unit
			// of
			// food could double with probablity = q
			else if (occupied == false && foodUnits != 0) {
				for (int k = 0; k < foodUnits; k++) {
					boolean val = rand.nextInt((int) (1 / q)) == 0;
					if (val == true) {
						// countOfDoubling++;
						cell.changeFood(1);
					}
				}
			} else {
				// do nothing
			}
		}
		return cell;
	}

	/**
	 * This method returns a boolean of food presence in the surrounding cells
	 * for a given cell.
	 * 
	 * @param currentCell
	 *            - the current cell
	 * @return - foodPresent[] - boolean of food presence in the neighboring
	 *         cells
	 */
	public boolean[] isFoodPresentInNeighboringCell(Cell currentCell) {

		boolean foodPresent[] = new boolean[5];
		for (int i = 0; i < foodPresent.length; i++) {
			foodPresent[i] = false;
		}

		if (currentCell.getFoodUnits() > 0) {
			foodPresent[0] = true;
		}
		int x = currentCell.getPoint().getX();
		int y = currentCell.getPoint().getY();

		if (generateWestPoint(x, y).getFoodUnits() > 0) {
			foodPresent[1] = true;
		}

		if (generateEastPoint(x, y).getFoodUnits() > 0) {
			foodPresent[2] = true;
		}
		if (generateNorthPoint(x, y).getFoodUnits() > 0) {
			foodPresent[3] = true;
		}

		if (generateSouthPoint(x, y).getFoodUnits() > 0) {
			foodPresent[4] = true;
		}

		return foodPresent;
	}

	/**
	 * This method checks the neighboring cells are occupied, and returns an int
	 * array
	 * 
	 * @param currentCell
	 *            - the current cell
	 * @return - neighbors[] - array showing if organisms are present in the
	 *         neighboring cells
	 */
	public int[] checkNeighbors(Cell currentCell) {
		int neighbors[] = new int[5];
		neighbors[0] = 1;

		int x = currentCell.getPoint().getX();
		int y = currentCell.getPoint().getY();

		if (generateWestPoint(x, y).isOccupied() == true) {
			neighbors[1] = 1;
		}

		if (generateEastPoint(x, y).isOccupied() == true) {
			neighbors[2] = 1;
		}
		if (generateNorthPoint(x, y).isOccupied() == true) {
			neighbors[3] = 1;
		}

		if (generateSouthPoint(x, y).isOccupied() == true) {
			neighbors[4] = 1;
		}

		return neighbors;
	}

	/**
	 * This method returns the cell of the direction selected by the player
	 * 
	 * @param movement
	 *            - integer value of the movement selected
	 * @param currentCell
	 *            - the current cell
	 * @return Cell - the cell location of the selected movement
	 */
	public Cell checkDirection(int movement, Cell currentCell) {
		Cell newcell = null;
		if (movement == Constants.WEST) {
			newcell = generateWestPoint(currentCell.getPoint().getX(), currentCell.getPoint().getY());
		} else if (movement == Constants.EAST) {
			newcell = generateEastPoint(currentCell.getPoint().getX(), currentCell.getPoint().getY());
		} else if (movement == Constants.NORTH) {
			newcell = generateNorthPoint(currentCell.getPoint().getX(), currentCell.getPoint().getY());
		} else if (movement == Constants.SOUTH) {
			newcell = generateSouthPoint(currentCell.getPoint().getX(), currentCell.getPoint().getY());
		} else if (movement == Constants.STAYPUT) {
			newcell = currentCell;
		}

		return newcell;
	}

	/**
	 * This method prompts the user if human for direction. It provides all the
	 * necessary information
	 * 
	 * @param food
	 *            - the boolean[] of food presence in neighboring cells
	 * @param neighbors
	 *            - the int[] of if neighboring cells are occupied
	 * @param foodleft
	 *            - the amount of food left in the current cell
	 * @param energyleft
	 *            - the amount of energy left for the organism
	 */
	public void askForInput(boolean[] food, int[] neighbors, int foodleft, int energyleft) {

		System.out.println("You have the following options: ");
		System.out.println("0. Stay Put");
		System.out.println("1. Move West");
		System.out.println("2. Move East");
		System.out.println("3. Move North");
		System.out.println("4. Move South");
		System.out.println("5. Reproduce.");
		System.out.println(
				"If you choose to reproduce, you have to enter the direction you want the new organism to occupy space in.");
		System.out.println();
		System.out.println("Here is the information you have that can help you make your decision:");
		System.out.println("Food left on your cell: " + foodleft);
		System.out.println("Your energy left: " + energyleft);
		System.out.println("Food and neighbors (1 if present, 0 is empty):");
		for (int i = 0; i < food.length; i++) {
			System.out.println(Constants.DIRECTIONS[i] + " (Food, Neighbor): " + food[i] + ", " + neighbors[i]);

		}

	}

	/**
	 * This method returns the Cell on the west of the given cell
	 * 
	 * @param xCoordinate
	 *            - the x coordinate value of the current cell
	 * @param yCoordinate
	 *            - the y coordinate value of the current cell
	 * @return Cell - the cell on the west of the current cell
	 */
	private Cell generateWestPoint(int xCoordinate, int yCoordinate) {
		if (yCoordinate == 0) {
			yCoordinate = height - 1;
		} else {
			yCoordinate = yCoordinate - 1;
		}

		return grid[xCoordinate][yCoordinate];
	}

	/**
	 * This method returns the Cell on the east of the given cell
	 * 
	 * @param xCoordinate
	 *            - the x coordinate value of the current cell
	 * @param yCoordinate
	 *            - the y coordinate value of the current cell
	 * @return Cell - the cell on the east of the current cell
	 */
	private Cell generateEastPoint(int xCoordinate, int yCoordinate) {
		if (yCoordinate == height - 1) {
			yCoordinate = 0;
		} else {
			yCoordinate = yCoordinate + 1;
		}

		return grid[xCoordinate][yCoordinate];
	}

	/**
	 * This method returns the Cell on the north of the given cell
	 * 
	 * @param xCoordinate
	 *            - the x coordinate value of the current cell
	 * @param yCoordinate
	 *            - the y coordinate value of the current cell
	 * @return Cell - the cell on the north of the current cell
	 */
	private Cell generateNorthPoint(int xCoordinate, int yCoordinate) {
		if (xCoordinate == 0) {
			xCoordinate = width - 1;
		} else {
			xCoordinate = xCoordinate - 1;
		}

		return grid[xCoordinate][yCoordinate];
	}

	/**
	 * This method returns the Cell on the south of the given cell
	 * 
	 * @param xCoordinate
	 *            - the x coordinate value of the current cell
	 * @param yCoordinate
	 *            - the y coordinate value of the current cell
	 * @return Cell - the cell on the south of the current cell
	 */
	private Cell generateSouthPoint(int xCoordinate, int yCoordinate) {
		if (xCoordinate == width - 1) {
			xCoordinate = 0;
		} else {
			xCoordinate = xCoordinate + 1;
		}

		return grid[xCoordinate][yCoordinate];
	}

}
