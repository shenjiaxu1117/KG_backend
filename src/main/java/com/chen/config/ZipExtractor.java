package com.chen.config;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor {
    /**
     * 对非结构化的压缩文件夹随机选择其中的一个文件进行解压后存储
     * @param zipFilePath 压缩文件夹的绝对路径
     * @param unzipDirectory 解压文件存储的目录绝对路径
     * @return 解压文件的绝对路径，即unzipDirectory+文件名
     * @throws IOException 异常
     */
    public static String randomUnzipFile(String zipFilePath,String unzipDirectory) throws IOException {
        // 获取所有的文件条目
        List<ZipEntry> entries = new ArrayList<>();
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    entries.add(entry); // 只收集文件条目
                }
                zipInputStream.closeEntry();
            }
        }
        System.out.println("===========> entries <==========");
        System.out.println(entries);

        // 随机选择一个文件条目
        Random random = new Random();
        ZipEntry selectedEntry = entries.get(random.nextInt(entries.size()));  // 随机选择一个文件
        Path unzipSavePath = Paths.get(unzipDirectory);
        String unzipFileName = selectedEntry.getName().substring(selectedEntry.getName().lastIndexOf("/") + 1);
        // 解压选中的文件
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)))) {
            // 寻找选中的文件并解压
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                // 比较当前 entry 是否与选中的 entry 匹配
                if (entry.getName().equals(selectedEntry.getName())) {
                    Path entryPath =unzipSavePath.resolve(unzipFileName);
                    // 如果目标路径中已经存在同名文件，则删除它
                    if (Files.exists(entryPath)) {
                        Files.delete(entryPath);
                        System.out.println("旧文件已删除: " + entryPath);
                    }
                    // 解压文件
                    Files.createDirectories(entryPath.getParent()); // 确保父目录存在
                    try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(entryPath))) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zipInputStream.read(buffer)) > 0) {
                            bos.write(buffer, 0, len);
                        }
                    }
                    break;  // 找到文件后解压，跳出循环
                }
                zipInputStream.closeEntry();
            }
        }
        String unzipFilePath =  unzipSavePath+"/"+unzipFileName;
        System.out.println("解压缩的随机文件存储在："+unzipFilePath);
        return unzipFilePath;
    }
}
