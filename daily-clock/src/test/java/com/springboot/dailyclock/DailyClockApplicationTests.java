package com.springboot.dailyclock;

import com.springboot.dailyclock.sign.dao.NeedClockUserDao;
import com.springboot.dailyclock.sign.model.NeedClockUserModel;
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		List<NeedClockUserModel> needClockUserModelList = needClockUserDao.findAllByCreateDateBetween(simpleDateFormat.parse(sdf.format(new Date()) + " 00:00:00"), simpleDateFormat.parse(sdf.format(new Date()) + " 23:59:59"));
		System.out.println(">>>>>>>>>>>>>>>" + needClockUserModelList);

		List<NeedClockUserModel> needClockUserModelList1 = needClockUserDao.findAll();
		System.out.println("<<<<<<<<<<<<<<" + needClockUserModelList1);
	}

}
