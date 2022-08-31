package src.CONTROLLER.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;


import junit.*;
import src.CONTROLLER.*;

public class ControllerTest {
    DataControls dc;

    @Test
    public void evaluateConnecitonProperties() {
        dc = new DataControls();
        
        //Establish connection to MySQL Local Host Server
        dc.conControls.createConnection(); 
        assertNotNull(dc.cProp.getConn()); //Assert connection obj created
        assertNotNull(dc.cProp.getStmt()); //Assert statement obj created

        //Close Connection
        dc.cProp.closeConnection();
        assertNull(dc.cProp.getConn()); //Assert connection set back to null
        assertNull(dc.cProp.getStmt()); //Asser statement set back to null;

    }



}
