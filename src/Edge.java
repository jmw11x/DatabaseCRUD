public class Edge {
	public Node end;
	public int weight;
	public char value;

	public Edge(Node end, int weight) {
		this.value = end.value;
		this.end = end;
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "Edge [end=" + end + ", weight=" + weight + ", value=" + value + "]";
	}
	
}