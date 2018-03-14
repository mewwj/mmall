package com.mall.service.impl;

import java.io.File;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.mall.service.IFileService;
import com.mall.util.FTPUtil;


@Service("iFileService")
public class FileServiceImpl implements IFileService {
	private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);
		public String upload(MultipartFile file,String path){
		
		String fileName = file.getOriginalFilename();
		//扩展名
		String fileExtensionName=fileName.substring(fileName.lastIndexOf(".")+1);
		String uploadFileName=UUID.randomUUID().toString()+"."+fileExtensionName;
		
		logger.info("文件开始上传，上传文件的文件名：{}，上传的路径是：{}，新文件名：{}",fileName,path,uploadFileName);
		
		File fileDir =new File(path);
		if(fileDir.exists()){
			fileDir.setWritable(true);
			fileDir.mkdirs();
		}
		File targetFile =new File(path,uploadFileName);		
			try {
				//文件上传成功
				file.transferTo(targetFile);
				System.out.println("已经上传到目标文件夹");
				//将targetFile 上传到FTP服务器
				FTPUtil.uploadFile(Lists.newArrayList(targetFile));  //已经上传到Ftp服务器上
				//上传完成后  ，删除upload下面的文件 
				targetFile.delete();
				
			} catch (Exception e) {
                     logger.error("上传文件异常：",e);
                     return null;
			}
	
				return targetFile.getName();
		}
}
