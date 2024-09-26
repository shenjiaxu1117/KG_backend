package com.chen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.mapper.GraphMapper;
import com.chen.pojo.Graph;
import com.chen.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class GraphServiceImpl extends ServiceImpl<GraphMapper, Graph>  implements GraphService {
    @Autowired
    GraphMapper graphMapper;

    @Override
    public List<Graph> getAllGraph() {
        return graphMapper.selectList(null);
    }
}
