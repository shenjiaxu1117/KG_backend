package com.chen.controller;

import com.chen.config.Response;
import com.chen.pojo.Graph;
import com.chen.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/graph")
public class GraphController {
    @Autowired
    GraphService graphService;

    @GetMapping("/getAll")
    public Response getAll(){
        List<Graph> graphs=graphService.getAllGraph();
        return Response.buildSuccess(graphs);
    }


}
