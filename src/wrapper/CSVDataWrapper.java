/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wrapper;

import engines.CSVSQLEngine;
import engines.ComparisonEngine;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CSVDataWrapper {

    static Properties csvprops;
    private CSVSQLEngine csvse;

    public CSVDataWrapper() {

        csvprops = new java.util.Properties();

    }

    public void SetConnection(String localpathwithfilename) throws Exception {
        //create csv engine object
        if (!csvprops.isEmpty()) {
            csvse = new CSVSQLEngine(localpathwithfilename, csvprops);
        } else {
            csvse = new CSVSQLEngine(localpathwithfilename);
        }
    }

    public void setSeparator(String separator) {
        csvprops.put("separator", separator);
    }

    public void supressHeader(String flag) {
        csvprops.put("suppressHeaders", flag);
    }

    public void fileExtension(String ext) {
        csvprops.put("fileExtension", "." + ext);
    }

    public void setHeaderLine(String header) {
        csvprops.put("suppressHeaders", "true");
        csvprops.put("headerline", header);
    }

    public void setColumnType(String coltype) {
        csvprops.put("columnTypes", coltype);
    }

    public List getData(String qry) throws SQLException {

        List data = new ArrayList();

        ResultSet rs = csvse.getResultSet(qry);
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();

        System.out.println("Column Count :" + colCount);

        while (rs.next()) {
            List row = new ArrayList();
            for (int col = 1; col <= colCount; col++) {
                row.add(rs.getObject(col));
            }
            data.add(row);
        }
        rs.close();
        System.out.println("Data Count: " + data.size());
        return data;
    }
    /*
    public static void main(String args[]) throws Exception{
        
        CSVDataWrapper csvdataobj1 = new CSVDataWrapper();
        //csvdataobj1.setHeaderLine("Surogate_id,Product_id,Product_name,Quantity,Price,group,flag_1,flag_2,flag_3,start_dt,end_dt");
        csvdataobj1.setHeaderLine("col1,col2,col3,col4,col5,col6,col7,col8,col9,col10,col11,col12");
        csvdataobj1.SetConnection("E:\\CSV AUTOMATION\\CSV-Files\\output\\Test1\\triggers0608.csv");
        List data1 = csvdataobj1.getData("select * from triggers0608");
        System.out.println("Data Size 1:"+ data1.size());
        
        System.out.println("\n");
        
        CSVDataWrapper csvdataobj2 = new CSVDataWrapper();
        //csvdataobj2.setHeaderLine("Product_id,Product_name,Quantity,Price,group,flag_1,flag_2,flag_3,start_dt,end_dt");
        csvdataobj2.setHeaderLine("col1,col2,col3,col4,col5,col6,col7,col8,col9,col10,col11,col12");
        csvdataobj2.SetConnection("E:\\CSV AUTOMATION\\CSV-Files\\output\\Test1\\triggers0609.csv");
        //where product_id = '125942' order by product_id limit 100
        List data2 = csvdataobj2.getData("select * from triggers0609");
        System.out.println("Data Size 2:"+ data2.size());
        System.out.println("\n");
        
        //comparison
        ComparisonEngine ce = new ComparisonEngine();
        System.out.println(ce.getUnSrcData(data1, data2).size());
        System.out.println("\n");
        System.out.println(ce.getUnTrgData(data1, data2).size());
       
        
        
        //Min and Max calculation for Thread Modeler
        List data3 = csvdataobj1.getData("select min(Surogate_id) from 0605category");
        System.out.println("Min : "+data3);
        List data4 = csvdataobj1.getData("select max(Surogate_id) from 0605category");
        System.out.println("Max : "+data4);
        
        //Calculating avg Min and Max Bounds
        List l1 = (List)data3.get(0);
        int min = Integer.valueOf(l1.get(0).toString());
        System.out.println("Min : "+min);

        List l2 = (List)data4.get(0);
        int max = Integer.valueOf(l2.get(0).toString());
        System.out.println("Max : "+max);
        
        //For Default four threads
        short splitsize =  (short) (min+max/4);
        System.out.println("splitsize : "+splitsize);
        
        //Testing Split size
        List data5 = csvdataobj1.getData("select * from 0605category"+" where Surogate_id < "+splitsize);
        System.out.println("splitsize : "+data5);
        List data6 = csvdataobj1.getData("select * from 0605category" + " where Surogate_id >= " + splitsize + " and Surogate_id < " + splitsize*2);
        System.out.println("splitsize : " + data6);
        List data7 = csvdataobj1.getData("select * from 0605category" + " where Surogate_id >= " + splitsize*2 + " and Surogate_id < " + splitsize*3);
        System.out.println("splitsize : " + data7);
        List data8 = csvdataobj1.getData("select * from 0605category" + " where Surogate_id >= " + splitsize*3 + " and Surogate_id < " + (splitsize*4+1));
        System.out.println("splitsize : " + data8);
        
        
    }
     */

}
