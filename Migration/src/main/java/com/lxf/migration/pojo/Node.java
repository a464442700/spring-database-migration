package com.lxf.migration.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "节点对象")
public class Node implements Serializable {
    @Schema(description  = "对象拥有者", example = "apps")
    public String owner;//四个只读属性
    @Schema(description  = "对象名称", example = "CUX_TEST_A")
    public String objectName;
    @JsonProperty("level")//postman输出缺失对象,必须加此注解
    public Integer level = 0;
    public String database;
    @Schema(description  = "对象类型", example = "PACKAGE")
    public String objectType;
    public Integer objectID;
    public java.sql.Timestamp  lastDDLTime;
    public String dataSource;
    public String dependence_type;
    public String mode = "add";
    public String sourceCode;
    public String sourceCodeHash;
    @JsonIgnore
    public Node rootNode;
    @JsonProperty("maxLevel")
    public Integer maxLevel;
    public boolean showSourceCode;
    @JsonIgnore
    public  Node parentNode ;
    @JsonIgnore
    public Node getParentNode() {
        return parentNode;
    }
    @JsonIgnore
    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getObjectType() {
        return objectType;
    }

    public Integer getObjectID() {
        return objectID;
    }

    public Timestamp getLastDDLTime() {
        return lastDDLTime;
    }

    public String getDataSource() {
        return dataSource;
    }

    public String getDependence_type() {
        return dependence_type;
    }

    public boolean isShowSourceCode() {
        return showSourceCode;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
    @JsonIgnore
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
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }



    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }



    public boolean getShowSourceCode() {
        return showSourceCode;
    }

    public void setShowSourceCode(boolean showSourceCode) {
        this.showSourceCode = showSourceCode;
    }

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
   @JsonIgnore
    public String getNodeVertex() {
        return this.objectName + "(" + this.objectType + ")";
    }

    public void setDatabase(String database) {
     //    if ( this.database  == null &&  this.database .isEmpty()) {
             this.database = database;
      //   }
    }

    public void setObjectID(Integer objectID) {
        this.objectID = objectID;
    }

    public void setLastDDLTime( java.sql.Timestamp  lastDDLTime) {
        this.lastDDLTime = lastDDLTime;
    }

    public Integer getLevel() {
        return level;
    }

    public void setDependence_type(String dependence_type) {
        this.dependence_type = dependence_type;
    }


    @Override
    public String toString() {
        return "Node{" +
                "owner='" + owner + '\'' +
                ", objectName='" + objectName + '\'' +
                ", objectType='" + objectType + '\'' +
                ", level=" + level +
                ", database='" + database + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", sourceCodeHash='" + sourceCodeHash + '\'' +
                ", maxLevel=" + maxLevel +
                ", sourceCode=" + (sourceCode != null) +
                ", objectID=" + objectID +
                ", lastDDLTime=" + lastDDLTime +
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
