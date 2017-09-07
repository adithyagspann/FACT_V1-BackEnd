
package engines;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class ComparsionEngine {
        
    public List getUnSrcData(List src,List trg){
        System.out.println("Processing the Differences between OLD and NEW File");
        return (ArrayList)CollectionUtils.removeAll(src,trg);
    }
    
    public List getUnTrgData(List src,List trg){
        System.out.println("Processing the Differences between NEW and OLD File");
        return  (ArrayList) CollectionUtils.removeAll(trg, src);
    }
    
}
