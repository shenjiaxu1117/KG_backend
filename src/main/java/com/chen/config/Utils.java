package com.chen.config;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Utils {
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
                return "image/png格式";
            case "gif":
                return "image/gif格式";
            case "pdf":
                return "pdf格式";
            case "zip":
                return "zip格式";
            case "docx":
                return "word格式";
            case "xlsx":
                return "excel格式";
            default:
                return "未知格式";
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
}
