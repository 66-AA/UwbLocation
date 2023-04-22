package com.nyj.gettinglabelcoordinates;

import com.nyj.tagcoordinatepoint.TagCoordinatePoint;
import lombok.Data;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/20 13:30
 * 通过TDOA算法和模拟退火算法计算标签坐标
 */
@Data
public class GettingLabelCoordinates {
    public static final double X_MAX = 11.2;
    public static final double X_MIN = 0.0;
    public static final double Y_MAX = 11.2;
    public static final double Y_MIN = 0.0;
    //初始坐标假设
    public static final double X = 6.1;
    public static final double Y = 6.1;
    public static final double PRECISION = 0.000001;
    public static final int L = 10;   //每个温度下的迭代次数
    public static final double K = 0.96;   //迭代系数（推荐缓慢降温数值：0.95-0.99）。
    public static final double S = 0.2;  //控制扰动范围

    public static TagCoordinatePoint coordinateProcessing(Float[] distance) {
        Double[][] uwbPositions = new Double[][]{{0.0, 0.0}, {0.0, 11.2}, {11.2, 11.2}, {11.2, 0.0}}; //UWB基站位置
        TagCoordinatePoint tagCoordinatePoint = new TagCoordinatePoint(); //创建标签坐标对象
        positionCalculate(distance, uwbPositions, tagCoordinatePoint);
        return tagCoordinatePoint;
    }

    private static double random() {
        /**
         * ThreadLocalRandom 是 JDK 1.7 中引入的一个多线程安全随机数生成器，
         * 它提供了比 Random 更高效、更方便的方式获取随机数。
         *
         * 与 Random 不同的是，每个线程都有自己的 ThreadLocalRandom 实例。
         * 这样可以避免不同线程之间的竞争，从而提高了并发性能。
         * */
        return ThreadLocalRandom.current().nextDouble(1);
    }

    public static void positionCalculate(Float[] distance, Double[][] uwbPositions, TagCoordinatePoint tagCoordinatePoint) {
        int i, p;
        double nextX = 0, nextY = 0;   //下一个点的坐标
        double changer; //概率
        double p1;
        tagCoordinatePoint.setX(X);
        tagCoordinatePoint.setY(Y);
        double preX = X; //前一个点的x坐标
        double preY = Y; //前一个点的y坐标
        double preValue = Double.MAX_VALUE;
        double curValue = Double.MAX_VALUE;
        double bestValue = Double.MAX_VALUE;
        double t = 1.0;   //初始温度
        while (t >= PRECISION) {
            t *= K;   //每次下降到多少温度
            for (i = 0; i < L; i++) {
                /**
                 * p表示一个标志变量，用于记录是否产生了新的可行解。
                 * 具体来说，该变量的值为0时表示当前的扰动没有产生新的可行解，需要继续尝试扰动；
                 * 而为1时表示已经找到了新的可行解，可以结束当前的尝试。
                 *
                 * 在算法中，通过随机扰动当前点来产生新的解，并判断该解是否在指定的搜索范围内。
                 * 如果不在，则标志变量p的值为0，需要继续尝试扰动；否则标志变量的值为1，表示已经找到了新的可行解。
                 * **/
                p = 0;
                //直到产生一个新解
                nextX = preX + t * S * (random() - 0.5) * (X_MAX - X_MIN);
                nextY = preY + t * S * (random() - 0.5) * (X_MAX - X_MIN);
                //如果在搜索范围内
                if ((nextX >= X_MIN) && (nextX <= X_MAX) && (nextY >= Y_MIN) && (nextY <= Y_MIN)) {
                    p = 1;
                }
            }
            //curValue指的是每次TDOA后三个方程误差的绝对值之和，误差越小，说明算法得出的坐标点越准确
            curValue = optimizationFunction(nextX, nextY, distance, uwbPositions);
            //判断最新解和最优解的误差
            if (bestValue > curValue) {
                //此为新的最优解
                tagCoordinatePoint.setX(nextX);
                tagCoordinatePoint.setY(nextY);
                bestValue = curValue;
            }
            //如果上一次解误差大于新解，则接受新解
            if (preValue > curValue) {
                //接受新解
                preX = nextX;
                preY = nextY;
                preValue = curValue;
            } else {
                changer = (preValue - curValue) / t;
                //计算概率
                p1 = Math.exp(changer);
                //以一定概率接受较差的解
                if (p1 > random()) {
                    preX = nextX;
                    preY = nextY;
                    preValue = curValue;
                }
            }
        }
    }
        /**
         *
         // optimizationFunction
         // TDOA算法
         // @描述: 解算方法
         //
         //			水平H ,垂直V
         //	     #  -- -- -- H -- -- -- #
         //	     |  B2(0,V)     B3(H,V) |
         //	     |  (10,11)     (20,21) |
         //	     V                      V
         //	     |  (00,01)     (30,31) |
         //	     |  B1(0,0)     B4(H,0) |
         //	     #  -- -- -- H -- -- -- #
         */
        private static double optimizationFunction(double x, double y, Float[] distance, Double[][] uwbPositions) {
            double f21 = 0,f31 = 0,f41 = 0;
            try {
                //基准点与待定点差值开方
                double baseDifference = Math.sqrt(x * x + y * y);
                // B2 - B1  ：标签坐标到基站B1与标签坐标到基站B2的差值-（标签实际到B1的距离-标签实际到B2的距离的差值）
                f21 = -(distance[1] - distance[0]) + Math.sqrt(x * x + (y - uwbPositions[1][1]) * (y - uwbPositions[1][1])) - baseDifference;
                // B3 - B1
                f31 = -(distance[2] - distance[0]) + Math.sqrt((x - uwbPositions[2][0]) * (x - uwbPositions[2][0]) + (y - uwbPositions[2][1]) * (y - uwbPositions[2][1])) - baseDifference;
                // B4 - B1
                f41 = -(distance[3] - distance[0]) + Math.sqrt((x - uwbPositions[3][0]) * (x - uwbPositions[3][0]) + y * y) - baseDifference;
            } catch (Exception e) {
                System.out.println("TDOA出错!");
                e.printStackTrace();

            }
            return Math.abs(f21) + Math.abs(f31) + Math.abs(f41);

        }

}
