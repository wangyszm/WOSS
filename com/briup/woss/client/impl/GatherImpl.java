package com.briup.woss.client.impl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import com.briup.common.Dom4jRoot;
import com.briup.util.BIDR;
import com.briup.util.impl.BackUPImpl;
import com.briup.woss.client.Gather;

public class GatherImpl implements Gather{

//	private String filePath="Abstract Files/radwtmp";
	private String storeMap="store/map";
	private String storeList="store/list";
	private String storeSkip="store/skip";
	private String filePath;
	private String xmlUrl="src/woss.xml";
	
	/**
	 *测试用方法,，测试gather()方法是否正常
	 *  
	 * @throws Exception
	 */
	@Test
	public void testInit() throws Exception {
		
	}
	
	@Override
	public void init(Properties properties) {
		Element root=Dom4jRoot.getRoot();
		
		Element src=root.element("gather").element("src-file");
		filePath=src.getTextTrim();
	}

	
	@Override
	public Collection<BIDR> gather() throws Exception {
		//用来存储封装好的BIDR对象
		Collection<BIDR> bidrs=new ArrayList<BIDR>();
		
		//构建用来读取文件的输入流
//		File skipFlag=new File("Abstract Files/skipFile");
		
		FileInputStream fis=new FileInputStream(filePath);
		InputStreamReader isr=new InputStreamReader(fis);
		BufferedReader br=new BufferedReader(isr);
		
		try{	
			//获取本次文档中的总字节数thisSize,以及需要跳过的字节数skipSize
//			long thisSize=fis.available();
//			long skipSize=getSkipSize(skipFlag);
			
			//跳过部分字节
//			fis.skip(skipSize);
			
			//处理信息的方法，核心逻辑从此开始
			readFile(bidrs,br,fis);
			
			//设置下一次需要跳过的字节数
//			setSkipSize(skipFlag, thisSize);
			
		} finally{
			if(br!=null)br.close();
			if(isr!=null)isr.close();
			if(fis!=null)fis.close();
		}
		
		System.out.println(bidrs.size());
		return bidrs;
	}
	
	//=================以下是本类中的私有方法=================
	/**
	 * 核心代码所在方法
	 * 该方法用来读取文件并处理其中信息，最后将封装好的BIDR对象储存在传入的集合中
	 * 
	 * @param bidrs 传入的集合
	 * @param br bufferedReader流，用来文档读取信息
	 * @throws Exception
	 */
	private void readFile(Collection<BIDR> bidrs,BufferedReader br,FileInputStream fis) throws Exception{
		Map<String, BIDR> bidrMap=null;
		
		//读取备份文件
		BackUPImpl bp=new BackUPImpl();
		
		long skip=0;
		long startSize=fis.available();
		
		File mapFile=new File(storeMap);
		File listFile=new File(storeList);
		File skipFile=new File(storeSkip);
		
		if(mapFile.exists() && listFile.exists() && skipFile.exists()){
			
			bidrMap=(HashMap<String,BIDR>)(bp.load(storeMap, bp.LOAD_UNREMOVE));
			bidrs=(ArrayList<BIDR>)(bp.load(storeList, bp.LOAD_UNREMOVE));

			//如果需要跳过已读过的信息，有两种方法
			//1、计算已读过的行数，当再次读取时，小于该行数的读取操作不处理
			//2、统计读过的字节数，当再次读取时，跳过
			
			//跳过radwtmp文件中已备份在Map文件中的字节数
//			br.skip(100000);
		}
		else{
			bidrMap=new HashMap<String,BIDR>();
		}
		
		//一行一行读
		//endStr也可判断是否读到文件尾
		String endStr=null;
		
		try {
			while((endStr=br.readLine())!=null){
				
				//如果读到一行空信息，则不处理此行
				if("".equals(endStr)){
					continue;
				}
				//读到的一行信息存入lineStr数组中
				String[] lineStr=endStr.trim().split("[|]");
				
				if("7".equals(lineStr[2])){
					
					//调用setValues方法先封装一部分信息，该方法是私有方法
					BIDR bidr=set7values(new BIDR(), lineStr);
					//将封装过的BIDR对象存入map集合，key的值是AAA_ip
					bidrMap.put(lineStr[4], bidr);
				}
				if("8".equals(lineStr[2])){
					
					//从map中根据用户ip匹配到bidr对象
					BIDR bidr=bidrMap.get(lineStr[4]);
					//将bidr传入到set8values()方法中，再将方法返回的对象存入集合bidrs中
					bidrs.add(set8values(bidr, lineStr));
				}
			}
		} finally{
			//对两个集合进行备份
			bp.store(storeMap, bidrMap, bp.STORE_OVERRIDE);
			bp.store(storeList, bidrs, bp.STORE_OVERRIDE);
		} 
	}
	
	/**
	 * 获取本次处理文档前需要跳过的字节数
	 * 
	 * @param skipFlag 传入File类型的存储有需要跳过字节数的文件对象
	 * @return	返回需要跳过的字节数，类型为long
	 * @throws Exception
	 */
	private long getSkipSize(File skipFlag) throws Exception{
		//构建输入流，DataInputStream流可以直接输入long类型数据
		FileInputStream fis=null;
		DataInputStream dis=null;
		//获取需要跳过的字节数skipSize
		long skipSize=0;
		
		try {
			
			fis=new FileInputStream(skipFlag);
			dis=new DataInputStream(fis);
			
			//如果文档中没有值则skipSize保持为0
			if(skipFlag.length()!=0){
				skipSize=dis.readLong();
			}
		} finally{
			if(fis!=null)fis.close();
			if(dis!=null)dis.close();
		}
		return skipSize;
	}
	
	/**
	 * 设置下一次处理文档前需要跳过的字节数
	 * 
	 * @param skipFlag 传入File类型的存储有需要跳过字节数的文件对象
	 * @param thisSize 传入本次文件总共的字节数，用来写入文件
	 * @throws Exception
	 */
	private void setSkipSize(File skipFlag,long thisSize) throws Exception{
		//构建输处流，DataInputStream流可以直接输出long类型数据
		FileOutputStream fos=null;
		DataOutputStream dos=null;
		
		try {
			fos=new FileOutputStream(skipFlag);
			dos=new DataOutputStream(fos);
			
			dos.writeLong(thisSize);
			dos.flush();
			
		} finally{
			if(fos!=null)fos.close();
			if(dos!=null)dos.close();
		}
	}
	
	
	/**
	 * 当读取到编号为7的一行时，调用该方法将其中的信息存入BIDR对象中
	 * 其中登录时间的存储类型为Timestamp对象
	 * 需要存入的信息为上线时间、用户名、NAS服务器名以及用户ip
	 * 
	 * @param bidr 传入需要封装的BIDR对象
	 * @param lineStr 传入某一行信息，此行是用户的登录信息
	 * @return 返回被封装过的BIDR对象
	 */
	private BIDR set7values(BIDR bidr,String[] lineStr){
		//用户名
		bidr.setAAA_login_name(lineStr[0]);
		//用户ip
		bidr.setLogin_ip(lineStr[4]);
		//NAS服务器名
		bidr.setNAS_ip(lineStr[1]);
		//上线时间
		long login_date=new Long(lineStr[3]).longValue();
		bidr.setLogin_date(new Timestamp(login_date*1000));
		
		return bidr;
	}
	
	/**
	 * 当读取到编号为8的一行时，调用该方法将剩余的信息存入BIDR对象中
	 * 其中涉及到了上线时长的计算
	 * 此方法结束时BIDR对象应该封装完毕
	 * 
	 * @param bidr 传入已封装一部分数据的BIDR对象，该对象已经存储了上线的信息
	 * @param lineStr 传入某一行信息，此信息是用户的下线信息
	 * @return 返回封装完毕的BIDR对象
	 */
	private BIDR set8values(BIDR bidr,String[] lineStr){
		//下线时间
		long logout_date=new Long(lineStr[3]).longValue();
		bidr.setLogout_date(new Timestamp(logout_date*1000));
		
		//在线时长
		long login=bidr.getLogin_date().getTime();
		long logout=bidr.getLogout_date().getTime();
		int durationTime=(int)((logout-login)/1000);
		if(durationTime==0){
			durationTime=1;
		}
		bidr.setTime_deration(new Integer(durationTime));
		
		return bidr;
	}

}
