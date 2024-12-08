package com.chen.controller;

import cn.hutool.core.io.FileUtil;
import com.chen.config.Response;
import com.chen.config.Utils;
import com.chen.enums.FileCategory;
import com.chen.pojo.FileInfo;
import com.chen.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@RestController
@CrossOrigin
@RequestMapping("/file")
public class FileController {

    @Autowired
    FileService fileService;

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Value("${files.template.path}")
    private String fileTemplatePath;

    @CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
    @PostMapping("uploadFile")
    public Response uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("graphID") int graphId, @RequestParam("category") FileCategory category) throws IOException {
        //获取文件原始名称
        String originalFilename = file.getOriginalFilename();
        System.out.println("originalFilename: "+originalFilename);
        //获取文件的类型
        String type = FileUtil.extName(originalFilename);
        System.out.println("type: "+type);
        String size="";
        // 获取文件大小（字节）
        long sizeInBytes = file.getSize();
        // 转换为 KB
        if (sizeInBytes<1024){
            size=sizeInBytes+"B";
        }else{
            double sizeInKB = sizeInBytes / 1024.0;
            if (sizeInKB<1024){
                size=String.format("%.2f", sizeInKB)+"KB";
            }else {
                // 转换为 MB
                double sizeInMB = sizeInBytes / (1024.0 * 1024.0);
                size=String.format("%.2f", sizeInMB)+"MB";
            }
        }
        System.out.println("size: "+size);

        String formattedTime = Utils.getCurTime();

        // 文件下载 URI
//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/files/")
//                .path(originalFilename)
//                .toUriString();
//        System.out.println("fileDownloadUri: "+fileDownloadUri);

        if (fileService.checkFile(originalFilename,type,size,graphId,category)){
            return Response.buildFailure("文件已存在！");
        }else {
            fileService.saveFile(originalFilename,type,size,formattedTime,graphId,category);
            //将临时文件转存到指定磁盘位置
            file.transferTo(new File(System.getProperty("user.dir") + fileUploadPath + originalFilename));
            //file.transferTo(new File(System.getProperty("user.dir") + "/src/main/resources/SentStrength_Data/input.txt"));
            return Response.buildSuccess();
        }

        //获取文件
//        File uploadParentFile = new File(fileUploadPath);
//        //判断文件目录是否存在
//        if(!uploadParentFile.exists()) {
//            //如果不存在就创建文件夹
//            uploadParentFile.mkdirs();
//        }
        //定义一个文件唯一标识码（UUID）
        //String uuid = UUID.randomUUID().toString();

    }

    @GetMapping("/allFile")
    public Response allFile(@RequestParam("graphID") int graphId){
        List<FileInfo> list=fileService.getAllFile(graphId);
        return Response.buildSuccess(list);
    }

    @GetMapping("/findFile")
    public Response findFileById(@RequestParam("fileId") int[] id){
        List<FileInfo> fileList = new ArrayList<>();
        for(int i=0;i<id.length;i++){
            FileInfo file=fileService.getFileById(id[i]);
            fileList.add(file);
        }
        return Response.buildSuccess(fileList);
    }

    /**
     * 删除存储在数据库和本地的指定id文件
     * @param list 需要删除文件id的列表
     * @return 返回没有成功删除的id列表，如果全部成功删除，返回00000
     */
    @CrossOrigin
    @GetMapping("/deleteFile")
    public Response deleteFileById(@RequestParam("idList") int[] list){
        List<Integer> idListNotDelete=new ArrayList<>();
        for (int i=0;i<list.length;i++){
            FileInfo fileInfo=fileService.getFileById(list[i]);
            if (fileInfo!=null) {
                //在数据库中删除
                fileService.deleteFile(list[i]);
                // 创建 File 对象
                File file = new File(System.getProperty("user.dir") + fileUploadPath + fileInfo.getName());
                // 在本地删除该文件
                if (file.exists()) {
                    if (file.delete()) {
                        continue;
                    } else {
                        idListNotDelete.add(list[i]);
                    }
                } else {
                    idListNotDelete.add(list[i]);
                }
            }else idListNotDelete.add(list[i]);
        }
        if (idListNotDelete.isEmpty()){
            return Response.buildSuccess();
        }else return Response.buildFailure(idListNotDelete);
    }

    @GetMapping("/fetchFileContent")
    public ResponseEntity<byte[]> fetchFile(@RequestParam("id") int fileId) throws IOException {
        FileInfo fileInfo=fileService.getFileById(fileId);
        Path path = Paths.get(System.getProperty("user.dir") + fileUploadPath + fileInfo.getName());
        byte[] fileContent = Files.readAllBytes(path);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(fileContent);
    }

    /**
     * 处理非结构化上传的压缩文件，取出文件夹中所有文件的文件名和文件内容
     * @param fileId 文件索引值
     * @return 返回包含文件类型、文件名称和文件内容的map，具体格式如下：
     * [
     *      {
     *          fileType:
     *          fileName:
     *          content://内容为String类型
     *      },
     *      ...
     * ]
     * @throws IOException 异常
     */
    @GetMapping("/fetchZipContent")
    public Response fetchZip(@RequestParam("id") int fileId) throws IOException {
        FileInfo fileInfo=fileService.getFileById(fileId);
        Path zipFilePath = Paths.get(System.getProperty("user.dir") + fileUploadPath + fileInfo.getName());
        // 解压 ZIP 文件
        List<Map<String, Object>> fileContents = new ArrayList<>();
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    Map<String, Object> fileMap = new HashMap<>();
                    String[] fileName = entry.getName().split("/");
                    if (fileName.length==2){
                        fileMap.put("fileType", fileName[0]);
                        fileMap.put("fileName", fileName[1]);
                        // 读取每个文件的内容
                        byte[] content = zipInputStream.readAllBytes();
//                        String encodedContent = Base64.getEncoder().encodeToString(content);
                        String contentStr = new String(content, StandardCharsets.UTF_8);  // 将字节数组转换为字符串
                        fileMap.put("content", contentStr);  // 返回文件内容，可以选择返回 String 或 Base64 编码后的内容
                        fileContents.add(fileMap);
                    }
                }
                zipInputStream.closeEntry();
            }
        }
        // 返回包含每个文件名和内容的列表
//        System.out.println("==========> fileContents <==========");
//        System.out.println(fileContents);
        return Response.buildSuccess(fileContents);
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<Resource> downloadFile(@RequestParam("id") int id){
        FileInfo fileInfo=fileService.getFileById(id);
        String fileName = fileInfo.getName();
        String filePath = System.getProperty("user.dir") + fileUploadPath + fileName;
        // 创建 FileSystemResource 对象
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }
        // 设置响应头，告诉浏览器下载文件
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(new FileSystemResource(filePath));
    }

    @GetMapping("/downloadMultipleFiles")
    public void downloadMultipleFiles(@RequestParam("fileIds") int[] fileIds, HttpServletResponse response) throws IOException{
        // 设置返回的响应头
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=files.zip");

        // 创建 Zip 输出流
        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            for (int fileId : fileIds) {
                // 从数据库中查询文件信息
                FileInfo fileInfo = fileService.getFileById(fileId);
                String filePath = System.getProperty("user.dir") + fileUploadPath + fileInfo.getName();

                // 创建文件输入流
                try (FileInputStream fis = new FileInputStream(new File(filePath))) {
                    // 创建压缩条目
                    zos.putNextEntry(new ZipEntry(fileInfo.getName()));

                    // 写入文件数据到压缩包
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    zos.closeEntry();
                }
            }
            zos.finish(); // 完成压缩
        }
    }

    @GetMapping("/downloadTemplateFiles")
    public void downloadTemplateFiles(HttpServletResponse response) throws IOException{
        // 设置返回的响应头
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=files.zip");
        String[] fileName={"template_item.xlsx","template_relation.xlsx","template_property.xlsx"};
        // 创建 Zip 输出流
        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            for (String eachFile : fileName) {
                String filePath = System.getProperty("user.dir") + fileTemplatePath + eachFile;
                System.out.println("filePath: "+filePath);
                // 创建文件输入流
                try (FileInputStream fis = new FileInputStream(new File(filePath))) {
                    // 创建压缩条目
                    zos.putNextEntry(new ZipEntry(eachFile));
                    // 写入文件数据到压缩包
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    zos.closeEntry();
                }
            }
            zos.finish(); // 完成压缩
        }
    }

}


