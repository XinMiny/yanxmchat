package com.yanxm.chat.utils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;


public class RedisTest {
	private static int size = 10000;
	private static BloomFilter<Integer> bloom = BloomFilter.create(Funnels.integerFunnel(), 1000 , 0.01);
	
	public static void main(String[] args) {
		 
		
	}
	
	
	/**
	 * 测试redis分布式锁
	 */
	public static void redisTest() {
		for(int i = 0 ; i < 20 ; i ++) {
			final int j = i;
			new Thread(()-> {
				if(j<10) {
					Jedis jedis = RedisUtil.getJedis();
					jedis.setnx("key"+j,Thread.currentThread().getName());
					jedis.del("key"+j);   //解锁
				}
			}).start();
		}
	}
	
	
	/**
	 * 布隆过滤器
	 */
	public void bloomFilter() {
		for(int i = 0 ; i < size ; i ++) {
			bloom.put(i);
		}
		List<Integer> list = new ArrayList<>(1000);
		for(int i = size + 1000 ; i < size + 2000 ; i++) {
			if(bloom.mightContain(i)) {
				list.add(i);
			}
		}
		System.err.println("误判的数量为 :   " + list.size());
	}
	
}
