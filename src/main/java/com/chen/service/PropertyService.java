package com.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.enums.DataType;
import com.chen.pojo.Property;

import java.util.List;

public interface PropertyService extends IService<Property> {
    List<Property> getPropertyListByEntityId(int entityId);

    String createProperty(int entityId, String propertyName, DataType type, String unit);

    String updateProperty(int entityId, int propertyId,String propertyName,DataType type,String unit);

    void deleteProperty(int propertyId);

    Property getPropertyByNameAndEntityId(String name,int entityId);

    Property getPropertyById(int id);
}
