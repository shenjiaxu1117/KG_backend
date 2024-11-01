package com.chen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.mapper.EntityMapper;
import com.chen.pojo.Entity;
import com.chen.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntityServiceImpl extends ServiceImpl<EntityMapper, Entity> implements EntityService {
    @Autowired
    EntityMapper entityMapper;

    @Override
    public String createEntity(String name, String color,int graphId) {
        //保证在每个图谱中的实体名称和颜色值唯一
        boolean isExistType = entityMapper.existName(name,graphId);
        boolean isExistColor = entityMapper.existColor(color,graphId);
        if (isExistType){
            return "实体名称已存在！";
        }else if (isExistColor){
            return "实体颜色已存在！";
        }else{
            Entity entity = new Entity();
            entity.setType(name);
            entity.setColor(color);
            entity.setGraphId(graphId);
            entityMapper.insert(entity);
            return "create entity success";
        }
    }

    @Override
    public String updateEntity(int id,String name,String color) {
        int graphId= entityMapper.searchGraphIdByEntityId(id);
        boolean isExistType = entityMapper.existName(name,graphId);
        boolean isExistColor = entityMapper.existColor(color,graphId);
        if (isExistType){
            return "实体名称已存在！";
        }else if (isExistColor){
            return "实体颜色已存在！";
        }else{
            Entity entity = entityMapper.selectById(id);
            entity.setType(name);
            entity.setColor(color);
            entityMapper.updateById(entity);
            return "update entity success";
        }

    }

    @Override
    public int deleteEntity(int id) {
        return entityMapper.deleteById(id); //如果未删除任何记录，返回值为 0
    }

    @Override
    public List<Entity> getEntityList(int graphId) {
        return entityMapper.getByGraphId(graphId);
    }

    @Override
    public Entity getEntityById(int id) {
        return entityMapper.selectById(id);
    }

    @Override
    public Entity getEntityByName(String name, int graphId) {
        return entityMapper.getEntityByName(name,graphId);
    }

    @Override
    public int getEntityNumber(int graphId) {
        return entityMapper.entityNumber(graphId);
    }
}
