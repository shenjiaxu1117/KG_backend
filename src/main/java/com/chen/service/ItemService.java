package com.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.pojo.Item;

import java.util.List;

public interface ItemService extends IService<Item> {
    void createItem(String itemName,int entityId);

    List<Item> getItemByEntityId(int entityId);

    Item getItemByItemNameAndEntityId(String itemName,int entityId);

    Item getItemByItemId(int id);

    List<Item> getItemByGraphId(int graphId);
}
