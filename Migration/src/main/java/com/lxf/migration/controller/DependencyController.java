package com.lxf.migration.controller;

import com.lxf.migration.algorithm.AdjacencyListGraph;
import com.lxf.migration.dao.SourceCodeDao;
import com.lxf.migration.dao.impl.SourceCodeDaoImpl;
import com.lxf.migration.exception.InvalidRequestException;
import com.lxf.migration.factory.BFSFactory;
import com.lxf.migration.file.SourceCode;
import com.lxf.migration.file.ZipFile;
import com.lxf.migration.file.impl.JgraphtGraphPictureImpl;
import com.lxf.migration.pojo.*;
import com.lxf.migration.service.BFS;
import com.lxf.migration.service.SourceCodeService;
import com.lxf.migration.service.SourceCodeParalleService;
import com.lxf.migration.thread.impl.BFSThreadPool;
import com.lxf.migration.thread.impl.ThreadPoolImpl;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

@Tag(name = "代码迁移端点服务")
@RestController
@Scope("request")
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

    @Autowired
    private SourceCodeParalleService sourceCodeParalleService;

    //返回所有节点对象数组
    @CrossOrigin(origins = "*")//允许跨域
    @PostMapping("/getAllDependencies")
    public ResponseEntity<List<Node>> createPayment(
            @RequestHeader(required = false) String requestId,
            @RequestBody Node node
    ) {
        bfs.init();
        bfs.setDataSource(node.dataSource);
        bfs.setStartNode(node);
        bfs.setDisplaySourceCode(false);
        bfs.Traverse();
        List<Node> stack = new ArrayList<>(bfs.getStack());

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
            @RequestHeader(required = false) boolean backupFlag,
            @Parameter(description = "nodes数组字符串")
            @RequestBody List<Node> nodes
    ) throws IOException {

        if (!backupFlag) {
            //不备份文件
            sourceCodeParalleService.getSourceCode(nodes);
            File file = sourceCode.getFile(nodes);


            InputStreamResource isr = file.getFileStream();
            String folderName = file.getFolderName();

            return ResponseEntity.ok()

                    .header("Content-Disposition", "attachment; filename=" + folderName + ".zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Access-Control-Expose-Headers", "Content-Disposition")
                    // 解决跨域问题，比如response打印Content-Disposition为空
                    .body(isr);



        } else {  //备份文件

            sourceCodeParalleService.getSourceCode(nodes);
            File localFile = sourceCode.getFile(nodes);
            String folderName = localFile.getFolderName();
            //再拷贝一个remote数组，只保留Update Delete
//            List<Node> remoteNodes = nodes.stream().filter(
//                    node ->  node.getMode().equals("Update") || node.getMode().equals("Delete")
//            ).map(node->{node.setDataSource("remote") ;return node;}).collect(Collectors.toList());


             List<Node> remoteNodes=    sourceCodeParalleService.getBackupNodes(nodes);
            File remoteFile = sourceCode.getFile(remoteNodes);

            InputStreamResource localResource  = localFile.getFileStream();
            InputStreamResource remoteResource  = remoteFile.getFileStream();
            ZipFile zipFile =new ZipFile();
            InputStreamResource   ZipInputStreamResource= zipFile.createNestedZip(localResource,remoteResource);

            return ResponseEntity.ok()

                    .header("Content-Disposition", "attachment; filename=" + folderName + ".zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Access-Control-Expose-Headers", "Content-Disposition")
                    // 解决跨域问题，比如response打印Content-Disposition为空
                    .body(ZipInputStreamResource);

        }


    }

    @Operation(summary = "传入node,返回结构化查询树")
    @CrossOrigin(origins = "*")//允许跨域
    @PostMapping(value = "/queryTreeList")
    public ResponseEntity<List<TreeListNode>> queryTreeList(
            @Parameter(description = "顶层节点")
            @RequestBody Node node) {

        bfs.init();
        bfs.setDataSource(node.dataSource);
        bfs.setStartNode(node);
        bfs.setDisplaySourceCode(false);
        bfs.Traverse();
        List<TreeListNode> treeListNodes = bfs.getTreeListNodes();


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(treeListNodes);
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
        var node2 = new Node(owner2, objectName2, objectType2);
        bfs.init();
        bfs.setDataSource(dataSource1);
        bfs.setStartNode(node1);
        bfs.setDisplaySourceCode(true);
        RemoteBfs.init();
        RemoteBfs.setDataSource(dataSource2);
        RemoteBfs.setStartNode(node2);
        RemoteBfs.setDisplaySourceCode(true);
//        //两个线程分别执行bfs
//        Thread thread1 = new Thread(bfs);
//        Thread thread2 = new Thread(RemoteBfs);
//        thread1.start();
//        thread2.start();
//
//        // 等待两个线程执行完毕
//        try {
//            thread1.join();
//            thread2.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

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

    @Operation(summary = "传入目标和源对象，返回有差异的节点")
    @CrossOrigin(origins = "*")//允许跨域
    @PostMapping("/getCompareNodes")
    @ResponseBody
    public ResponseEntity<List<Node>> getCompareNodes(

            @RequestBody CompareNode compareNode

    ) throws IOException {


        Node node1 = new Node(compareNode.owner, compareNode.objectName, compareNode.objectType);
        Node node2 = new Node(compareNode.owner, compareNode.objectName, compareNode.objectType);
        node1.setShowSourceCode(true);//不显示源码
        node2.setShowSourceCode(true);//不显示源码

        node1.setDataSource(compareNode.dataSource);
        node2.setDataSource(compareNode.remoteDataSource);

//        BFS bfs = BFSFactory.createBFS(node1);//工厂生产出来的不能自动注入
//        BFS RemoteBfs = BFSFactory.createBFS(node2);

        bfs.start(node1);
        bfs.setDataSource(node1.dataSource);//AOP切面只能捕捉到对象外的调用，只能加到这里
        bfs.setDisplaySourceCode(true);
        RemoteBfs.start(node2);
        RemoteBfs.setDataSource(node2.dataSource);
        RemoteBfs.setDisplaySourceCode(true);

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
        List<Node> downloadNode = sourceCode.getCompareNode(localNodes, RemoteNodes);


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(downloadNode);

    }

    @CrossOrigin(origins = "*")
    @GetMapping("/checkRedisService")
    public ResponseEntity<Boolean> checkRedisHealth() {
        try {
            // 尝试连接 Redis，通过执行简单的操作来确定 Redis 服务是否生效
            redisTemplate.opsForValue().get("health_check_key");

            // 如果 Redis 连接正常，返回 HTTP 200 OK
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            // 如果连接失败或发生异常，返回 HTTP 503 Service Unavailable
            e.printStackTrace();
            return ResponseEntity.ok(false);
        }
    }



    @CrossOrigin(origins = "*")
    @GetMapping("/checkDatabaseStatus")
    public ResponseEntity<String> checkSpringBootService() {
        try {


            // 如果 Redis 连接正常，返回 HTTP 200 OK
            return ResponseEntity.ok("SpringBoot is up and running.");
        } catch (Exception e) {
            // 如果连接失败或发生异常，返回 HTTP 503 Service Unavailable
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("SpringBoot is not available.");
        }
    }

}