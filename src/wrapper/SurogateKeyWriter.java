
package wrapper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class SurogateKeyWriter {
    
    final Charset charset = StandardCharsets.ISO_8859_1;
    final String lineSeparator = System.lineSeparator();
    private Path src;
    
    public SurogateKeyWriter(String srcfilenamewithpath){
        src = Paths.get(srcfilenamewithpath);
    }

    public boolean write() throws IOException{
        
        try (
            
            final BufferedWriter writer = Files.newBufferedWriter(src, charset, StandardOpenOption.CREATE);) {
            int lineNumber = 1;
            for (final String line : Files.readAllLines(src, charset)) {
                writer.write(String.format("%d%s%s%s", lineNumber++, ",", line, lineSeparator));
            }
            writer.flush();
        }
    
        
        return false;
    }
    /*
    public static void main(String arg[]) throws IOException{
        
         SurogateKeyWriter skwobj = new SurogateKeyWriter("E:/CSV AUTOMATION/CSV-Files/output/Test1/0605category.csv");
        //400 MB CSV taking 30 to 40 secs to insert sarogate key
        //SurogateKeyWriter skwobj = new SurogateKeyWriter("E:/CSV_Samples/merch_data2/merch_data2.csv");
        
        skwobj.write();
        
        
    }
*/

    
}
