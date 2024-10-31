package com.chen.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;

@TableName("relation_item")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CrossOrigin(origins = {"*"})
public class ItemTriple {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    @TableField("relationid")
    private int relationId;

    @TableField("startitem")
    private int startItem;

    @TableField("enditem")
    private int endItem;
}
