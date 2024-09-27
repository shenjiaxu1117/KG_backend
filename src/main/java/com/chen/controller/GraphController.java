package com.chen.controller;

import com.chen.config.Response;
import com.chen.pojo.Graph;
import com.chen.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/graph")
public class GraphController {
    @Autowired
    GraphService graphService;

    @GetMapping("/getAll")
    public Response getAll(){
        List<Graph> graphs=graphService.getAllGraph();
        return Response.buildSuccess(graphs);
    }

    @PostMapping("/createGraph")
    public Response createGraph(@RequestBody Graph graph){
        graphService.insertGraph(graph);
        return Response.buildSuccess();
    }

    @PostMapping("/updateGraph")
    public Response updateGraph(@RequestBody Graph graph){
        graphService.updateGraph(graph);
        return Response.buildSuccess();
    }

    @PostMapping("/deleteGraph")
    public Response deleteGraph(@RequestBody Graph graph){
        graphService.deleteGraph(graph);
        return Response.buildSuccess();
    }

}
