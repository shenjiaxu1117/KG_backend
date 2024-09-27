package com.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.pojo.Graph;

import java.util.List;

public interface GraphService extends IService<Graph> {
    /**
     * @return 获取所有的已创建的图谱列表
     */
    List<Graph> getAllGraph();

    void insertGraph(Graph graph);

    void updateGraph(Graph graph);

    void  deleteGraph(Graph graph);
}
