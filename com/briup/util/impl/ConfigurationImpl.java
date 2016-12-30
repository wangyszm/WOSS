package com.briup.util.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.ibatis.javassist.Loader;
import org.apache.ibatis.ognl.PropertyAccessor;

import com.briup.util.BackUP;
import com.briup.util.Logger;
import com.briup.woss.client.Client;
import com.briup.woss.client.Gather;
import com.briup.woss.client.impl.ClientImpl;
import com.briup.woss.client.impl.GatherImpl;
import com.briup.woss.server.DBStore;
import com.briup.woss.server.Server;
import com.briup.woss.server.impl.DBStoreImpl;
import com.briup.woss.server.impl.ServerImpl;

public class ConfigurationImpl implements com.briup.util.Configuration{
	
	private static String PropertiesUrl="src/WossInfo.properties";
	private static Properties properties;
	
	static{
		properties=new Properties();
		FileInputStream fis=null;
		try {
			fis=new FileInputStream(PropertiesUrl);
			properties.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
				try {
					if(fis!=null)fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	/*public ConfigurationImpl(){
		properties=new Properties();
		try {
			properties.load(ConfigurationImpl.class.getResourceAsStream(PropertiesUrl));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	
	@Override
	public Logger getLogger() throws Exception {
		Logger li=new LoggerImpl();
		li.init(properties);
		return li;
	}

	@Override
	public BackUP getBackup() throws Exception {
		BackUP bi=new BackUPImpl();
		bi.init(properties);
		return bi;
	}

	@Override
	public Gather getGather() throws Exception {
		Gather gi=new GatherImpl();
		gi.init(properties);
		return gi;
	}

	@Override
	public Client getClient() throws Exception {
		Client ci=new ClientImpl();
		ci.init(properties);
		return ci;
	}

	@Override
	public Server getServer() throws Exception {
		Server si=new ServerImpl();
		si.init(properties);
		return si;
	}

	@Override
	public DBStore getDBStore() throws Exception {
		DBStore db=new DBStoreImpl();
		db.init(properties);
		return db;
	}

}
