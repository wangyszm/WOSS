package com.briup.woss.client.impl;

import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

import org.dom4j.Element;
import org.junit.Test;

import com.briup.common.Dom4jRoot;
import com.briup.util.BIDR;
import com.briup.util.Logger;
import com.briup.util.impl.ConfigurationImpl;
import com.briup.util.impl.LoggerImpl;
import com.briup.woss.client.Client;
import com.briup.woss.client.Gather;

public class ClientImpl implements Client{
	private int port;
	private String host;
	private Logger logger;
	
	@Test
	public void testStart() throws Exception{
		Client ci=new ConfigurationImpl().getClient();
		ci.send(new ConfigurationImpl().getGather().gather());
	}
	
//	@Test
	public void testInit(){
		Element root=Dom4jRoot.getRoot();
		
		host=root.element("client").element("ip").getTextTrim();
		port=Integer.parseInt(root.element("client").element("port").getTextTrim());
		
		try {
			logger=new ConfigurationImpl().getLogger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init(Properties properties) {
		Element root=Dom4jRoot.getRoot();
		
		host=root.element("client").element("ip").getTextTrim();
		port=Integer.parseInt(root.element("client").element("port").getTextTrim());
		
		try {
			logger=new ConfigurationImpl().getLogger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void send(Collection<BIDR> collection) throws Exception {
		//构建Socket
		Socket socket=new Socket(host, port);
		OutputStream os=socket.getOutputStream();
		BufferedOutputStream bos=new BufferedOutputStream(os);
		ObjectOutputStream oos=new ObjectOutputStream(bos);
		
		logger.info("正在传输对象");

		/*Gather gi=new ConfigurationImpl().getGather();
		//传输！
		oos.writeObject(gi.gather());*/
		
		oos.writeObject(collection);
		oos.flush();
		
		logger.info("传输完毕");
		
		//关闭流
		if(oos!=null)oos.close();
		if(bos!=null)bos.close();
		if(os!=null)os.close();
		if(socket!=null)socket.close();
	}
	
//	@Test
	/**
	 * 测试用方法，测试send()方法是是否正常
	 * 
	 * @throws Exception
	 */
	public void testSend() throws Exception{
		//构建Socket
		Socket socket=new Socket(host, port);
		OutputStream os=socket.getOutputStream();
		BufferedOutputStream bos=new BufferedOutputStream(os);
		ObjectOutputStream oos=new ObjectOutputStream(bos);
		
		logger.info("正在传输对象");

		Gather gi=new ConfigurationImpl().getGather();
		//传输！
		oos.writeObject(gi.gather());
		
		/*oos.writeObject(collection);
		oos.flush();*/
		
		logger.info("传输完毕");
		
		//关闭流
		if(oos!=null)oos.close();
		if(bos!=null)bos.close();
		if(os!=null)os.close();
		if(socket!=null)socket.close();
	}
}
