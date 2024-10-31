package com.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.enums.TaskStatus;
import com.chen.pojo.Task;

import java.util.List;

public interface TaskService extends IService<Task> {
    void createTask(String name, int fileId, String type, String description, TaskStatus status,String updateTime,int graphId);

    void updateTask(int id, String name, int fileId, String type,String description,TaskStatus status,String updateTime,int graphId);

    int deleteTask(int taskId);

    List<Task> getAllTask(int graphId);

    Task getTaskById(int taskId);

    void updateTaskStatus(int taskId, TaskStatus status);
}
