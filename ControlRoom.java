package DroneDelivery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControlRoom {

	private static Connection con;

	int droneCount;		//no. of rows in Drone table
	int productCount;	//no. of rows in Products table
	int warehouseCount;	//no. of rows in Warehouse table
	int customerCount;	//no. of rows in Customer table

	static BufferedReader ob = new BufferedReader(new InputStreamReader(System.in));


	//this function connects the application to database : DroneDatabase
	void connectToDatabase() throws SQLException, ClassNotFoundException
	{

		if(con==null)
		{
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:DroneDatabase.db");

			con.setAutoCommit(true);
			System.out.print("\n\n\t****Drone Delivery System database opened successfully");
			
		}

		Statement state = con.createStatement();
		ResultSet res = state.executeQuery("select *from Drone ORDER BY id DESC LIMIT 1;");
		droneCount = res.getInt(1);
		res = state.executeQuery("select *from Products ORDER BY id DESC LIMIT 1;");
		productCount = res.getInt(1);
		res = state.executeQuery("select *from Warehouse ORDER BY id DESC LIMIT 1;");
		warehouseCount = res.getInt(1);

		state.close();
		res.close();

	}


	//places an order and adds order details to the database
	void placeOrder() throws IOException, SQLException
	{
		Cart c = new Cart(++customerCount);
		c.ShopByCategory(con);
	}

	//shows the path taken by drone to reach a customer
	void trackOrder() throws NumberFormatException, IOException, SQLException
	{
		List<Node> nodes = new ArrayList<>();
		DijkstrasAlgorithm d =new DijkstrasAlgorithm();
		Statement state = con.createStatement();
		ResultSet res;
		boolean flag = false;

		System.out.print("\n\t ENTER CUSTOMER ID :  ");
		int id = Integer.parseInt(ob.readLine());

		res = state.executeQuery("SELECT id, X, Y, cartWeight,Warehouse FROM Customer WHERE id='"+id+"';");
		float weight = res.getFloat("cartWeight");
		

		//to select drone for delivery and add drone node to the graph
		res = state.executeQuery("SELECT *from Drone ORDER BY Capacity");
		flag = false;
		while(res.next() && !flag)
		{
			if(weight <= res.getFloat("Capacity"))
			{
				Node drone = new Node(res.getInt("X"),res.getInt("Y"),'d',res.getInt("id"),"Drone : "+res.getString("model"));
				nodes.add(drone);
				flag=true;
			}
		}
		
		// to add warehouse node/s to the graph
		String s = res.getString("Warehouse").trim();
		String arr_s[]=s.split(" ");
		for(int i=0; i<arr_s.length ; i++)
		{
			int warehouseNo = Integer.parseInt(arr_s[i]);
			res = state.executeQuery("SELECT * FROM WareHouse WHERE id='"+warehouseNo+"';");

			Node wh = new Node(res.getInt("X"),res.getInt("Y"),'w',res.getInt("id"),res.getString("name"));
			nodes.add(wh);

		}
		//add Customer node to the graph
		Node Cust = new Node(res.getInt("X"),res.getInt("Y"),'c',res.getInt("id"), "Customer");
		nodes.add(Cust);
		flag=true;
		
		state.close();
		res.close();

		d.dijkstra(nodes);	
	}

	//Displays all Drones details
	void displayDroneDB() throws SQLException
	{
		Statement s=con.createStatement();
		ResultSet droneDB = s.executeQuery("SELECT * FROM Drone");
		System.out.print("\n-------------------------------------------------------------------------------------------------------------");
		System.out.print("\n\t CODE\t\t\t    MODEL NAME\t\t\tX \t Y \t CAPACITY");
		System.out.print("\n-------------------------------------------------------------------------------------------------------------");
		while(droneDB.next())
		{
			System.out.printf("\n %10d %40s %12d %8d %12f",droneDB.getInt("id"),
					droneDB.getString("model"),droneDB.getInt("X"),
					droneDB.getInt("Y"),droneDB.getFloat("Capacity"));
		}
		System.out.print("\n-------------------------------------------------------------------------------------------------------------");
		s.close();
		droneDB.close();
	}

	//Displays all warehouses details
	void displayWarehouseDB() throws SQLException
	{
		Statement s=con.createStatement();
		ResultSet warehouseDB = s.executeQuery("SELECT * FROM Warehouse");
	
		System.out.print("\n-------------------------------------------------------------------------------------------------------------");
		System.out.print("\n\t CODE \t\t WAREHOUSE NAME \t   X \t\t Y");
		System.out.print("\n-------------------------------------------------------------------------------------------------------------");
		while(warehouseDB.next())
		{
			System.out.printf("\n %10d %30s %9d %13d",warehouseDB.getInt("id"),
					warehouseDB.getString("name"),warehouseDB.getInt("X"),
					warehouseDB.getInt("Y"));
		}
		System.out.print("\n-------------------------------------------------------------------------------------------------------------");
		s.close();
		warehouseDB.close();
	}

	//Displays all Products details
	void displayProductDB() throws SQLException
	{
		Statement s=con.createStatement();
		ResultSet productDB = s.executeQuery("SELECT * FROM Products");
		System.out.print("\n--------------------------------------------------------------------------------------------------------------------------------------------------------");
		System.out.print("\n\t CODE \t\t\t PRODUCT NAME \t\t\t\t CATEGORY \t WAREHOUSE NO. \t\t WEIGHT \t PRICE \t\t STOCK");
		System.out.print("\n--------------------------------------------------------------------------------------------------------------------------------------------------------");
		while(productDB.next())
		{
			System.out.printf("\n %10d %40s %30s %15d %18s %15d %15d",productDB.getInt("id"),
					productDB.getString("Product"),productDB.getString("Category"),
					productDB.getInt("WarehouseNo"),productDB.getFloat("Weight"),
					productDB.getInt("Price"),productDB.getInt("Stock"));
		}
		System.out.print("\n--------------------------------------------------------------------------------------------------------------------------------------------------------");
		s.close();
		productDB.close();
	}


	//Displays all Customers details
	void displayCustomerDB() throws SQLException
	{
		Statement s=con.createStatement();
		ResultSet custDB = s.executeQuery("SELECT * FROM Customer");
	
		System.out.print("\n-----------------------------------------------------------------------------------------------------------------------------");
		System.out.print("\n\t CUST ID \t CUSTOMER NAME \t\t CONTACT \t COOR X \t COOR Y \tCART AMT \t CART WEIGHT");
		System.out.print("\n-----------------------------------------------------------------------------------------------------------------------------");
		while(custDB.next())
		{
			System.out.printf("\n %10d %25s %20s %12d %12d %16d %18s",custDB.getInt("id"),
					custDB.getString("name"),custDB.getString("contact"),
					custDB.getInt("X"), custDB.getInt("Y"), custDB.getInt("cartAmount"), custDB.getString("cartWeight"));
		}
		System.out.print("\n-----------------------------------------------------------------------------------------------------------------------------");
		s.close();
		custDB.close();
	}


	// Adds 'n' number of drones to Drone table
	void addDrones(int n) throws IOException, SQLException
	{
		Statement state = con.createStatement();
		float capacity = 0.0f;
		int id = 0;
		for(int i=0; i<n ; i++)
		{
			System.out.print("\n\t Enter :");

			System.out.print("\n\t\t FOR DRONE : "+(i+1));
			System.out.print("\n\t\t Model Name :");
			String model= ob.readLine();
			System.out.print("\n\t\t Capacity :");
			capacity =Float.parseFloat(ob.readLine());
			id = ++droneCount;

			String insQuery = "INSERT INTO Drone(id,model,X,Y,Capacity)"
					+ " VALUES('"+id+"','"+model+"','0', '0','"+ capacity+"');";
			state.executeUpdate(insQuery);

			System.out.print("\n Drone added to successfully to the database");
		}
		state.close();
	}

	// Adds 'n' number of Warehouses to Warehouse table
	void addWarehouse(int n) throws IOException, SQLException
	{
		Statement state=null;
		state = con.createStatement();	

		int x=0, y=0, id=0, choice = 2;
		for(int i=0; i<n ; i++)
		{
			System.out.print("\n\t Enter :");

			System.out.print("\n\t\t FOR Warehouse : "+(i+1));
			System.out.print("\n\t\t Warehouse Name :");
			String name= ob.readLine();
			System.out.print("\n\t\t Location : Coordinate (X) : ");
			x = Integer.parseInt(ob.readLine());
			System.out.print("\n\t\t Location : Coordinate (Y) : ");
			y = Integer.parseInt(ob.readLine());

			id = ++warehouseCount;

			String insQuery = "INSERT INTO Warehouse(id,name,X,Y)"
					+ " VALUES('"+id+"','"+name+"','"+x+"', '"+y+"');";
			state.executeUpdate(insQuery);
			System.out.print("\n Warehouse added to successfully to the database");

			System.out.print("\n\n  Do you want to add products in "+name+"?");
			System.out.print("\n Press 1, if YES "
					+ "\n Press 2, if NO");
			System.out.print("\n\t CHOICE = ");

			do
			{
				choice = Integer.parseInt(ob.readLine());

				if(choice<1 || choice>2)
					System.out.print("\n\t***Enter a Valid choice : ");

			}while(choice<1 || choice>2);

			if(choice == 1)
			{
				System.out.print("\n\tEnter Total Number of Products To Be Added :  ");
				int p = Integer.parseInt(ob.readLine());
				addProduct(p,warehouseCount);
			}


		}
		state.close();
	}

	// Adds 'n' number of Products to Products table
	void addProduct(int n) throws IOException, SQLException
	{
		Statement state=null;
		int wn=0, price=0, stock=0, id=0;
		float wt= 0.0f;
		state = con.createStatement();

		for(int i=0; i<n ; i++)
		{
			System.out.print("\n\t Enter :");

			System.out.print("\n\t\t FOR Product : "+(i+1));

			System.out.print("\n\t\t Product Name :");
			String prod= ob.readLine();

			System.out.print("\n\t\t Category :");
			String category= ob.readLine();

			System.out.print("\n\t\t Warehouse No. : ");
			do
			{
				wn = Integer.parseInt(ob.readLine());

				if(wn>warehouseCount)
					System.out.print("\n\t***Enter a Valid choice : ");

			}while(wn>warehouseCount);

			System.out.print("\n\t\t Weight : ");
			wt = Float.parseFloat(ob.readLine());

			System.out.print("\n\t\t Product Price : ");
			price = Integer.parseInt(ob.readLine());

			System.out.print("\n\t\t Stock Available : ");
			stock = Integer.parseInt(ob.readLine());

			id = ++productCount;

			String insQuery = "INSERT INTO Products(id,Product,Category,WarehouseNo,Weight,Price,Stock)"
					+ " VALUES('"+id+"','"+prod+"','"+category+"', '"+wn+"','"+ wt+"','"+price+"','"+stock+"');";
			state.executeUpdate(insQuery);
			System.out.print("\n Item added to successfully to the database");

		}
		state.close();
	}


	// Adds 'n' number of Products with warehouseNo = wn to Products table 
	void addProduct(int n, int wn) throws IOException, SQLException
	{
		Statement state=null;
		int price=0, stock=0, id=0;
		float wt= 0.0f;
		state = con.createStatement();

		for(int i=0; i<n ; i++)
		{
			System.out.print("\n\t Enter :");

			System.out.print("\n\t\t FOR Product : "+(i+1));

			System.out.print("\n\t\t Product Name :");
			String prod= ob.readLine();

			System.out.print("\n\t\t Category :");
			String category= ob.readLine();

			System.out.print("\n\t\t Weight : ");
			wt = Float.parseFloat(ob.readLine());

			System.out.print("\n\t\t Product Price : ");
			price = Integer.parseInt(ob.readLine());

			System.out.print("\n\t\t Stock Available : ");
			stock = Integer.parseInt(ob.readLine());

			id = ++productCount;

			String insQuery = "INSERT INTO Products(id,Product,Category,WarehouseNo,Weight,Price,Stock)"
					+ " VALUES('"+id+"','"+prod+"','"+category+"', '"+wn+"','"+ wt+"','"+price+"','"+stock+"');";
			state.executeUpdate(insQuery);
			System.out.print("\n Item added to successfully to the database");

		}
		state.close();
	}
	
	//closes Database connection
	void closeDB() throws SQLException
	{
		con.close();
	}
}