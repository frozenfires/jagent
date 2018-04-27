package com.ethink.agent.task.executor.server;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ethink.agent.Config;
import com.ethink.agent.SerialNumber;
import com.ethink.agent.annotation.TaskExecutors;
import com.ethink.agent.decode.RdpEncoder;
import com.ethink.agent.net.SendMessage;
import com.ethink.agent.task.bean.RDPTask;
import com.ethink.agent.task.bean.ServerTask;
import com.ethink.agent.task.bean.Task;
import com.ethink.agent.task.executor.TaskExecutor;
import com.ethink.agent.util.FileUtil;

@TaskExecutors(value="getTermScreenAct")
public class GetTermScreen implements TaskExecutor{
	
	private final static Logger log = LoggerFactory.getLogger(GetTermScreen.class);
	
	String taskServer = Config.get(Config.TASK_SERVER);

	//RDP截屏
	String url = taskServer + "/task/getTermScreenAct.ebf";
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Task task = (Task) context.getJobDetail().getJobDataMap().get("taskData");
		// 判断task对象是否合法
		if (isGetTermScreen(task)) {
			
			ServerTask serverTask = (ServerTask) task;
			
			String fileName = serverTask.getFilename();
		 
			if(fileName == null || "".equals(fileName)){
				log.error("未获取到截屏文件名", fileName);
				return;
			}
			
			if(fileName.substring(fileName.lastIndexOf(".") + 1).equalsIgnoreCase("ZIP")){
				fileName = fileName.substring(0, fileName.lastIndexOf("."));
			}
			
			String folderPath = "C:/ScreenPicture/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + fileName.substring(0, fileName.lastIndexOf("."));
			
			File folder = new File(folderPath);
			if(!folder.exists()){
				folder.mkdirs();
			}

			BufferedImage image;
			
			try {
				
				image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
				ImageIO.write(image, fileName.substring(fileName.lastIndexOf(".") + 1), new FileOutputStream(new File(folderPath + "/" + fileName)));
				
			} catch (Exception e) {
				log.error("Agaent截取终端屏幕失败", e);
				return;
			}
			
            try {
            	
				if(!FileUtil.fileToZip(folderPath, folderPath.substring(0, folderPath.lastIndexOf("/")), fileName)){
					log.error("Agaent截屏图片打ZIP压缩包失败");
					return;
				}
            	
			} catch (Exception e) {
				log.error("Agaent截屏图片打ZIP压缩包失败", e);
				return;
			}
            
            FileUtil.deleteFile(folderPath);
	  }
	}
	
	public boolean isGetTermScreen(Task task){
		if(task != null && task instanceof ServerTask){
			ServerTask serverTask = (ServerTask)task;
			return serverTask.getTasktype().equals("getTermScreenAct");
		}else{
			return false;
		}
		
	}
	
	
	public static void main(String[] args) throws JobExecutionException{
		
		new GetTermScreen().execute(null);
	}
}