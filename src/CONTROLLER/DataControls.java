package src.CONTROLLER;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
//Look here for most info https://www3.ntu.edu.sg/home/ehchua/programming/java/JDBC_Basic.html

import com.mysql.cj.x.protobuf.MysqlxPrepare.Prepare;
import com.mysql.cj.xdevapi.Result;

public class DataControls {
    //Create statment to execute SQL query
    public ConnectionControls conControls = new ConnectionControls();
    public UserProfile up = new UserProfile();
    public ConnectionProperties cProp = new ConnectionProperties();
    public ExpenseQueryCommands query = new ExpenseQueryCommands();


    class UserProfile {
        private String username = null;
        private String password = null;
        private int UID;
        
        public String getUser() {return this.username;}
        public String getPass() {return this.password;};
        public int getUID() {return this.UID;}

        public void authenticateUser(Connection conn, String u, String p) {
        
            String queryCommand = "SELECT UID FROM AUTHENTICATION WHERE USERNAME LIKE ? AND PASSWORD LIKE ?";
        
            try {
                PreparedStatement prpSt = conn.prepareStatement(queryCommand);
                prpSt.setString(1, this.username);
                prpSt.setString(2, this.password);
                ResultSet rset = prpSt.executeQuery();
                while(rset.next()) {
                    this.username = rset.getString("USERNAME");
                    this.password = rset.getString("PASSWORD");
                    this.UID = rset.getInt("UID");
                };
                } catch (SQLException sqle) {
                    //throw an account does not exist custom exception here
            }
        }

        public void createUser(Connection conn, Statement stmt, String u, String p) {
            int min = 100;
            int max = 99999999;
            int ID = (int)Math.floor(Math.random()*(max-min+1)+min);
            
            String insertQueryCommand = "INSERT INTO authentication VALUES (?, ?, ?) ";
            String userVerifyQuery = "SELECT username FROM AUTHENTICATION WHERE username LIKE ?";
            String idVerifyQUery = "SELECT UID FROM AUTHENTICATION WHERE UID LIKE ?";

            try {
                stmt = conn.createStatement();
                PreparedStatement checkUserSt = conn.prepareStatement(userVerifyQuery);
                checkUserSt.setString(1, u);

                PreparedStatement checkUidSt = conn.prepareStatement(idVerifyQUery);
                checkUidSt.setInt(1, this.UID);

                ResultSet rset = checkUidSt.executeQuery();
                while(rset.next()) {
                    ID = (int)Math.floor(Math.random()*(max-min+1)+min);
                }
                this.UID = ID;

                PreparedStatement insertUserSt = conn.prepareStatement(insertQueryCommand);
                insertUserSt.setInt(1, this.UID);
                insertUserSt.setString(2, u);
                insertUserSt.setString(3, p);

                //verifying the username
                rset = checkUserSt.executeQuery();
                while(rset.next()) {
                    throw new UsernameTakenException("Username is not available");
                } 

                //verify the ID
                insertUserSt.executeUpdate();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } 
        }
    }

    public class ConnectionProperties {
        private final String mysqlU = "vinr567";
        private final String mysqlP = "3529";
        private final String connectionUrl = "jdbc:mysql://127.0.0.1:3306/expensemanager?user=root";
        private Connection conn = null;
        private Statement stmt = null;

        public Connection getConn() {return this.conn;}
        public Statement getStmt() {return this.stmt;}

        public void createConnection() {
            //Debug statement
            System.out.println("Establishing a connection to server");

            //Creating connection properties
            Properties connectionProps = new Properties();
            connectionProps.put("user", this.mysqlU);
            connectionProps.put("password", this.mysqlP);

            //Establishing the connection
            try {
                this.conn = DriverManager.getConnection(connectionUrl, connectionProps);
                this.stmt = conn.createStatement();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }

        public void closeConnection() {
            try {
                this.conn.close();
                this.conn = null;
                this.stmt = null;
                System.out.println("Connection closed");
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
    }

    // Hold all controls to setup the Data Controls class
    // - createConnection() -> sets up the connection to the server
    // - closeConnection() -> closes the connection to the server
    // - createUser() -> sets up the user profile with inputed username and password
    public class ConnectionControls {
        //Create objects of relevant classes for setup
        ConnectionProperties cProp = new ConnectionProperties();
        UserProfile up = new UserProfile();
        ExpenseQueryCommands eq = new ExpenseQueryCommands();

        //Connection setup options
        public void createConnection() {cProp.createConnection();};
        public void breakConnection() {cProp.closeConnection();};

        //User setup options
        public void authUser(String u, String p) {up.authenticateUser(cProp.getConn(), u, p);}
        public void createUser(String u, String p) {up.createUser(cProp.getConn(), cProp.getStmt(), u, p);} //throws runtime exception

        //Model Manipulation controls
        public void addExpense(String n, String c, Float a) {eq.addExpenseQuery(cProp.getConn(), n, a, up.getUID(), c);}
        public void deleteExpense(String n, String c) {eq.deleteExepenseQuery(cProp.getConn(), n, c, up.getUID());}
    }


    class ExpenseQueryCommands {
        //For temporary expense information
        // String tempName = null;
        // Float tempCost = null;
        // String tempCat = null;

        // public String getName() {
        //     if (tempName != null) return this.tempName;
        //     throw new NoExpenseSetException("Expense name has not been set with setExpense");
        // }

        // public Float getCost() {
        //     if (tempCost != null) return this.tempCost;
        //     throw new NoExpenseSetException("Expense cost has not been set with setExpense");
        // }

        // public String getCat() {
        //     if (tempCat != null) return this.tempCat;
        //     throw new NoExpenseSetException("Expense cat has not been set with setExpense");
        // }
        public void addExpenseQuery(Connection conn, String name, Float amount, int ID, String cat) {
            String addExpenseQuery = "INSERT INTO EXPENSES (UID, EXPENSE, EXPENSE_COST, DATE, CATEGORY) VALUES (?,?,?,?,?)";
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-mm-dd");
            LocalDate date = LocalDate.now();
            String dateStr = dtf.format(date);

            try {
                PreparedStatement prpSt = conn.prepareStatement(addExpenseQuery);
                prpSt.setInt(1, ID);
                prpSt.setString(2, name);
                prpSt.setFloat(3, amount);
                prpSt.setString(4, dateStr);
                prpSt.setString(5, cat);
                prpSt.executeUpdate();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }

        public void deleteExepenseQuery(Connection conn, String name, String cat, int ID) {
            String deleteQuery = "DELETE FROM Expenses WHERE EXPENSES LIKE ? AND CATEGORY LIKE ? AND UID LIKE ?";
            try {
                PreparedStatement prpSt = conn.prepareStatement(deleteQuery);
                prpSt.setString(1, name);
                prpSt.setString(2, cat);
                prpSt.setInt(3, ID);
                prpSt.executeUpdate();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }

    }


    // //Custom exception classes 
    class UsernameTakenException extends RuntimeException {
        public UsernameTakenException(String error) {
            super(error);
        }
    }

    class NoExpenseSetException extends RuntimeException {
        public NoExpenseSetException(String error) {
            super(error);
        }
    }
}
 