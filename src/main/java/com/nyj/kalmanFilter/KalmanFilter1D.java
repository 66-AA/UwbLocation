package com.nyj.kalmanFilter;

import java.util.ArrayList;

/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/21 15:07
 * 一维卡尔曼滤波
 */
public class KalmanFilter1D {
    private double[] x; // 状态变量：位置和速度
    private double[] p; // 误差协方差矩阵
    private double[] q, r; // 过程噪声和观测噪声的协方差矩阵
    private double[] k; // 卡尔曼增益

    public KalmanFilter1D() {
        this.x = new double[]{0, 0};
        this.p = new double[]{1, 0, 0, 1};
        this.q = new double[]{0.01, 0, 0, 0.01};
        this.r = new double[]{10, 0, 0, 10};
        this.k = new double[]{0, 0};
    }

    public double[] predict(double[] p) {
        this.p = p;
        // 预测状态变量
        this.x[0] = this.x[0] + this.x[1];

        // 预测误差协方差矩阵
        p[0] = p[0] + p[1] + p[2] + p[3] + q[0];
        p[1] = p[1] + p[3] + q[1];
        p[2] = p[2] + p[3] + q[2];
        p[3] = p[3] + q[3];
        return p;
    }

    public ArrayList<double[]> update(double[] z, double[] k, double[] p) {
        ArrayList<double[]> list = new ArrayList<double[]>();
        this.k = k;
        this.p = p;
        // 计算卡尔曼增益
        this.k[0] = this.p[0] / (this.p[0] + this.r[0]);
        this.k[1] = this.p[2] / (this.p[0] + this.r[1]);

        // 更新状态变量
        this.x[0] = this.x[0] + this.k[0] * (z[0] - this.x[0]);
        this.x[1] = this.x[1] + this.k[1] * (z[1] - this.x[0]);

        // 更新误差协方差矩阵
        this.p[0] = (1 - this.k[0]) * this.p[0];
        this.p[1] = (1 - this.k[0]) * this.p[1];
        this.p[2] = (1 - this.k[1]) * this.p[2];
        this.p[3] = (1 - this.k[1]) * this.p[3];

        list.add(z);
        list.add(this.k);
        list.add(this.p);
        return list;
    }

    public ArrayList<double[]> filter(double x, double y, double[] k, double[] p) {
        this.p = p;
        this.k = k;

        // 进行预测
        this.p = predict(p);

        // 进行更新
        double[] z = {x, y};
        ArrayList<double[]> list = update(z, this.k, this.p);

        // 返回过滤后的坐标点和k、p
        return list;
    }
}
