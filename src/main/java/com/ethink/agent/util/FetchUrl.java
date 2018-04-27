package com.ethink.agent.util;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/* @类描述：通过url下载文件并将文件存放指定目录
 * @date: 2017年10月17日
 * @author: dingfan
 * @param httpUrl 请求地址
 * @param fileSavePath 文件保存地址
 * @param fileName  文件名
 * 
 */

public class FetchUrl {	
	private static String fileSavePath="d:/local_download";
	final static String downloadResult[] =new String[3];
	private final static Logger log = LoggerFactory.getLogger(HttpUtil.class);
    public static File fecthFile(String httpUrl,String fileName)throws MalformedURLException, IOException {
    	//文件下载情况  	
    	downloadResult[0] = "";
    	downloadResult[1]="0";
    	downloadResult[2]="0";
    	final Timer timer = new Timer();  
        timer.scheduleAtFixedRate(new TimerTask() {  
            public void run() {  
            	if (!"Ok".equals(downloadResult[0])) {
            		  log.info("下载超时……………………………………"+"百分比为"+downloadPercent()+"%"+"………………………………………………");        								
    				downloadResult[2]="downloadPercent()"+"%";    				
    			}
    			else {	   			
    				log.info("下载成功…………………………"+"百分比为"+downloadPercent()+"%"+"…………………………………………………………");
    				timer.cancel();
    			}
            }  
        },300000, 300000);
        log.info("开始下载文件啦++++++++++++++++++++++++++++++");       
        // 打开输入流
        BufferedInputStream in = new BufferedInputStream(
            getInputStream(httpUrl));
      
        File file1 = new File(fileSavePath+"\\"+"无实际意思");
        if (!file1.getParentFile().mkdirs()) {           	
        }               
        File file = new File(fileSavePath +"\\"+ fileName);
        //文件大小
        downloadResult[1]= file.length()+"";        
        if (!file.exists()) {        	
            file.createNewFile();         
        }  
        // 打开输出流
        FileOutputStream out = new FileOutputStream(file);
        byte[] buff = new byte[1];
        // 读取数据
        while (in.read(buff) > 0) {
            out.write(buff);
        }
        out.flush();
        out.close();
        in.close();
        //文件是否下载成功
        downloadResult[0] = "Ok";
       log.info("文件下载成功………………………………………………………………………………");
        return file;
    }

  
   //获取url中的文件流
    private static InputStream getInputStream(String httpUrl) throws
        IOException {
        // 网页Url
        URL url = new URL(httpUrl);
        URLConnection ur = url.openConnection();
        ur.setRequestProperty("Content-Type", "text/plain; charset=utf-8");     
        return ur.getInputStream();
    }
    
    
   public static void main(String[] args) {
	
		try {
			fecthFile("https://nj02all01.baidupcs.com/file/42ae4d7569d6a3a31692c9e21b24aa1a?bkt=p3-0000d39108409a8056b01746f07be5985d3d&fid=1385760722-250528-603532029082290&time=1508395441&sign=FDTAXGERLQBHSK-DCb740ccc5511e5e8fedcff06b081203-oRY4AK5Ci8Fv3XUsE8G%2FT%2FQxd9w%3D&to=69&size=8819589&sta_dx=8819589&sta_cs=1&sta_ft=rar&sta_ct=1&sta_mt=1&fm2=MH,Guangzhou,Netizen-anywhere,,shanxi,ct&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=0000d39108409a8056b01746f07be5985d3d&sl=79364174&expires=8h&rt=pr&r=699468741&mlogid=6763349680662546086&vuk=1385760722&vbdid=2283364380&fin=%E4%BA%BA%E8%84%B8%E8%AF%86%E5%88%ABdemo.rar&fn=%E4%BA%BA%E8%84%B8%E8%AF%86%E5%88%ABdemo.rar&rtype=1&iv=0&dp-logid=6763349680662546086&dp-callid=0.1.1&hps=1&tsl=100&csl=100&csign=JVxy3Ui2TliJUpi1JKrlNkPtfKM%3D&so=0&ut=6&uter=4&serv=0&uc=1167526842&ic=1071270247&ti=f8fdaa4589ff2f185cc208d2089c7e29f40a2b427302ee09&by=themis" ,"abcd");
		} catch (MalformedURLException e) {
			System.out.println("下载失败");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("下载失败");
			e.printStackTrace();
		}
		System.out.println("下载成功");
}

   /* @方法描述：递归方式 计算文件的大小
    * @date: 2017年10月17日
    * @author: dingfan
    * @param file 文件
    * 
    */
   public static long getFile( File file) {
       if (file.isFile())
           return file.length();
       final File[] children = file.listFiles();
       long total = 0;
       if (children != null)
           for (final File child : children)
               total += getFile(child);
       return total;
   }   
   
   /* @方法描述：得到下载百分比
    * @date: 2017年10月17日
    * @author: dingfan
    * 
    */
   public static String downloadPercent() {	
	   log.info("获得下载百分比………………………………………………………………………………");
	int downloadSize= new Long(getFile(new File(fileSavePath))).intValue(); 	        				
	int fileTotalSize= new Long(downloadResult[1]).intValue();	        					        				        					        	  
	String downloadPercent = new DecimalFormat("0.00").format((float)downloadSize/fileTotalSize*100);  
	return downloadPercent;	
   }
   
   /* @方法描述：复制文件
    * @date: 2017年10月17日
    * @author: dingfan
    * @param file 文件
    * @param file 文件
    */
   public static void copyFolder(File filePath, File destFile) throws IOException {  
	   log.info("文件复制………………………………………………………………………………");
	   //判断文件是否是文件夹如果不是直接下载,如果是则进行文件夹文件递归复制 
	    if(filePath.isDirectory()){  
	        File newFolder=new File(destFile,filePath.getName());  	       
	        newFolder.mkdirs();  
	        File[] fileArray=filePath.listFiles();  	          
	        for(File file:fileArray){  
	            copyFolder(file, newFolder);  
	        }  	          
	    }else{  
	        File newFile=new File(destFile,filePath.getName());  
	        copyFile(filePath,newFile);  
	    } 
	    log.info("文件复制完成………………………………………………………………………………");
	}  

   
   /* @方法描述：复制文件
    * @date: 2017年10月17日
    * @author: dingfan
    * @param srcFile 文件原始地址
    * @param newFile 文件复制到的位置
    */
	private static void copyFile(File srcFile, File newFile) throws IOException{  
	    BufferedInputStream bis=new BufferedInputStream(new FileInputStream(srcFile));  
	    BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(newFile));  	      
	    byte[] bys=new byte[1024];  
	    int len=0;  
	    while((len=bis.read(bys))!=-1){  
	        bos.write(bys,0,len);  
	    }  
	    bos.close();  
	    bis.close();  	      
	}  

}
