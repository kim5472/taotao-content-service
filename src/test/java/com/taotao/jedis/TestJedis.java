package com.taotao.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class TestJedis {
	@Test
	public void testJedis() throws Exception{
		// 创建一个jedis对象。需要制定服务的ip和端口号
		Jedis jedis = new Jedis("192.168.25.153",6379);
		// 直接操作数据库
		jedis.set("jedis-key", "1234");
		String result = jedis.get("jedis-key");
		// 关闭jedis
		jedis.close();
	}
	
	@SuppressWarnings("resource")
	@Test
	public void testJedisPool()throws Exception{
		// 创建一个数据库连接池对象（单例），需要只当服务的ip和端口号
		JedisPool jedisPool = new JedisPool("192.168.25.153",6379);
		Jedis jedis = jedisPool.getResource();
		String result = jedis.get("jedis-key");
		System.out.println(result);
		
		jedis.close();
		
		jedisPool.close();
	}
	
	@SuppressWarnings("resource")
	@Test
	public void testJedisCluster()throws Exception{
		//创建一个jedisCluster对象，构造参数set类型，集合中每个元素是HostAndPort类型
		Set<HostAndPort> nodes = new HashSet<>();
		// 向集合中添加节点
		nodes.add(new HostAndPort("192.168.25.153", 7001));
		nodes.add(new HostAndPort("192.168.25.153", 7002));
		nodes.add(new HostAndPort("192.168.25.153", 7003));
		nodes.add(new HostAndPort("192.168.25.153", 7004));
		nodes.add(new HostAndPort("192.168.25.153", 7005));
		nodes.add(new HostAndPort("192.168.25.153", 7006));
		JedisCluster jedisCluster = new JedisCluster(nodes);
		//直接使用jedisCluster操作jedis，自带连接池。jedisCluster对象是单粒的。
		jedisCluster.set("cluster-test", "hello jedis cluster");
		String result = jedisCluster.get("cluster-test");
		System.out.println(result);
		//系统关闭钱关闭JedisCluster
		jedisCluster.close();
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
