package com.lxf.migration.controller;

import com.lxf.migration.algorithm.AdjacencyListGraph;
import com.lxf.migration.dao.SourceCodeDao;
import com.lxf.migration.dao.impl.SourceCodeDaoImpl;
import com.lxf.migration.exception.InvalidRequestException;
import com.lxf.migration.file.SourceCode;
import com.lxf.migration.file.impl.JgraphtGraphPictureImpl;
import com.lxf.migration.pojo.Response;
import com.lxf.migration.pojo.File;
import com.lxf.migration.pojo.Node;
import com.lxf.migration.service.BFS;
import com.lxf.migration.service.SourceCodeService;
import com.lxf.migration.thread.impl.BFSThreadPool;
import com.lxf.migration.thread.impl.ThreadPoolImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

@Tag(name = "代码迁移端点服务")
@RestController
public class DependencyController {
    @Autowired
    private BFS bfs;

    @Autowired
   private SourceCodeService sourceCodeService;
    @Autowired
    private BFS RemoteBfs;
    @Autowired
    private SourceCode sourceCode;

    // @Autowired
    // private BFSThreadPool threadPool;
    private Stack<Node> stack;

    @Autowired
    private RedisTemplate redisTemplate;

    //返回所有节点对象数组
    @CrossOrigin(origins = "*")//允许跨域
    @PostMapping("/getAllDependencies")
    public ResponseEntity<Stack<Node>> createPayment(
            @RequestHeader(required = false) String requestId,
            @RequestBody Node node
    ) {
        bfs.init();
        bfs.setDataSource(node.dataSource);
        bfs.setStartNode(node);
        bfs.setDisplaySourceCode(false);
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
        bfs.init();
        bfs.setDataSource(node.dataSource);
        bfs.setStartNode(node);


        bfs.Traverse();
        var getGraph = bfs.getGraph();

        JgraphtGraphPictureImpl jgraphtGraphPicture = new JgraphtGraphPictureImpl();
        return jgraphtGraphPicture.GetBinaryPicture(getGraph);
    }

    @PostMapping(value = "/allDependenciesGraphCode")
    @ResponseBody
    public ResponseEntity<AdjacencyListGraph<Node>> allDependenciesGraphCode(
            @RequestBody Node node

    ) {
        bfs.init();
        bfs.setDataSource(node.dataSource);
        bfs.setStartNode(node);
        bfs.setDisplaySourceCode(false);

        bfs.Traverse();
        AdjacencyListGraph<Node> graph = bfs.getGraph();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(graph);

    }


    //get请求返回节点顺序png图
    @PostMapping(value = "/downAllDependenciesGraph", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] downAllDependenciesGraph(
            @RequestBody Node node
    ) {
        bfs.init();
        bfs.setDataSource(node.dataSource);
        bfs.setStartNode(node);
        bfs.setDisplaySourceCode(false);
        bfs.Traverse();
        var getGraph = bfs.getGraph();

        JgraphtGraphPictureImpl jgraphtGraphPicture = new JgraphtGraphPictureImpl();
        return jgraphtGraphPicture.GetBinaryPicture(getGraph);
    }


    //get请求下载所有依赖文件
    @PostMapping(value = "/downloadAllDependenciesFile", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadAllDependenciesFile(
            @RequestBody Node node
    ) throws IOException {

        bfs.init();
        bfs.setDataSource(node.dataSource);
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

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseBody
    public ResponseEntity<Response> handleInvalidRequest(InvalidRequestException ex) {
        Response e = new Response("E", ex.getMessage());
        return ResponseEntity.badRequest().body(e);
    }
    @CrossOrigin(origins = "*")//允许跨域
    @Operation(summary = "传入nodes数组，返回源代码")
    @PostMapping(value = "/downloadFileByNodes", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadFileByNodes(
            @Parameter(description = "nodes数组字符串")
            @RequestBody List<Node> nodes
    ) throws IOException {

        sourceCodeService.getSourceCode(nodes);
        File file = sourceCode.getFile(nodes);


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
//        var node1 = new Node(owner1, objectName1, objectType1);
//        bfs.init();
//        bfs.setDataSource(dataSource1);
//        bfs.setStartNode(node1);
//        bfs.setDisplaySourceCode(true);
//        bfs.Traverse();
//        List<Node> localNodes =bfs.getStack();
//
//        var node2= new Node(owner2, objectName2, objectType2);
//        bfs.init();
//        bfs.setDataSource(dataSource2);
//        bfs.setStartNode(node2);
//        bfs.setDisplaySourceCode(true);
//        bfs.Traverse();
//        List<Node> RemoteNodes =bfs.getStack();


        var node1 = new Node(owner1, objectName1, objectType1);
        var node2 = new Node(owner2, objectName2, objectType2);
        bfs.init();
        bfs.setDataSource(dataSource1);
        bfs.setStartNode(node1);
        bfs.setDisplaySourceCode(true);
        RemoteBfs.init();
        RemoteBfs.setDataSource(dataSource2);
        RemoteBfs.setStartNode(node2);
        RemoteBfs.setDisplaySourceCode(true);
        //两个线程分别执行bfs
        Thread thread1 = new Thread(bfs);
        Thread thread2 = new Thread(RemoteBfs);
        thread1.start();
        thread2.start();

        // 等待两个线程执行完毕
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Node> localNodes = bfs.getStack();
        List<Node> RemoteNodes = RemoteBfs.getStack();
        File file = sourceCode.getCompareFile(localNodes, RemoteNodes);


        InputStreamResource isr = file.getFileStream();
        String folderName = file.getFolderName();

        return ResponseEntity.ok()

                .header("Content-Disposition", "attachment; filename=" + folderName + ".zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(isr);


    }


    @GetMapping("/checkRedisService")
    public ResponseEntity<String> checkRedisHealth() {
        try {
            // 尝试连接 Redis，通过执行简单的操作来确定 Redis 服务是否生效
            redisTemplate.opsForValue().get("health_check_key");

            // 如果 Redis 连接正常，返回 HTTP 200 OK
            return ResponseEntity.ok("Redis is up and running.");
        } catch (Exception e) {
            // 如果连接失败或发生异常，返回 HTTP 503 Service Unavailable
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Redis is not available.");
        }
    }


}