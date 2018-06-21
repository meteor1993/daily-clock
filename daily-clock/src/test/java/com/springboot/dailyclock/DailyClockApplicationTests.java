package com.springboot.dailyclock;

import com.springboot.dailyclock.account.dao.UserAccountDao;
import com.springboot.dailyclock.sign.dao.NeedClockUserDao;
import com.springboot.dailyclock.sign.dao.UserClockLogDao;
import com.springboot.dailyclock.sign.model.NeedClockUserModel;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
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

//		DateTime dateTime = new DateTime("06:30:00");
//		DateTime dateTime1 = new DateTime("08:00:00");
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>" + Minutes.minutesBetween(dateTime1, dateTime).getMinutes());
	}

}
