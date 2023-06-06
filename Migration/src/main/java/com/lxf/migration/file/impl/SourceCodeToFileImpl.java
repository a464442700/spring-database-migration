package com.lxf.migration.file.impl;

import com.lxf.migration.file.SourceCodeToFile;
import com.lxf.migration.pojo.File;
import com.lxf.migration.pojo.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
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
  @Override
    public void dealNodes(List<Node> nodes) {
     //   if (!dealFlag) {
            Map<String, Object> map = getMaxLevel(nodes);


            nodes.forEach((node) -> {
                node.setMaxLevel((Integer) map.get("maxLevel"));
                node.setRootNode((Node) map.get("rootNode"));
            });
       //     dealFlag = true;
       // }
    }

    public String getFolderName(Node node) {

        return "[" + node.getRootNode().getDatabase() + "]" + "[" +
                node.getRootNode().owner + "." +
                node.getRootNode().objectName + "." + node.getRootNode().objectType + "]" +

                sdf.format(date).toString();

    }

    private String getFileName(Node node) {

        String fileName = "[" + StringUtils.leftPad(String.valueOf(node.getMaxLevel() - node.getLevel()), 3, "0") + "]"

                + "[" + node.getMode() + "]"
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
        File file = new File();
        if (nodes.isEmpty()) {
            return file;
        }

      //  dealNodes(nodes);

        String folderName = getFolderName(nodes.get(0));
        InputStreamResource inputStreamResource = getFileStream(nodes);

        file.setFolderName(folderName);
        file.setFileStream(inputStreamResource);
        return file;
    }

    public File getCompareFile(List<Node> localNodes, List<Node> remoteNodes) throws IOException {

        return getFile(getCompareNode(localNodes, remoteNodes));
    }

    public String  getDeleteContent(Node node){

        return "DROP"+ " "+node.objectType+" "+node.owner+"."+node.objectName+";";

    }
    public List<Node> getCompareNode(List<Node> localNodes, List<Node> remoteNodes) {
        System.out.println("localNodes:" + localNodes);
        System.out.println("remoteNodes:" + remoteNodes);
        List<Node> result = new ArrayList<Node>();
        //add
        List<Node> AddResult = localNodes.stream()
                .filter(node1 -> remoteNodes.stream()
                        .noneMatch(node2 -> Objects.equals(node1.owner, node2.owner)
                                && Objects.equals(node1.objectType, node2.objectType)
                                && Objects.equals(node1.objectName, node2.objectName)
                        ))
                .collect(Collectors.toList());

        AddResult.stream().forEach(node -> {
            node.setMode("Add");

        });
        //   System.out.println("AddResult:");
        //  System.out.println(AddResult);

        List<Node> UpdateResult = localNodes.stream()
                .filter(node1 -> remoteNodes.stream()
                        .anyMatch(node2 -> Objects.equals(node1.owner, node2.owner)
                                && Objects.equals(node1.objectType, node2.objectType)
                                && Objects.equals(node1.objectName, node2.objectName)
                                && !Objects.equals(node1.sourceCodeHash, node2.sourceCodeHash)
                        ))
                .collect(Collectors.toList());

        UpdateResult.stream().forEach(node -> {
            node.setMode("Update");

        });
        //    System.out.println("UpdateResult:");
        //  System.out.println( UpdateResult);
        List<Node> DeleteResult = remoteNodes.stream()
                .filter(node1 -> localNodes.stream()
                        .noneMatch(node2 -> Objects.equals(node1.owner, node2.owner)
                                && Objects.equals(node1.objectType, node2.objectType)
                                && Objects.equals(node1.objectName, node2.objectName)
                        ))
                .collect(Collectors.toList());

        DeleteResult.stream().forEach(node -> {
            node.setMode("Delete");
            node.setSourceCode(getDeleteContent(node));

        });
        //System.out.println(" DeleteResult:");
        //    System.out.println(  DeleteResult);
        result.addAll(AddResult);
        result.addAll(UpdateResult);
        result.addAll(DeleteResult);
        return result;


    }


}
