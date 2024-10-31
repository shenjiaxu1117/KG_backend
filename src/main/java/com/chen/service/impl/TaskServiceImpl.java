package com.chen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.enums.TaskStatus;
import com.chen.mapper.TaskMapper;
import com.chen.pojo.Task;
import com.chen.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    @Autowired
    TaskMapper taskMapper;

    @Override
    public void createTask(String name, int fileId, String type, String description, TaskStatus status, String updateTime,int graphId) {
        Task task = new Task();
        task.setName(name);
        task.setType(type);
        task.setSource(fileId);
        task.setDescription(description);
        task.setStatus(status);
        task.setUpdateTime(updateTime);
        task.setGraphId(graphId);

        taskMapper.insert(task);
    }

    @Override
    public void updateTask(int id, String name, int fileId, String type, String description, TaskStatus status, String updateTime, int graphId) {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setType(type);
        task.setSource(fileId);
        task.setDescription(description);
        task.setStatus(status);
        task.setUpdateTime(updateTime);
        task.setGraphId(graphId);

        taskMapper.updateById(task);
    }

    @Override
    public int deleteTask(int id){
        return taskMapper.deleteById(id);
    }

    @Override
    public List<Task> getAllTask(int graphId) {
        return taskMapper.getTaskByGraphId(graphId);
    }

    @Override
    public Task getTaskById(int taskId) {
        return taskMapper.selectById(taskId);
    }

    @Override
    public void updateTaskStatus(int taskId, TaskStatus status) {
        Task task = taskMapper.selectById(taskId);
        task.setStatus(status);
        taskMapper.updateById(task);
    }
}

