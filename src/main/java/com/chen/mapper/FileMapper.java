package com.chen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.enums.FileCategory;
import com.chen.pojo.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileMapper extends BaseMapper<FileInfo> {
    /**
     *
     * @param name 文件名
     * @param type 文件类型
     * @param size 文件大小（b/kb/mb）
     * @param graphId 图谱索引
     * @return 如果数据表中已存在该文件，返回true；不存在则返回false
     */
    @Select("SELECT COUNT(*) > 0 FROM file WHERE name = #{name} AND type = #{type} AND size = #{size} AND graphid = #{graphId} AND category = #{category}")
    boolean existsFile(String name, String type, String size, int graphId, FileCategory category);

    /**
     * 查询指定图谱中所有的文件
     * @param graphId 图谱id
     * @return 文件列表
     */
    @Select("SELECT * FROM file WHERE graphid = #{graphId}")
    List<FileInfo> findFileByGraphId(int graphId);
}
