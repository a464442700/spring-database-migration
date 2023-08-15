package com.lxf.migration.file;

import com.lxf.migration.pojo.File;
import com.lxf.migration.pojo.Node;

import java.io.IOException;
import java.util.List;

public interface SourceCodeToFile {
    public void dealNodes(List<Node> nodes);
    public File getFile(List<Node> nodes) throws IOException;


    public File getCompareFile(List<Node> localNodes,List<Node> remoteNodes) throws IOException;

    public List<Node> getCompareNode(List<Node> localNodes, List<Node> remoteNodes);

    public  List<Node> getBackupNode(List<Node> localNodes);

}
