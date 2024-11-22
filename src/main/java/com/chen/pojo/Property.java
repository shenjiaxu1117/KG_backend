package com.chen.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chen.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;

@TableName("property")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CrossOrigin(origins = {"*"})
public class Property {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    private String name;

    private DataType type;

    private String unit;

    @TableField("entityid")
    private int entityId;
}
