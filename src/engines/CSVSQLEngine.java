/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engines;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.relique.jdbc.csv.CsvDriver;

/**
 *
 * @author Ravindra
 */
public class CSVSQLEngine {
    
   private Properties props;
   private Connection conn;
   
    public CSVSQLEngine(String path) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        File f = new File(path);
        path = f.getParent();
        System.out.println("CSV ENGINE FF CONN PATH:" + path);
        // Load the driver.
        Class.forName("org.relique.jdbc.csv.CsvDriver");
        conn = DriverManager.getConnection("jdbc:relique:csv:" + path);

    }

    public CSVSQLEngine(String path, Properties props) throws Exception {

        File f = new File(path);
        path = f.getParent();
        System.out.println("CSV ENGINE FF CONN PATH:" + path);
        // Load the driver.
        Class.forName("org.relique.jdbc.csv.CsvDriver");
        conn = DriverManager.getConnection("jdbc:relique:csv:" + path, props);
    }
    
    public ResultSet getResultSet(String qry) throws SQLException {

        ResultSet rs;

        System.out.println("CSV ENGINE TO EXECUTE QRY:" + qry);

        Statement stmt = conn.createStatement();
        rs = stmt.executeQuery(qry);
        
        return rs;

    }
    
    public void close(){
        
       try {
           conn.close();
       } catch (SQLException ex) {
           //ignore
       }
        
    }
    
}
