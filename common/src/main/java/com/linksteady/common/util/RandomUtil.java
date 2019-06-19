package com.linksteady.common.util;

import java.util.Random;

public class RandomUtil {

    private static Random random=new Random();

    public static int getIntRandom(int min,int max) {
        return random.nextInt(max) % (max - min + 1) + min;
    }
}
