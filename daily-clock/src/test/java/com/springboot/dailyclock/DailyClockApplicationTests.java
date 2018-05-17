package com.springboot.dailyclock;

import com.springboot.dailyclock.account.dao.UserAccountDao;
import com.springboot.dailyclock.sign.dao.NeedClockUserDao;
import com.springboot.dailyclock.sign.dao.UserClockLogDao;
import com.springboot.dailyclock.sign.model.NeedClockUserModel;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DailyClockApplicationTests {

	@Autowired
	NeedClockUserDao needClockUserDao;

	@Autowired
	UserAccountDao userAccountDao;

	@Autowired
	UserClockLogDao userClockLogDao;



	@Test
	public void contextLoads() throws ParseException {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>" + userClockLogDao.findEarlyClockUser("0", new Date()));
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>" + userAccountDao.getClockUserCount0(new Date()));
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>" + userAccountDao.getUnClockUserCount0(new Date()));
	}

}
