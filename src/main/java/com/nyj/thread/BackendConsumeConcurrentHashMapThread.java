package com.nyj.thread;

import com.nyj.gettinglabelcoordinates.GettingLabelCoordinates;
import com.nyj.kalmanFilter.KalmanFilter1D;
import com.nyj.kalmanFilter.KalmanFilter2D;
import com.nyj.tagcoordinatepoint.TagCoordinatePoint;
import com.nyj.uwbcountdistancedata.UwbCountDistanceData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/20 12:49
 */
public class BackendConsumeConcurrentHashMapThread implements Runnable {
    public static ConcurrentHashMap<String, TagCoordinatePoint> pointConcurrentHashMap = new ConcurrentHashMap<String, TagCoordinatePoint>(); //存储坐标点
    public static Lock lock = new ReentrantLock(); //加锁。共享数据安全性，线程间有序性
    double[] p = new double[]{1, 0, 0, 1};
    double[] k = new double[]{0, 0};
    ArrayList<double[]> list = new ArrayList<>();
    HashMap<String, ArrayList<double[]>> hs = new HashMap<>();

    @Override
    public void run() {
        KalmanFilter1D kf = new KalmanFilter1D();

        while (true) {
            try {
                if (BackendDataSolvingThread.concurrentHashMap.size() != 0) {
                    for (Map.Entry<String, UwbCountDistanceData> entry : BackendDataSolvingThread.concurrentHashMap.entrySet()) {
                        lock.lock();
                        try {
                            String s = entry.getKey();
                            UwbCountDistanceData v = entry.getValue();
                            if (v.getCount() == 4) {
                                TagCoordinatePoint point = GettingLabelCoordinates.coordinateProcessing(v.getDistince());
                                String tagId = s.substring(0, 8);
                                v.setCount(0);
                                if (!hs.containsKey(tagId)) {
                                    list.add(new double[]{point.getX(), point.getY()});
                                    list.add(k);
                                    list.add(p);
                                    hs.put(tagId, list);
                                }
                                k = hs.get(tagId).get(1);
                                p = hs.get(tagId).get(2);
                                list = kf.filter(point.getX(), point.getY(), k, p);
                                hs.remove(tagId);
                                hs.put(tagId, list);
                                point.setX(list.get(0)[0]);
                                point.setY(list.get(0)[1]);

                                System.out.println(tagId + ":" + point);
                                pointConcurrentHashMap.put(tagId, point);
                            }
                            if (BackendDataSolvingThread.linkedBlockingQueue.size() > 80) {
                                int count = 0;
                                while (count < 40) {
                                    BackendDataSolvingThread.concurrentHashMap.remove(BackendDataSolvingThread.linkedBlockingQueue.poll());
//                                    System.out.println("清理无法结算的tagid");
//                                    System.out.println("concurrentHashMap的大小为= " + DataSolving.concurrentHashMap.size());
                                    count++;
                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            lock.unlock();
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

