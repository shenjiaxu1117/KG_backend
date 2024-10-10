package com.chen.controller;

import com.chen.config.Response;
import com.chen.config.Utils;
import com.chen.enums.TaskStatus;
import com.chen.pojo.Task;
import com.chen.service.FileService;
import com.chen.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/task")
public class TaskController {

    @Autowired
    TaskService taskService;

    @Autowired
    FileService fileService;

    @GetMapping("/createTask")
    public Response createTask(@RequestParam(value = "name") String name,@RequestParam(value = "fileId") int source,@RequestParam(value = "description") String description,@RequestParam(value = "graphId") int graphId){
        String fileName=fileService.getFileById(source).getName();
        String type= Utils.fileTypeAnalysis(fileName);
        String currentTime = Utils.getCurTime();
        taskService.createTask(name,source,type,description, TaskStatus.waiting,currentTime,graphId);
        return Response.buildSuccess();
    }

    @GetMapping("/allTask")
    public Response allFile(@RequestParam("graphID") int graphId){
        List<Task> list=taskService.getAllTask(graphId);
        return Response.buildSuccess(list);
    }

    @GetMapping("/updateTask")
    public Response updateTask(@RequestParam(value = "id") int id, @RequestParam(value = "name") String name,@RequestParam(value = "fileId") int source,@RequestParam(value = "description") String description,@RequestParam(value = "graphId") int graphId){
        Task origin=taskService.getTaskById(id);
        String fileName=fileService.getFileById(source).getName();
        String type= Utils.fileTypeAnalysis(fileName);
        String currentTime = Utils.getCurTime();
        if (origin.getSource()==source){
            taskService.updateTask(id,name,source,type,description,origin.getStatus(),currentTime,graphId);
        }else{
            taskService.updateTask(id,name,source,type,description, TaskStatus.waiting,currentTime,graphId);
        }
        return Response.buildSuccess();
    }

    @CrossOrigin
    @GetMapping("/deleteTask")
    public Response deleteFileById(@RequestParam("id") int id){
        int target=taskService.deleteTask(id);
        if(target==1){
            return Response.buildSuccess();
        }else return Response.buildFailure("删除失败！不存在该任务");
    }


}


