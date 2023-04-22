package com.nyj.statemachine;

/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/6 10:42
 * 状态机枚举
 *  *  MESSAGE_HEADER:4个连续的0xFF表示消息开始
 *  *  PACKET_LENGTH:消息体长度
 *  *  REQUEST_COMMAND:命令码
 *  *  SEQUENCE_ID:消息流水号
 *  *  VERSION_ID:协议版本
 *  *  ANCHOR_ID:基站ID
 *  *  TAG_ID:标签ID
 *  *  DISTANCE:标签与基站间的距离
 *  *  TAG_STATUS:标签的状态
 *  *  BATCH_SN:测距序号
 *  *  XOR_BYTE:该字节前所有字节的异或校验
 */
public class MyStateEnum {
    public static final int MESSAGE_HEADER = 0X01;
    public static final int PACKET_LENGTH = 0X02;
    public static final int REQUEST_COMMAND = 0X03;
    public static final int SEQUENCE_ID = 0X04;
    public static final int VERSION_ID = 0X05;
    public static final int ANCHOR_ID = 0X06;
    public static final int TAG_ID = 0X07;
    public static final int DISTANCE = 0X08;
    public static final int TAG_STATUS = 0X09;
    public static final int BATCH_SN = 0X0A;
    public static final int  XOR_BYTE =0X0B;
}
