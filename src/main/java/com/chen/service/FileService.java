package com.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.enums.FileCategory;
import com.chen.pojo.FileInfo;

import java.util.List;

public interface FileService extends IService<FileInfo> {
    void saveFile(String name, String type, String size, String updateTime, int graphId, FileCategory category);

    boolean checkFile(String name,String type,String size,int graphId,FileCategory category);

    List<FileInfo> getAllFile(int graphId);

    int deleteFile(int id);

    FileInfo getFileById(int id);
}
