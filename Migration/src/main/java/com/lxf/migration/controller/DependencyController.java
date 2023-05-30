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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Stack;
import org.jgrapht.graph.DefaultEdge;
@RestController
public class DependencyController {
    @Autowired
    private BFS bfs;
    private Stack<Node> stack;

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


    @RequestMapping(value = "/getAllDependenciesGraphTest", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] generateGraph() {
        DirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
        directedGraph.addVertex("A");
        directedGraph.addVertex("B");
        directedGraph.addVertex("C");
        directedGraph.addEdge("A", "B");
        directedGraph.addEdge("B", "C");
        directedGraph.addEdge("C", "A");
        JGraphXAdapter<String, DefaultEdge> graphAdapter = new JGraphXAdapter<>(directedGraph);
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());
        BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "PNG", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }


    @PostMapping(value = "/allDependenciesGraph", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] allDependenciesGraph(
            @RequestBody StartBFS startBFS

    ) {

        bfs.setStartNode(startBFS.node);
        bfs.setDisplaySourceCode(startBFS.showSourceCode);
        bfs.Traverse();
        var getGraph = bfs.getGraph();
        System.out.println(getGraph);
        JgraphtGraphPictureImpl jgraphtGraphPicture = new JgraphtGraphPictureImpl();
        return jgraphtGraphPicture.GetBinaryPicture(getGraph);
    }



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
        System.out.println(getGraph);
        JgraphtGraphPictureImpl jgraphtGraphPicture = new JgraphtGraphPictureImpl();
        return jgraphtGraphPicture.GetBinaryPicture(getGraph);
    }

}