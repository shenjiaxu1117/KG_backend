package com.chen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.pojo.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    /**
     * 查询指定图谱中所有的任务
     * @param graphId 图谱id
     * @return 任务列表
     */
    @Select("SELECT * FROM task WHERE graphid = #{graphId}")
    List<Task> getTaskByGraphId(int graphId);

}
