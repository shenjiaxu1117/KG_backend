package com.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.pojo.FileInfo;
import org.springframework.stereotype.Service;

import java.util.List;

public interface FileService extends IService<FileInfo> {
    void saveFile(String name,String type,String size,String updateTime,int graphId);

    boolean checkFile(String name,String type,String size,int graphId);

    List<FileInfo> getAllFile(int graphId);

    int deleteFile(int id);

    FileInfo getFile(int id);
}
