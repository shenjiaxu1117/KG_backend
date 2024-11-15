package com.chen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.pojo.Entity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EntityMapper extends BaseMapper<Entity> {
    @Select("SELECT COUNT(*) FROM entity WHERE type = #{name} AND graphid = #{graphId}")
    int existName(String name,int graphId);

    @Select("SELECT COUNT(*) FROM entity WHERE color = #{color} AND graphid = #{graphId}")
    int existColor(String color,int graphId);

    @Select("SELECT COUNT(*) > 0 FROM entity WHERE type = #{name} AND color = #{color} AND graphid = #{graphId}")
    boolean existTypeOrColor(String type,String color,int graphId);

    @Select("SELECT * FROM entity WHERE graphid = #{graphId}")
    List<Entity> getByGraphId(int graphId);

    @Select("SELECT graphid FROM entity WHERE id = #{id}")
    int searchGraphIdByEntityId(int id);

    @Select("SELECT * FROM entity WHERE type = #{name} AND graphid = #{graphId}")
    Entity getEntityByName(String name,int graphId);

    @Select("SELECT COUNT(*) FROM entity WHERE graphid = #{graphId}")
    int entityNumber(int graphId);
}
