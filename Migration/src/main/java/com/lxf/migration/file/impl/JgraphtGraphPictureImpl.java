package com.lxf.migration.file.impl;

import com.lxf.migration.algorithm.AdjacencyListGraph;
import com.lxf.migration.file.GraphPicture;
import com.lxf.migration.pojo.Node;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
//import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import java.io.ByteArrayOutputStream;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class JgraphtGraphPictureImpl implements GraphPicture {


    @Override
    public byte[] GetBinaryPicture(AdjacencyListGraph<Node> adjacencyListGraph) {

        DirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (Node node : adjacencyListGraph.getVertex()) {
           directedGraph.addVertex(node.getNodeVertex());
        }



        for (Node node : adjacencyListGraph.getSide().keySet()) {
            for (Node sideNode : adjacencyListGraph.getSide().get(node)) {
                directedGraph.addEdge(node.getNodeVertex(),sideNode.getNodeVertex());
            }
        }



        JGraphXAdapter<String, DefaultEdge> graphAdapter = new JGraphXAdapter(directedGraph);

        graphAdapter.getEdgeToCellMap().forEach((edge,cell)->cell.setValue(null));

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

}
