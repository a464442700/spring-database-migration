package com.lxf.migration.pojo;

import java.sql.Date;

public class DbaObjects {
    private String owner;
    private String objectName;
    private String objectType;
    private Integer objectID;
    private  java.sql.Timestamp  lastDDLTime;

    public String getObjectType() {
        return objectType;
    }

    public Integer getObjectID() {
        return objectID;
    }

    public void setObjectID(Integer objectID) {
        this.objectID = objectID;
    }

    public  java.sql.Timestamp  getLastDDLTime() {
        return lastDDLTime;
    }

    public void setLastDDLTime( java.sql.Timestamp  lastDDLTime) {
        this.lastDDLTime = lastDDLTime;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }



    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public void setobjectType(String objectType) {
        this.objectType = objectType;
    }


    public String getObjectName() {
        return objectName;
    }

    public String getobjectType() {
        return objectType;
    }



    public String getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return "DbaObjects{" +
                "owner='" + owner + '\'' +
                ", objectName='" + objectName + '\'' +
                ", objectType='" + objectType + '\'' +
                '}';
    }
}
