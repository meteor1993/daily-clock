package com.springboot.dailyclock.admin.service;

import com.springboot.dailyclock.system.model.CommonJson;

import java.text.ParseException;

public interface AdminService {
    CommonJson gatherData() throws ParseException;
}
