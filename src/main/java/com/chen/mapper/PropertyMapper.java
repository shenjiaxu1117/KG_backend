package com.chen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.pojo.Property;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PropertyMapper extends BaseMapper<Property> {
    @Select("SELECT * FROM property WHERE entityid = #{entityId}")
    List<Property> selectByEntityId(int entityId);

    @Select("SELECT COUNT(*) > 0 FROM property WHERE name = #{propertyName} AND entityid = #{entityId}")
    boolean existProperty(int entityId,String propertyName);

    @Select("SELECT * FROM property WHERE name = #{propertyName} AND entityid = #{entityId}")
    Property getPropertyByNameAndEntityId(String propertyName,int entityId);


}
