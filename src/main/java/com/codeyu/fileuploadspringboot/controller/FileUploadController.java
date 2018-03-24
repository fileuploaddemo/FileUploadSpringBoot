package com.codeyu.fileuploadspringboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.codeyu.fileuploadspringboot.Utils;
import com.codeyu.fileuploadspringboot.domain.FileInfo;
import com.codeyu.fileuploadspringboot.domain.RespCode;
import com.codeyu.fileuploadspringboot.domain.RespEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class FileUploadController {
    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "_temp//";

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/files")
    @ResponseBody
    public RespEntity getFiles() {
        List<FileInfo> files = new ArrayList<>();
        Utils.traverseFileList(UPLOADED_FOLDER, files);
        return new RespEntity(RespCode.SUCCESS, files);
    }

    @PostMapping("/files")
    @ResponseBody
    public RespEntity fileUpload(@RequestParam("newfile") MultipartFile file, HttpServletRequest req) {
        RespEntity result = new RespEntity(RespCode.SUCCESS);
        List<FileInfo> files = new ArrayList<FileInfo>();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
        String dateDir = df.format(new Date());// new Date()为获取当前系统时间
        String fileId = Utils.get32UUID();
        String serviceName = fileId + "_" + file.getOriginalFilename();
        File tempFile = new File(UPLOADED_FOLDER + dateDir + File.separator + serviceName);
        if (!tempFile.getParentFile().exists()) {
            tempFile.getParentFile().mkdirs();
        }
        if (!file.isEmpty()) {
            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile));
                out.write(file.getBytes());
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                result.setCode(-1);
                result.setMsg("上传失败," + e.getMessage());
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                result.setCode(-1);
                result.setMsg("上传失败," + e.getMessage());
                return result;
            }
            String fileName = file.getOriginalFilename();
            FileInfo fileInfo = new FileInfo();
            fileInfo.setId(fileId);
            fileInfo.setName(fileName);
            fileInfo.setSize(file.getSize());
            files.add(fileInfo);
            result.setData(files);
            return result;
        } else {
            result.setCode(-1);
            result.setMsg("上传失败，因为文件是空的");
            return result;
        }
    }

    @DeleteMapping("/files/{fileName:.+}")
    @ResponseBody
    public String deleteFiles(@PathVariable("fileName") String fileName) {
        boolean isDelete = Utils.deleteFile(UPLOADED_FOLDER, fileName);
        return Boolean.toString(isDelete);
    }

    //@GetMapping("/files/{fileName:.+}")
    public void downloadFile_deprecated(@PathVariable("fileName") String fileName, HttpServletResponse response) throws JsonProcessingException {
        RespEntity result = new RespEntity(RespCode.SUCCESS);
        File file = Utils.findIt(UPLOADED_FOLDER, fileName);
        if (file != null && file.exists()) { //判断文件父目录是否存在
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
            response.setHeader("content-type", "application/octet-stream");

            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;

            OutputStream os = null; //输出流
            try {
                os = response.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer);
                    os.flush();
                    i = bis.read(buffer);
                }

            } catch (Exception e) {
                e.printStackTrace();
                result.setCode(-1);
                result.setMsg("下载失败," + e.getMessage());
            }
            try {
                bis.close();
                fis.close();
                result.setCode(0);
                result.setMsg("下载成功");
            } catch (IOException e) {
                e.printStackTrace();
                result.setCode(-1);
                result.setMsg("下载失败," + e.getMessage());
            }
        } else {
            result.setCode(-1);
            result.setMsg("下载失败，因为文件不存在");
        }
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(result);
		System.out.println(jsonInString);
    }

    @GetMapping("/files/{fileName:.+}")
    public void downloadFile(HttpServletResponse response, @PathVariable("fileName") String fileName)
            throws IOException {
        File file = Utils.findIt(UPLOADED_FOLDER, fileName);
        if (file != null && file.exists()) {
            // Get your file stream from wherever.
            InputStream myStream = new FileInputStream(file);

            // Set the content type and attachment header.
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
            response.setHeader("content-type", "application/octet-stream");

            // Copy the stream to the response's output stream.
            FileCopyUtils.copy(myStream, response.getOutputStream());
            if (myStream != null) {
                myStream.close();
            }
            response.flushBuffer();
        } else {
            System.out.println(fileName + "下载失败，因为文件不存在");
        }
    }

}