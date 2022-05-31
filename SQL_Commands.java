import java.sql.*;
import java.util.*;
//Look here for most info https://www3.ntu.edu.sg/home/ehchua/programming/java/JDBC_Basic.html

class JBDC_Functions {
    //DB Connection URL: jdbc:mysql://localhost:3306/expenseTracking?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=PST
    private String connectionURL;

    //unmuteable access variables for intial connection
    private String username = "vinr567";
    private String password = "3529";

    //Connection to driver manager
    private Connection conn;
    //Create statment to execute SQL query
    private Statement stmt;

    //User verified key for sql queries
    private String authKey;

    //Act to create the connection at initialization
    //Sort of a constructor variable because otherwise it is not possible to connect to database
    public void createConnection() {
        try {
            conn = DriverManager.getConnection(connectionURL, username, password);
            stmt = conn.createStatement();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    //Function to verify login information through sql query and return authkey
    //Can do through rset
    public boolean authenticateUser(String username, String password) {
        //Return authkey if user and pass match
        String rowSelect = "select auth_id from authentication_table where username=" + username + " and password=" + password;
        System.out.println("The auth command is: " + rowSelect);
        
        try {
            ResultSet rset = stmt.executeQuery(rowSelect);
            authKey = rset.getString("auth_id");
            return true;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println("There was an error authenticating account");
            return false;
        }
    }

    //retrieve the running total for current and previous months through sql query 
    //Mainly for use in GUI generating graph and recent expense breakdown
    //Return total for entered month
    //For more months simply use multiple times
    public Float retreiveTotalCosts(String setMonth) {
        Float returnAmount = 0f;
        //Creating the query
        String totalExpenseQuery = "select expense_name, amount where auth_id=" + authKey + " from " + setMonth;
        System.out.println("The command is: " + totalExpenseQuery);
        if (authCheck()) {
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
        } else {
            System.out.println("Authcheck failed");
            System.exit(1);
            return returnAmount;
        }
    }

    //retrieves the most recent expenses. Can specify how many of the most recent expenses
    //will return an unordered_map ("expense name", cost) in the order of most recent to least recent
    public Map<String, Float> retrieveRecentExpenses(String setMonth, int amountToRetrieve) {
        //use authkey check
        //return map of specified amount of expenses so map length will always have to be equal to func param within set authkey
        Map<String, Float> tempMap = new HashMap<String, Float>();
        //Create query
        String retrieveExpensesQuery = "select expense_name, amount from " + setMonth + " where auth_id=" + authKey + " limit " + amountToRetrieve;
        System.out.println("The command is: " + retrieveExpensesQuery);
        if(authCheck()) {
            try {
                ResultSet rset = stmt.executeQuery(retrieveExpensesQuery);
                while(rset.next()) {
                    if (tempMap.containsKey(rset.getString("expense_name"))) {
                        //add expense for that category
                        Float tempVal = tempMap.get(rset.getString("expense_name"));
                        tempVal += rset.getFloat("amount");
                        tempMap.replace(rset.getString("expense_name"), rset.getFloat("amount")); 
                    } else {
                        tempMap.put(rset.getString("expense_name"), rset.getFloat("amount"));
                    }
                }
                return tempMap;
            } catch (SQLException sqle) {
                sqle.getStackTrace();
                System.exit(1);
                return tempMap;
            }
        } else {
            System.out.println("There was an issue with authentication");
            System.exit(1);
            return tempMap;
        }
    }

    //run sql query to retrieve whole table and save expenses and costs in a map <Expense Name, Cost>
    //might be easier to just iterate over whole table and to map.put(expense name, cost)
    //intend to use this for one of the nav bar options "SHOW ALL EXPENSES"
    public Map<String, Float> retrieveAllExpenses(String setMonth) {
        //implement function to make sure there are no repeats
        //Keep track of times added ot each month?
        //retunrs map of all expenses that can be used to display
        Map<String, Float> tempMap = new HashMap<String, Float>();
        //Create query
        String retrieveAllExpensesQuery = "select * from " + setMonth;

        while(authCheck()) {
            try {
                ResultSet rset = stmt.executeQuery(retrieveAllExpensesQuery);
                while(rset.next()) {
                    if(tempMap.containsKey(rset.getString("expense_name"))) {
                        Float tempVal = rset.getFloat("amount");
                        tempVal += tempMap.get(rset.getString("expense_amount"));
                        tempMap.replace(rset.getString("expense_name"), rset.getFloat("amount"));
                    } else {
                        tempMap.put(rset.getString("expense_name"), rset.getFloat("amount"));
                    }
                }
                return tempMap;
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                System.exit(1);
                return tempMap;
            }
        }
        return tempMap;
    }


    //This function will add a new expense through dynamic sql insert query
    //If this is done call other GUI functions to update the values.
    public void addExpense(String setMonth,  String expenseName, Float expenseAmount) {
        //include function to order expenses from greatest to least
    }

    //This function will remove an expense by name CASE SENSITIVE
    //If this is done then the other gui update functions have to be done to match changes
    public void deleteExpense(String setMonth, String expenseName) {
        //perform simple sql update query from selected table
        //include function to order expenses from greatest to least
    }

    //This is to rename a selected expense
    //Must pass in correct old expense name in order to change the expense -> can get it from on click action event
    //Must pass in name to replace the old name  
    public void renameExpense(String setMonth, String toDelete, String toReplace) {
        //will store sql data from row with incorrect name
        //delete sql row with name
        //create new row containing name and all other info
        //if there is a simple SQL REPLACE function use that instead
        
        //Must check for name. If for some reason name is incorrect and query cannot match it throw error
    }

    //Function that will perform simple query to alter the amount that an expense is worth ie change the float in row "expense amount"
    //pass in month, name of expense, and the amount to change it to
    public void alterExpenseAmount(String setMonth, String toChange, float newAmount) {
        //User SQL query in order to alter the amount of the selected expense
    }

    //check to make sure that an authkey is set otherwise the program needs to exit with failure 
    //if not set then can risk seeing all data instead of allowed data
    public boolean authCheck() {
        if(authKey != "") {
            return true;
        } else {
            return false;
        }
    }


}
