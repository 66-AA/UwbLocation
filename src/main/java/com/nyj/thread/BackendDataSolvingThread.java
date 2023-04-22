package com.nyj.thread;

import com.nyj.fielddatadefinition.FieldDataDefinition;
import com.nyj.tools.BinaryTools;
import com.nyj.uwbcountdistancedata.UwbCountDistanceData;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/20 10:02
 *
 * 获取同一测距序号下的标签id和各基站到标签的距离
 */
public class BackendDataSolvingThread implements Runnable {
    public static ConcurrentHashMap<String, UwbCountDistanceData> concurrentHashMap = new ConcurrentHashMap<>(); //存放基站的编号和距离
    /**
     * 这个阻塞队列支持多线程并发操作，能够实现高效地插入和删除元素，并且在队列为空时获取元素的操作将被阻塞，直到有新的元素加入到队列中。
     * 同样地，在队列已满时添加元素的操作也会被阻塞，直到有其他线程从队列中取出元素。
     * 在该静态变量声明中，使用了整数100作为构造函数的参数，表示该队列的容量为100。
     * 因此，当队列中的元素数量达到100时，后续的入队操作将被阻塞。而由于该变量是静态变量，
     * 因此可以被该类的所有实例对象所共享和访问。
     */
    public static LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue(100); //链表阻塞队列，存储标签ID+测距序号
    public static String tagIdBatchSn;

    @Override
    public void run() {
        while (true) {
            try {
                if (!BackendReceiveBaseStationThread.uwbReceiveInfoData.isEmpty()) {
                    /*
                     * ConcurrentLinkedQueue是Java中的一个线程安全的队列，它实现了Queue接口，
                     * 并且内部使用链表实现。它支持高并发访问，而且在并发环境下不需要加锁就可以保证线程安全。
                     *ConcurrentLinkedQueue中的元素遵循先进先出（FIFO）的原则。它提供了一些基本的队列操作方法，
                     * 例如offer()用于添加元素、poll()用于获取并移除队列头部的元素、peek()用于获取但不移除队列头部的元素等等。
                     *由于ConcurrentLinkedQueue是无界队列，因此它不会阻塞生产者线程，
                     * 即使队列已经达到最大容量，也只会返回false。这一点与有界队列（例如ArrayBlockingQueue）不同。
                     *ConcurrentLinkedQueue适用于多个线程并发读写的场景，它的性能通常比使用锁机制实现的队列更好。
                     * 同时，它还可以作为一种数据缓存结构，用于异步处理和消息传递等场景。
                     * */
                    FieldDataDefinition queueHeadData = BackendReceiveBaseStationThread.uwbReceiveInfoData.poll(); //根据队列先进先出特性，从头结点拿出一组数据解析出所需要的信息
                    String tagId = BinaryTools.IntegersArrayToHexString(queueHeadData.getTagId()); //获取标签ID
                    String batchSn = Integer.toHexString(queueHeadData.getBatchSn());  //测距序号
                    //queueHeadData对象中的tagId和batchSn字段值拼接为一个字符串，并去除其中的空格
                    tagIdBatchSn = (tagId + batchSn).replaceAll(" ", "");
                    Integer anchorId = queueHeadData.getAnchorId()[3];  //基站Id
                    //System.out.println("基站id" + anchorId);
                    String distanceStr = BinaryTools.IntegersArrayToHexString(queueHeadData.getDistance()).replaceAll(" ", ""); //基站到标签的距离
                    //System.out.println("distanceStr " + distanceStr);
                    //把16进制字符串转化为float类型
                    BigInteger bigInteger = new BigInteger(distanceStr, 16);
                    //System.out.println("bigInteger" + bigInteger);
                    Float distance = Float.intBitsToFloat(bigInteger.intValue());
                    //System.out.println("distance " + distance);
                    if (!linkedBlockingQueue.contains(tagIdBatchSn)) {
                        linkedBlockingQueue.offer(tagIdBatchSn);
                    }
                    int index = anchorId - 251; //基站Id变为1-4
                    if (index < 0) {
                        System.out.println("基站号错误");
                        continue;
                    }
                    BackendConsumeConcurrentHashMapThread.lock.lock(); //加锁
                    try {
                        if (concurrentHashMap.containsKey(tagIdBatchSn)) {
                            //假设前面一组只收到1,2,4，后面又来了1，就清空
                            Float[] distance1 = concurrentHashMap.get(tagIdBatchSn).getDistince();
                            if (distance1[index] != null) {
                                Arrays.fill(concurrentHashMap.get(tagIdBatchSn).getDistince(), null);
                                concurrentHashMap.get(tagIdBatchSn).setCount(0);
                            }
                            distance1[index] = distance;
                            concurrentHashMap.get(tagIdBatchSn).setDistince(distance1);
                            Integer count = concurrentHashMap.get(tagIdBatchSn).getCount();
                            count += 1;
                            concurrentHashMap.get(tagIdBatchSn).setCount(count);
                        } else {
                            UwbCountDistanceData uwbCountDistanceData = new UwbCountDistanceData();
                            Float[] distance1 = new Float[4];
                            distance1[index] = distance;
                            uwbCountDistanceData.setDistince(distance1);
                            uwbCountDistanceData.setCount(1);
                            concurrentHashMap.put(tagIdBatchSn, uwbCountDistanceData);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        BackendConsumeConcurrentHashMapThread.lock.unlock();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
