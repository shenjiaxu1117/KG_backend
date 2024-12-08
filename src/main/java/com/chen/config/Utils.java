package com.chen.config;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
public class Utils {
    /**
     * 分析文件列表后缀名
     * @param fileNameList 文件名列表，例如[xxxxx.后缀,xxxx.后缀]
     * @return 后缀名
     */
    public static String fileListTypeAnalysis(List<String> fileNameList) {
        List<String> extensionList = new ArrayList<>();
        for (String fileName : fileNameList) {
            String[] arr = fileName.split("\\.");
            String extension = arr[arr.length - 1];
            switch (extension.toLowerCase()) {
                case "jpg":
                    extension = "image/jpg格式";
                case "jpeg":
                    extension = "image/jpeg格式";
                case "png":
                    extension = "image/png格式";
                case "gif":
                    extension = "image/gif格式";
                case "pdf":
                    extension = "pdf格式";
                case "zip":
                    extension = "zip格式";
                case "docx":
                    extension = "word格式";
                case "xlsx":
                    extension = "excel格式";
                default:
                    extension = "未知格式";
            }
            if (!extensionList.contains(extension)) {
                extensionList.add(extension);
            }
        }
        return String.join(",", extensionList);
    }

    /**
     * 分析文件后缀名
     * @param fileName 文件名，例如[xxxxx.后缀]
     * @return 后缀名
     */
    public static String fileTypeAnalysis(String fileName) {
        String[] arr = fileName.split("\\.");
        String extension = arr[arr.length - 1];
        switch (extension.toLowerCase()) {
            case "jpg":
                return "image/jpg格式";
            case "jpeg":
                return "image/jpeg格式";
            case "png":
                return  "image/png格式";
            case "gif":
                return  "image/gif格式";
            case "pdf":
                return  "pdf格式";
            case "zip":
                return  "zip格式";
            case "docx":
                return  "word格式";
            case "xlsx":
                return  "excel格式";
            default:
                return  "未知格式";
        }
    }

    /**
     * 获取当前时间
     * @return 当前时间的年月日时分秒形式
     */
    public static String getCurTime(){
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        // 格式化输出
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        return formattedTime;
    }

    /**
     * 读取实体类型-实体实例的Excel文件后形成key、value形式的键值对
     * @param filePath 文件路径
     * @return 格式如下
     * {
     *     实体1：[实例1，实例2，实例3...],
     *     实体2：[实例1，实例2，实例3...],
     *     ...
     * }
     * @throws IOException 异常
     */
    public static Map<String, List<String>> readEntityItemExcel2Map(String filePath) throws IOException {
//        String filePath = "/Users/shenjiaxu/Desktop/实验室/知识图谱/KG_backend/src/main/resources/static/files/测试1.xlsx";  // 替换为你的文件路径
        Map<String, List<String>> dataMap = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);  // 获取表头行

            // 遍历每一列
            for (int colIndex = 0; colIndex < headerRow.getLastCellNum(); colIndex++) {
                Cell headerCell = headerRow.getCell(colIndex);
                String key = headerCell.getStringCellValue();  // 获取表头作为 key

                List<String> columnData = new ArrayList<>();

                // 从第二行开始遍历该列的数据
                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row != null) {
                        Cell cell = row.getCell(colIndex);
                        if (cell != null && (!columnData.contains(cell.toString()))) {
                            columnData.add(cell.toString());  // 将单元格内容加入列表
                        }
                    }
                }
                dataMap.put(key, columnData);  // 将列数据加入 map
            }
        }
        // 打印结果
        System.out.println(dataMap);
        return dataMap;
    }

    /**
     * 读取实例-关系的Excel文件，处理为实例与关系的三元组列表
     * @param filePath 文件路径
     * @return 返回格式如下
     * [
     *      {
     *          headItem: 头实例
     *          relation: 关系
     *          tailItem: 尾实例
     *      },
     *      ...
     * ]
     * @throws IOException 异常
     */
    public static List<Map<String,String>> readRelationItemExcel2List(String filePath) throws IOException {
//        String filePath="/Users/shenjiaxu/Desktop/实验室/知识图谱/test/relation.xlsx";
        List<Map<String,String>> relationList=new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表

            // 读取表头行，记录每个列名对应的索引
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> headerIndexMap = new HashMap<>();
            for (int colIndex = 0; colIndex < headerRow.getLastCellNum(); colIndex++) {
                Cell cell = headerRow.getCell(colIndex);
                if (cell != null) {
                    String headerName = cell.getStringCellValue();
                    headerIndexMap.put(headerName, colIndex);
                }
            }
            // 根据列名获取对应的列索引
            Integer headEntityIndex = headerIndexMap.get("头实体");
            Integer headItemIndex = headerIndexMap.get("头实例");
            Integer relationIndex = headerIndexMap.get("关系");
            Integer tailItemIndex = headerIndexMap.get("尾实例");
            Integer tailEntityIndex = headerIndexMap.get("尾实体");
            System.out.println("头实体:"+headEntityIndex);
            System.out.println("头实例:"+headItemIndex);
            System.out.println("关系:"+relationIndex);
            System.out.println("尾实体:"+tailEntityIndex);
            System.out.println("尾实例:"+tailItemIndex);

            if (headEntityIndex==null||headItemIndex==null||relationIndex==null||tailItemIndex==null||tailEntityIndex==null){
                return null;
            }

            // 逐行读取数据，跳过表头行
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    // 根据表头列索引读取对应数据
                    String headEntity = getCellValue(row.getCell(headEntityIndex));
                    String headItem = getCellValue(row.getCell(headItemIndex));
                    String relation = getCellValue(row.getCell(relationIndex));
                    String tailItem = getCellValue(row.getCell(tailItemIndex));
                    String tailEntity = getCellValue(row.getCell(tailEntityIndex));

                    Map<String,String> map=new HashMap<>();
                    map.put("headEntity", headEntity);
                    map.put("headItem",headItem);
                    map.put("relation",relation);
                    map.put("tailEntity", tailEntity);
                    map.put("tailItem",tailItem);

                    relationList.add(map);
                }
            }
            System.out.println("==========relationList==========");
            for (Map<String, String> stringStringMap : relationList) {
                System.out.println(stringStringMap);
            }
        }
        return relationList;
    }

    /**
     * 读取实例-属性的Excel文件
     * @param filePath 文件路径
     * @return 格式如下
     * [
     *    {
     *        entityName: 实体名称
     *        itemName: 实例名称
     *        propertyName: 属性名
     *        propertyValue: 属性值
     *    },
     *    ...
     * ]
     * @throws IOException 异常
     */
    public static List<Map<String,String>> readItemPropertyExcel2List(String filePath) throws IOException {
        List<Map<String,String>> property=new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(fis)) {
                Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表

                // 读取表头行，记录每个列名对应的索引
                Row headerRow = sheet.getRow(0);
                Map<String, Integer> headerIndexMap = new HashMap<>();
                for (int colIndex = 0; colIndex < headerRow.getLastCellNum(); colIndex++) {
                    Cell cell = headerRow.getCell(colIndex);
                    if (cell != null) {
                        String headerName = cell.getStringCellValue();
                        headerIndexMap.put(headerName, colIndex);
                    }
                }
                // 根据列名获取对应的列索引
                Integer entityIndex = headerIndexMap.get("实体");
                Integer itemIndex = headerIndexMap.get("实例");
                Integer propertyName = headerIndexMap.get("属性名");
                Integer propertyValue = headerIndexMap.get("属性值");
                System.out.println("实体:"+entityIndex);
                System.out.println("实例:"+itemIndex);
                System.out.println("属性名:"+propertyName);
                System.out.println("属性值:"+propertyValue);

                if (entityIndex==null||itemIndex==null||propertyName==null||propertyValue==null){
                    return null;
                }

                // 逐行读取数据，跳过表头行
                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row != null) {
                        // 根据表头列索引读取对应数据
                        String entity = getCellValue(row.getCell(entityIndex));
                        String item = getCellValue(row.getCell(itemIndex));
                        String name = getCellValue(row.getCell(propertyName));
                        String value = getCellValue(row.getCell(propertyValue));

                        Map<String,String> map=new HashMap<>();
                        map.put("entityName", entity);
                        map.put("itemName",item);
                        map.put("propertyName",name);
                        map.put("propertyValue", value);

                        property.add(map);
                    }
                }
                System.out.println("==========property==========");
                for (Map<String, String> stringStringMap : property) {
                    System.out.println(stringStringMap);
                }
            }
        return property;
    }

    // 工具方法：根据单元格类型获取值
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING: // 字符串类型
                return cell.getStringCellValue();
            case NUMERIC: // 数字类型
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 如果是日期类型，格式化为日期字符串
                    return cell.getDateCellValue().toString();
                } else {
                    // 如果是数字类型，将其转换为非科学计数法的字符串
                    return new BigDecimal(cell.getNumericCellValue()).toPlainString();
                }
            case BOOLEAN: // 布尔类型
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: // 公式类型
                return cell.getCellFormula();
            default: // 其他类型
                return cell.toString();
        }
    }

    /***
     * 对llm返回的语句进行结构化处理，得到所需要的实体、属性和关系内容
     * @param content llm输出内容
     * @return 格式如下：
     * {
     *     entity:[
     *          {
     *              entity:实体1
     *              property:[属性1，属性2...]
     *          },
     *          {
     *              entity:实体2
     *              property:[属性1，属性2...]
     *          },
     *          ...
     *     ]
     *     relation:[
     *          {
     *              headEntity:头实体
     *              relation:关系
     *              tailEntity:尾实体
     *          },
     *          {
     *              headEntity:头实体
     *              relation:关系
     *              tailEntity:尾实体
     *          },
     *          ...
     *     ]
     * }
     */
    public static Map<String,Object> getAPIContent(String content) {
        List<String> contentList= new ArrayList<>(Arrays.asList(content.split("\n")));
        CollectionUtil.removeEmpty(contentList);
        int relationIndex=0;
        for (int i=0;i<contentList.size();i++) {
            if (contentList.get(i).equals("实体之间的关系：")){
                relationIndex=i;
                break;
            }
        }
        List<String> entityList=contentList.subList(1,relationIndex);
        List<String> relationList=contentList.subList(relationIndex+1,contentList.size());
        System.out.println("==========> entityList:"+entityList);
        System.out.println("==========> relationList:"+relationList);
        List<Map<String,Object>> entityPropertyList=new ArrayList<>();
        //处理实体和属性
        for (int i=0;i<entityList.size();i++) {
            // 使用正则表达式进行分割，按中文括号（）、逗号（、）进行分割
            List<String> list = new ArrayList<>(Arrays.asList(entityList.get(i).split("[（）、]+")));
            String entity=list.get(0);
            if (list.size()>1){
                list=list.subList(1,list.size());
                CollectionUtil.removeEmpty(list);
                if ((!list.isEmpty()) && (!containsEntity(entityPropertyList,entity))){
                    Map<String,Object> map=new HashMap<>();
                    map.put("entity",entity);
                    map.put("property",list);
                    entityPropertyList.add(map);
                }
            }else{
                if ((!entity.equals("实体类型：")) && (!containsEntity(entityPropertyList,entity))){
                    Map<String,Object> map=new HashMap<>();
                    map.put("entity",entity);
                    map.put("property",new ArrayList<>());
                    entityPropertyList.add(map);
                }
            }
        }
        //处理实体之间的关系
        List<Map<String,String>> relationTripleList=new ArrayList<>();
        for (int i=0;i<relationList.size();i++) {
            List<String> list = new ArrayList<>(Arrays.asList(relationList.get(i).split("-")));
            if (list.size()==3){
                Map<String,String> relationMap=new HashMap<>();
                relationMap.put("headEntity",list.get(0));
                relationMap.put("relation",list.get(1));
                relationMap.put("tailEntity",list.get(2));
                relationTripleList.add(relationMap);
            }
        }
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("entity",entityPropertyList);
        resultMap.put("relation",relationTripleList);
        return resultMap;
    }

    /***
     * getAPIContent内部工具，判断实体是否已在处理好的列表中
     * @param entityList 实体及其属性的结构化列表
     * @param entity 实体名称
     * @return 需要处理的实体是否重复
     */
    private static boolean containsEntity(List<Map<String,Object>> entityList,String entity){
        boolean flag = false;
        for (Map<String,Object> map:entityList) {
            if (map.get("entity").equals(entity)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    /***
     * 随机生成颜色
     * @return 颜色的6位16进制表示法
     */
    public static String randomColor(){
        // 创建随机数生成器
        Random random = new Random();

        // 随机生成红、绿、蓝的16进制值
        String red = Integer.toHexString(random.nextInt(256));  // 随机生成0-255之间的整数，并转为16进制
        String green = Integer.toHexString(random.nextInt(256));
        String blue = Integer.toHexString(random.nextInt(256));

        // 如果生成的颜色值只有1位，则在前面补0
        red = (red.length() == 1) ? "0" + red : red;
        green = (green.length() == 1) ? "0" + green : green;
        blue = (blue.length() == 1) ? "0" + blue : blue;

        // 拼接颜色代码并输出
        String randomColor = "#" + red.toUpperCase() + green.toUpperCase() + blue.toUpperCase();
//        System.out.println("随机颜色: " + randomColor);
        return randomColor;
    }

    public static void main(String[] args) throws IOException {
        String color = randomColor();
        //readEntityItemExcel2Map("/Users/shenjiaxu/Desktop/实验室/知识图谱/数据集/司法/副本patch1-entity.xlsx");
        //readRelationItemExcel2List("/Users/shenjiaxu/Desktop/实验室/知识图谱/数据集/司法/副本patch1-relation.xlsx");
        //readItemPropertyExcel2List("/Users/shenjiaxu/Desktop/实验室/知识图谱/KG_backend/src/main/resources/static/templateFile/template_property.xlsx");
    }
}
