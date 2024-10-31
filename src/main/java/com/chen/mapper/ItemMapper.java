package com.chen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.pojo.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ItemMapper extends BaseMapper<Item> {
    @Select("SELECT COUNT(*) > 0 FROM item WHERE name = #{name} AND entityid = #{entityId}")
    boolean existsItemByName(String name,int entityId);

    @Select("SELECT * FROM item WHERE entityid = #{entityId}")
    List<Item> getItemsByEntityId(int entityId);

    @Select("SELECT * FROM item WHERE name = #{name} AND entityid = #{entityId}")
    Item getItemByNameAndEntity(String name,int entityId);
}
