package com.chen.service;

import com.chen.pojo.EntityTriple;
import com.chen.pojo.ItemTriple;

import java.util.List;
import java.util.Map;

public interface ConstructionService {
    String insertTriple(int s_e,int r,int e_e);

    List<String[]> getAll(int graphId);

    List<EntityTriple> getAllTripleId(int graphId);

    void deleteTriple(int id);

    void insertItemRelationTriple(int head,int tail,int relation);

    List<ItemTriple> getItemRelationTriple(int graphId);

    void insertItemProperty(int itemId,int propertyId,String value);

    List<Integer> getPropertyIdListByItemId(int itemId);

    List<Map<String, String>> getPropertyNameAndValue(int itemId);
}
