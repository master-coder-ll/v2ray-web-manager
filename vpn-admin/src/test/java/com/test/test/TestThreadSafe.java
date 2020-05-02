package com.test.test;

import com.jhl.admin.util.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class TestThreadSafe {
    static Integer count = 0;
    @Test
    public  void Test() throws InterruptedException {
        Integer len =10;
        CountDownLatch countDownLatch = new CountDownLatch(len);
        for (int i = 0; i < len; i++) {
            new Thread(() -> {
                Object intern = Utils.getInternersPoll().intern(new Integer(4335));
                synchronized (intern) {
                    count += 1;
                }
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        System.out.println(count);
        Assert.assertEquals(count,len);
    }

    @Test
    public  void Test2() {
        //cache  -127~128
        Object intern1 = Utils.getInternersPoll().intern(1);
        Object intern2 = Utils.getInternersPoll().intern(new AtomicInteger(1).get());
        Assert.assertEquals(intern1,intern2);

        Object intern3 = Utils.getInternersPoll().intern(new StringBuffer("22").append(22).toString());
        Object intern4 = Utils.getInternersPoll().intern("2222");
        Assert.assertEquals(intern3,intern4);

        Object intern5 = Utils.getInternersPoll().intern(1111);
        Object intern6 = Utils.getInternersPoll().intern(new String("1111"));
        Assert.assertNotEquals(intern5,intern6);
    }
}
