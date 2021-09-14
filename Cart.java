package DroneDelivery;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
public class Cart {
	ArrayList<Integer> cartItems;
	ArrayList<Integer> quantity;
	int totalPrice;
	float totalWeight;
	int id;


	static BufferedReader ob = new BufferedReader(new InputStreamReader(System.in));

	public Cart(int id) {
		this.id = id;
		cartItems = new ArrayList<Integer>();
		quantity = new ArrayList<Integer>();
		totalPrice = 0;
		totalWeight = 0.0f;
	}


	int categoryMenu(ArrayList<String> category) throws NumberFormatException, IOException 
	{
		int choice = 0;
		System.out.print("\n------------------------------------------------------");
		System.out.print("\n\n\t\t***** SHOP-BY-CATEGORY *****");
		for(int i=0;i< category.size();i++)
		{
			System.out.print("\n "+(i+1)+". "+category.get(i));
		}
		System.out.print("\n Enter '0' to go back to MAIN MENU.");
		System.out.print("\n\n\tCHOOSE CATEGORY : ");
		do
		{
			choice = Integer.parseInt(ob.readLine());

			if(choice<0 || choice>category.size())
				System.out.print("\n\t***Enter a valid choice : ");

		}while(choice<0 || choice>category.size());
		System.out.print("\n------------------------------------------------------");

		return choice;
	}

	void ShopByCategory(Connection con) throws SQLException, NumberFormatException, IOException
	{
		int choice = 0, selectedProdCode = 0, i = 0, qty = 0;
		Statement state = con.createStatement();
		ArrayList<String> category = new ArrayList<String>();

		String Query = "SELECT DISTINCT Category FROM Products ORDER BY WarehouseNo";
		ResultSet res = state.executeQuery(Query);
		while (res.next())
		{
			category.add(res.getString("Category"));	
		}

		do
		{
			choice = categoryMenu(category);
			if(choice != 0)
			{
				String query = "SELECT id, Product, Price, Stock FROM Products WHERE Category='"+category.get(choice-1)+"'ORDER BY id";
				res = state.executeQuery(query);
				ArrayList<Integer> product = new ArrayList<Integer>();

				i = 0;
				System.out.print("\n-----------------------------------------------------------------------");
				System.out.print("\n\n\t\t***** PRODUCTs LIST *****");
				System.out.print("\n-----------------------------------------------------------------------");
				System.out.printf("\n SR. NO. \t\t ITEM NAME \t\t\t PRICE");
				System.out.print("\n-----------------------------------------------------------------------");
				while (res.next())
				{
					if(res.getInt("Stock") > 0)
					{
						System.out.printf("\n "+(++i)+"%40s %18d", res.getString("Product"), res.getInt("Price"));
						product.add(res.getInt("id"));
					}
				}
				System.out.print("\n Enter '0' to go back to CATEGORIES.");
				System.out.print("\n-----------------------------------------------------------------------");
				do
				{
					System.out.print("\n\n\tSELECT A PRODUCT TO ADD TO CART : ");
					do
					{
						selectedProdCode = Integer.parseInt(ob.readLine());

						if(selectedProdCode<0 || selectedProdCode>product.size())
							System.out.print("\n\t***Enter a valid choice : ");

					}while(selectedProdCode<0 || selectedProdCode>product.size());

					if(selectedProdCode != 0)
					{
						selectedProdCode = product.get(selectedProdCode - 1);
						ResultSet prod = state.executeQuery("SELECT Product, Price, Weight, Stock, WarehouseNo FROM Products WHERE id='"+selectedProdCode+"';");

						System.out.print("\n\n\t Quantity to be purchased = ");
						do
						{
							qty = Integer.parseInt(ob.readLine());

							if(qty > prod.getInt("Stock"))
								System.out.print("\n Only "+prod.getInt("Stock")+" items available");
						}while(qty > prod.getInt("Stock"));

						totalPrice += qty * prod.getInt("Price");
						totalWeight += qty * prod.getFloat("Weight");

						state.execute("UPDATE Products SET Stock = "+(prod.getInt("Stock") - qty)+" WHERE id='"+selectedProdCode+"';");
						cartItems.add(selectedProdCode);
						quantity.add(qty);
						System.out.print("\n\t***Item added successfully to cart.");

						System.out.print("\n-----------------------------------------------------------------------");
						System.out.print("\n\n Enter '0' to go back to CATEGORIES.");
					}
				}while(selectedProdCode != 0);
			}
		}while(choice != 0);


		if(totalPrice != 0)
		{
			addCustomer(con);
			generateBill(con);
		}

		state.close();

	}

	void addCustomer(Connection con) throws SQLException, IOException
	{
		Statement state = con.createStatement();
		System.out.print("\n\t ENTER DETAILS BELOW : ");
		System.out.print("\n\t\t Name :  ");
		String name= ob.readLine();
		System.out.print("\n\t\tContact No. :  ");
		long contact = Long.parseLong(ob.readLine());
		System.out.print("\n\t\tFor Delivery Address, Enter => ");
		System.out.print("\n\t\tCoordinate X :  ");
		int x = Integer.parseInt(ob.readLine());
		System.out.print("\n\t\tCoordinate Y :  ");
		int y = Integer.parseInt(ob.readLine());
		ResultSet res = state.executeQuery("select *from Customer ORDER BY id DESC LIMIT 1;");
		int id = res.getInt(1) + 1;

		String insQuery  = "INSERT into Customer(id,name,contact,X,Y,cartAmount,cartWeight) "
				+ "VALUES('"+id+"','"+name+"','"+contact+"','"+x+"','"+y+"','"+totalPrice+"','"+totalWeight+"');";
		state.executeUpdate(insQuery);
		state.close();
	}
	void generateBill(Connection con) throws SQLException
	{
		Statement state = con.createStatement();
		ResultSet res ; 
		System.out.print("\n------------------------------------------------------------------------------------------------");
		System.out.printf("\n SR.NO. \t\t\t ITEM NAME \t\t\t QTY \t\t AMT");
		System.out.print("\n------------------------------------------------------------------------------------------------");
		for(int i=0 ; i<cartItems.size() ; i++)
		{
			res = state.executeQuery("SELECT Product, Price FROM Products WHERE id='"+(cartItems.get(i) - 1)+"';");
			System.out.printf("\n %10d %40s %15d %15s",(i+1), res.getString("Product"), quantity.get(i), quantity.get(i)*res.getInt("Price"));
		}
		System.out.print("\n------------------------------------------------------------------------------------------------");
		System.out.print("\n\t\t\t TOTAL AMOUT = "+totalPrice);
		System.out.print("\n------------------------------------------------------------------------------------------------");
	}

	float getWeight()
	{
		return totalWeight;
	}

	ArrayList<Integer> getWarehouse(Connection con) throws SQLException
	{
		ArrayList<Integer> warehouses = new ArrayList<Integer>();
		Statement state = con.createStatement();
		ResultSet res;

		for(int i=0 ; i<cartItems.size() ; i++)
		{
			res = state.executeQuery("SELECT WarehouseNo form Products WHERE id='"+cartItems.get(i)+"';");

			warehouses.add(res.getInt("WarehouseNo"));
		}

		return warehouses;
	}
}