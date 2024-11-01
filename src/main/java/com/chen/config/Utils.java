package com.chen.config;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.hutool.poi.excel.cell.CellUtil.getCellValue;

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
     * @throws IOException
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
                        if (cell != null) {
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
     * @throws IOException
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

            // 逐行读取数据，跳过表头行
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    // 根据表头列索引读取对应数据
                    String headEntity = row.getCell(headEntityIndex)!= null ? row.getCell(headEntityIndex).toString() : "";
                    String headItem = row.getCell(headItemIndex)!= null ? row.getCell(headItemIndex).toString() : "";
                    String relation = row.getCell(relationIndex)!= null ? row.getCell(relationIndex).toString() : "";
                    String tailItem = row.getCell(tailItemIndex)!= null ? row.getCell(tailItemIndex).toString() : "";
                    String tailEntity = row.getCell(tailEntityIndex)!= null ? row.getCell(tailEntityIndex).toString() : "";

                    Map<String,String> map=new HashMap<>();
                    map.put("headEntity", headEntity);
                    map.put("headItem",headItem);
                    map.put("relation",relation);
                    map.put("tailEntity", tailEntity);
                    map.put("tailItem",tailItem);
                    relationList.add(map);
                }
            }
            System.out.println(relationList);
        }
        return relationList;
    }
}
