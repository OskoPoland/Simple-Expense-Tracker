package Data_Controls;
import java.sql.*;
import java.util.*;
//Look here for most info https://www3.ntu.edu.sg/home/ehchua/programming/java/JDBC_Basic.html

class dataControls {
    //DB Connection URL: jdbc:mysql://localhost:3306/expenseTracking?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=PST

    //unmuteable access variables for intial connection
    private static String username = "vinr567";
    private static String password = "3529";

    //Connection to driver manager
    private  Connection conn = createConnection();

    //Create statment to execute SQL query
    private Statement stmt = null;

    //User verified key for sql queries
    private String authKey;

    //createConnection: establishes connection to local host that is hosting the server
    public Connection createConnection() {
        //debug statment
        System.out.println("Establishing Connection to JBDC Server");

        //creating connection establishment pieces
        Connection tempConn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", username);
        connectionProps.put("password", password);

        //Establishing the connection jdbc:mysql://127.0.0.1:3306/?user=root
        String connectionURL = "jdbc:mysql://127.0.0.1:3306/expensemanager?user=root";
        
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            //Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();
            tempConn = DriverManager.getConnection(connectionURL, connectionProps);
            return tempConn;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }

    public void closeConnection() {
        try {
            conn.close();
            System.out.println("connection closed");
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
    
    //authenticateUser: verify username and password and return unique account ID
    public boolean authenticateUser(String username, String password) {
        String queryCommand = "select ID from AUTHENTICATION where USERNAME=" + username + " and PASSWORD=" + password;
        //Test Statement - Delete Once Done
        System.out.println("The auth command is: " + queryCommand);
        try {
            ResultSet rset = stmt.executeQuery(queryCommand);
            authKey = rset.getString("ID");
            return true;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println("There was an error authenticating account");
            return false;
        }
    }

    //createUser: create a new user account adding a unique username, password, and ID
    // - create limit for ID reset
    public void createUser(String username, String password) {
        int min = 100000;
        int max = 999999;
        int ID = (int)Math.floor(Math.random()*(max-min+1)+min);
        String insertQueryCommand = "INSERT INTO authentication VALUES (?, ?, ?) ";
        

        try {
            stmt = conn.createStatement();
            if (!verifyUsername(username)) {
                System.out.println("username verified");
                while(verifyID(ID)) {ID = (int)Math.floor(Math.random()*(max-min+1)+min); }
                //test statement
                System.out.println("ID is unique");
                //stmt.executeUpdate(insertQueryCommand);
                PreparedStatement prpSt = conn.prepareStatement(insertQueryCommand);
                prpSt.setInt(1, ID);
                prpSt.setString(2, username);
                prpSt.setString(3, password);

                prpSt.executeUpdate();
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    //verifyUsername: verifies the username of the user
    private boolean verifyUsername(String username) {
        String verifyUser = "SELECT username FROM AUTHENTICATION WHERE " + username + " EXISTS";
        try {
            stmt.executeUpdate(verifyUser);
            return true;
        } catch(SQLException sqle) {
            //sqle.printStackTrace();
            return false;
        }
    }

    //verifyID: verifies the ID of user
    private Boolean verifyID(int ID) {
        String verifyID = "SELECT ID FROM Authentication WHERE " + ID + " EXISTS";
        try {
            stmt.executeUpdate(verifyID);
            return true;
        } catch (SQLException sqle) {
            //sqle.printStackTrace();
            return false;
        }
    } 

    
    public Float retreiveTotalCosts(String setMonth) {
        Float returnAmount = 0f;
        //Creating the query
        String totalExpenseQuery = "SELECT expense_name, amount WHERE auth_id=" + authKey + " FROM " + setMonth;
        System.out.println("The command is: " + totalExpenseQuery);
        try {
             ResultSet rset = stmt.executeQuery(totalExpenseQuery);
            //iterate through result set
            while(rset.next()) {
                returnAmount += rset.getFloat("amount");
            }
                return returnAmount;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return returnAmount;
        }
    }

    //retrieveExpenses: will return expenses and their costs accounting for repeat entries
    // - at some point should be able to limit the search area to a specific month and year
    public Map<String, Float> retrieveExpenses(int ID, int amountOfExpenses) {
        Map<String, Float> tempMap = new HashMap<String, Float>();

        String retrievalQuery = "SELECT EXPENSE_NAME, COST FROM Expenses WHERE ID=" + ID;
        
        try {
            ResultSet rset = stmt.executeQuery(retrievalQuery);
            while(rset.next()) {
                if (tempMap.containsKey(rset.getString("EXPENSE"))) {
                    Float tempVal = tempMap.get(rset.getString("EXPENSE"));
                    tempVal += rset.getFloat("COST");
                    tempMap.replace(rset.getString("EXPENSE"), rset.getFloat("COST"));
                } else {
                    tempMap.put(rset.getString("EXPENSE"), rset.getFloat("COST"));
                }
            }
            return tempMap;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return tempMap;
        }
    }

    //addExpense: adds an expense to the SQL database
    public void addExpense(String expenseName, Float expenseAmount, String ID) {
        //include function to order expenses from greatest to least
        //create query
        String addExpenseQuery = "INSERT INTO Expenses ( ID, expense_name, amount) VALUES (" + ID +"," + expenseName + "," + expenseAmount + ")";
        try {
            stmt.executeUpdate(addExpenseQuery);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    //This function will remove an expense by name CASE SENSITIVE
    //If this is done then the other gui update functions have to be done to match changes
    public void deleteExpense( String expenseName, String ID) {
        String deleteExpenseQuery = "DELETE FROM Expenses WHERE " + "expense_name=" + expenseName + "ID=" + ID;
        try {
            stmt.executeUpdate(deleteExpenseQuery);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.exit(1);
        }
    }

    //When the object is instantiated then a connection is established. From there the rest of the functions are non-static
    //because they should be callable before or after a connection is created
    public static void main(String args[]) {
        dataControls dc = new dataControls();
        // System.out.println("Establishing a connection");
        // dc.createConnection();
        System.out.println("Creating user arye pass aa");
        dc.createUser("arye", "aaa");
        dc.closeConnection();
    }
}
 
