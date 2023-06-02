package com.lxf.migration.output.impl;

import com.lxf.migration.output.SourceCodeToFile;
import com.lxf.migration.pojo.File;
import com.lxf.migration.pojo.Node;
import oracle.security.crypto.core.SREntropySource;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class SourceCodeToFileImpl implements SourceCodeToFile {
    public final Date date = new Date();
    public final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");


    private Boolean dealFlag = false;

    private Map<String, Object> getMaxLevel(List<Node> nodes) {
        Node rootNode = null;
        Integer maxLevel = 0;
        for (Node node : nodes) {
            if (node.getLevel().equals(0)) {
                rootNode = node;
            }
            if (node.getLevel() > maxLevel) {
                maxLevel = node.getLevel();
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("maxLevel", maxLevel);
        map.put("rootNode", rootNode);
        return map;
    }

    public void dealNodes(List<Node> nodes) {
        if (!dealFlag) {
            Map<String, Object> map = getMaxLevel(nodes);


            nodes.forEach((node) -> {
                node.setMaxLevel((Integer) map.get("maxLevel"));
                node.setRootNode((Node) map.get("rootNode"));
            });
            dealFlag = true;
        }
    }

    public String getFolderName(Node node) {

        return "[" + node.getRootNode().getDatabase() + "]" + "[" +
                node.getRootNode().owner + "." +
                node.getRootNode().objectName + "." + node.getRootNode().objectType + "]" +

                sdf.format(date).toString();

    }

    private String getFileName(Node node) {
        String fileName = "[" + StringUtils.leftPad(String.valueOf(node.getMaxLevel() - node.getLevel()), 3, "0") + "]"
                + "[" + node.getDatabase() + "]"
                + "[" + node.objectType + "]"
                + node.owner + "."
                + node.objectName;

        String extension = "sql";
        if (node.objectType.equals("PACKAGE")) {
            extension = "pck";

        }
        return fileName + "." + extension;

    }

    private String getContent(Node node) {
        return node.getSourceCode();
    }

    private void addToZipFile(String filename, String content, ZipOutputStream zos) throws IOException {
        ZipEntry zipEntry = new ZipEntry(filename);
        zos.putNextEntry(zipEntry);
        zos.write(content.getBytes());
        zos.closeEntry();
    }

    public InputStreamResource getFileStream(List<Node> nodes) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        for (Node node : nodes) {

            addToZipFile(getFileName(node), getContent(node), zos);


        }
        zos.close();
        baos.close();
        InputStreamResource isr = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
        return isr;
    }

    public File getFile(List<Node> nodes) throws IOException {
        dealNodes(nodes);
        File file = new File();
        String folderName = getFolderName(nodes.get(0));
        InputStreamResource inputStreamResource = getFileStream(nodes);

        file.setFolderName(folderName);
        file.setFileStream(inputStreamResource);
        return file;
    }

}
