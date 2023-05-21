package com.lxf.migration.pojo;

public class DbaObjects {
    private String owner;
    private String objectName;
    private String objectType;



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
