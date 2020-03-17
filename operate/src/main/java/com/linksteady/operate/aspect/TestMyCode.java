package com.linksteady.operate.aspect;

import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.*;

/**
 * @author hxcao
 * @date 2020/3/17
 */
public class TestMyCode {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool
                (Runtime.getRuntime().availableProcessors(),
                        ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                        null, true);
        ForkJoinTask<Integer> submit = forkJoinPool.submit(new Fibonacci(10));
        System.out.println(submit.get());
    }
}


class Fibonacci extends RecursiveTask<Integer> {

    final int n;

    public Fibonacci(int n) {
        this.n = n;
    }

    @Override
    protected Integer compute() {
        if (n <= 1) {
            return n;
        }
        Fibonacci f1 = new Fibonacci(n -1);
        // 任务拆分
        f1.fork();
        Fibonacci f2 = new Fibonacci(n -2);
        f2.fork();
        return f1.compute() + f2.compute();
    }
}