package com.founder.xy.system.init;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.founder.e5.sys.SysConfig;

import redis.clients.jedis.HostAndPort;

public class ParamConfigInjectSupport implements ApplicationListener<ContextRefreshedEvent>{
	private final Logger log = Logger.getLogger("xy.paramConfig");
	
	@SuppressWarnings("unchecked")
	public void init(){
		Configuration cfg = new Configuration().configure();
		Session session = cfg.buildSessionFactory().openSession();
		List<SysConfig> list = session
				.createSQLQuery("SELECT * FROM fsys_sysconfig WHERE appID=0 and project=?")
				.addEntity(SysConfig.class)
				.setString(0, "配置").list();
		if(log.isDebugEnabled()) printDB(list);
		for(SysConfig config: list){
			if(config.getValue() != null 
					&& !"-1".equals(config.getValue().trim())
					&& !"".equals(config.getValue().trim())) {
				System.setProperty(config.getItem(), config.getValue().trim());
			}
		}
		session.close();
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		if(context.getParent() == null){
			String brokerURL = ((ActiveMQConnectionFactory) context.getBean(PooledConnectionFactory.class)
					.getConnectionFactory()).getBrokerURL();
			if(log.isDebugEnabled()) printEnv();
			printUrl(brokerURL, context.getBeansOfType(HostAndPort.class));
		}
	}
	
	private void printUrl(String brokerURL, Map<?,?> map){
		log.info("------------------Redis与ActiveMQ连接信息--------------");
		log.info("ActiveMQ地址 : ");
		log.info("brokerURL = " + brokerURL);
		log.info("Redis节点信息 : ");
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			log.info("key = "+entry.getKey()+", value = "+entry.getValue());
		}
		log.info("-----------------------------------------------------\r\n\r\n");
	}
	
	private void printEnv(){
		log.debug("---------------------系统变量信息----------------------");
		Properties properties = System.getProperties();
		for(Map.Entry<Object, Object> entry:properties.entrySet()){
			if(entry.getKey().toString().contains("_ADDR")){
				log.debug("key = "+entry.getKey()+", value = "+entry.getValue());
			}
		}
		Map<String, String> map = System.getenv();
		log.debug("---------------------环境变量信息-----------------------");
		for(Entry<String, String> entry:map.entrySet()){
			if(entry.getKey().toString().contains("_ADDR")){
				log.debug("key = "+entry.getKey()+", value = "+entry.getValue());
			}
		}
		log.debug("-----------------------------------------------------");
	}
	
	private void printDB(List<SysConfig> list){
		log.debug("------------------数据库参数变量信息----------------------");
		for(SysConfig config:list){
			log.debug("item = " + config.getItem() + ", value = " + config.getValue());
		}
		log.debug("------------------------------------------------------");
	}
}
