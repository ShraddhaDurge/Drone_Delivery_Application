# Drone_Delivery_Application
Drone Delivery System

1.Problem Statement: - Deliver a package taken from different warehouses to clients using drones.
2.Classes: -  
	Node: - To create node of drones, clients and warehouses using arguments x coordinate, y coordinate and type.
	DijkstrasAlgorithm: - To calculate shortest distance between starting node and end node, to find path followed by drone.
	InitalizeNodes(List<Node> nodes): - To initialize distance of all nodes from source node to infinity. Also set distance of source node to 0.
	CreateGraph(List<Node> nodes): -  To connect drone to warehouses, connect 2 warehouses, connect warehouses to clients and 2 clients. 
	calculateDistance(Node n1,Node n2): - To calculate distance between 2 nodes.
	getMin(List<Node> nodes): - To get node having minimum distance from source node.
	checkDistance(Node previous, Node next): - to check distance between two nodes.
	dijkstra(List<Node> nodes):-to calculate path followed by drone.
	ControlRoom: - To connect to Drone Database. To add and display drones, warehouses, products and customers table structure. To place and track a order by customer id.	
	Application:- Contains main method. Connects main menu to control room.
	Cart :-  To take a order from customer, generate bill, updates product’s stock, add customer details to the customer table structure for future reference.

2. Data Structure used: - 
Graph: A graph is a non-linear data structure defined as G= (V, E) where V is a finite set of vertices and E is a finite set of edges, such that each edge is a line or arc connecting any two vertices.
	Weighted graph: It is a special type of graph in which every edge is assigned a numerical value, called weight
	Connected graph: A path exists between each pair of vertices in this type of graph
	Spanning tree for a graph G is a subgraph G’ including all the vertices of G connected with minimum number of edges. Thus, for a graph G with n vertices, spanning tree G’ will have n vertices and maximum n-1 edges.
	Dijkstra’s Algorithm is a single-source shortest path algorithm for connected graphs.

HashMap: -  It provides the basic implementation of the Map interface of Java. It stores the data in (Key, Value) pairs. To access a value one must know its key. Few important features of HashMap are:
	HashMap is a part of java.util package.
	HashMap extends an abstract class AbstractMap which also provides an incomplete implementation of Map interface.
	It also implements Cloneable and Serializable interface. K and V in the above definition represent Key and Value respectively.
	HashMap doesn’t allow duplicate keys but allows duplicate values. That means A single key can’t contain more than 1 value but more than 1 key can contain a single value.
	HashMap allows null key also but only once and multiple null values.
	This class makes no guarantees as to the order of the map; in particular, it does not guarantee that the order will remain constant over time. It is roughly similar to HashTable but is unsynchronized.

3.Logic related to operations: -
Dijkstra’s Algorithm is implemented using the graph to find shortest path followed by drone while delivering a package containing different products from warehouse to client. If package has products from different warehouses, the drone that will deliver this order must pass through all three warehouses to pick the products and then go for delivery.
