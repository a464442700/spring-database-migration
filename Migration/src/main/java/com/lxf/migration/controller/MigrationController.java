package com.lxf.migration.controller;

import com.lxf.migration.pojo.DataSource;
import com.lxf.migration.service.BFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MigrationController {
    @Autowired
    private BFS localBfs;

    @Autowired
    private BFS remoteBfs;


    @GetMapping("/")
    public String HomePage(Model model) {
        List<DataSource> dataSources = new ArrayList<DataSource>();
        localBfs.setDataSource("local");
        remoteBfs.setDataSource("remote");
        String database1 = localBfs.getDataBase();
        String database2 = remoteBfs.getDataBase();

        dataSources.add(new DataSource(database1,"local" ));
        dataSources.add(new DataSource(database2,"remote"));
        System.out.println(dataSources);
        model.addAttribute("dataSources",dataSources);
        return "HomePage";
    }

}
