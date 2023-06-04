package com.lxf.migration.file;

import com.lxf.migration.pojo.File;
import com.lxf.migration.pojo.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.io.IOException;
import java.util.List;

@Component
@RequestScope
public class SourceCode {
    @Autowired
    private SourceCodeToFile sourceCodeToFile;


    public File getFile(List<Node> nodes) throws IOException {
        return sourceCodeToFile.getFile(nodes);

    }
    public File getCompareFile(List<Node> localNodes,List<Node> remoteNodes) throws IOException {
        return sourceCodeToFile.getCompareFile(localNodes,remoteNodes);

    }


}
