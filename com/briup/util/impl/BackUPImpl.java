package com.briup.util.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

import com.briup.util.BackUP;

public class BackUPImpl implements BackUP{
	private File file;
	
	@Override
	public void init(Properties properties) {
		
	}

	@Override
	public void store(String key, Object data, boolean flag) throws Exception {
		file=new File(key);
		//如果文件不存在，创建一个
		if(!file.exists()){
			file.createNewFile();
		}
		
		//构建各种流，flag控制是否追加或者覆盖
		FileOutputStream fos=new FileOutputStream(file,flag);
		ObjectOutputStream oos=new ObjectOutputStream(fos);
		
		//向文件中写入数据
		oos.writeObject(data);
		oos.flush();
		
		oos.close();
		fos.close();
	}

	@Override
	public Object load(String key, boolean flag) throws Exception {
		File file=new File(key);
		
		if(file.exists()){
			FileInputStream fis=new FileInputStream(file);
			ObjectInputStream ois=new ObjectInputStream(fis);
			
			Object o=ois.readObject();
			
			ois.close();
			fis.close();  
			
			if(flag==true){
				file.delete();
			}
			return o;
		}
		else{
			System.out.println("没有备份！");
			return null;
		}
	}
	
}
