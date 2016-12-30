package test;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class Test_log4j {
	
	public static void main(String[] args){
		PropertyConfigurator.configure("src/test/log4j.properties");
		
		/*Logger l1=Logger.getRootLogger();
		l1.fatal("---fatal---");
		l1.error("---error---");
		l1.warn("---warn---");
		l1.info("---info---");
		l1.info("---debug---");*/
		
		Logger l2=Logger.getLogger("Gater");
		l2.fatal("---fatal---");
		l2.error("---error---");
		l2.warn("---warn---");
		l2.info("---info---");
		l2.info("---debug---");
	}
}
