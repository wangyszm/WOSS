package com.briup.woss.server.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

import org.dom4j.Element;
import org.junit.Test;

import com.briup.common.Dom4jRoot;
import com.briup.util.BIDR;
import com.briup.util.Logger;
import com.briup.util.impl.ConfigurationImpl;
import com.briup.woss.server.DBStore;
import com.briup.woss.server.Server;

public class ServerImpl implements Server{
	private static int port;
	private static ServerSocket ss;
	private static Socket socket;
	private Logger logger;
	
	@Test
	public void testStart() throws Exception{
		Server si=new ConfigurationImpl().getServer();
		si.revicer();
	}
	
	@Override
	public void init(Properties properties) {
		Element root=Dom4jRoot.getRoot();
		
		port=Integer.parseInt(root.element("server").element("port").getTextTrim());
		
		try {
			logger=new ConfigurationImpl().getLogger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	/**
	 * 测试用方法
	 * 
	 * @throws Exception
	 */
	public void testRevicer() throws Exception{
		try{	
//			System.out.println("创建链接端...");
			logger.info("创建链接端...");
			
			//构建ServerSocket并接收
			ss=new ServerSocket(port);
//			System.out.println("创建成功");
			logger.info("创建成功");
			
			//设置超时值
			ss.setSoTimeout(100000);
			
			while(true){
//				System.out.println("等待链接...");
				logger.info("等待链接...");
				
				socket=ss.accept();
				
				System.out.println("链接建立，准备接收数据...");
				logger.info("链接建立，准备接收数据...");
//				revicer();
//				System.out.println("分配线程...");
				logger.info("分配线程...");
				
				//线程构建
				Thread t=new Thread(new ThreadHandler(socket));
				t.start();
				
//				System.out.println("线程分配完毕，准备运行...");
				logger.info("线程分配完毕，准备运行...");
				
			}
		} finally{
			shutdown();
		}
	}
	
	@Override
	public void revicer() throws Exception {
		try{	
			logger.info("创建链接端...");
			
			//构建ServerSocket并接收
			ss=new ServerSocket(port);
			logger.info("创建成功");
			
			//设置超时值
			ss.setSoTimeout(100000);
			
			while(true){
				logger.info("等待链接...");
				
				socket=ss.accept();
				
				System.out.println("链接建立，准备接收数据...");
				logger.info("链接建立，准备接收数据,开始分配线程...");
				
				//线程构建
				Thread t=new Thread(new ThreadHandler(socket));
				t.start();
				
				logger.info("线程分配完毕，准备运行...");
			}
		} finally{
			shutdown();
		}
	}

	@Override
	public void shutdown() {
		try {
			if(ss!=null)ss.close();
			logger.info("服务器已关闭");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//===================线程部分=====================
	class ThreadHandler implements Runnable{
		private InputStream is=null;
		private ObjectInputStream ois=null;
		private Socket socket=null;
		
		public ThreadHandler(){}
		public ThreadHandler(Socket socket){
			this.socket=socket;
		}
		
		@Override
		public void run(){
			logger.info("线程运行中...");
			//接收流
			try {
				logger.info("创建输入流...");
				
				is=socket.getInputStream();
				ois=new ObjectInputStream(is);
				
				logger.info("输入流创建完毕,正在接收数据...");
				
				//集合传递到DBStore类中的saveToDB()方法
				DBStore dbs=new ConfigurationImpl().getDBStore();
				dbs.saveToDB((Collection<BIDR>)ois.readObject());
				
				logger.info("接收完毕");

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				//关闭流及socket对象
				try {
					if(socket!=null)socket.close();
					if(ois!=null)ois.close();
					if(is!=null)is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
