package com.springcloud.gatherdata.admin.service;


import com.springcloud.gatherdata.system.model.CommonJson;

import java.text.ParseException;

public interface AdminService {
    CommonJson gatherData() throws ParseException;
}
