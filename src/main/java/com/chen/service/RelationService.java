package com.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.pojo.Relation;

import java.util.List;

public interface RelationService extends IService<Relation> {
    void createRelation(String relationName,int graphId);

    void updateRelation(int id, String relationName);

    Relation getRelationByName(String relationName,int graphId);

    Relation getRelationById(int id);

    List<Relation> getRelationByGraphId(int graphId);

    int getRelationNumber(int graphId);
}
