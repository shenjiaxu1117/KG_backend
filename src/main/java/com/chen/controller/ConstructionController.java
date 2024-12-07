package com.chen.controller;

import com.chen.config.CsvGenerator;
import com.chen.config.Response;
import com.chen.config.Utils;
import com.chen.config.ZipExtractor;
import com.chen.enums.DataType;
import com.chen.enums.FileCategory;
import com.chen.enums.TaskStatus;
import com.chen.mapper.ConstructionMapper;
import com.chen.pojo.*;
import com.chen.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Random;

@RestController
@CrossOrigin
@RequestMapping("/kg")
public class ConstructionController {
    @Autowired
    EntityService entityService;

    @Autowired
    PropertyService propertyService;

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
    @Autowired
    private ConstructionMapper constructionMapper;

    /*** Entity ***/

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

    /*** Property ***/

    @GetMapping("/createProperty")
    public Response createProperty(@RequestParam("name") String name, @RequestParam("type") DataType type,@RequestParam("unit") String unit,@RequestParam("entityId") int entityId){
        String res= propertyService.createProperty(entityId,name,type,unit);
        return Response.buildSuccess(res);
    }

    @GetMapping("/updatePropertyInfo")
    public Response updateProperty(@RequestParam("id") int id, @RequestParam("name") String name, @RequestParam("type") DataType type,@RequestParam("unit") String unit,@RequestParam("entityId") int entityId){
        String res= propertyService.updateProperty(entityId,id,name,type,unit);
        if (res.equals("属性修改成功")){
            return Response.buildSuccess();
        }else{
            return Response.buildFailure(res);
        }
    }

    @GetMapping("/getEntityPropertyByGraphId")
    public Response getEntityProperty(@RequestParam("graphId") int graphId){
        List<Entity> entityList=entityService.getEntityList(graphId);
        Map<Integer,List<Property>> map=new HashMap<>();
        for (Entity entity:entityList){
            List<Property> propertyList=propertyService.getPropertyListByEntityId(entity.getId());
            map.put(entity.getId(),propertyList);
        }
        System.out.println("map: "+map);
        return Response.buildSuccess(map);
    }


    /*** Relation ***/

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
        }else if (file.getCategory().equals(FileCategory.relation)){
            //处理实例-关系表格
            List<Map<String,String>> relationItemList=Utils.readRelationItemExcel2List(filePath);
            if (relationItemList==null){
                return Response.buildFailure("格式错误！请参照模版文件");
            }
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
        }else{
            //处理实例-属性表格
            List<Map<String,String>> propertyMapList=Utils.readItemPropertyExcel2List(filePath);
            if (propertyMapList==null){
                return Response.buildFailure("格式错误！请参照模版文件");
            }
            for (Map<String,String> itemMap : propertyMapList) {
                Entity entity=entityService.getEntityByName(itemMap.get("entityName"),task.getGraphId());
                if (entity!=null){
                    Item item=itemService.getItemByItemNameAndEntityId(itemMap.get("itemName"),entity.getId());
                    Property property=propertyService.getPropertyByNameAndEntityId(itemMap.get("propertyName"),entity.getId());
                    if (item!=null&&property!=null){
                        constructionService.insertItemProperty(item.getId(),property.getId(),itemMap.get("propertyValue"));
                    }
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

    @GetMapping("/getAllItemProperty")
    public Response getAllItemProperty(@RequestParam("graphId") int graphId){
        List<Item> itemList=itemService.getItemByGraphId(graphId);
        Map<Integer,List<Map<String,String>>> allItemProperty=new HashMap<>();
        for (Item item:itemList){
            List<Map<String,String>> propertyList=constructionService.getPropertyNameAndValue(item.getId());
            allItemProperty.put(item.getId(),propertyList);
        }
        System.out.println("==========/getAllItemProperty: allItemProperty==========");
        System.out.println(allItemProperty);
        return Response.buildSuccess(allItemProperty);
    }

    @Value("${files.csv.path}")
    private String csvPath;

    @Value("${files.neo4j.path}")
    private String neo4jPath;

    @Autowired
    private Driver driver;  // Neo4j 驱动

    /**
     * 将指定图谱中的实例关系和属性存储为csv文件，导入生成neo4j图数据
     * @param graphId 图谱索引
     * @return 生成图谱
     * @throws IOException
     *
     * 表格分别命名为[图谱id]item/property/relation.csv，分别表示实例-实体表，实例-属性表，实例关系表
     *
     * ========== 表结构 ==========
     * item表：
     * node_name,label_name,name
     * 实例id，实例对应的实体名称，实例名称
     *
     * relation表：
     * head_node_name,relation,tail_node_name
     * 头实例id，关系名称，尾实例id
     *
     * property表：
     * //TODO：
     *
     *
     * ========== 配置 ==========
     * 下载安装neo4j并安装apoc插件
     * 修改 /conf/neo4j.conf:# server.directories.import=import
     * 在 LOAD CSV 时使用外部文件
     */
    @GetMapping("/generateGraph")
    public Response generateGraph(@RequestParam("graphId") int graphId) throws IOException {
        //实例-关系三元组
        List<Item> itemList=itemService.getItemByGraphId(graphId);
        List<List<String>> itemEntity=new ArrayList<>();
        List<List<String>> itemProperty=new ArrayList<>();
        for (Item item:itemList){
            List<String> eachItem=new ArrayList<>();
            Entity entity=entityService.getEntityById(item.getEntityId());
            eachItem.add(item.getId()+"");
            eachItem.add(entity.getType());
            eachItem.add(item.getName());
            itemEntity.add(eachItem);
            //property
            List<Map<String, String>> propertyNameAndValue=constructionService.getPropertyNameAndValue(item.getId());
            for(Map<String,String> itemMap:propertyNameAndValue){
                String propertyName=itemMap.get("name");
                String propertyValue=itemMap.get("value");
                List<String> eachPropertyLine=new ArrayList<>();
                eachPropertyLine.add(item.getId()+"");
                eachPropertyLine.add(propertyName);
                eachPropertyLine.add(propertyValue);
                itemProperty.add(eachPropertyLine);
            }
        }
        List<ItemTriple> tripleList=constructionService.getItemRelationTriple(graphId);
        List<List<String>> itemRelation=new ArrayList<>();

        for (ItemTriple itemTriple:tripleList){
            Item headItem=itemService.getItemByItemId(itemTriple.getStartItem());
            Item tailItem=itemService.getItemByItemId(itemTriple.getEndItem());
            Relation relation=relationService.getRelationById(itemTriple.getRelationId());
            List<String> eachItemRelation=new ArrayList<>();
            eachItemRelation.add(headItem.getId()+"");
            eachItemRelation.add(relation.getName());
            eachItemRelation.add(tailItem.getId()+"");
            itemRelation.add(eachItemRelation);
            //TODO:实例-属性
        }
        System.out.println("=====itemEntity=====\n"+itemEntity);
        System.out.println("=====itemRelation=====\n"+itemRelation);
        System.out.println("=====itemProperty=====\n"+itemProperty);
        String path = System.getProperty("user.dir") + csvPath;
        String csv_itemEntity=graphId+"item.csv";
        String csv_relation=graphId+"relation.csv";
        String csv_itemProperty=graphId+"property.csv";
        //生成csv
        CsvGenerator.generateCsv(List.of("node_name", "label_name", "name"),itemEntity,path,csv_itemEntity);
        CsvGenerator.generateCsv(List.of("head_node_name", "relation", "tail_node_name"),itemRelation,path,csv_relation);
        CsvGenerator.generateCsv(List.of("node_name", "property_name", "property_value"),itemProperty,path,csv_itemProperty);
//        Files.copy(Paths.get(path+csv_itemEntity), Paths.get(neo4jPath+csv_itemEntity), StandardCopyOption.REPLACE_EXISTING);
//        Files.copy(Paths.get(path+csv_relation), Paths.get(neo4jPath+csv_relation), StandardCopyOption.REPLACE_EXISTING);
//        Files.copy(Paths.get(path+csv_itemProperty), Paths.get(neo4jPath+csv_itemProperty), StandardCopyOption.REPLACE_EXISTING);
        //neo4j读取csv
        try (Session session = driver.session()) {
            String clearQuery="MATCH (n) DETACH DELETE n";
            String cypherQuery1 = String.format(
                    "LOAD CSV WITH HEADERS FROM 'file:///%s' AS row " +
                            "CALL apoc.create.node([row.label_name], {node_name: row.node_name, name: row.name}) YIELD node " +
                            "RETURN node",
                    URLEncoder.encode(path + csv_itemEntity, "UTF-8")
            );
            String cypherQuery2 = String.format(
                    "LOAD CSV WITH HEADERS FROM 'file:///%s' AS row " +
                            "MATCH (head {node_name: row.head_node_name}), (tail {node_name: row.tail_node_name}) " +
                            "CALL apoc.create.relationship(head, row.relation, {}, tail) YIELD rel " +
                            "RETURN head, tail, rel",
                    URLEncoder.encode(path + csv_relation, "UTF-8")
            );
            //:TODO:cypherQuery3未debug
            String cypherQuery3 = String.format(
                    "LOAD CSV WITH HEADERS FROM 'file:///%s' AS row " +
                            "MATCH (n {node_name: row.node_name}) " +
                            "SET n[row.property_name] = row.property_value ",
                    URLEncoder.encode(path + csv_itemProperty, "UTF-8")
            );
            try (Transaction tx = session.beginTransaction()) {
                tx.run(clearQuery);
                tx.run(cypherQuery1);
                tx.run(cypherQuery2);
                tx.run(cypherQuery3);
                tx.commit();
            }
        }catch (Exception e) {
            // Log the exception or handle it without causing recursion
            e.printStackTrace();
        }
//        driver.close();
        return Response.buildSuccess();
    }

    @Value("${files.unzip.path}")
    private String unzipPath;

    /***
     * 调用kimiAPI对文件进行初步的实体、属性和关系归类抽取
     * @param fileId 选择的非结构化文件
     * @return 返回api抽取得到的结果，具体格式详见Utils.getAPIContent()
     * @throws IOException 异常
     */
    @GetMapping("/useAPI")
    public Response useApiGenerateEntity(@RequestParam("fileId") int fileId) throws IOException{
        FileInfo fileInfo=fileService.getFileById(fileId);
        String zipPath = System.getProperty("user.dir") + fileUploadPath + fileInfo.getName();
        String unzipDirectory = System.getProperty("user.dir") + unzipPath;
        String targetFilePath = ZipExtractor.randomUnzipFile(zipPath,unzipDirectory);

        String line;
        Map<String,Object> contentMap;
        try {
            // 定义 Python 脚本路径
            String pythonScriptPath = System.getProperty("user.dir")+"/src/main/resources/program/LLMapi.py";
            // 构建 ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath ,targetFilePath);
            // 启动进程
            Process process = processBuilder.start();

            // 等待脚本执行完毕
            int exitCode = process.waitFor();
            System.out.println("Python script executed with exit code: " + exitCode);

            // 获取 Python 脚本的标准输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            // 获取 Python 脚本的错误输出流
            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            // 读取错误输出
            while ((line = errorReader.readLine()) != null) {
                System.err.println("Python script error: " + line);
            }

            // 输出 Python 脚本返回的内容
            String content = output.toString();
            System.out.println("Content from Python script: ");
            System.out.println(content);

            if (!content.equals("")){
                contentMap=Utils.getAPIContent(content);
                System.out.println("=========> contentMap <=========");
                System.out.println(contentMap);
                return Response.buildSuccess(contentMap);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return Response.buildFailure();
    }

    @PostMapping("/createMultiEntitiesAndProperties")
    public Response createMultiEntity(@RequestBody Map<String,Object> config) {
        List<Map<String,Object>> entityPropertyList = (List<Map<String, Object>>) config.get("entityList");
        int graphId = Integer.parseInt(config.get("graphId").toString());
        for (Map<String,Object> entityProperty : entityPropertyList) {
            String entityName=entityProperty.get("entity").toString();
            String createEntityResult="";
            while((!createEntityResult.equals("实体名称已存在！"))&&(!createEntityResult.equals("create entity success"))){ //防止随机生成的颜色与已存在实体的颜色重复
                createEntityResult=entityService.createEntity(entityName, Utils.randomColor(), graphId);
            }
            Entity entity=entityService.getEntityByName(entityName,graphId);
            List<String> propertyList= (List<String>) entityProperty.get("property");
            if(!propertyList.isEmpty()){
                for(String propertyName:propertyList){
                    DataType defaultDataType=DataType.string;
                    String defaultUnit="无";
                    String res= propertyService.createProperty(entity.getId(),propertyName,defaultDataType,defaultUnit);
                }
            }
        }
        return Response.buildSuccess();
    }

    @PostMapping("/createMultiRelations")
    public Response createMultiRelations(@RequestBody Map<String,Object> config) {
        List<Map<String,String>> relationList = (List<Map<String, String>>) config.get("relationList");
        int graphId = Integer.parseInt(config.get("graphId").toString());
        for (Map<String,String> eachRelation : relationList) {
            String relationName=eachRelation.get("relation");
            String headEntityName=eachRelation.get("headEntity");
            String tailEntityName=eachRelation.get("tailEntity");
            relationService.createRelation(relationName,graphId);
            Relation relation=relationService.getRelationByName(relationName,graphId);
            Entity headEntity=entityService.getEntityByName(headEntityName,graphId);
            Entity tailEntity=entityService.getEntityByName(tailEntityName,graphId);
            if (headEntity!=null && tailEntity!=null){
                String res=constructionService.insertTriple(headEntity.getId(),relation.getId(),tailEntity.getId());
            }
        }
        return Response.buildSuccess();
    }
}
