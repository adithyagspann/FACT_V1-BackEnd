
package engines;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class ComparisonEngine {
        
    public List getUnSrcData(List src,List trg){
        return (ArrayList)CollectionUtils.removeAll(src,trg);
    }
    
    public List getUnTrgData(List src,List trg){
        return  (ArrayList) CollectionUtils.removeAll(trg, src);
    }
    
}
