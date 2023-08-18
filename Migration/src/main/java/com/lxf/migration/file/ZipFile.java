package com.lxf.migration.file;

import org.springframework.core.io.InputStreamResource;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import java.io.ByteArrayOutputStream;


public class ZipFile {


    public InputStreamResource createNestedZip(InputStreamResource resource1, InputStreamResource resource2) throws IOException {
        ByteArrayOutputStream outerBaos = new ByteArrayOutputStream();
        ZipOutputStream outerZos = new ZipOutputStream(outerBaos);

        // 添加第一个压缩包
        byte[] innerZipData1 = getResourceAsByteArray(resource1);
        addToZipFile("迁移文件.zip", innerZipData1, outerZos);

        // 添加第二个压缩包
        byte[] innerZipData2 = getResourceAsByteArray(resource2);
        addToZipFile("备份文件.zip", innerZipData2, outerZos);

        outerZos.close();
        outerBaos.close();

        InputStreamResource isr = new InputStreamResource(new ByteArrayInputStream(outerBaos.toByteArray()));
        return isr;
    }

    private byte[] getResourceAsByteArray(InputStreamResource resource) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamUtils.copy(resource.getInputStream(), baos);
        return baos.toByteArray();
    }

    private void addToZipFile(String fileName, byte[] content, ZipOutputStream zos) throws IOException {
        ZipEntry zipEntry = new ZipEntry(fileName);
        zos.putNextEntry(zipEntry);
        zos.write(content);
        zos.closeEntry();
    }

}