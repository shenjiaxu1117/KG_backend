package com.chen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.mapper.ItemMapper;
import com.chen.pojo.Item;
import com.chen.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {
    @Autowired
    ItemMapper itemMapper;

    @Override
    public void createItem(String itemName, int entityId) {
        boolean isExist=itemMapper.existsItemByName(itemName,entityId);
        if(!isExist){
            Item item = new Item();
            item.setName(itemName);
            item.setEntityId(entityId);
            itemMapper.insert(item);
        }

    }

    @Override
    public List<Item> getItemByEntityId(int entityId) {
        return itemMapper.getItemsByEntityId(entityId);
    }

    @Override
    public Item getItemByItemNameAndEntityId(String itemName, int entityId) {
        return itemMapper.getItemByNameAndEntity(itemName,entityId);
    }

    @Override
    public Item getItemByItemId(int id) {
        return itemMapper.selectById(id);
    }

    @Override
    public List<Item> getItemByGraphId(int graphId) {
        return itemMapper.getItemsByGraphId(graphId);
    }
}
