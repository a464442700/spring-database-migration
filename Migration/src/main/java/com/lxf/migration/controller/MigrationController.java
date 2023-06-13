package com.lxf.migration.controller;

import com.lxf.migration.file.SourceCode;
import com.lxf.migration.pojo.DataSource;
import com.lxf.migration.pojo.File;
import com.lxf.migration.pojo.Node;
import com.lxf.migration.service.BFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MigrationController {
    @Autowired
    private BFS localBfs;

    @Autowired
    private BFS remoteBfs;
    @Autowired
    private SourceCode sourceCode;

    @GetMapping("/")
    public String HomePage(Model model) {
        List<DataSource> dataSources = new ArrayList<DataSource>();
        localBfs.setDataSource("local");
        remoteBfs.setDataSource("remote");
        String database1 = localBfs.getDataBase();
        String database2 = remoteBfs.getDataBase();

        dataSources.add(new DataSource(database1, "local"));
        dataSources.add(new DataSource(database2, "remote"));


        model.addAttribute("dataSources", dataSources);

        return "HomePage";
    }

    @PostMapping("/downloadFile")
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadAllDependenciesFile(
            @RequestParam String owner,
            @RequestParam String objectName,
            @RequestParam String objectType,
            @RequestParam("dataSource") String dataSource

    ) throws IOException {

        var node = new Node(owner, objectName, objectType);
        localBfs.init();//清空所有栈，map，起点，数据源等信息
        localBfs.setDataSource(dataSource);//设置数据源
        localBfs.setStartNode(node);//设置广度优先搜索的起点
        localBfs.setDisplaySourceCode(true);//搜索源码
        localBfs.Traverse();//开始搜索
        File file = sourceCode.getFile(localBfs.getStack());//获取文件


        InputStreamResource isr = file.getFileStream();
        String folderName = file.getFolderName();

        return ResponseEntity.ok()

                .header("Content-Disposition", "attachment; filename=" + folderName + ".zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(isr);

    }

    @PostMapping("/downloadCompareFile")
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadCompareDependenciesFile(
            @RequestParam String owner,
            @RequestParam String objectName,
            @RequestParam String objectType,
            @RequestParam("dataSource") String dataSource,
            @RequestParam("remoteDataSource") String remoteDataSource

    ) throws IOException {
        System.out.println(dataSource);
        System.out.println(remoteDataSource);
        var node1 = new Node(owner, objectName, objectType);
        var node2 = new Node(owner, objectName, objectType);;

        localBfs.setDataSource(dataSource);
        localBfs.setStartNode(node1);
        localBfs.setDisplaySourceCode(true);

        remoteBfs.setDataSource(remoteDataSource);
        remoteBfs.setStartNode(node2);
        remoteBfs.setDisplaySourceCode(true);

        Thread thread1 = new Thread(localBfs);
        Thread thread2 = new Thread(remoteBfs);
        thread1.start();
        thread2.start();

        // 等待两个线程执行完毕
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Node> localNodes = localBfs.getStack();
        List<Node> RemoteNodes = remoteBfs.getStack();
        File file = sourceCode.getCompareFile(localNodes, RemoteNodes);


        InputStreamResource isr = file.getFileStream();
        String folderName = file.getFolderName();

        return ResponseEntity.ok()

                .header("Content-Disposition", "attachment; filename=" + folderName + ".zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(isr);

    }


}
