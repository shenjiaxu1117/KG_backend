package com.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.pojo.Entity;

import java.util.List;

public interface EntityService extends IService<Entity> {
    String createEntity(String name,String color,int graphId);

    String updateEntity(int id,String name,String color);

    int deleteEntity(int id);

    List<Entity> getEntityList(int graphId);

    Entity getEntityById(int id);

    Entity getEntityByName(String name,int graphId);

    int getEntityNumber(int graphId);
}
