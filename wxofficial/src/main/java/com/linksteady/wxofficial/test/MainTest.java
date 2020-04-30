package com.linksteady.wxofficial.test;

import com.linksteady.wxofficial.dao.WxOfficialUserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MainTest {

    @Autowired
    private WxOfficialUserMapper wxOfficialUserMapper;

    @Test
    public void test() {
        List<String> openIdList = wxOfficialUserMapper.getUserListByTagId(null);
        System.out.println(openIdList);
    }
}
