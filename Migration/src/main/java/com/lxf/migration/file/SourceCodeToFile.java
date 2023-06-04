package com.lxf.migration.file;

import com.lxf.migration.pojo.File;
import com.lxf.migration.pojo.Node;

import java.io.IOException;
import java.util.List;

public interface SourceCodeToFile {
    public File getFile(List<Node> nodes) throws IOException;



}
