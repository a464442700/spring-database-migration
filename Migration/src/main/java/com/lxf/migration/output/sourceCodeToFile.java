package com.lxf.migration.output;

import org.springframework.core.io.InputStreamResource;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
public interface sourceCodeToFile {
    public InputStreamResource getFile(byte[] sourceCode );

    public byte[] getSourceCode(  )
}
