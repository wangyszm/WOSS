package com.briup.woss.server.impl;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Collection;
import java.util.Properties;

import org.dom4j.Element;
import org.junit.Test;

import com.briup.common.Dom4jRoot;
import com.briup.util.BIDR;
import com.briup.util.Logger;
import com.briup.util.impl.ConfigurationImpl;
import com.briup.woss.server.DBStore;

public class DBStoreImpl implements DBStore{
	private String driver;
	private String url;
	private String user;
	private String password;
//	private Properties info;
	private Logger logger;
	
//	@Test
	public void testInit(){

	}
	
	@Override
	public void init(Properties properties) {
//		info=properties;
		
		Element root=Dom4jRoot.getRoot();
		Element dbstore=root.element("dbstore");
		
		driver=dbstore.elementText("driver").trim();
		url=dbstore.elementText("url").trim();
		user=dbstore.elementText("username").trim();
		password=dbstore.elementText("password").trim();
		
		try {
			logger=new ConfigurationImpl().getLogger();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void saveToDB(Collection<BIDR> collection) throws Exception {
		
		//创建JDBC用对象
		Connection conn=null;
		PreparedStatement pstmt=null;
		
		//注册、链接数据库
//		FileInputStream fis=new FileInputStream("src/WossInfo.properties");
		
		try{
			//构建Properties对象
//			Properties info=new Properties();
//			info.load(fis);
			
			logger.info("与数据库建立链接...");
//			String driver=info.getProperty("driver");
//			String url=info.getProperty("url");
			
			//注册并与数据库建立链接
			Class.forName(driver);
//			conn=DriverManager.getConnection(url, info);
			conn=DriverManager.getConnection(url, user, password);
			
			long before=Calendar.getInstance().getTimeInMillis();
			
			//i是用来监测同一个pstmt对象执行的次数
			//如果超过某一定值，则需要对pstmt进行处理
			int i=0;
			String sql=null;
			
			//用于判断上线日期是否改变的字符串
			String flag=null;
			
			logger.info("建立链接成功，开始插入数据...");
			//遍历集合，逐对象存入数据库
			//增强for循环
			for(BIDR bidr:collection){
				conn.setAutoCommit(false);

				java.sql.Date login_date=new java.sql.Date(bidr.getLogin_date().getTime());
				java.sql.Date logout_date=new java.sql.Date(bidr.getLogout_date().getTime());
				
				//获取对象中的上线日期，用于改变sql语句中的表名
				String day=login_date.toString().split("-")[2];
				
				//如果pstmt没有初始化，先初始化
				//如果上线日期改变，则获取新的sql语句以及pstmt对象
				if(!day.equals(flag) || pstmt==null || flag==null){
					//重新赋值flag,刷新i的值
					flag=day;
					i=0;
					
					//先关闭旧的pstmt对象，再创建新的
					//在关闭pstmt对象之前先批处理其中的sql语句
					if(pstmt!=null){
						pstmt.executeBatch();
						pstmt.close();
					}
					
					//获取新的sql语句，改变其中的表名
					sql="insert into t_"+day
							+" values(?,?,?,?,?,?)";
					
					//获取新的pstmt对象
					pstmt=conn.prepareStatement(sql);
					
					conn.commit();
				}
				i++;
				
				pstmt.setString(1, bidr.getAAA_login_name());
				pstmt.setString(2, bidr.getLogin_ip());
				pstmt.setDate(3, login_date);
				pstmt.setDate(4, logout_date);
				pstmt.setString(5, bidr.getNAS_ip());
				pstmt.setInt(6, bidr.getTime_deration().intValue());
				
				pstmt.addBatch();
				
				//当同一个pstmt对象的Batch中存储过多数据时，刷新其中的sql语句
				if(i==2000){
					pstmt.executeBatch();
				}
			}
			//for循环完毕时，在pstmt中可能存在仍未执行的sql语句，需要进行处理
			if(pstmt!=null)pstmt.executeBatch();
			logger.info("数据插入完毕");
			
			long after=Calendar.getInstance().getTimeInMillis();
			logger.info("共花费： "+(after-before)+"毫秒");
			
		} finally{
//			if(fis!=null)fis.close();
			if(pstmt!=null)pstmt.close();
			if(conn!=null)conn.close();
			logger.info("关闭与数据库的链接");
		}
	}

//	@Test
	/**
	 * 测试用方法，已失效
	 * 
	 */
	public void testSaveToDB(){
		
	}
}
