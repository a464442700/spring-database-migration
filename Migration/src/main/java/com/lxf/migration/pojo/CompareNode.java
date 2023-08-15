package com.lxf.migration.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public class CompareNode {
    @Schema(description  = "对象拥有者", example = "apps")
    public String owner;//四个只读属性
    @Schema(description  = "对象名称", example = "CUX_TEST_A")
    public String objectName;

    @Schema(description  = "对象类型", example = "PACKAGE")
    public String objectType;


    public String dataSource;
    public String remoteDataSource;
}
