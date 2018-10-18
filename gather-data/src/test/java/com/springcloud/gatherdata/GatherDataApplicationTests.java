package com.springcloud.gatherdata;

import com.springcloud.gatherdata.account.dao.UserAccountDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GatherDataApplicationTests {

    @Autowired
    UserAccountDao userAccountDao;

    @Test
    public void contextLoads() {

    }

}
