import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
/**
 * Utilizes Node and Edge class with stock.txt as inventory;  '`' marks out of stock in txt file
 * Vertexes are converted to an adjacancy matrix depandant on number of verticies;
 *
 * Takes a list of isles needed to be visited and outputs if it takes too long to get in and out 
 * of the store.
 *
 * @author Jacob Wilson
 *
 */
public class Main {
	
	//Static variables needed for helper methods.
	private static int count = 0;// line number on in the file of inventory items
	private static final int N = 4;// number of verticies
	private static List<String> list = new ArrayList<>();// represents inventory in stock
	private static int[][] adjacancyMatrix = new int[N][N];
	private static Set<Character> vertexList = new HashSet<>();
	private static int[][] visitMatrix = new int[(int) Math.pow(2, N)][N];
	private static int allVisited = (1 << N) - 1;// all bits are set...

	public static void main(String[] args) throws IOException{
		Scanner k = new Scanner(System.in);

		//Collect inupt
		System.out.print("How many minutes are you willing to spend in the store? ");
		int limit = k.nextInt();
		System.out.println();
		k.close();

		// Initialize arrays to -1 for unvisited and 0 for default only self node.
		for (int i = 0; i < (int) Math.pow(2, N); i++) {
			for (int j = 0; j < N; j++) {
				visitMatrix[i][j] = -1;
			}
		}

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				adjacancyMatrix[i][j] = 0;
			}
		}

		// Assign inventory
		assignItems(list);

		Random r = new Random();
		// Create nodes and edges and random values between 1 and 10 to all edges.
		int aTb = 1+r.nextInt(10);
		int aTc = 1+r.nextInt(10);
		int aTd = 1+r.nextInt(10);
		int bTd = 1+r.nextInt(10);
		int bTc = 1+r.nextInt(10);
		int cTd = 1+r.nextInt(10);
		Node node = new Node('a');
		addToV(node);
		Node node1 = new Node('b');
		addToV(node1);
		Node node2 = new Node('c');
		addToV(node2);
		Node node3 = new Node('d');
		addToV(node3);

		// Assign random values to edges to show random traffic throughout the day
		// a
		node.edges.add(new Edge(node1, aTb));
		node.edges.add(new Edge(node2, aTc));
		node.edges.add(new Edge(node3, aTd));
		addToMatrix(node, 0);
		// c
		node2.edges.add(new Edge(node1, bTc));
		node2.edges.add(new Edge(node, aTc));
		node2.edges.add(new Edge(node3, cTd));
		addToMatrix(node2, 2);
		// b
		node1.edges.add(new Edge(node, aTb));
		node1.edges.add(new Edge(node2, bTc));
		node1.edges.add(new Edge(node3, bTd));
		addToMatrix(node1, 1);
		// d
		node3.edges.add(new Edge(node1, bTd));
		node3.edges.add(new Edge(node2, cTd));
		node3.edges.add(new Edge(node, aTd));
		addToMatrix(node3, 3);

		//Create random list for shopping: Could modify to input but to handle exceptions I will keep it simple
		List<String> shoppingList = new ArrayList<>();
		shoppingList.add("lettuce");
		shoppingList.add("water");
		shoppingList.add("canned foods");
		shoppingList.add("turkey");
		shoppingList.add("chicken");
		shoppingList.add("cleaner");
		shoppingList.add("bread");
		shoppingList.add("pasta");
		shoppingList.add("coffee");

		//check stock do not add out of stock items to vertex list
		for (String items : shoppingList) {
			if (node.items.contains(items) || node.items.contains(items + "`")) {
				vertexList.add(node.value);
			} else if (node1.items.contains(items) || node1.items.contains(items + "`")) {
				vertexList.add(node1.value);
			} else if (node2.items.contains(items) || node2.items.contains(items + "`")) {
				vertexList.add(node2.value);
			} else if (node3.items.contains(items) || node3.items.contains(items + "`")) {
				vertexList.add(node3.value);
			}
		}
		
		//how long will it take?
		int time = 0;
		if(vertexList.size()<4){
			//adjust list sizes
			allVisited = genMask(vertexList);
			updateMatrix();
			visitMatrix = new int[(int)Math.pow(2,adjacancyMatrix.length)][vertexList.size()];
			for(int i =0; i<(int)Math.pow(2,vertexList.size());i++){
				for(int j = 0; j<vertexList.size();j++){
					visitMatrix[i][j]=-1;
				}
			}
			time = shop(1, 0);
		}else {
			time = shop(1, 0);
		}
		
		// Worth it?
		if (time > limit) {
			System.out.print("too much exposure do not enter! It would take: ");
			System.out.println(time + " minutes");
			System.exit(0);
		}
		
		//current isle inventory as follows
		System.out.println("Isle1: " + node.items);
		System.out.println("Isle2: " + node1.items);
		System.out.println("Isle3: " + node2.items);
		System.out.println("Isle4: " + node3.items);
		System.out.println();
		
		//current traffic of store represented as a matrix for easier computations
		System.out.println("This store at this time can be represented by an adjacancy matrix as shown below..");
		printMatrix();
		System.out.println();

		//What do we need and where do we need to go
		System.out.println("The shopper needs: " + shoppingList);
		System.out.print("The shopper needs to visit isles: ");
		System.out.println(vertexList);
		System.out.println();

		//What was time taken?
		System.out.print("They take: " + time + " minutes\n");
			
		//What can they not get
		checkStock(shoppingList);
		double avg = 0;
		for (int i = 0; i < 100; i++){
			avg+=shop(1,0);
		}
		System.out.println(avg/100);
	}

	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;END MAIN PROC;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;END MAIN PROC;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;END MAIN PROC;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

	/**
	 * Alters size of adjacancy matrix if not all isles need visited
	 *
	 **/
	private static int[][] updateMatrix(){
		int newAdj[][]=new int[vertexList.size()][vertexList.size()];
		int l =0;
		int m = 0;
		for(int i = 0; i<N;i++){
			if(!vertexList.contains(((char)(i+97)))){
				continue;
			}
			for(int j = 0; j<N;j++){
				if(vertexList.contains(((char)(j+97)))){
					newAdj[l][m]=adjacancyMatrix[i][j];
					m++;
				}else{
					continue;
				}
			}
			l++;
			m=0;
		}
		return adjacancyMatrix=newAdj;
	}
	/**
	 *Generate bit mask corresponding to the isles needed
	 **/ 
	private static int genMask(Set<Character> s){
		int m = 0;
		for(char c : s){
			if(c=='a'){
				m|=1<<0;
			}else if(c=='b'){
				m|=1<<1;
			}else if(c == 'c'){
				m|=1<<2;
			}else if(c=='d'){
				m|=1<<3;
			}
		}
//I KNOW THIS NEXT LINE IS UGLY
		if(vertexList.size()==3) {
			m=7;
		}
		return m;
	}

	/**
	 * Check to make sure all items on list are in stock or not. Displays those that
	 * are not.
	 * 
	 * @param shoppingList
	 */
	private static void checkStock(List<String> shoppingList) {
		try {
			boolean allGood = true;
			System.out.print("Unfourtunatly we do not have the following items on your list: ");
			BufferedReader rd = new BufferedReader(new FileReader("stock.txt"));
			String currentLine = null;
			while ((currentLine = rd.readLine()) != null) {
				if (currentLine.contains("`")
						&& shoppingList.contains(currentLine.substring(0, currentLine.length() - 1))) {
					System.out.print(currentLine.substring(0, currentLine.length() - 1) + " ");
					allGood = false;
				}
			}
			if (allGood) {
				System.out.println("we got it all!");
			}
			System.out.println();
			rd.close();
		} catch (IOException e) {
			System.out.println("Exception loading words.txt.");
		}
	}
	/**
	 * Add Node objects and edges to a matrix utalizing Ascii values and C style character manipulation
	 **/ 
	private static void addToMatrix(Node node, int index) {
		for (Edge e : node.edges) {
			int i = node.value - 97;
			int j = e.end.value - 97;
			adjacancyMatrix[i][j] = e.weight;
		}
	}

	/**
	 * adds 10 items from the file stock.txt to a Nodes item list attribute.
	 * keeps track with variable count which line we are on for the next 10 items
	 * 
	 * @param n
	 */
	private static void addToV(Node n) {
		String[] s = new String[10];
		if (count > 0) {
			s[0] = list.get(count);
			count++;
		} else {
			s[0] = list.get(0);
			count++;
		}
		int i = 1;
		while (count % 10 != 0) {
			if (count + 1 >= list.size() - 1 || list.get(count) == null)
				break;
			s[i] = list.get(count);
			i++;

			count++;
		}
		n.addItems(s);
	
	}
	
	/**
	 * prints the current adjacancy matrix
	 */
	private static void printMatrix(  ) {
		for (int i = 0; i < vertexList.size(); i++) {
			for (int j = 0; j < vertexList.size(); j++) {
				System.out.print(adjacancyMatrix[i][j] + " ");
			}
			System.out.println();
		}
	}

	/**
	 * Assigns items to a list to keep track of total inventory
	 * 
	 * @param l
	 */
	private static void assignItems(List<String> l) {
		try {
			BufferedReader rd = new BufferedReader(new FileReader("stock.txt"));
			String currentLine = null;
			while ((currentLine = rd.readLine()) != null) {
				if (currentLine.contains("!")) {
					continue;
				} else {
					l.add(currentLine);
				}
			}
			rd.close();
		} catch (IOException e) {
			System.out.println("Exception loading words.txt.");
		}
	}
	private static void printVisit(int[][] a){
		for(int i =0; i<(int)Math.pow(2,N);i++){
			System.out.print("[");
			for(int j=0; j<N;j++){
				System.out.print(a[i][j]+",");
			}
			System.out.println();
		}
	}	

	/*
	 * TSP Algorithm using bitmasking, recursion for a more elagant look, optimizied for speed using a memoized matrix(DP)
	 * ;;;;;;;; Basically an optimized DFS...... 
	 * assume we start from 0...01 where each bit represents if that spot has been
	 * visited.
	 *
	 * Makes use of adjacancy matrix and the memoization matrix visitMatix; 
	 * Size of those is N*N and 2^N*N respectivly
	 * 
	 * The project assumes customers know shortest distance so path is
	 * irrelelavent.. more about predicting how long it will take them to get in and
	 * out. Distance is relative to traffic in the store, not physical distance.
	 *
	 * So to the textbooks Intro to algo, and algo design manual, Data Structures here at GSU, Abdul Bari, Tushar Roy and MIT open courseware on youtube; 
	 * and as always, geeks for geeks for the tutorials and text on how to implement this algorithm efficiently!
	 */
	private static int shop(int m, int location) {
		//done or need to go to the next path
		if (m == allVisited) {
			return adjacancyMatrix[location][0];
		}else if (visitMatrix[m][location] != -1) {
			return visitMatrix[m][location];
		}else{
			int min = Integer.MAX_VALUE;
			for (int isles = 0; isles < adjacancyMatrix.length; isles++) {
				if ((m & (1 << isles)) == 0) {
					//not visited
					//check new meen and keep going till we finish this path and hit all items. add where we are to the next unexplored vertex down this path.
					int continuedPath = adjacancyMatrix[location][isles] + Math.abs(shop(m | (1 << isles), isles));
					//there can never be a negative path
					min = (min<continuedPath) ? Math.abs(min) : Math.abs(continuedPath);
				}
			}
			visitMatrix[m][location]=min;
			//return min from stack
			return visitMatrix[m][location];
		}
	}

}
