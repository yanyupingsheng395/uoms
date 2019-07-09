package com.linksteady.jobmanager.task;

import com.linksteady.jobmanager.aspect.CronTag;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author hxcao
 * @date 2019-07-09
 */
@Slf4j
@CronTag("testTask")
public class TestTask {

    public void test() throws InterruptedException{
        Thread.sleep(5000);
        log.info("正在调度资源...,当前线程:{}" + Thread.currentThread().getName());
    }

    public void test2() {
        log.info("正在处理任务2..., 当前线程：{}, 当前时间：{}" ,Thread.currentThread().getName(), LocalTime.now());
        try {
            Thread.sleep(1000 * 60);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void test3() {
        log.info("正在处理任务3..., 当前线程：{}, 当前时间：{}" ,Thread.currentThread().getName(), LocalTime.now());

        try {
            Thread.sleep(1000 * 60);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void test4() {
        log.info("正在处理任务4..., 当前线程：{}, 当前时间：{}" ,Thread.currentThread().getName(), LocalTime.now());
        try {
            Thread.sleep(1000 * 60);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
