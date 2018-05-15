package com.springboot.dailyclock;

import com.springboot.dailyclock.sign.dao.NeedClockUserDao;
import com.springboot.dailyclock.sign.model.NeedClockUserModel;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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



	@Test
	public void contextLoads() throws ParseException {
		DateTime dateTime = new DateTime(new Date());
		System.out.println(dateTime.minusDays(1).toString("yyyy-MM-dd"));
	}

}
