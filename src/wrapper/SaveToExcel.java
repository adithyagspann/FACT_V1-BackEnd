/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wrapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 *
 * @author Ravindra
 */
public class SaveToExcel {

    WritableWorkbook wbook;
    WritableSheet shSheet;
    int sheetno = 0;

    public SaveToExcel(String filenamewithpath) throws Exception {

        if (filenamewithpath.isEmpty()) {
            System.out.println("[Error] : Missing file name");
            throw new Exception("[Error] : Missing file name");

        } else {
            wbook = Workbook.createWorkbook(new File(filenamewithpath));
        }

    }

    public void saveSheet(String sheetname, String header, List data) throws Exception {

        if (sheetname.isEmpty() || header.isEmpty()) {

            System.out.println("[Error] : Missing Source or Target Information");
            throw new Exception("[Error] : Missing Source or Target Information");

        } else {

            /*if(data.isEmpty()){
                System.out.println("[Error] : Empty Data Exception");
                throw new Exception("[Error] : Empty Data Exception");    
            } 
            else{*/
            shSheet = wbook.createSheet(sheetname, sheetno++);
            String[] colNames = header.split(",");
            for (int l = 0; l < colNames.length; l++) {
                jxl.write.Label labTemp = new jxl.write.Label(l, 0, colNames[l]);
                shSheet.addCell(labTemp);

            }
            for (int m = 0; m < data.size(); m++) {

                List inddata = (List) data.get(m);

                for (int l = 0; l < inddata.size(); l++) {
                    String celldata = (String) inddata.get(l);
                    jxl.write.Label labTemp = new jxl.write.Label(l, m + 1, celldata);
                    shSheet.addCell(labTemp);

                }

            }
            // }
        }
    }

    public void writeClose() throws IOException, WriteException {
        wbook.write();
        wbook.close();
    }

}
