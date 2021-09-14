package DroneDelivery;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class Node {

	public static final char TYPE_WAREHOUSE = 'W';
    public static final char TYPE_CLIENT = 'C';
    public static final char TYPE_DRONE = 'D';
    private char type;
   	private int x;
   	private int y;
   	private int id;
   	private String name;
    boolean showMoreInfo = false;
    double srcDistance = 0;
    static double TotalDist=0;

    public Map<Node, Double> neighbors = new HashMap<>();

    public Node(int x,int y,char type,int id,String name) {
		this.x = x;
		this.y=y;
		this.type=type;
		this.id=id;
		this.name=name;
    }
    
    public String getName() {
		return name;
	}

    public int getID() {
		return id;
	}
    
    public int getX() {
		return x;
	}
    
	public int getY() {
		return y;
	}
	public char getType() {
        return type;
    }
    
    public static double getTotalDistance() {
    	return TotalDist;
    }

	public double getDistance() {
		// TODO Auto-generated method stub
		return srcDistance;
	}
}

public class DijkstrasAlgorithm {
    
	    public double calculateDistance(Node n1,Node n2) 
	    {
			return Math.sqrt(Math.pow((n1.getX() - n2.getX()), 2))
					+ Math.sqrt(Math.pow((n1.getY() - n2.getY()), 2));
		}
    
	    List<Node> CreateGraph(List<Node> nodes) 
    	{
	    	List<Node> drones = new ArrayList<>();
	    	List<Node> warehouses = new ArrayList<>();
	    	List<Node> clients = new ArrayList<>();
	    	List<Node> nodesCopy = new ArrayList<>();
	    	
	    	for(Node node : nodes) {
	    		if(node.getType()==Node.TYPE_DRONE)
	    			drones.add(node);
	    		else if(node.getType()==Node.TYPE_WAREHOUSE)
	    				warehouses.add(node);
				else
					clients.add(node);
	    	}
	    	
	    	for(int i=0;i<drones.size();i++)  //connect drone to warehouses
	    	{
	    		for(int j=0;j<warehouses.size();j++)
	    		{
	    			drones.get(i).neighbors.put(warehouses.get(j),calculateDistance(drones.get(i),warehouses.get(j)));
	    		}
	    	}
	    	
	    	for(int i=0;i<warehouses.size()-1;i++) //Connect Warehouses
	    	{
	    		warehouses.get(i).neighbors.put(warehouses.get(i),calculateDistance(warehouses.get(i),warehouses.get(i+1)));
	    	}
	    	
	    	for(int i=0;i<warehouses.size();i++) //Connect Warehouses to clients
	    	{
	    		for(int j=0;j<clients.size();j++)
	    		{
	    			warehouses.get(i).neighbors.put(clients.get(j),calculateDistance(warehouses.get(i),clients.get(j)));
	    		}
	    	} 
	        
	        for(int i=0;i<clients.size();i++) //Connect clients to clients	
	    	{
	        	int a = (int) (Math.random() *clients.size() );  
	            int b = (int) (Math.random() * clients.size()); 
	    		clients.get(a).neighbors.put(clients.get(b),calculateDistance(clients.get(a),clients.get(b)));
	    	}
	        
	        for(Node node : nodes) 
	        {
	    		if(node.getType()==Node.TYPE_DRONE)
	    			nodesCopy.add(node);    
	    	}
	        for(Node node : nodes) 
	        {
	    		if(node.getType()==Node.TYPE_WAREHOUSE)
	    			nodesCopy.add(node);    
	    	}
	        for(Node node : nodes) 
	        {
	    		if(node.getType()==Node.TYPE_CLIENT)
	    			nodesCopy.add(node);    
	    	}
	        
	        return nodesCopy;
	        
	    }
    void initializeNodes(List<Node> nodes)
    {
        for(Node node:nodes)
            node.srcDistance = Integer.MAX_VALUE;
        nodes.get(0).srcDistance = 0;
    }
    
    void checkDistance(Node previous, Node next)
    {
        if(next.srcDistance > previous.srcDistance + previous.neighbors.get(next))
        {
            next.srcDistance = previous.srcDistance + previous.neighbors.get(next);
        }
    }
    
    List<Node> findWarehouseNode(List<Node> nodes)
    {
    	List<Node> wh =new ArrayList<>();
    	
        for(int i=0;i<nodes.size();++i)
        {
            if(nodes.get(i).getType()==Node.TYPE_WAREHOUSE)
            {
                wh.add(nodes.get(i));
            }
        }
       return wh;
    }
    
    Node getMin(List<Node> nodes)
    {
        double min = Double.MAX_VALUE; 
        int minIndex = -1;
        
        for(int i=0;i<nodes.size();++i)
        {
            if(nodes.get(i).srcDistance < min )
            {
                min = nodes.get(i).srcDistance; 
                minIndex = i;
            }
        }
        return nodes.get(minIndex);
    }
    
    public void dijkstra(List<Node> nodes)
    {
    	List<Node> nodesCopy = new ArrayList<>();
        
        nodesCopy = CreateGraph(nodes);
        initializeNodes(nodesCopy);
        List<Node> Warehouses = findWarehouseNode(nodesCopy);
  
    	while(!Warehouses.isEmpty())
        {
	        Node min = getMin(Warehouses);
	        
	        Map<Node, Double> neighbors = min.neighbors;
	        
	        for(Map.Entry<Node, Double> me:neighbors.entrySet())
	        {
	        	checkDistance(min, me.getKey());
	        }
	        Warehouses.remove(min);
	        nodesCopy.remove(min);
        }
    
    	while(!nodesCopy.isEmpty())
        {
	        Node min = getMin(nodesCopy);
	        
	        Map<Node, Double> neighbors = min.neighbors;
	        
	        for(Map.Entry<Node, Double> me:neighbors.entrySet())
	        {
	        	checkDistance(min, me.getKey());
	        }
	        nodesCopy.remove(min);
        }
    	
    	double TotalDist = 0.0f;
    	NumberFormat formatter = new DecimalFormat("#0.00");
    	System.out.print("\n------------------------------------------------------------------------------------------------");
    	System.out.println("\nSr No.\tName\t\tID\tLocation\tDistances");
    	System.out.print("\n------------------------------------------------------------------------------------------------\n");
    	
    	for(int i=0;i<nodes.size();i++)
    	{
    		  {
	    		if(i==0) {
	    		System.out.println(i +"\t"
	    	                    +nodes.get(i).getName() +"\t\t"
	    						+nodes.get(i).getID()+"\t("
	    						+nodes.get(i).getX()+","+nodes.get(i).getY()+")\t\t"
	    						+nodes.get(i).getDistance());
	    		}else {
	    			System.out.println(i +"\t"
		                    +nodes.get(i).getName() +"\t"
							+nodes.get(i).getID()+"\t("
							+nodes.get(i).getX()+","+nodes.get(i).getY()+")\t\t"
							+nodes.get(i).getDistance());
	    		}
    		 }
    	}
    	System.out.print("\n------------------------------------------------------------------------------------------------");
    	System.out.print("\n\tPATH FOLLOWED BY DRONE =>  \n\t\t");
    	for(int i=0;i<nodes.size();i++) 
    	{
    		TotalDist = TotalDist+ nodes.get(i).getDistance() ;
    		if(i<nodes.size()-1)
    			System.out.print(nodes.get(i).getName()+"("
						+nodes.get(i).getX()+","+nodes.get(i).getY()+")"  +" -> ");
    		else {
    			System.out.print(nodes.get(i).getName()+"("
						+nodes.get(i).getX()+","+nodes.get(i).getY()+")");
  
    			System.out.println("\n\n\tSHORTEST DISTANCE FOLLWED BY DRONE :-  "+formatter.format(TotalDist)+" KM");
    			
    		}
    		
    	}
    	
    }
}