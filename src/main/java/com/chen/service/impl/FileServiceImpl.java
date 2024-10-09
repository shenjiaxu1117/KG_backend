package com.chen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.mapper.FileMapper;
import com.chen.pojo.FileInfo;
import com.chen.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileInfo> implements FileService {

    @Autowired
    FileMapper fileMapper;

    @Override
    public void saveFile(String name,String type,String size,String updateTime,int graphId) {
        FileInfo file = new FileInfo();
        file.setName(name);
        file.setType(type);
        file.setSize(size);
        file.setUpdateTime(updateTime);
        file.setGraphId(graphId);

        fileMapper.insert(file);
    }

    @Override
    public boolean checkFile(String name,String type,String size,int graphId){
        return fileMapper.existsFile(name,type,size,graphId);
    }

    @Override
    public List<FileInfo> getAllFile(int graphId) {
        return fileMapper.findFileByGraphId(graphId);
    }

    @Override
    public int deleteFile(int id) {
        return fileMapper.deleteById(id);
    }

    @Override
    public FileInfo getFile(int id) {
        return fileMapper.selectById(id);
    }
}
