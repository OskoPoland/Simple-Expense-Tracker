package CONTROLLER;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
//Look here for most info https://www3.ntu.edu.sg/home/ehchua/programming/java/JDBC_Basic.html

import com.mysql.cj.x.protobuf.MysqlxPrepare.Prepare;

public class DataControls {
    //DB Connection URL: jdbc:mysql://localhost:3306/expenseTracking?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=PST

    //immuteable access variables for intial connection
    private static String username = "vinr567";
    private static String password = "3529";

    //Connection to driver manager
    private  Connection conn = createConnection();

    //Create statment to execute SQL query
    private Statement stmt = null;

    //Set on login and used for all other functions
    private int ID;

    //createConnection: establishes connection to local host that is hosting the server
    private Connection createConnection() {
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
        String queryCommand = "SELECT UID FROM AUTHENTICATION WHERE USERNAME LIKE ? AND PASSWORD LIKE ?";
        //Test Statement - Delete Once Done
        System.out.println("The auth command is: " + queryCommand);
        try {
            PreparedStatement prpSt = conn.prepareStatement(queryCommand);
            prpSt.setString(1, username);
            prpSt.setString(2, password);
            ResultSet rset = prpSt.executeQuery();
            while(rset.next()) {
                ID = rset.getInt("UID");
                return true;
            };
            return false;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
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

    public int getID() {return ID;};

    //verifyUsername: verifies the username of the user
    private boolean verifyUsername(String username) {
        String verifyUser = "SELECT username FROM AUTHENTICATION WHERE username LIKE ?";
        //SELECT * FROM q_table, choices, answers  WHERE questions.QID=? AND choices.CID=? AND answers.AID=?
        System.out.println("checking");
        try {
            PreparedStatement prpSt = conn.prepareStatement(verifyUser);
            prpSt.setString(1, username);
            ResultSet rset = prpSt.executeQuery();
            
            while(rset.next()) {return true;};

            return false;
        } catch(SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }

    //verifyID: verifies the ID of user
    private Boolean verifyID(int ID) {
        String verifyID = "SELECT UID FROM Authentication WHERE UID LIKE ?";
        try {
            PreparedStatement prpSt = conn.prepareStatement(verifyID);
            prpSt.setInt(1, ID);
            ResultSet rset = prpSt.executeQuery();

            while(rset.next()) {return true;}


            return false;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    } 

    //addExpense: adds an expense to the SQL database
    // - want to add categories as well to make organization easier.
    // - categories will be added to make adding expenses easier in the sense that there is a unique name but it belongs in a common cat
    public void addExpense(String expenseName, Float expenseAmount, int ID, String category) {
        String addExpenseQuery = "INSERT INTO Expenses ( UID, EXPENSE, EXPENSE_COST, DATE, CATEGORY) VALUES (?, ?, ?, ?, ?) ";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-mm-dd");
        LocalDate date = LocalDate.now();
        String dateStr = dtf.format(date);

        try {
            PreparedStatement prpSt = conn.prepareStatement(addExpenseQuery);
            prpSt.setInt(1, ID);
            prpSt.setString(2, expenseName);
            prpSt.setDouble(3, expenseAmount);
            prpSt.setString(4, dateStr);
            prpSt.setString(5, category);
            prpSt.executeUpdate();            
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    //deleteExpense: removes an expense from the database
    public void deleteExpense( String expenseName, int ID) {
        String deleteExpenseQuery = "DELETE FROM Expenses WHERE EXPENSE LIKE ? AND UID LIKE ?";
        try {
            PreparedStatement prpSt = conn.prepareStatement(deleteExpenseQuery);
            prpSt.setString(1, expenseName);
            prpSt.setInt(2, ID);
            prpSt.executeUpdate();
            System.out.println("Expense Deleted");
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.exit(1);
        }
    }

    //This should use the month dates on input to organize because this is for the monthly expense JChart
    public Map<String, Float> createDataSet() {
        Map<String, Float> expData = new HashMap<String, Float>();
        String getDataQuery = "SELECT DATE, EXPENSE_COST FROM expenses";
        try {
            ResultSet rset = stmt.executeQuery(getDataQuery);
            while(rset.next()) {
                if (expData.containsKey(rset.getString("DATE"))) {
                    expData.put(dateTrimmer(rset.getString("DATE"),"m"), expData.get(dateTrimmer(rset.getString("DATE"), "m") + rset.getFloat("EXPENSE_COST")));
                } else {
                    expData.put(dateTrimmer(rset.getString("DATE"), "m"), rset.getFloat("EXPENSE_COST"));
                }
            }
            return expData;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }

    private String dateTrimmer(String date, String parseFor) {
        String[] tempDate = date.split("-");
        if (parseFor == "y") {
            return tempDate[0];
        } else if (parseFor == "m") {
            return tempDate[1];
        } else if (parseFor == "d") {
            return tempDate[2];
        } else {
            return null;
        }
    }


    //Will return all categories that will be used in expense GUI combo box
    public Object[] getCategories(int ID) {
        ArrayList<String> tempCats = new ArrayList<String>();
        String catQuery = "SELECT CATEGORY FROM expenses WHERE UID = ?";
       try {
            PreparedStatement prpSt = conn.prepareStatement(catQuery);
            prpSt.setInt(1, ID);
            ResultSet rset = prpSt.executeQuery();
            while(rset.next()) {
                if (tempCats.contains(rset.getString("CATEGORY"))) {
                    continue;
                } else {
                    tempCats.add(rset.getString("CATEGORY"));
                }
            } 
            Object[] returnCats = tempCats.toArray();
            return returnCats;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        }
    }

    

    //This function will remove an expense by name CASE SENSITIVE
    //If this is done then the other gui update functions have to be done to match changes
    

    //When the object is instantiated then a connection is established. From there the rest of the functions are non-static
    //because they should be callable before or after a connection is created
    //public static void main(String args[]) {
        //DataControls dc = new dataControls();
        // --DATABSE CONNECTION CONTROLS -- \\
        //System.out.println("Establishing a connection");
        //dc.createConnection();         - WORKING 

        // --LOG IN OUT CONTROLS -- \\
        //dc.verifyID(222347);           - WORKING
        //dc.verifyUsername("arye");     - WORKING
        //dc.createUser("arye", "aaa");  - WORKING
        //dc.authenticateUser("arye", "aaa");   - WORKING

        // --EXPENSE MANAGEMENT CONTROLS -- \\
        //dc.addExpense("expense", 65.42, 222347); - WORKING
        //dc.deleteExpense("p", 222347);           -

        //dc.closeConnection(); //         - WORKING
    //}
}
 
