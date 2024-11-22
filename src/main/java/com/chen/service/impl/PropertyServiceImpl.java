package com.chen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.enums.DataType;
import com.chen.mapper.PropertyMapper;
import com.chen.pojo.Property;
import com.chen.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyServiceImpl extends ServiceImpl<PropertyMapper, Property> implements PropertyService {
    @Autowired
    PropertyMapper propertyMapper;

    @Override
    public List<Property> getPropertyListByEntityId(int entityId) {
        return propertyMapper.selectByEntityId(entityId);
    }

    @Override
    public String createProperty(int entityId, String propertyName, DataType type, String unit) {
        boolean isExist=propertyMapper.existProperty(entityId,propertyName);
        if(isExist){
            return "该属性已存在！";
        }else {
            Property property=new Property();
            property.setName(propertyName);
            property.setType(type);
            property.setUnit(unit);
            property.setEntityId(entityId);
            propertyMapper.insert(property);
            return "创建成功！";
        }
    }

    @Override
    public String updateProperty(int entityId, int propertyId, String propertyName, DataType type, String unit) {
        Property property=propertyMapper.selectById(propertyId);
        if (property.getName().equals(propertyName)){
            property.setType(type);
            property.setUnit(unit);
            propertyMapper.updateById(property);
            return "属性修改成功";
        }else{
            boolean isExist=propertyMapper.existProperty(entityId,propertyName);
            if(isExist){
                return "属性名已存在！";
            }else {
                property.setName(propertyName);
                property.setType(type);
                property.setUnit(unit);
                propertyMapper.updateById(property);
                return "属性修改成功";
            }
        }
    }

    @Override
    public void deleteProperty(int propertyId) {
        propertyMapper.deleteById(propertyId);
    }

    @Override
    public Property getPropertyByNameAndEntityId(String name,int entityId) {
        return propertyMapper.getPropertyByNameAndEntityId(name,entityId);
    }

    @Override
    public Property getPropertyById(int id) {
        return propertyMapper.selectById(id);
    }
}
