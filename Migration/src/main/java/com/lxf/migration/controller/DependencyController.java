package com.lxf.migration.controller;

import com.lxf.migration.file.SourceCode;
import com.lxf.migration.file.impl.JgraphtGraphPictureImpl;
import com.lxf.migration.pojo.File;
import com.lxf.migration.pojo.Node;
import com.lxf.migration.service.BFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

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
            @RequestBody Node node
    ) {
        bfs.setDataSource(node.dataSource);
        bfs.setStartNode(node);
        //    bfs.setDisplaySourceCode(true);
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
            @RequestBody Node node

    ) {

        bfs.setDataSource(node.dataSource);
        bfs.setStartNode(node);


        bfs.Traverse();
        var getGraph = bfs.getGraph();

        JgraphtGraphPictureImpl jgraphtGraphPicture = new JgraphtGraphPictureImpl();
        return jgraphtGraphPicture.GetBinaryPicture(getGraph);
    }


    //get请求返回节点顺序png图
    @GetMapping(value = "/downAllDependenciesGraph", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] downAllDependenciesGraph(
            @RequestParam String owner,
            @RequestParam String objectName,
            @RequestParam String objectType,
            @RequestParam String dataSource
    ) {
        var node = new Node(owner, objectName, objectType);
        bfs.setDataSource(dataSource);
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
            @RequestParam String objectType,
            @RequestParam String dataSource
    ) throws IOException {
        var node = new Node(owner, objectName, objectType);
        bfs.init();
        bfs.setDataSource(dataSource);
        bfs.setStartNode(node);
        bfs.setDisplaySourceCode(true);
        bfs.Traverse();
        File file = sourceCode.getFile(bfs.getStack());


        InputStreamResource isr = file.getFileStream();
        String folderName = file.getFolderName();

        return ResponseEntity.ok()

                .header("Content-Disposition", "attachment; filename=" + folderName + ".zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(isr);


    }


    @GetMapping(value = "/downloadCompareDependenciesFile", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadCompareDependenciesFile(
            @RequestParam String owner1,
            @RequestParam String objectName1,
            @RequestParam String objectType1,
            @RequestParam String dataSource1,
            @RequestParam String owner2,
            @RequestParam String objectName2,
            @RequestParam String objectType2,
            @RequestParam String dataSource2

    ) throws IOException {
        var node1 = new Node(owner1, objectName1, objectType1);
        bfs.init();
        bfs.setDataSource(dataSource1);
        bfs.setStartNode(node1);
        bfs.setDisplaySourceCode(true);
        bfs.Traverse();
        List<Node> localNodes =bfs.getStack();

        var node2= new Node(owner2, objectName2, objectType2);
        bfs.init();
        bfs.setDataSource(dataSource2);
        bfs.setStartNode(node2);
        bfs.setDisplaySourceCode(true);
        bfs.Traverse();
        List<Node> RemoteNodes =bfs.getStack();

        File file = sourceCode.getCompareFile(localNodes,RemoteNodes);



        InputStreamResource isr = file.getFileStream();
        String folderName = file.getFolderName();

        return ResponseEntity.ok()

                .header("Content-Disposition", "attachment; filename=" + folderName + ".zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(isr);


    }


}