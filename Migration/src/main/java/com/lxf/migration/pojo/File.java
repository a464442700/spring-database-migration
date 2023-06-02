package com.lxf.migration.pojo;

import org.springframework.core.io.InputStreamResource;

public class File {
    public InputStreamResource fileStream;
    public  String folderName;

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public InputStreamResource getFileStream() {
        return fileStream;
    }

    public void setFileStream(InputStreamResource fileStream) {
        this.fileStream = fileStream;
    }
}
