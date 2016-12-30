package com.briup.woss.server.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestJDBC {
	private Connection conn;
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	private String sql="insert into t_25 values "
			+"('aaa','127.0.0.1',"
			+ "to_date('16-8月-15'),"
			+ "to_date('16-8月-15'),"
			+ "'#3ASF4425FSAW',"
			+ "100)";
	
	@Before
	public void init(){
		Properties info=new Properties();
		try {
			info.load(new FileInputStream("src/WossInfo.properties"));
			String driver=info.getProperty("driver");
			String url=info.getProperty("url");
			
//			Class.forName("oracle.jdbc.driver.OracleDriver");
//			conn=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE","briup","briup");
			Class.forName(driver);
			conn=DriverManager.getConnection(url, info);
			stmt=conn.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void testStatement(){
		try {
			//设置手动提交
			conn.setAutoCommit(false);
			
			//获得当前的时间戳
			long l1=System.currentTimeMillis();
			
			for(int i=1;i<=10000;i++){
				
				
				stmt.execute(sql);
			}
			
			long l2=System.currentTimeMillis();
			System.out.println("花费时间： "+(l2-l1)+"毫秒");
			
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void testStatement_bunch(){
		try {
			conn.setAutoCommit(false);
			
			//获得当前的时间戳
			long l1=System.currentTimeMillis();
			
			for(int i=1;i<=10000;i++){
				
				//添加一个批次
				stmt.addBatch(sql);
				if(i%2000==0){
					stmt.executeBatch();
				}
			}
			
			long l2=System.currentTimeMillis();
			System.out.println("花费时间： "+(l2-l1)+"毫秒");
			
			stmt.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPstmt_bunch(){
		String ssql="insert into t_25 values(?,?,?,?,?,?)";
		
		try {
			conn.setAutoCommit(false);
			pstmt=conn.prepareStatement(ssql);
			
			
			//获得当前的时间戳
			long l1=System.currentTimeMillis();
			
			for(int i=1;i<=10000;i++){
				
				pstmt.setString(1,"AAA");
				pstmt.setString(2,"ip");
				pstmt.setDate(3,new java.sql.Date(1100l));
				pstmt.setDate(4,new java.sql.Date(110000l));
				pstmt.setString(5,"NAS_IP");
				pstmt.setInt(6,i);
				
				pstmt.addBatch();
				
				if(i%2000==0){
					pstmt.executeBatch();
				}
//				pstmt.execute();
			}
			pstmt.executeBatch();
			
			long l2=System.currentTimeMillis();
			System.out.println("花费时间： "+(l2-l1)+"毫秒");
			
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void close(){
			try {
				if(rs!=null)rs.close();
				if(pstmt!=null)pstmt.close();
				if(stmt!=null)stmt.close();
				if(conn!=null)conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
}
