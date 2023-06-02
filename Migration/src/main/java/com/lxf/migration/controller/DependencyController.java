package com.lxf.migration.controller;

import com.lxf.migration.output.SourceCode;
import com.lxf.migration.output.impl.JgraphtGraphPictureImpl;
import com.lxf.migration.pojo.File;
import com.lxf.migration.pojo.Node;
import com.lxf.migration.pojo.StartBFS;
import com.lxf.migration.service.BFS;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jgrapht.graph.DefaultEdge;

@RestController
public class DependencyController {
    @Autowired
    private BFS bfs;
    @Autowired
    private SourceCode sourceCode;
    private Stack<Node> stack;

    //返回所有节点对象属性
    @PostMapping("/getAllDependencies")
    public ResponseEntity<Stack<Node>> createPayment(
            @RequestHeader(required = false) String requestId,
            @RequestBody StartBFS startBFS
    ) {

        bfs.setStartNode(startBFS.node);
        bfs.setDisplaySourceCode(startBFS.showSourceCode);
        bfs.Traverse();
        stack = bfs.getStack();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stack);
    }


    //post请求返回节点顺序png图
    @PostMapping(value = "/allDependenciesGraph", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] allDependenciesGraph(
            @RequestBody StartBFS startBFS

    ) {

        bfs.setStartNode(startBFS.node);
        bfs.setDisplaySourceCode(startBFS.showSourceCode);
        bfs.Traverse();
        var getGraph = bfs.getGraph();

        JgraphtGraphPictureImpl jgraphtGraphPicture = new JgraphtGraphPictureImpl();
        return jgraphtGraphPicture.GetBinaryPicture(getGraph);
    }


    //get请求返回节点顺序png图
    @GetMapping(value = "/getAllDependenciesGraph", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getAllDependenciesGraph(
            @RequestParam String owner,
            @RequestParam String objectName,
            @RequestParam String objectType
    ) {
        var node = new Node(owner, objectName, objectType);
        bfs.setStartNode(node);
        bfs.setDisplaySourceCode(false);
        bfs.Traverse();
        var getGraph = bfs.getGraph();

        JgraphtGraphPictureImpl jgraphtGraphPicture = new JgraphtGraphPictureImpl();
        return jgraphtGraphPicture.GetBinaryPicture(getGraph);
    }


    //get请求下载所有依赖文件
    @GetMapping(value = "/downloadAllDependenciesFile", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadAllDependenciesFile(
            @RequestParam String owner,
            @RequestParam String objectName,
            @RequestParam String objectType
    ) throws IOException {
        var node = new Node(owner, objectName, objectType);
        bfs.init();
        bfs.setStartNode(node);
        bfs.setDisplaySourceCode(true);
        bfs.Traverse();
        File file = sourceCode.getFile(bfs.getStack());


        InputStreamResource isr = file.getFileStream();
        String folderName = file.getFolderName();

        return ResponseEntity.ok()
                .contentLength(isr.length())
                .header("Content-Disposition", "attachment; filename=" + folderName + ".zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(isr);


    }

    private void addToZipFile(String filename, String content, ZipOutputStream zos) throws IOException {
        ZipEntry zipEntry = new ZipEntry(filename);
        zos.putNextEntry(zipEntry);
        zos.write(content.getBytes());
        zos.closeEntry();
    }

    @GetMapping(value = "/download")
    public ResponseEntity<InputStreamResource> download() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        addToZipFile("file1.txt", "1", zos);
        addToZipFile("file2.txt", "2", zos);

        zos.close();
        baos.close();
        InputStreamResource isr = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));

        return ResponseEntity.ok()

                .header("Content-Disposition", "attachment; filename=files.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(isr);
    }

}