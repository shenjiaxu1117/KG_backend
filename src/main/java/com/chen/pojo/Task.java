package com.chen.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chen.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@TableName("task")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CrossOrigin(origins = {"*"})
public class Task {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    private String name;

    private String type;

    private int source;

    private TaskStatus status;

    private String description;

    @TableField("updatetime")
    private String updateTime;

    @TableField("graphid")
    private int graphId;
}
