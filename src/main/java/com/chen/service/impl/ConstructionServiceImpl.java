package com.chen.service.impl;

import com.chen.mapper.ConstructionMapper;
import com.chen.mapper.EntityMapper;
import com.chen.mapper.RelationMapper;
import com.chen.pojo.EntityTriple;
import com.chen.pojo.ItemTriple;
import com.chen.service.ConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ConstructionServiceImpl implements ConstructionService {
    @Autowired
    ConstructionMapper constructionMapper;

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private RelationMapper relationMapper;

    @Override
    public String insertTriple(int s_e, int r, int e_e) {
        boolean isExistTriple= constructionMapper.existRelationEntity(s_e,r,e_e);
        if(isExistTriple){
            return "该三元组已存在！";
        }else{
            constructionMapper.insert2RelationEntity(s_e, r, e_e);
            return "insert success";
        }

    }

    @Override
    public List<EntityTriple> getAllTripleId(int graphId) {
        return constructionMapper.findAllRelationEntitiesByGraphId(graphId);
    }

    /**
     * 找出该图谱对应的所有实体和关系之间的三元组关系
     * @param graphId 图谱索引
     * @return 返回三元组信息
     * 格式：
     * [
     *      [三元组id,头实体名称,关系名称,尾实体名称],
     *      ...
     * ]
     */
    @Override
    public List<String[]> getAll(int graphId) {
        List<EntityTriple> tripleList = getAllTripleId(graphId);
        System.out.println("tripleList: "+tripleList);
        List<String[]> nameList= new ArrayList<>();
        for (EntityTriple each:tripleList){
            String[] eachNameList = new String[4];
            eachNameList[0]=each.getId()+"";
            //取出每个三元组id对应的三元组关系和实体名称，(头实体，关系，尾实体)
            eachNameList[1]=entityMapper.selectById(each.getStartEntity()).getType();
            eachNameList[2]=relationMapper.selectById(each.getRelationId()).getName();
            eachNameList[3]=entityMapper.selectById(each.getEndEntity()).getType();
            nameList.add(eachNameList);
        }
        return nameList;
    }

    @Override
    public void deleteTriple(int id) {
        constructionMapper.deleteRelationEntity(id);
    }

    @Override
    public void insertItemRelationTriple(int head, int tail, int relation) {
        boolean isExist=constructionMapper.existItemRelation(head,tail,relation);
        if(!isExist){
            constructionMapper.insert2ItemRelation(relation,head,tail);
        }
    }

    /**
     * 获取实例-关系三元组
     * @param graphId 图谱索引
     * @return 实例-关系三元组列表
     */
    @Override
    public List<ItemTriple> getItemRelationTriple(int graphId) {
        return constructionMapper.findAllItemRelationByGraphId(graphId);
    }

    @Override
    public void insertItemProperty(int itemId, int propertyId, String value) {
        boolean isExist=constructionMapper.existItemProperty(itemId,propertyId);
        if(!isExist){
            constructionMapper.insert2ItemProperty(itemId,propertyId,value);
        }
    }

    @Override
    public List<Integer> getPropertyIdListByItemId(int itemId) {
        return constructionMapper.getPropertyByItemId(itemId);
    }

    @Override
    public List<Map<String, String>> getPropertyNameAndValue(int itemId) {
        return constructionMapper.getPropertyNameAndValueByItemId(itemId);
    }
}
