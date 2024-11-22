package com.chen.mapper;

import com.chen.pojo.EntityTriple;
import com.chen.pojo.ItemTriple;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

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

    @Insert("INSERT INTO item_property (itemid, propertyid, propertyvalue) VALUES (#{itemId}, #{propertyId}, #{propertyValue})")
    void insert2ItemProperty(int itemId,int propertyId,String propertyValue);

    @Select("SELECT COUNT(*)>0 FROM item_property WHERE itemid = #{itemId} AND propertyid = #{propertyId}")
    boolean existItemProperty(int itemId,int propertyId);

    @Select("SELECT propertyid FROM item_property WHERE itemid = #{itemId}")
    List<Integer> getPropertyByItemId(int itemId);

    /**
     * 查询其指定实例的所有属性名和属性值
     * @param itemId 实例id
     * @return 格式如下
     * [
     *      {
     *          name: 属性名1
     *          value: 属性值1
     *      },
     *      ...
     * ]
     */
    @Select("SELECT p.name AS name,ip.propertyvalue AS value FROM item_property ip JOIN property p ON ip.propertyid = p.id WHERE ip.itemid = #{itemId}")
    List<Map<String, String>> getPropertyNameAndValueByItemId(int itemId);

    /* Entity */


    /* Relation */


    /* Item */


    /* Property */


}
