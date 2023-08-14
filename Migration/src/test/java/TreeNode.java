import java.util.ArrayList;
import java.util.List;

class TreeNode {
    public String value;
    public String title;
    public List<TreeNode> children;

    public TreeNode(String value, String title) {
        this.value = value;
        this.title = title;
        this.children = new ArrayList<>();
    }
}
