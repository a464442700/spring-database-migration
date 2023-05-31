package com.lxf.migration.controller;

import com.lxf.migration.output.impl.JgraphtGraphPictureImpl;
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
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jgrapht.graph.DefaultEdge;
@RestController
public class DependencyController {
    @Autowired
    private BFS bfs;
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
     var node =new Node(owner,objectName,objectType);
        bfs.setStartNode(node);
        bfs.setDisplaySourceCode(false);
        bfs.Traverse();
        var getGraph = bfs.getGraph();

        JgraphtGraphPictureImpl jgraphtGraphPicture = new JgraphtGraphPictureImpl();
        return jgraphtGraphPicture.GetBinaryPicture(getGraph);
    }
    private void addToZipFile(String filename, String content, ZipOutputStream zos) throws IOException {
        ZipEntry zipEntry = new ZipEntry(filename);
        zos.putNextEntry(zipEntry);
        zos.write(content.getBytes());
        zos.closeEntry();
    }
    //get请求下载所有依赖文件
    @GetMapping(value = "/downloadAllDependenciesFile", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource>  downloadAllDependenciesFile(
            @RequestParam String owner,
            @RequestParam String objectName,
            @RequestParam String objectType
    ) {
//        var node =new Node(owner,objectName,objectType);
//        bfs.setStartNode(node);
//        bfs.setDisplaySourceCode(true);
//        bfs.Traverse();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        try {
            addToZipFile("file1.txt", "1", zos);
            zos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            addToZipFile("file2.txt", "2", zos);
            baos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        InputStreamResource isr = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=files.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(isr);


    }

}