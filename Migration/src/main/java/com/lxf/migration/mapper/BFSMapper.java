package com.lxf.migration.mapper;



import com.lxf.migration.pojo.DbaObjects;
import com.lxf.migration.pojo.Node;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository
public interface BFSMapper {
    DbaObjects selectDbaObjects(Map map);
    List<DbaObjects> selecteDirectDependencies(Map map);
    List<DbaObjects> selectIndexes(Map map);
    List<DbaObjects> selectSynonym(Map map);
    List<DbaObjects> selectDBlink(Map map);
    List<DbaObjects> selectTrigger(Map map);
    List<Package> selectPackage(Node node);//直接传node进来，不然每次要转map
    List<Package> selectPackageBody(Node node);
    String selectDataBase();
    void callGetHashCode(Map map);
    void callGetDDL(Map map);
}
