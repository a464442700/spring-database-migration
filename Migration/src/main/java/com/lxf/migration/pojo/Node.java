package com.lxf.migration.pojo;

import java.util.Objects;

public class Node {
    public String owner;//四个只读属性
    public String objectName;
    public String objectType;
    private Integer level = 0;
    private String database;
    private String dependence_type;
    private String sourceCode;
    private String sourceCodeHash;
    private Node rootNode;
    private Integer maxLevel;

    public Integer getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(Integer maxLevel) {
        this.maxLevel = maxLevel;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

    public String getSourceCodeHash() {
        return sourceCodeHash;
    }

    public void setSourceCodeHash(String sourceCodeHash) {
        this.sourceCodeHash = sourceCodeHash;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public Integer getLevel() {
        return level;
    }

    public void setDependence_type(String dependence_type) {
        this.dependence_type = dependence_type;
    }

    public void setLevel(Node parentNode) {
        if (parentNode.objectType.equals("TABLE")
                && (this.objectType.equals("INDEX")
                || this.objectType.equals("TRIGGER")
                || this.objectType.equals("SYNONYM")

        )
        ) {
            this.level = parentNode.level - 1;
        } else {
            this.level = parentNode.level + 1;
        }

    }

    @Override
    public String toString() {
        return "Node{" +
                "owner='" + owner + '\'' +
                ", objectName='" + objectName + '\'' +
                ", objectType='" + objectType + '\'' +
                ", level=" + level +
                ", database='" + database + '\'' +
                ", sourceCodeHash='" + sourceCodeHash + '\'' +
                ", maxLevel=" + maxLevel +
                '}';
    }

    public Node() {
    }

    public Node(String owner, String objectName, String objectType) {
        this.owner = owner;
        this.objectName = objectName;
        this.objectType = objectType;
        this.dependence_type = "Direct";

    }

    @Override
    public int hashCode() {
        return Objects.hash(this.owner, this.objectName, this.objectType);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) { // 如果两个对象引用的是同一个实例，返回 true
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) { // 如果传入的对象为空或不是该类的实例，返回 false
            return false;
        }
        // 将 obj 转换为该类的实例类型
        Node node = (Node) obj;

        // 判断该类的两个属性是否相等
        return Objects.equals(this.owner, node.owner)
                //  && this.owner == node.owner
                &&
                Objects.equals(this.objectName, node.objectName)
                // && this.objectName == node.objectName
                &&
                Objects.equals(this.objectType, node.objectType);
        // && this.objectType == node.objectType;

    }

}
