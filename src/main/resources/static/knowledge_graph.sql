CREATE DATABASE knowledge_graph;

USE knowledge_graph;

-- 创建图谱表
CREATE TABLE graph (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- id 是自增主键
    name VARCHAR(255) NOT NULL,         -- name 是字符串，不允许为空
    build ENUM('custom', 'template') NOT NULL,  -- build 只能取值 'custom' 或 'template'，构建方式
    description TEXT                    -- description 是字符串，可以存储较长的文本
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建关系表 
CREATE TABLE relation (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- id 是自增的唯一标识，作为主键
    name VARCHAR(255) NOT NULL          -- name 是字符串类型，不允许为空，最多255个字符
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建实体表
CREATE TABLE entity (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- 自增主键，唯一标识
    type VARCHAR(255) NOT NULL,         -- 字符串类型，支持中文，最大长度255字符
    color CHAR(7) NOT NULL              -- 固定长度为7的字符串，用于存储16进制颜色值（如 #ffffff）
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建实体的实例表
CREATE TABLE item (
    id INT AUTO_INCREMENT PRIMARY KEY,        -- 自增主键，唯一标识
    name VARCHAR(255) NOT NULL,               -- 字符串类型，支持中文，最大长度255字符
    entityid INT NOT NULL,                             -- 整数类型，用于存储 entity 表中的 id
    CONSTRAINT fk_entity FOREIGN KEY (entityid) REFERENCES entity(id) ON DELETE CASCADE  -- 外键，引用 entity 表中的 id
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建属性表
CREATE TABLE `property` (
    id INT AUTO_INCREMENT PRIMARY KEY,   -- id 是自增主键，唯一标识
    name VARCHAR(255) NOT NULL,          -- name 是字符串，不允许为空，支持中文
    type VARCHAR(255) NOT NULL,          -- type 是字符串，不允许为空，支持中文
    unit VARCHAR(255) NOT NULL           -- unit 是字符串，不允许为空，支持中文
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


CREATE TABLE `graph-entity` (
    graphid INT NOT NULL,                                    -- graph 表中的 id
    entityid INT NOT NULL,                                   -- entity 表中的 id
    PRIMARY KEY (graphid, entityid),                         -- 定义联合主键
    CONSTRAINT fk_graph_entity_graph FOREIGN KEY (graphid) REFERENCES graph(id) ON DELETE CASCADE,  -- 外键，引用 graph 表中的 id
    CONSTRAINT fk_graph_entity_entity FOREIGN KEY (entityid) REFERENCES entity(id) ON DELETE CASCADE -- 外键，引用 entity 表中的 id
);

CREATE TABLE `graph-relation` (
    graphid INT NOT NULL,
    relationid INT NOT NULL,
    PRIMARY KEY (graphid, relationid),
    CONSTRAINT fk_graph_relation_graph FOREIGN KEY (graphid) REFERENCES graph(id) ON DELETE CASCADE,      -- 外键，引用 graph 表的 id
    CONSTRAINT fk_graph_relation_relation FOREIGN KEY (relationid) REFERENCES relation(id) ON DELETE CASCADE -- 外键，引用 relation 表的 id
);

CREATE TABLE `relation-entity` (
    relationid INT NOT NULL,                                -- relation 表中的 id
    startentity INT NOT NULL,                               -- entity 表中的 id，表示起始实体
    endentity INT NOT NULL,                                 -- entity 表中的 id，表示结束实体
    PRIMARY KEY (relationid, startentity, endentity),       -- 定义联合主键
    CONSTRAINT fk_relation_entity_relation FOREIGN KEY (relationid) REFERENCES relation(id) ON DELETE CASCADE,   -- 外键，引用 relation 表中的 id
    CONSTRAINT fk_relation_entity_start FOREIGN KEY (startentity) REFERENCES entity(id) ON DELETE CASCADE,       -- 外键，引用 entity 表中的 id
    CONSTRAINT fk_relation_entity_end FOREIGN KEY (endentity) REFERENCES entity(id) ON DELETE CASCADE            -- 外键，引用 entity 表中的 id
);

CREATE TABLE `relation-item` (
    relationid INT NOT NULL,                                -- relation 表中的 id
    startitem INT NOT NULL,                               -- entity 表中的 id，表示起始实例
    enditem INT NOT NULL,                                 -- entity 表中的 id，表示结束实例
    PRIMARY KEY (relationid, startitem, enditem),       -- 定义联合主键
    CONSTRAINT fk_relation_item_relation FOREIGN KEY (relationid) REFERENCES relation(id) ON DELETE CASCADE,   -- 外键，引用 relation 表中的 id
    CONSTRAINT fk_relation_item_start FOREIGN KEY (startitem) REFERENCES item(id) ON DELETE CASCADE,       -- 外键，引用 item 表中的 id
    CONSTRAINT fk_relation_item_end FOREIGN KEY (enditem) REFERENCES item(id) ON DELETE CASCADE            -- 外键，引用 item 表中的 id
);

CREATE TABLE `item_property` (
    itemid INT NOT NULL,                  -- itemid 是 int 类型，外键，引用 item 表的 id
    propertyid INT NOT NULL,              -- propertyid 是 int 类型，外键，引用 property 表的 id
    value VARCHAR(255) NOT NULL,          -- value 是字符串，支持中文
    PRIMARY KEY (itemid, propertyid),     -- 定义联合主键 (itemid, propertyid)
    CONSTRAINT fk_item FOREIGN KEY (itemid) REFERENCES item(id) ON DELETE CASCADE,         -- 外键，引用 item 表的 id
    CONSTRAINT fk_property FOREIGN KEY (propertyid) REFERENCES property(id) ON DELETE CASCADE -- 外键，引用 property 表的 id
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE `entity_property` (
    entityid INT NOT NULL,                  -- itemid 是 int 类型，外键，引用 item 表的 id
    propertyid INT NOT NULL,              -- propertyid 是 int 类型，外键，引用 property 表的 id
    PRIMARY KEY (entityid, propertyid),     -- 定义联合主键 (itemid, propertyid)
    CONSTRAINT fk_entity_property_entity FOREIGN KEY (entityid) REFERENCES entity(id) ON DELETE CASCADE,         -- 外键，引用 item 表的 id
    CONSTRAINT fk_entity_property_property FOREIGN KEY (propertyid) REFERENCES property(id) ON DELETE CASCADE -- 外键，引用 property 表的 id
);

-- 创建文件表
CREATE TABLE `file` (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- id 是自增主键
    name VARCHAR(255) NOT NULL,         -- name 是字符串，不允许为空，支持中文
    type VARCHAR(50) NOT NULL,          -- type 是字符串，不允许为空
    size VARCHAR(50) NOT NULL,            -- size 表示文件大小
    updatetime DATETIME NOT NULL,         -- updatetime 是时间格式，不允许为空
    graphid INT NOT NULL,
    CONSTRAINT fk_graph FOREIGN KEY (graphid) REFERENCES graph(id) ON DELETE CASCADE
);


