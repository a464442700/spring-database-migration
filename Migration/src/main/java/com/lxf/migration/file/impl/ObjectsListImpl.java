package com.lxf.migration.file.impl;

import com.lxf.migration.file.ObjectsListService;
import com.lxf.migration.pojo.Node;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ObjectsListImpl implements ObjectsListService {
    private HashSet<String> hashSet = new HashSet<>();


    public ObjectsListImpl() {
        //初始化hashset
        //目前objectlist支持的DB对象只有这些
        hashSet.add("SYNONYM");
        hashSet.add("TYPE");
        hashSet.add("TABLE");
        hashSet.add("SEQUENCE");
        hashSet.add("VIEW");
        hashSet.add("TRIGGER");
        hashSet.add("FUNCTION");
        hashSet.add("PACKAGE");

    }

    private String getText(HashMap<String, List<Node>> hashMap) {
        String text = "";

        for (String objectType : hashMap.keySet()) {
            if (this.hashSet.contains(objectType)) {

                List<Node> nodes = hashMap.get(objectType);

                if (objectType.equals("PACKAGE")) {
                    text=text+"["+"PACKAGE_SPECIAL"+"]" +"\n";
                    for  (Node node : nodes){
                        text=text+"PACKAGE_SPECIAL"+" "+node.owner+"."+node.objectName+"\n";
                    }
                    text=text+"\n";
                    text=text+"["+"PACKAGE_BODY"+"]" +"\n";
                    for  (Node node : nodes){
                        text=text+"PACKAGE_BODY"+" "+node.owner+"."+node.objectName+"\n";
                    }
                    text=text+"\n";
                } else {
                    text=text+"["+objectType+"]" +"\n";
                    for  (Node node : nodes){
                      text=text+objectType+" "+node.owner+"."+node.objectName+"\n";
                    }
                    text=text+"\n";
                }

            }
        }

        return text;
    }

    @Override
    public String getObjectList(List<Node> nodes) {

        HashMap<String, List<Node>> hashMap = new HashMap<>();


        // 遍历 nodes 数组
        for (Node node : nodes) {
            //objectList不是所有key都能使用
            if (hashSet.contains(node.objectType)) {
                // 检查当前节点的 objectType 是否已经存在于 HashMap 中

                if (hashMap.containsKey(node.objectType)) {
                    // 如果存在，则将当前节点添加到对应的 value 数组中


                    hashMap.get(node.objectType).add(node);
                } else {
                    // 如果不存在，则创建一个新的 ArrayList，将当前节点作为第一个元素添加，并将 ArrayList 放入 HashMap


                    List<Node> nodeList = new ArrayList<>();
                    nodeList.add(node);
                    hashMap.put(node.objectType, nodeList);
                }
            }
        }
       String text=getText(hashMap);


        return text;

    }
}
