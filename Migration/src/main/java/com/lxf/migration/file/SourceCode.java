package com.lxf.migration.file;

import com.lxf.migration.pojo.File;
import com.lxf.migration.pojo.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.io.IOException;
import java.util.List;

@Component
//@RequestScope
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SourceCode {
    @Autowired
    private SourceCodeToFile sourceCodeToFile;


    public File getFile(List<Node> nodes) throws IOException {
        sourceCodeToFile.dealNodes(nodes);//用于获取max level 和root node
        return sourceCodeToFile.getFile(nodes);

    }
    public File getCompareFile(List<Node> localNodes,List<Node> remoteNodes) throws IOException {
        sourceCodeToFile.dealNodes(localNodes);
        sourceCodeToFile.dealNodes(remoteNodes);
        return sourceCodeToFile.getCompareFile(localNodes,remoteNodes);

    }

    public List<Node> getCompareNode(List<Node> localNodes, List<Node> remoteNodes) {

        return sourceCodeToFile.getCompareNode(localNodes,remoteNodes);
    }


    public  List<Node> getBackupNode(List<Node> localNodes){
        return  sourceCodeToFile.getBackupNode(localNodes);

    }

}
