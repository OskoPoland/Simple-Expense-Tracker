//It might be worth actually using the note sub contorller as a sub class instead of creating a seperate class for it 
//it makes more sense to have it as a subclass because its directly related to our query sub-controller


package src.CONTROLLER.SubControllers;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
//Look here for most info https://www3.ntu.edu.sg/home/ehchua/programming/java/JDBC_Basic.html

import com.mysql.cj.x.protobuf.MysqlxPrepare.Prepare;
import com.mysql.cj.xdevapi.Result;

import src.CONTROLLER.SubControllers.UserSubController.BadConstructorValuesException;

//we are no longer going to have the note subcontroller as a seperate class but rather bring it in as a subclass of the 
//query sub controller for greater clairty
public class QuerySubController {
    //Creating usersubcontroller instance
    //For retrieval of user information stored in user sub controller instance

    UserSubController USB; // will be created on construction

    //Connection vars non-muteable
    private final String connectionUrl = "jdbc:mysql://127.0.0.1:3306/expensemanager?user=root";
    private final String mySqlPass = "3529";
    private final String mySqlUser = "vinr567";

    //Non-muteable created on class creation connection properties
    private final Connection conn = null; // Want this to be created upon construction of the object
    private final Statement stmt = null; // Want this to be created upon construction of the object
    private int DID = 0;                 // May want this to be tracked somehow not sure yet

    //Constructor -> must pass in created obj without we have to create a chain of exceptions to 
    //               guard against null values
    //               CONSTRUCTOR ONLY FOR VIEW TO ACCESS METHODS
    public QuerySubController(UserSubController usb) {
        this.USB = usb;
        setConnection();
        setStatement();        
    }

    //Constructor for USB to access methods ONLY
    protected QuerySubController() {}

    //Getters
    protected Connection getConn() {return conn;}
    protected Statement getStatement() {return stmt;}
    protected int getDID() {return DID;}

    //Setters
    public Connection setConnection() {
        Properties connectionProps = new Properties();
        connectionProps.put("user", mySqlUser);
        connectionProps.put("pass", mySqlPass);
        try {
            return DriverManager.getConnection(connectionUrl, connectionProps);
        } catch (SQLException sqle) {
            System.out.println("Coulnd't establish connection to MySQL Server");
            sqle.printStackTrace();
            return null;
        }
    }

    public Statement setStatement() {
        try {
            return conn.createStatement();
        } catch (SQLException sqle) {
            System.out.println("Either connection could not be verified or statement could not be created");
            sqle.printStackTrace();
            return null;
        }
    }

    
    //sets new expense for the current user
    public boolean setExpense(String title, String category, String note, Double cost) {

        String query = "INSERT INTO general_expense (UID, DID, date, cost, category, title)" +
                        "VALUES (u.UID from user_table u where u.UID = ?,?,?,?,?,?)";

        //creating properly formated date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-mm-dd");
        LocalDate date = LocalDate.now();
        String temp = dtf.format(date);
        Date dateStr = Date.valueOf(temp);

        //generate unique DID
        int tempDID = checkDID();
        while (tempDID == 0) tempDID = checkDID();

        //When creating a new expense have to call create note page 
        try {
            PreparedStatement prpSt = conn.prepareStatement(query);
            prpSt.setInt(1, USB.getUID());
            prpSt.setInt(2, tempDID);
            prpSt.setDate(3, dateStr);
            prpSt.setDouble(4, cost);
            prpSt.setString(5, category);
            prpSt.setString(6, title);
            prpSt.executeUpdate();

            //Creating the associated note page and throw error when creating
            if (!setNote(DID, title, note)) throw new SQLException();

            //Clearing curr DID
            DID = 000000;

            //return true on successfull insertion
            return true;
        } catch (SQLException sqle) {
            System.out.println("Could not execute update to general_expense");
            sqle.printStackTrace();
            return false;
        }
    }
    
    public boolean removeExpense() {
        //when removing expense we should know UID, DID
        //each DID should be unique therefore we can remove an expense and note with the DID of the expense
        //removing an expense should also remove the note
        
        String query = "DELETE FROM general_expense WHERE UID LIKE ? AND DID LIKE ?";

        try {
            //removing the expense entry
            PreparedStatement prpSt = conn.prepareStatement(query);
            prpSt.setInt(1, USB.getUID());
            prpSt.setInt(2, DID);

            prpSt.executeUpdate();

            //removing the note entry
            if (!removeNote(DID)) throw new SQLException();

            return true;
        } catch (SQLException sqle) {
            System.out.println("Could not remove expense or note");
            sqle.printStackTrace();
            return false;
        }
    }

    //will remove the seperate class it can be simplified to a couple of functions
    private boolean setNote(int DID, String title, String note) {
        //we know our DID but we want to take the title and message. This can only really be used in the set expense func
        String query = "INSERT INTO expense_notes (UID, DID, Title, Note) VALUES" +
                       "(?, ? ,?, ?)";

        try {
            PreparedStatement prpSt = conn.prepareStatement(query);
            prpSt.setInt(1, USB.getUID());
            prpSt.setInt(2, DID);
            prpSt.setString(3, title);
            prpSt.setString(4, note);

            prpSt.executeUpdate();
            return true;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }

    //to remove a note completely
    //we make this pub because we can remove a not independant of adding or removing a full expense
    //because we can use this indepently then we need to pass in DID to idenfity note for removal
    public boolean removeNote(int DID) {
        //we know did and uid and dont need anything else for removal
        String query = "DELETE FROM expense_notes WHERE UID LIKE ? AND DID LIKE ?";

        try {
            PreparedStatement prpSt = conn.prepareStatement(query);
            prpSt.setInt(1, USB.getUID());
            prpSt.setInt(2, DID);

            prpSt.executeUpdate();
            return true;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
    }

    //Utility
    //Checks if DID being created or used already exists and creates DID
    private int checkDID() {
        final int MIN = 100000;
        final int MAX = 999999;
        int rDID = (int) Math.floor(Math.random() * (MAX - MIN + 1) + MIN);

        try {
            String checkQuery = "SELECT DID FROM general_expense WHERE DID LIKE ?";
            PreparedStatement prpSt = conn.prepareStatement(checkQuery);
            prpSt.setInt(1, rDID);
            ResultSet rset = prpSt.executeQuery();
            while (rset.next()) {
                checkDID();
            }
            return rDID;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println("Could not access DID list");
            return 0;
        }
    }
}
