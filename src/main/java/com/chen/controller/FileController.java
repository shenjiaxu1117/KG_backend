package com.chen.controller;

import cn.hutool.core.io.FileUtil;
import com.chen.config.Response;
import com.chen.config.Utils;
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

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@CrossOrigin
@RequestMapping("/file")
public class FileController {

    @Autowired
    FileService fileService;

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @PostMapping("uploadFile")
    public Response uploadFile(@RequestParam("file") MultipartFile file,@RequestParam("graphID") int graphId) throws IOException {
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

        if (fileService.checkFile(originalFilename,type,size,graphId)){
            return Response.buildFailure("文件已存在！");
        }else {
            fileService.saveFile(originalFilename,type,size,formattedTime,graphId);
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
    public Response findFileById(@RequestParam("fileId") int id){
        FileInfo file=fileService.getFileById(id);
        return Response.buildSuccess(file);
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

//    @GetMapping("/fetchFileContent")
//    public Response fetchFile(@RequestParam("id") int fileId){
//        FileInfo fileInfo=fileService.getFile(fileId);
//        Path path = Paths.get(System.getProperty("user.dir") + fileUploadPath + fileInfo.getName());
//        try {
//            Resource resource = new UrlResource(path.toUri());
//            System.out.println(path.toUri());
//            System.out.println("resource: "+resource);
////            InputStream inputStream = resource.getInputStream();
////            // 读取文件内容到字节数组
////            byte[] data = StreamUtils.copyToByteArray(inputStream);
//            // 设置下载响应头
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
//            // 返回自定义的 Response
//            return Response.buildSuccess(resource);
//        } catch (MalformedURLException e) {
//            e.printStackTrace(); // 打印错误信息
//        }
////        catch (IOException e) {
////            e.printStackTrace(); // 打印文件读取错误
////        }
//        return Response.buildFailure();
//    }

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
            for (Integer fileId : fileIds) {
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

}


