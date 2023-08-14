import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.ArrayList;
import java.util.List;



public class TreeSerializationExample {
    public static void main(String[] args) throws JsonProcessingException {
        TreeNode nodeC = new TreeNode("C", "C");
        TreeNode nodeD = new TreeNode("D", "D");

        TreeNode nodeB = new TreeNode("B", "B");
        nodeB.children.add(nodeC);
        nodeB.children.add(nodeD);

        TreeNode nodeA = new TreeNode("A", "A");
        nodeA.children.add(nodeB);

        List<TreeNode> tree = new ArrayList<>();
        tree.add(nodeA);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json = objectMapper.writeValueAsString(tree);

        System.out.println(json);
    }
}