package com.chen.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvGenerator {
    // 生成CSV文件
    public static void generateCsv(List<String> headers, List<List<String>> data, String filePath, String fileName) throws IOException {
        // 创建文件夹，如果不存在的话
        System.out.println();
        File directory = new File(filePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 创建CSV文件
        File csvFile = new File(directory, fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            // 写入表头
            writer.write(String.join(",", headers));
            writer.newLine();

            // 写入数据行
            for (List<String> row : data) {
                writer.write(String.join(",", row));
                writer.newLine();
            }

            System.out.println("CSV文件已成功生成： " + csvFile.getAbsolutePath());
        }
    }
//    public static void main(String[] args) {
//        try {
//            // 示例表头和数据
//            List<String> headers = List.of("head-node-name", "head-label-name", "relation", "tail-node-name", "tail-label-name");
//            List<List<String>> data = List.of(
//                    List.of("1", "商品", "xx关系", "2", "人员"),
//                    List.of("1", "商品", "xx关系", "3", "公司"),
//                    List.of("2", "人员", "xx关系", "3", "公司"),
//                    List.of("3", "公司", "xx关系", "2", "人员")
//            );
//            // 指定存储路径和文件名
//            String path = "/Users/shenjiaxu/Desktop/实验室/知识图谱/KG_backend/src/main/resources/static/csv"; // 修改为你的存储路径
//            String fileName = "关系表.csv";
//
//            // 生成CSV文件
//            generateCsv(headers, data, path, fileName);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
