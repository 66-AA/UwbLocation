package com.nyj.kalmanFilter;

/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/21 15:08
 * 二维卡尔曼滤波
 *
 *
 * /**
 *  * 二维卡尔曼滤波算法
 *  * A：状态转移矩阵，用于描述系统状态从一个时刻到下一个时刻的转移关系。
 *  * H：观测矩阵，用于描述系统状态与观测之间的关系。
 *  * Q：状态转移协方差矩阵，用于描述状态转移过程中的噪声。
 *  * R：观测噪声协方差，用于描述观测噪声的大小。
 *  * P：初始估计协方差矩阵，用于描述估计状态与真实状态之间的差异。
 *  * 代码中还包含了一些模拟数据的生成和卡尔曼滤波器的预测和更新过程。
 *  * 其中，x_true 表示真实状态
 *  * x_noisy 表示加噪声的观测值
 *  * x_est 表示估计状态。
 *  * 在预测过程中，根据上一个时刻的估计状态和状态转移矩阵，可以得到当前时刻的状态预测值 x_pred。
 *  * 在更新过程中，根据观测值和卡尔曼增益，可以得到当前时刻的状态更新值 x_update 和估计协方差矩阵更新值 P_update。
 *  */
public class KalmanFilter2D {
    public static void KalmanFiltering() {

        double[][] A = {{1, 1}, {0, 1}}; // 状态转移矩阵
        double[][] H = {{1, 0}}; // 观测矩阵
        double[][] Q = {{0.01, 0}, {0, 0.001}}; // 状态转移协方差矩阵
        double R = 1; // 观测噪声协方差   观测矩阵 * 观测矩阵的转置
        double[][] P = {{1, 0}, {0, 1}}; // 初始估计协方差矩阵

// 生成模拟数据
        double[] t = new double[101];
        for (int i = 0; i < t.length; i++) {
            t[i] = i * 0.1;
        }
        double[][] x_true = new double[2][101];
        for (int i = 0; i < t.length; i++) {
            x_true[0][i] = Math.sin(t[i]);
            x_true[1][i] = Math.cos(t[i]);
        } // 真实状态
        double[][] x_noisy = new double[2][101];
        for (int i = 0; i < t.length; i++) {
            x_noisy[0][i] = x_true[0][i] + Math.random();
            x_noisy[1][i] = x_true[1][i] + Math.random();
        } // 加噪声的观测值

// 卡尔曼滤波器预测和更新
        double[][] x_est = new double[2][101]; // 初始化估计状态
        for (int i = 0; i < t.length; i++) {
            // 预测
            double[][] x_pred;
            if (i == 0) {
                x_pred = new double[][]{{x_noisy[0][0]}, {x_noisy[1][0] - x_noisy[0][0]}}; // 初始状态
            } else {
                x_pred = matrixMultiply(A, new double[][]{{x_est[0][i - 1]}, {x_est[1][i - 1]}}); // 状态预测
            }
            double[][] p_pred;
            p_pred = matrixAdd(matrixMultiply(matrixMultiply(A,P),transpose(A)),Q);


// 更新
            double[][] I = {{1, 0}, {0, 1}}; // 单位矩阵
            double[][] K =matrixDivide(matrixMultiply(p_pred,transpose(H)),matrixAdd(matrixMultiply(matrixMultiply(H,p_pred),transpose(H)),R)); // 卡尔曼增益
            double[][] x_update = matrixAdd(x_pred, matrixMultiply(K, matrixSubtract(new double[][]{{x_noisy[0][i]}, {x_noisy[1][i]}},matrixMultiply(H, x_pred)))); // 状态更新
            double[][] P_update = matrixMultiply(matrixSubtract(I, matrixMultiply(K, H)), P); // 估计协方差矩阵更新
            x_est[0][i] = x_update[0][0];
            x_est[1][i] = x_update[1][0];
            P = P_update; // 更新估计协方差矩阵
        }
    }
    /**
     * 矩阵乘法
     */
    public static double[][] matrixMultiply(double[][] A, double[][] B) {
        int m = A.length;
        int n = A[0].length;
        int p = B[0].length;
        double[][] C = new double[m][p];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                for (int k = 0; k < n; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }
    /**
     *  2行1列矩阵除以1行1列矩阵
     */

    public static double[][] matrixDivide(double[][] A, double[][] B) {
        int m = A.length;
        int n = A[0].length;
        double[][] C = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] / B[0][0];
            }
        }
        return C;
    }


    /**
     * 矩阵加法
     */
    public static double[][] matrixAdd(double[][] A, double[][] B) {
        int m = A.length;
        int n = A[0].length;
        double[][] C = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] + B[i][j];
            }
        }
        return C;
    }
    /**
     * 矩阵加一个double类型的数
     */
    public static double[][] matrixAdd(double[][] A, double b) {
        int m = A.length;
        int n = A[0].length;
        double[][] C = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] + b;
            }
        }
        return C;
    }
    /**
     *一个整数减一个矩阵
     */
    public static double[][] matrixSubtract(double[][] A, double[][] B) {
        int m = A.length;
        int n = A[0].length;
        if (B.length == 1 && B[0].length == 1) {
            double[][] C = new double[m][n];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    C[i][j] = A[i][j] - B[0][0];
                }
            }
            return C;
        } else {
            double[][] C = new double[m][n];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    C[i][j] = A[i][j] - B[i][j];
                }
            }
            return C;
        }
    }



    /**
     * 矩阵转置
     */
    public static double[][] transpose(double[][] A) {
        int m = A.length;
        int n = A[0].length;
        double[][] C = new double[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[j][i] = A[i][j];
            }
        }
        return C;
    }
}
