package com.springboot.dailyclock;

import com.google.common.collect.Maps;
import com.springboot.dailyclock.sign.model.UserClockLogModel;
import com.springboot.dailyclock.system.utils.Constant;
import com.springboot.dailyclock.system.utils.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DailyClockApplicationTests {

	@Test
	public void contextLoads() {
		RedisUtil redisUtil = RedisUtil.getInstance();
		UserClockLogModel userClockLogModel = new UserClockLogModel();
		userClockLogModel.setType("1");
		userClockLogModel.setOpenId("1");
		userClockLogModel.setCreateDate(new Date());
		Map<String, Object> signMap = Maps.newHashMap();
		signMap.put("111", userClockLogModel);
		userClockLogModel.setType("2");
		userClockLogModel.setOpenId("2");
		userClockLogModel.setCreateDate(new Date());
		signMap.put("222", userClockLogModel);
		redisUtil.hmset(Constant.TODAY_SIGN_USER, signMap, 1000 * 60 * 60);
		Map<Object, Object> map = redisUtil.hmget(Constant.TODAY_SIGN_USER);
		System.out.println(map.toString());
		userClockLogModel.setType("3");
		userClockLogModel.setOpenId("3");
		userClockLogModel.setCreateDate(new Date());
		map.put("333", userClockLogModel);
//		redisUtil.hmset()
	}

}
