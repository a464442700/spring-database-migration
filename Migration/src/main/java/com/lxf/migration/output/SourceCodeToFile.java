package com.lxf.migration.output;

import com.lxf.migration.pojo.File;
import com.lxf.migration.pojo.Node;
import org.springframework.core.io.InputStreamResource;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import java.io.IOException;
import java.util.List;

public interface SourceCodeToFile {
    public File getFile(List<Node> nodes) throws IOException;



}
