package com.chen.mapper;

import com.chen.pojo.EntityTriple;
import com.chen.pojo.ItemTriple;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ConstructionMapper {
    /**
     * 将三元(s_e,r,e_e)组插入relation_entity表
     * @param start 开始实体类型的id
     * @param relation 两者之间的关系id
     * @param end 结束实体类型的id
     */
    @Insert("INSERT INTO relation_entity (relationid, startentity, endentity) VALUES (#{relation}, #{start}, #{end})")
    void insert2RelationEntity(int start, int relation, int end);

    @Select("SELECT COUNT(*) > 0 FROM relation_entity WHERE relationid = #{relation} AND startentity = #{start} AND endentity = #{end}")
    boolean existRelationEntity(int start, int relation, int end);

    @Select("SELECT * FROM relation_entity WHERE relationid IN (SELECT id FROM relation WHERE graphid = #{graphId})")
    List<EntityTriple> findAllRelationEntitiesByGraphId(int graphId);

    @Delete("DELETE FROM relation_entity WHERE id = #{id}")
    void deleteRelationEntity(int id);

    @Select("SELECT COUNT(*) > 0 FROM relation_item WHERE relationid = #{relation} AND startitem = #{head} AND enditem = #{tail}")
    boolean existItemRelation(int head,int tail,int relation);

    @Insert("INSERT INTO relation_item (relationid, startitem, enditem) VALUES (#{relation}, #{head}, #{tail})")
    void insert2ItemRelation(int relation,int head,int tail);

    @Select("SELECT * FROM relation_item WHERE relationid IN (SELECT id FROM relation WHERE graphid = #{graphId})")
    List<ItemTriple> findAllItemRelationByGraphId(int graphId);

    /* Entity */


    /* Relation */


    /* Item */


    /* Property */


}
