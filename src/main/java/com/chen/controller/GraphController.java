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

    /**
     * 获取所有图谱
     * @return 图谱列表
     */
    @GetMapping("/getAll")
    public Response getAll(){
        List<Graph> graphs=graphService.getAllGraph();
        return Response.buildSuccess(graphs);
    }

    /**
     * 插入新增图谱
     * @param graph 新增图谱
     * @return
     */
    @PostMapping("/createGraph")
    public Response createGraph(@RequestBody Graph graph){
        graphService.insertGraph(graph);
        return Response.buildSuccess();
    }

    /**
     * 编辑图谱
     * @param graph 需要更新的图谱
     * @return
     */
    @PostMapping("/updateGraph")
    public Response updateGraph(@RequestBody Graph graph){
        graphService.updateGraph(graph);
        return Response.buildSuccess();
    }

    /**
     * 删除制定图谱
     * @param graph 需要删除的图谱
     * @return
     */
    @PostMapping("/deleteGraph")
    public Response deleteGraph(@RequestBody Graph graph){
        graphService.deleteGraph(graph);
        return Response.buildSuccess();
    }

}
