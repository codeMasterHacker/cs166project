/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.Date;
import java.text.*;

/*
* This class defines a simple embedded SQL utility class that is designed to work with PostgreSQL JDBC drivers.
*/

public class DBproject
{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException 
	{
		System.out.print("Connecting to database...");
		
		try
		{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        	this._connection = DriverManager.getConnection(url, user, passwd);
	        	System.out.println("Done");
		}
		catch(Exception e)
		{
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        	System.out.println("Make sure you started postgres on this machine");
	        	System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate(String sql) throws SQLException 
	{
		Statement stmt = null;

		try 
		{
			// creates a statement object
			stmt = this._connection.createStatement();

			// issues the update instruction
			stmt.executeUpdate(sql);
		} 
		catch (Exception e) 
		{
			System.err.println("Error - Unable to Execute Update: " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			// close the instruction
			if (stmt != null)
				stmt.close();
		}
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult(String query) throws SQLException 
	{
		Statement stmt = null;
		int rowCount = 0;

		try 
		{
			//creates a statement object
			stmt = this._connection.createStatement();

			//issues the query instruction
			ResultSet rs = stmt.executeQuery(query);
			
			/*
			*  obtains the metadata object for the returned result set.  The metadata
			*  contains row and column info.
			*/
			ResultSetMetaData rsmd = rs.getMetaData();
			int numCol = rsmd.getColumnCount();
		
			//iterates through the result set and output them to standard out.
			boolean outputHeader = true;
			while (rs.next())
			{
				if(outputHeader)
				{
					for(int i = 1; i <= numCol; i++)
					{
						System.out.print(rsmd.getColumnName(i) + "\t");
			    	}

			    	System.out.println();
			    	outputHeader = false;
				}

				for (int i=1; i<=numCol; ++i)
					System.out.print (rs.getString(i) + "\t");

				System.out.println();
				++rowCount;
			}//end while
		} 
		catch (Exception e) 
		{
			System.err.println("Error - Unable to Execute Query: " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (stmt != null)
				stmt.close();
		}

		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult(String query) throws SQLException 
	{
		Statement stmt = null;
		List<List<String>> result = new ArrayList<List<String>>(); 

		try 
		{
			// creates a statement object
			stmt = this._connection.createStatement();

			//issues the query instruction 
			ResultSet rs = stmt.executeQuery(query); 
	 
			/*
		 	* obtains the metadata object for the returned result set.  The metadata 
		 	* contains row and column info. 
			*/ 
			ResultSetMetaData rsmd = rs.getMetaData(); 
			int numCol = rsmd.getColumnCount(); 
	 
			//iterates through the result set and saves the data returned by the query.
			while (rs.next())
			{
				List<String> record = new ArrayList<>(); 
			
				for (int i=1; i<=numCol; ++i) 
					record.add(rs.getString(i)); 
			
				result.add(record); 
		
			}//end while 
		} 
		catch (Exception e) 
		{
			System.err.println("Error - Unable to Execute Query: " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			// close the instruction
			if (stmt != null)
				stmt.close();
		}

		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery(String query) throws SQLException 
	{
		//creates a statement object
		Statement stmt = null;
		int rowCount = 0;

		try 
		{
			stmt = this._connection.createStatement();
			
			//issues the query instruction
			ResultSet rs = stmt.executeQuery(query);

			//iterates through the result set and count nuber of results.
			while (rs.next())
			{
				rowCount++;
			}//end while
		} 
		catch (Exception e) 
		{
			System.err.println("Error - Unable to Execute Query: " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (stmt != null)
				stmt.close();
		}

		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException 
	{
		Statement stmt = null;
		ResultSet rs = null;

		boolean hasNext = false;

		try
		{
			stmt = this._connection.createStatement();

			rs = stmt.executeQuery(String.format("Select currval('%s')", sequence));

			if (rs.next())
				hasNext = true;
		}
		catch (Exception e) 
		{
			System.err.println("Error - Unable to Get Current Sequel Value: " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (stmt != null)
				stmt.close();
		}

		if (hasNext)
			return rs.getInt(1);
		else
			return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup()
	{
		try
		{
			if (this._connection != null)
			{
				this._connection.close ();
			}//end if
		}
		catch (SQLException e)
		{
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) 
	{
		if (args.length != 3) 
		{
			System.err.println("Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () + " <dbname> <port> <user>");
			return;
		}//end if
		
		DBproject esql = null;
		
		try
		{
			System.out.println("(1)");
			
			try 
			{
				Class.forName("org.postgresql.Driver");
			}
			catch(Exception e)
			{

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new DBproject (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon)
			{
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Ship");
				System.out.println("2. Add Captain");
				System.out.println("3. Add Cruise");
				System.out.println("4. Book Cruise");
				System.out.println("5. List number of available seats for a given Cruise.");
				System.out.println("6. List total number of repairs per Ship in descending order");
				System.out.println("7. Find total number of passengers with a given status");
				System.out.println("8. < EXIT");
				
				switch (readChoice())
				{
					case 1: AddShip(esql); break;
					case 2: AddCaptain(esql); break;
					case 3: AddCruise(esql); break;
					case 4: BookCruise(esql); break;
					case 5: ListNumberOfAvailableSeats(esql); break;
					case 6: ListsTotalNumberOfRepairsPerShip(esql); break;
					case 7: FindPassengersCountWithStatus(esql); break;
					case 8: keepon = false; break;
					default: keepon = false; break;
				}
			}
		}
		catch(Exception e)
		{
			System.err.println (e.getMessage ());
		}
		finally
		{
			try
			{
				if(esql != null) 
				{
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}
			catch(Exception e)
			{
				// ignored.
			}
		}
	}

	public static int readChoice() 
	{
		int input;

		// returns only if a correct value is given.
		do 
		{
			System.out.print("Please make your choice: ");

			try //read the integer, parse it and break.
			{ 
				input = Integer.parseInt(in.readLine());
				break;
			}
			catch (Exception e) 
			{
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}
		while (true);

		return input;
	}//end readChoice

	public static void AddShip(DBproject esql) //1
	{
		//Add Ship: Ask the user for details of a Ship and add it to the DB
		String make = "";
		String model = "";
		int age = -1;
		int seats = -1;
		int id = 0;

		System.out.println("Enter the information for a new ship: ");

		try
		{
			String query = "SELECT Ship.id\n" +
					"FROM Ship;";

			id = esql.executeQuery(query) + 1;
		}
		catch (Exception e)
		{       
			System.out.println(e.getMessage());
                        System.out.println("Error...terminating command");
                        return;
		}

		while (true)
		{
			System.out.print("Enter make: ");

			try
			{
				make = in.readLine();

				for (int i = 0; i < make.length(); i++)
				{
					if (!Character.isLetter(make.charAt(i)))
						throw new Exception("Not a string!");
				}

				break;
			}
			catch (Exception e)
			{
				System.out.println("Input must be a string!");
			}
		}

		while (true)
		{
			System.out.print("Enter model: ");

			try
			{
				model = in.readLine();

				for (int i = 0; i < model.length(); i++)
                                {
                                        if (!Character.isLetter(model.charAt(i)))
                                                throw new Exception("Not a string!");
                                }

				break;
			}
			catch (Exception e)
			{
				System.out.println("Input must be a string!");
			}
		}

		do
		{
			System.out.print("Enter a nonnegative age: ");

			try
			{
				age = Integer.parseInt(in.readLine());
			}
			catch (Exception e)
			{
				System.out.println("Input must be an integer!");
			}
		}
		while (age < 0);

		do
		{
			System.out.print("Enter a nonnegative number of seats: ");

			try
			{
				seats = Integer.parseInt(in.readLine());
			}
			catch (Exception e)
			{
				System.out.println("Input must be an integer!");
			}
		}
		while(seats < 0);

		System.out.println("Inserting information into the DB");

		try
		{
			String query = String.format("INSERT INTO Ship\n" +
							"VALUES ('%d', '%s', '%s', '%d', '%d');",
							id, make, model, age, seats);
			
			esql.executeUpdate(query);

			System.out.println(String.format("Successfully inserted the record: (id:%d, make:%s, model:%s, age:%d, seats:%d)",
							id, make, model, age, seats));
		}

		catch (Exception e)
		{
			System.out.println(e.getMessage());
			System.out.println("Insertion of information into the DB was UNSUCCESSFULL");
		}
	}

	public static void AddCaptain(DBproject esql) //2
	{
		//Add Captain: Ask the user for details of a Captain and add it to the DB
		String fullname = "";
		String nationality = "";
		int id = 0;

		System.out.println("Enter the information for the new Captain:");

		try
		{       
			String query = "SELECT Captain.id\n" +
                                        "FROM Captain;";
                                
                        id = esql.executeQuery(query) + 1;
                }       
                catch (Exception e)
                {       
			System.out.println(e.getMessage());
                        System.out.println("Error...terminating command");
                        return;
                }

		while (true)
		{
			System.out.print("Enter fullname: ");

			try
			{
				fullname = in.readLine();

				for (int i = 0; i < fullname.length(); i++)
                                {
                                        if (!Character.isLetter(fullname.charAt(i)) && !Character.isSpaceChar(fullname.charAt(i)))
                                                throw new Exception("Not a string!");
                                }

				break;
                	}
                	catch (Exception e)
                	{
				System.out.println("Input must be a string!");
                	}
		}

		while (true)
		{

			System.out.print("Enter naionality: ");

			try
			{
				nationality = in.readLine();

				for (int i = 0; i < nationality.length(); i++)
                                {
					if (!Character.isLetter(nationality.charAt(i)) && !Character.isSpaceChar(nationality.charAt(i)))
                                                throw new Exception("Not a string!");
                                }
				
				break;
			}
			catch (Exception e)
			{
				System.out.println("Input must be a string!");
			}
		}

		System.out.println("Inserting information into the DB");

		try
		{
			String query = String.format("INSERT INTO Captain\n" +
							"VALUES ('%d', '%s', '%s');",
							id, fullname, nationality);
			
			esql.executeUpdate(query);

			System.out.println(String.format("Successfully inserted the record: (id: %d, fullname:%s, nationality:%s)",
							id, fullname, nationality));
		}

		catch (Exception e)
		{
			System.out.println(e.getMessage());
			System.out.println("Insertion of information into the DB was UNSUCCESSFULL");
		}
	}	

	public static void AddCruise(DBproject esql) //3
	{
		//Add Cruise: Ask the user for details of a Cruise and add it to the DB
		int cost = -1;
		int num_sold = -1;
		int num_stops = -1;
		String actual_departure_date = "";
		String actual_arrival_date = "";
		String arrival_port = "";
		String departure_port = "";
		int cnum = 0;

		System.out.println("Enter the information for the new Cruise:");

		try
		{
			String query = "SELECT Cruise.cnum\n" +
                                        "FROM Cruise;";

                        cnum = esql.executeQuery(query) + 1;
		}
                catch (Exception e)
                {
			System.out.println(e.getMessage());
                        System.out.println("Error...terminating command");
                        return;
             	}

		do
		{
			System.out.print("Enter a nonnegative cost: ");

			try
			{
				cost = Integer.parseInt(in.readLine());
			}
			catch (Exception e)
			{
				System.out.println("Input must be an integer!");
			}
		}
		while (cost < 0);

		do
		{
			System.out.print("Enter a nonnegative number of tickets sold: ");

			try
			{
				num_sold = Integer.parseInt(in.readLine());
			}
			catch (Exception e)
			{
				System.out.println("Input must be an integer!");
			}
		}
		while (num_sold < 0);

		do
		{
			System.out.print("Enter a nonnegative number of stops: ");

			try
			{
				num_stops = Integer.parseInt(in.readLine());
			}
			catch (Exception e)
			{
				System.out.println("Input must be an integer!");
			}
		}
		while (num_stops < 0);

		int year = -1;
		do
		{
			System.out.print("Enter a departure year between 2014 and 2021: ");

			try
                        {
                                year = Integer.parseInt(in.readLine());
                        }
                        catch (Exception e)
                        {
                                System.out.println("Input must be an integer!");
                        }
		}
		while (year < 2014 || year > 2021);

		int m = -1;
                do
                {
                        System.out.print("Enter a departure month(1 <-> 12): ");

                        try
                        {
                                m = Integer.parseInt(in.readLine());
                        }
                        catch (Exception e)
                        {
                                System.out.println("Input must be an integer!");
                        }
                }
                while (m < 1 || m > 12);
		String month = (m < 10) ? "0"+Integer.toString(m) : Integer.toString(m);

		int d = -1;
                do
                {
                        System.out.print("Enter a departure day(1 <-> 31): ");

                        try
                        {
                                d = Integer.parseInt(in.readLine());
                        }
                        catch (Exception e)
                        {
                                System.out.println("Input must be an integer!");
                        }
                }
                while (d < 1 || d > 31);
                String day = (d < 10) ? "0"+Integer.toString(d) : Integer.toString(d);

		actual_departure_date = year + "-" + month + "-" + day;

		year = -1;
                do
                {
                        System.out.print("Enter an arrival year between 2014 and 2021: ");

                        try
                        {
                                year = Integer.parseInt(in.readLine());
                        }
                        catch (Exception e)
                        {
                                System.out.println("Input must be an integer!");
                        }
                }
                while (year < 2014 || year > 2021);

                m = -1;
                do
                {
                        System.out.print("Enter an arrival month(1 <-> 12): ");

                        try
                        {
                                m = Integer.parseInt(in.readLine());
                        }
                        catch (Exception e)
                        {
                                System.out.println("Input must be an integer!");
                        }
                }
                while (m < 1 || m > 12);
                month = (m < 10) ? "0"+Integer.toString(m) : Integer.toString(m);

                d = -1;
                do
                {
                        System.out.print("Enter an arrival day(1 <-> 31): ");

                        try
                        {
                                d = Integer.parseInt(in.readLine());
                        }
                        catch (Exception e)
                        {
                                System.out.println("Input must be an integer!");
                        }
                }
                while (d < 1 || d > 31);
                day = (d < 10) ? "0"+Integer.toString(d) : Integer.toString(d);

                actual_arrival_date = year + "-" + month + "-" + day;

		while (true)
		{
			System.out.print("Enter an arrival port: ");

			try
			{
				arrival_port = in.readLine();
	
				for (int i = 0; i < arrival_port.length(); i++)
                                {
                                        if (!Character.isLetter(arrival_port.charAt(i)))
                                                throw new Exception("Not a string!");
                                }
			
				break;
			}
			catch (Exception e)
			{
				System.out.println("Input must be a string!");
			}
		}

		while (true)
		{
			System.out.print("Enter a departure port: ");

			try
			{
				departure_port = in.readLine();

				for (int i = 0; i < departure_port.length(); i++)
                                {
                                        if (!Character.isLetter(departure_port.charAt(i)))
                                                throw new Exception("Not a string!");
                                }
				
				break;
			}
			catch (Exception e)
			{
				System.out.println("Input must be a string!");
			}
		}

		System.out.println("Inserting information into the DB");

		try
		{
			String query = String.format("INSERT INTO Cruise\n" +
							"Values ('%d', '%d', '%d', '%d', '%s', '%s', '%s', '%s');",
							cnum, cost, num_sold, num_stops, actual_departure_date, actual_arrival_date, arrival_port, departure_port);

			esql.executeUpdate(query);

			System.out.println(String.format("Successfully inserted the record: (cnum:%d, cost:%d, num_sold:%d, num_stops:%d, actual_departure_date:%s, actual_arrival_date:%s, arrival_port:%s, departure_port:%s)",
							cnum, cost, num_sold, num_stops, actual_departure_date, actual_arrival_date, arrival_port, departure_port));	
		}

		catch (Exception e)
		{
			System.out.println(e.getMessage());
			System.out.println("Insertion of information into the DB was UNSUCCESSFULL");
		}		
	}

	public static void BookCruise(DBproject esql) //4
	{
		// Given a customer and a Cruise that he/she wants to book, add a reservation to the DB
		//Given a customer and Cruise that he/she wants to book, determine the status of the reservation (Waitlisted/Confirmed/Reserved) and add the reservation to the database with appropriate status.
		/*
		CREATE TABLE Reservation
		(
			rnum INTEGER NOT NULL,
			ccid INTEGER NOT NULL,
			cid INTEGER NOT NULL,
			status _STATUS,
			PRIMARY KEY (rnum),
			FOREIGN KEY (ccid) REFERENCES Customer(id),
			FOREIGN KEY (cid) REFERENCES Cruise(cnum)
		);
		*/

		int rnum = 0;

		try
		{
			String query = "SELECT Reservation.rnum\n" +
					"FROM Reservation;";

			rnum = esql.executeQuery(query) + 1;
		}
		catch (Exception e)
                {
                        System.out.println(e.getMessage());
			System.out.println("Error...terminating command");
			return;
                }

		int customerNumber = -1;
                while (true)
                {
			System.out.print("Enter a nonnegative customer number: ");

                        try
                        {
                                customerNumber = Integer.parseInt(in.readLine());

				if (customerNumber < 0)
				{
					System.out.println("Customer number is negative, incorrect input!");
					continue;
				}

				String query = "SELECT Customer.id\n" +
						"FROM Customer\n" +
						"WHERE Customer.id = " + customerNumber + ";";

				if (esql.executeQuery(query) == 0)
                                {
                                        System.out.println("The record with Customer Number " + customerNumber + " does not exist.");
                                        continue;
                                }

				break;
                        }
                        catch (NumberFormatException e)
                        {
                                System.out.println("Input must be an integer!");
                        }
			catch (SQLException e)
                        {
                                System.out.println("There are no records associated with that number!");
                        }
			catch (IOException e)
			{
				System.out.println(e.getMessage());
				System.out.println("IO Error...terminating command");
                        	return;
			}
                }

		int cruiseNumber = -1;
		while (true)
		{
			System.out.print("Enter a nonnegative cruise number: ");
                        
			try
			{        
                                cruiseNumber = Integer.parseInt(in.readLine());

                                if (cruiseNumber < 0)
                                {       
                                        System.out.println("Cruise number is negative, incorrect input!");
                                        continue;
                                }       
                                
                                String query = "SELECT Cruise.cnum\n" +
                                                "FROM Cruise\n" +
                                                "WHERE Cruise.cnum = " + cruiseNumber + ";";
                                                
                                if (esql.executeQuery(query) == 0)
                                {       
                                        System.out.println("The record with Cruise Number " + cruiseNumber + " does not exist.");
                                        continue;
                                }

				break;
                                
                        }
                        catch (NumberFormatException e)
                        {
                                System.out.println("Input must be an integer!");
                        }       
                        catch (SQLException e)
                        {
                                System.out.println("There are no records associated with that number!");
                        }
			catch (IOException e)
                        {
                                System.out.println(e.getMessage());
				System.out.println("IO Error...terminating command");
                                return;
                        }
		}

		char status = 0;
		try
                {
			//String query1 = "SELECT Reservation.status\n" +
			//		"FROM Reservation\n" +
			//		"WHERE Reservation.ccid = " + customerNumber + " AND Reservation.cid = " + cruiseNumber + ";";

                        //String query2 = "SELECT Ship.seats\n" +
                          //              "FROM CruiseInfo, Ship\n" +
                            //            "WHERE CruiseInfo.cruise_id = " + cruiseNumber + " AND CruiseInfo.ship_id = Ship.id;";

                        String query3 = "SELECT Cruise.num_sold\n" +
                                        "FROM Cruise\n" +
                                        "WHERE Cruise.cnum = " + cruiseNumber + " ;";

			//List<List<String>> resExistsResult = esql.executeQueryAndReturnResult(query1);
                        //List<List<String>> seatsResult = esql.executeQueryAndReturnResult(query2);
                        List<List<String>> soldResult = esql.executeQueryAndReturnResult(query3);

                        //int numSeats = Integer.parseInt(seatsResult.get(0).get(0));
                        int numSold = Integer.parseInt(soldResult.get(0).get(0));
			int x = numSold;
			numSold = numSold-1;

			status = (numSold > 0) ? 'R' : 'W';

			if (status == 'R')
			{
				String query5 = "UPDATE Cruise\n" +
                "SET num_sold = " + (x+1) + "\n" +
                "WHERE cnum = " + cruiseNumber + " ;";
                                             
               esql.executeUpdate(query5);
			}	

			//if (!resExistsResult.isEmpty() && resExistsResult.get(0).get(0).charAt(0) == 'R')
			//	status = 'C';

		//	if (status == 'R')
		//	{
		//		String query5 = "UPDATE Cruise\n" +
		//				"SET num_sold = " + (numSold+1) + "\n" +
		//				"WHERE cnum = " + cruiseNumber + " ;";
//
//				esql.executeUpdate(query5);
//			}

			String query4 = String.format("INSERT INTO Reservation\n" +
                                                      		"VALUES ('%d', '%d', '%d', '%c');", rnum, customerNumber, cruiseNumber, status);

			esql.executeUpdate(query4);

                        System.out.println(String.format("Successfully inserted/updated the record: (rnum:%d, customerID:%d, cruiseID:%d, status:%c)",
							rnum, customerNumber, cruiseNumber, status));
                }
                catch (Exception e)
                {
                        System.out.println(e.getMessage());
			System.out.println("Unable to perform insert/update");
                }
	}

	public static void ListNumberOfAvailableSeats(DBproject esql) //5
	{
		// For Cruise number and date, find the number of availalbe seats (i.e. total Ship capacity minus booked seats )
		//Given a Cruise number and a departure date, find the number of available seats in the Cruise.

		int cruiseNumber = -1;

		do
		{
			System.out.print("Enter a nonnegative cruise number: ");

			try
			{
				cruiseNumber = Integer.parseInt(in.readLine());
			}
			catch (NumberFormatException e)
                        {       
                                System.out.println("Input must be an integer!");
                        }       
                        catch (IOException e)
                        {       
                                System.out.println(e.getMessage());
                                System.out.println("IO Error...terminating command");
                                return;
                        }
		}
		while (cruiseNumber < 0);
		
		try
		{
//			String query1 = "SELECT Ship.seats\n" +
//                                	"FROM CruiseInfo, Ship\n" +
  //                              	"WHERE CruiseInfo.cruise_id = " + cruiseNumber + " AND CruiseInfo.ship_id = Ship.id;";

                	String query2 = "SELECT Cruise.num_sold\n" +
                                	"FROM Cruise\n" +
                                	"WHERE Cruise.cnum = " + cruiseNumber + " ;";

    //            	List<List<String>> seatsResult = esql.executeQueryAndReturnResult(query1);
                	List<List<String>> soldResult = esql.executeQueryAndReturnResult(query2);

      //          	int numSeats = Integer.parseInt(seatsResult.get(0).get(0));
                	int numSold = Integer.parseInt(soldResult.get(0).get(0));

			System.out.println("The number of available seats for cruise " + cruiseNumber + " is " + (numSold));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	public static void ListsTotalNumberOfRepairsPerShip(DBproject esql) //6
	{
		// Count number of repairs per Ships and list them in descending order
		// Return the list of Ships in decreasing order of number of repairs that have been made on the Ships.

		try
		{
			String query = "SELECT Repairs.ship_id, COUNT(Repairs.rid) AS repairCount\n" +
					"FROM Repairs\n" +
					"GROUP BY Repairs.ship_id\n" +
					"ORDER BY repairCount DESC;";

			esql.executeQueryAndPrintResult(query);
		}
		catch (Exception e)
                {
                        System.out.println(e.getMessage());
                }
	}

	
	public static void FindPassengersCountWithStatus(DBproject esql) //7
	{
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.
		// For a given Cruise and passenger status, return the number of passengers with the given status.
		
		int cruiseNumber = -1;
                do      
                {       
                        System.out.print("Enter a nonnegative cruise number: ");
                        
                        try     
                        {       
                                cruiseNumber = Integer.parseInt(in.readLine());
                        }
			catch (NumberFormatException e)
                        {       
                                System.out.println("Input must be an integer!");
                        }       
                        catch (IOException e)
                        {       
                                System.out.println(e.getMessage());
                                System.out.println("IO Error...terminating command");
                                return;
                        }
                }
                while (cruiseNumber < 0);

		char status = 0;
                do      
                {       
                        System.out.print("Enter a passenger status(W,C,R): ");
                        
                        try     
                        {       
                                status = in.readLine().charAt(0);
				status = Character.toUpperCase(status);
                        }
			catch (NumberFormatException e)
                        {       
                                System.out.println("Input must be an integer!");
                        }
                        catch (IOException e)
                        {   
                                System.out.println(e.getMessage());
                                System.out.println("IO Error...terminating command");
                                return;
                        }
                }
                while (status != 'W' && status != 'C' && status != 'R');

		try
		{
			String query = "SELECT Reservation.ccid\n" +
					"FROM Reservation\n" +
                                        "WHERE Reservation.cid = " + cruiseNumber + " AND Reservation.status = '" + status + "';";

			List<List<String>> passengerIDs = esql.executeQueryAndReturnResult(query);

			System.out.println("There are " + passengerIDs.size() + " passengers with passenger status " + status + " on cruise " + cruiseNumber + ", specifically");
			
			for (List<String> passengerID : passengerIDs)
			{
				for (String ccid : passengerID)
					System.out.println("Passenger ID: " + ccid);
			}
		}
		catch (Exception e)
                {
                        System.out.println(e.getMessage());
                }
	}
}
