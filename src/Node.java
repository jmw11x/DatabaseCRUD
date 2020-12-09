import java.util.ArrayList;

public class Node {
	public ArrayList<Edge> edges;
	public ArrayList<String> items;
	public char value;
	public int sizeOf = 0;
	public int numItems=0;

	public Node(char value) {
		edges = new ArrayList<>();
		items = new ArrayList<>();
		this.value = value;
		sizeOf++;
	}
	public void addItems(String[] items) {
		for(String item : items) {
			this.items.add(item);
			numItems++;
		}
	}
	public int getSizeOfNodesCreated() {
		return this.sizeOf;
	}
	
	@Override
	public String toString() {
		return "Node [value=" + value + "]";
	}
	public boolean contains(char c) {
		for (int i = 0; i < this.edges.size(); i++) {
			if (this.edges.get(i).value == c) {
				return true;
			}
		}
		return false;
	}
}