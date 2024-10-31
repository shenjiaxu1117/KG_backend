package com.chen.controller;

import com.chen.config.Response;
import com.chen.config.Utils;
import com.chen.enums.FileCategory;
import com.chen.enums.TaskStatus;
import com.chen.pojo.*;
import com.chen.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/kg")
public class ConstructionController {
    @Autowired
    EntityService entityService;

    @Autowired
    RelationService relationService;

    @Autowired
    ConstructionService constructionService;

    @Autowired
    TaskService taskService;

    @Autowired
    FileService fileService;

    @Autowired
    ItemService itemService;

    @Autowired
    GraphService graphService;

    @Value("${files.upload.path}")
    private String fileUploadPath;

    /* Entity */

    @GetMapping("/getAllEntity")
    public Response getAllEntity(@RequestParam("graphId") int graphId) {
        List<Entity> allEntity=entityService.getEntityList(graphId);
        System.out.println(allEntity);
        return Response.buildSuccess(allEntity);
    }

    @GetMapping("/createEntity")
    public Response createEntity(@RequestParam("name") String name, @RequestParam("color") String color, @RequestParam("graphId") int graphId) {
        String result = entityService.createEntity(name, color, graphId);
        if (result.equals("create entity success")){
            return Response.buildSuccess();
        }else{
            return Response.buildFailure(result);
        }
    }

    @GetMapping("/updateEntityInfo")
    public Response updateEntityInfo(@RequestParam("id") int id, @RequestParam("name") String name, @RequestParam("color") String color){
        String res=entityService.updateEntity(id,name,color);
        if (res.equals("update entity success")){
            return Response.buildSuccess();
        }else{
            return Response.buildFailure(res);
        }
    }

    @GetMapping("/deleteEntity")
    public Response deleteEntity(@RequestParam("id") int id){
        int res=entityService.deleteEntity(id);
        if (res>0){
            return Response.buildSuccess();
        }else{
            return Response.buildFailure("delete entity failed");
        }
    }

    /* Relation */

    @GetMapping("/addRelation")
    public Response addRelation(@RequestParam("relation") String relationName, @RequestParam("startEntityId") int startEntityId, @RequestParam("endEntityId") int endEntityId, @RequestParam("graphId") int graphId){
        relationService.createRelation(relationName,graphId);
        Relation relation=relationService.getRelationByName(relationName,graphId);
        String res=constructionService.insertTriple(startEntityId,relation.getId(),endEntityId);
        if (res.equals("insert success")){
            return Response.buildSuccess();
        }else{
            return Response.buildFailure(res);
        }
    }

    @GetMapping("/getAllRelation")
    public Response getRelationByGraphId(@RequestParam("graphId") int graphId){
        return Response.buildSuccess(relationService.getRelationByGraphId(graphId));
    }

    @GetMapping("/updateRelationName")
    public Response updateRelationName(@RequestParam("id") int id, @RequestParam("relationName") String relationName){
        relationService.updateRelation(id,relationName);
        return Response.buildSuccess();
    }

    @GetMapping("/getEntityRelationNumber")
    public Response getEntityRelationNumber(){
        List<Graph> allGraphList=graphService.getAllGraph();
        Map<Integer,Map<String,Integer>> map=new HashMap<>();
        for (Graph graph:allGraphList){
            int entityCount=entityService.getEntityNumber(graph.getId());
            int relationCount=relationService.getRelationNumber(graph.getId());
            Map<String,Integer> each=new HashMap<>();
            each.put("entityCount",entityCount);
            each.put("relationCount",relationCount);
            map.put(graph.getId(),each);
        }
        System.out.println(map);
        return Response.buildSuccess(map);
    }

    @GetMapping("/getAllTripleName")
    public Response getAllTriple(@RequestParam("graphId") int graphId){
        List<String[]> tripleNameList=constructionService.getAll(graphId);
        return Response.buildSuccess(tripleNameList);
    }

    @GetMapping("/deleteTriple")
    public Response deleteTriple(@RequestParam("id") int id){
        constructionService.deleteTriple(id);
        return Response.buildSuccess();
    }

    @GetMapping("/task")
    public Response doTask(@RequestParam("taskId") int id) throws IOException {
        Task task=taskService.getTaskById(id);
        FileInfo file=fileService.getFileById(task.getSource());
        String filePath=System.getProperty("user.dir") + fileUploadPath + file.getName();

        if (file.getCategory().equals(FileCategory.entity)){
            //处理实体-实例表格
            Map<String, List<String>> entityItemMap= Utils.readEntityItemExcel2Map(filePath);

            for (Map.Entry<String, List<String>> entry : entityItemMap.entrySet()) {
                String key = entry.getKey();                    // 键，实体
                List<String> valueList = entry.getValue();      // 值，实例列表
                Entity entity=entityService.getEntityByName(key,task.getGraphId());
                if (entity!=null){
                    System.out.println("================valueList: "+valueList);
                    for (String value : valueList) {
                        itemService.createItem(value,entity.getId());   //Service层做了重名判断，如果重复不写入数据库
                    }
                }
            }
        }else {
            //处理实例-关系表格
            List<Map<String,String>> relationItemList=Utils.readRelationItemExcel2List(filePath);
            //获取关系设计中定义的实体和关系
            List<EntityTriple> tripleList=constructionService.getAllTripleId(task.getGraphId());

            for (Map<String,String> itemMap : relationItemList) {
                System.out.println("================itemMap: "+itemMap);
                Entity headEntity=entityService.getEntityByName(itemMap.get("headEntity"),task.getGraphId());
                Entity tailEntity=entityService.getEntityByName(itemMap.get("tailEntity"),task.getGraphId());
                Relation relation=relationService.getRelationByName(itemMap.get("relation"),task.getGraphId());
                if (headEntity!=null && tailEntity!=null && relation!=null && existInTriple(tripleList,headEntity.getId(),relation.getId(),tailEntity.getId())>-1){
                    Item headItem=itemService.getItemByItemNameAndEntityId(itemMap.get("headItem"),headEntity.getId());
                    Item tailItem=itemService.getItemByItemNameAndEntityId(itemMap.get("tailItem"),tailEntity.getId());
                    constructionService.insertItemRelationTriple(headItem.getId(),tailItem.getId(),relation.getId());
                }
            }
        }
        //修改任务状态
        taskService.updateTaskStatus(id, TaskStatus.success);
        return Response.buildSuccess();
    }

    public int existInTriple(List<EntityTriple> tripleList, int head, int relation, int tail){
        for (int i=0;i<tripleList.size();i++) {
            if (tripleList.get(i).getStartEntity()==head && tripleList.get(i).getEndEntity()==tail && tripleList.get(i).getRelationId()==relation){
                return i;
            }
        }
        return -1;
    }

    /**
     * 将指定图谱中所有的实体和实例列出，其中返回格式为：
     * [
     *  {
     *      itemId:
     *      itemName:
     *      entityId:
     *      entityName:
     *  },
     *  ...
     * ]
     * @param graphId 图谱索引
     * @return 实体和实例对应关系数组
     */
    @GetMapping("/getAllEntityItem")
    public Response getAllEntityItem(@RequestParam("graphId") int graphId){
        List<Entity> allEntity=entityService.getEntityList(graphId);
        List<Map<String, String>> entityItems = new ArrayList<>();
        for (Entity entity:allEntity){
            List<Item> items=itemService.getItemByEntityId(entity.getId());
            for (Item item:items){
                Map<String, String> itemMap=new HashMap<>();
                itemMap.put("itemId",item.getId()+"");
                itemMap.put("itemName",item.getName());
                itemMap.put("entityId",entity.getId()+"");
                itemMap.put("entityName",entity.getType());
                entityItems.add(itemMap);
            }
        }
        System.out.println(entityItems);
        return Response.buildSuccess(entityItems);
    }

    /**
     * 将指定图谱中的实例-关系列出，返回格式为：
     * [
     *      {
     *          headItemId:
     *          tailItemId:
     *          relationId:
     *          headItemName:
     *          tailItemName:
     *          relationName:
     *      },
     *      ...
     * ]
     * @param graphId  图谱索引
     * @return 实例与关系的对应数组
     */
    @GetMapping("/getAllItemRelation")
    public Response getAllItemRelation(@RequestParam("graphId") int graphId){
        List<ItemTriple> tripleList=constructionService.getItemRelationTriple(graphId);
        List<Map<String, String>> itemRelation = new ArrayList<>();
        for (ItemTriple itemTriple:tripleList){
            Relation relation=relationService.getRelationById(itemTriple.getRelationId());
            Item headItem=itemService.getItemByItemId(itemTriple.getStartItem());
            Item tailItem=itemService.getItemByItemId(itemTriple.getEndItem());
            Map<String,String> itemMap=new HashMap<>();
            itemMap.put("headItemId",headItem.getId()+"");
            itemMap.put("tailItemId",tailItem.getId()+"");
            itemMap.put("relationId",relation.getId()+"");
            itemMap.put("headItemName",headItem.getName());
            itemMap.put("tailItemName",tailItem.getName());
            itemMap.put("relationName",relation.getName());
            itemRelation.add(itemMap);
        }
        return Response.buildSuccess(itemRelation);
    }

}
