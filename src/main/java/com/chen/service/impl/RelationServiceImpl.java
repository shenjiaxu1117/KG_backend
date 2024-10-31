package com.chen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.mapper.RelationMapper;
import com.chen.pojo.Relation;
import com.chen.service.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelationServiceImpl extends ServiceImpl<RelationMapper, Relation> implements RelationService {
    @Autowired
    RelationMapper relationMapper;

    /**
     * 对没有在relation表中的关系进行存储
     * @param relationName 关系名称
     * @param graphId 对应的图谱
     */
    @Override
    public void createRelation(String relationName,int graphId) {
        boolean isExistRelation=relationMapper.existsRelation(relationName,graphId);
        if(!isExistRelation){
            Relation relation=new Relation();
            relation.setName(relationName);
            relation.setGraphId(graphId);
            relationMapper.insert(relation);
        }
    }

    @Override
    public void updateRelation(int id, String relationName) {
        relationMapper.selectById(id).setName(relationName);
    }

    @Override
    public Relation getRelationByName(String relationName,int graphId) {
        return relationMapper.getRelationByName(relationName,graphId);
    }

    @Override
    public Relation getRelationById(int id) {
        return relationMapper.selectById(id);
    }

    @Override
    public List<Relation> getRelationByGraphId(int graphId) {
        return relationMapper.getRelationListByGraphId(graphId);
    }

    @Override
    public int getRelationNumber(int graphId) {
        return relationMapper.relationNumber(graphId);
    }
}
