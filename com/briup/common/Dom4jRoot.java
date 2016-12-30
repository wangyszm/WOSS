package com.briup.common;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 获取woss.xml的root标签
 * 
 * @author Administrator
 *
 */
public class Dom4jRoot {
	
	public static Element getRoot(){
		SAXReader read=new SAXReader();
		try{
			Document doc=read.read("src/woss.xml");
			Element root=doc.getRootElement();
			return root;
		} catch(DocumentException e){
			e.printStackTrace();
			return null;
		}
	}
}
