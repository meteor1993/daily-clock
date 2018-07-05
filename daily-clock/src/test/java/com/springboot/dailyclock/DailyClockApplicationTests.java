package com.springboot.dailyclock;

import com.springboot.dailyclock.account.dao.UserAccountDao;
import com.springboot.dailyclock.sign.dao.NeedClockUserDao;
import com.springboot.dailyclock.sign.dao.UserClockLogDao;
import com.springboot.dailyclock.sign.model.NeedClockUserModel;
import com.springboot.dailyclock.system.mail.service.MailService;
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

	@Autowired
	MailService mailService;



	@Test
	public void contextLoads() throws ParseException {
//		mailService.sendSimpleMail("136736247@qq.com", "test", "test");
//		DateTime dateTime = new DateTime("06:30:00");
//		DateTime dateTime1 = new DateTime("08:00:00");
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>" + Minutes.minutesBetween(dateTime1, dateTime).getMinutes());
	}

}
