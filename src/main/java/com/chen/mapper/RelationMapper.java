package com.chen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.pojo.Relation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RelationMapper extends BaseMapper<Relation> {
    @Select("SELECT COUNT(*) > 0 FROM relation WHERE name = #{relationName} AND graphid = #{graphId}")
    boolean existsRelation(String relationName,int graphId);

    @Select("SELECT * FROM relation WHERE name = #{name} AND graphid = #{graphId}")
    Relation getRelationByName(String name,int graphId);

    @Select("SELECT * FROM relation WHERE graphid = #{graphId}")
    List<Relation> getRelationListByGraphId(int graphId);

    @Select("SELECT COUNT(*) FROM relation WHERE graphid = #{graphId}")
    int relationNumber(int graphId);
}
