//on login will use constructor
//on logout will set obj refernce to null to free memory
package src.CONTROLLER.SubControllers;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
//Look here for most info https://www3.ntu.edu.sg/home/ehchua/programming/java/JDBC_Basic.html

import com.mysql.cj.x.protobuf.MysqlxPrepare.Prepare;
import com.mysql.cj.xdevapi.Result;

public class UserSubController {
    QuerySubController QSB;

    //User Information
    private String user = null;
    private String pass = null;
    private int UID = 000000;

    //Getters
    public String getUser() {return user;}
    private String getPass() {return pass;}
    protected int getUID() {return UID;}

    //Setters
    public void setUID(int tempID) {UID = tempID;}
    public void setUser(String u) {user = u;}
    public void setPass(String p) {pass = p;}

    //protected constructor for querysubcontroller to call bc in same package
    //TODO - > assumes values are populated guard needed to handle
    protected UserSubController() {}

    //Constructor -> considering its a contrsuctor we want to be able to load an existing user but if no existing user 
    //               exists then we want throw an exception. The question is how to handle the exception in the querysubcont class
    //               because we create an object of this class and if an exception is thrown then we want to throw an error
    //               there as well. That error and the other should be handled outside of the function to be caught by the view

    //pub constructor for view to call
    public UserSubController(String newU, String newP) throws BadConstructorValuesException { // -> EXCEPTION TYPE IS TEMP
        QSB = new QuerySubController();
        //we want to load user if auth is true otherwise throw error to prevent false population
        if (!authUser(newU, newP)) throw new BadConstructorValuesException();  //exception (cant tell if SQLE or NO u & p)
    }

    //user validation -> when false throws exception in constructor
    private boolean authUser(String newU, String newP) {
        String query = "SELECT UID FROM user_table WHERE username LIKE ? AND password LIKE ?";

        try {
            PreparedStatement prpSt = QSB.getConn().prepareStatement(query);
            prpSt.setString(1, newU);
            prpSt.setString(2, newP);
            ResultSet rset = prpSt.executeQuery();

            //not valid u or p
            if (!rset.next()) throw new NonSQLError("Username of Password Incorrect");

            //valid must update u & p
            this.user = newU;
            this.pass = newP;
            this.UID = rset.getInt("UID");
            return true;

        } catch (SQLException sqle) {
            System.out.println("Database access error: Could not authorize user");
            sqle.printStackTrace();
            return false;

        } catch (NonSQLError unf) {
            System.out.println("Username or password are incorrect");
            unf.printStackTrace();
            return false;
        }
    }

    //for new user creation
    public boolean setUser(String newUser, String newPass) {
        String query = "INSERT INTO user_table (UID, username, password) VALUES (?, ?, ?) ";

        try {
            int tempUID = checkUID();
            PreparedStatement prpSt = QSB.getConn().prepareStatement(query);
            prpSt.setInt(1, tempUID);
            prpSt.setString(2, newUser);
            prpSt.setString(3, newPass);
            prpSt.executeUpdate();

            //Set current newUser into vars
            this.UID = tempUID;
            this.user = newUser;
            this.pass = newPass;
            return true;

        } catch (SQLException sqle) {
            System.out.println("Database Access Error: Could not create user");
            sqle.printStackTrace();
            return false;
        }
    }

    //user deletion -> extends to deleting all related expenses and their associated notes
    public boolean removeUser() {
        String removeExpenseQuery = "DELETE FROM general_expense WHERE UID LIKE ?";
        String removeNotesQuery = "DELTE FROM expense_notes WHERE DID LIKE ?";
        String removeAccountQuery = "DELETE FROM user_table WHERE UID LIKE ?";

        try {
            PreparedStatement prpStExpense = QSB.getConn().prepareStatement(removeExpenseQuery);
            PreparedStatement prpStNotes = QSB.getConn().prepareStatement(removeNotesQuery);
            PreparedStatement prpStAccount = QSB.getConn().prepareStatement(removeAccountQuery);

            //setting UID for expense removal
            prpStExpense.setInt(1, this.UID);
            
            //setting DID for note removal
            prpStNotes.setInt(1, QSB.getDID());

            //setting UID for user and pass removal
            prpStAccount.setInt(1, this.UID);

            //executing updates
            prpStExpense.executeUpdate();
            prpStNotes.executeUpdate();
            prpStAccount.executeUpdate();

            //return true upon successfull deletion -> should trigger a logout
            return true;

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }


    //Exceptions for bad data to differentiate from slqexception errors when debugging
    class NonSQLError extends RuntimeException {
        public  NonSQLError(String msg) {
            super(msg);
        }
    }

    //should chaing in query sub to throw exception in its contructor
    class BadConstructorValuesException extends RuntimeException {
        public BadConstructorValuesException() {
            super("Cannot construct Username Sub Controller");
        }
    }

    //Utility
    private int checkUID() {
        final int MIN = 100000;
        final int MAX = 999999;
        int rUID = (int) Math.floor(Math.random() * (MAX - MIN + 1) + MIN);

        try {
            String checkQuery = "SELECT UID FROM user_table WHERE UID LIKE ?";
            PreparedStatement prpSt = QSB.getConn().prepareStatement(checkQuery);
            prpSt.setInt(1, rUID);
            ResultSet rset = prpSt.executeQuery();
            while (rset.next()) {
                checkUID();
            }
            return rUID;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println("Database Access Error: Can't access UID list for UID check");
            return 0;
        }
    }
}
