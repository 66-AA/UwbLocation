package com.nyj.statemachineparsesbytebybyte;

import com.nyj.fielddatadefinition.FieldDataDefinition;
import com.nyj.statemachine.MyStateEnum;
import com.nyj.tools.XorCheckTools;

/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/6 11:08
 * 状态机,
 * @return:返回经过状态机解析完整的数据
 *
 *  *  messageHeader:4个连续的0xFF表示消息开始
 *  *  packetLength:消息体长度
 *  *  requestCommand:命令码
 *  *  sequenceId:消息流水号
 *  *  versionId:协议版本
 *  *  anchorId:基站ID
 *  *  tagId:标签ID
 *  *  distance:标签与基站间的距离
 *  *  tagStatus:标签的状态
 *  *  batchSn:测距序号
 *  *  XorByte:该字节前所有字节的异或校验
 */

public class StateMachineParsesBytebybyte {
    private Integer state = MyStateEnum.MESSAGE_HEADER;
    private Integer[] messageHeader = new Integer[4];
    private Integer[] packetLength = new Integer[4];
    private Integer[] requestCommand = new Integer[4];
    private Integer[] sequenceId = new Integer[4];
    private Integer[] versionId = new Integer[4];
    private Integer[] anchorId = new Integer[4];
    private Integer[] tagId = new Integer[4];
    private Integer[] distance = new Integer[4];
    private Integer tagStatus ;
    private Integer batchSn ;
    private Integer xorByte;
    private Integer count = 0;


    //状态机转换
    public FieldDataDefinition parsingData(Integer readByte){
        FieldDataDefinition fieldDataDefinition = null;
        switch (this.state){
            case MyStateEnum.MESSAGE_HEADER:
                if (readByte == 0XFF){
                    messageHeader[count] = readByte;
                    count++;
                    if(count == 4){
                        state = MyStateEnum.PACKET_LENGTH;
                        count = 0;
                    }
                }
                break;
            case MyStateEnum.PACKET_LENGTH:
                packetLength[count] = readByte;
                count++;
                if(count == 4){
                    state = MyStateEnum.REQUEST_COMMAND;
                    count = 0;
                }
                break;
            case MyStateEnum.REQUEST_COMMAND:
                requestCommand[count] = readByte;
                count++;
                if(count == 4){
                    state = MyStateEnum.SEQUENCE_ID;
                    count = 0;
                }
                break;
            case MyStateEnum.SEQUENCE_ID:
                sequenceId[count] = readByte;
                count++;
                if(count == 4){
                    state = MyStateEnum.VERSION_ID;
                    count = 0;
                }
                break;
            case MyStateEnum.VERSION_ID:
                versionId[count] = readByte;
                count++;
                if(count == 4){
                    state = MyStateEnum.ANCHOR_ID;
                    count = 0;
                }
                break;
            case MyStateEnum.ANCHOR_ID:
                anchorId[count] = readByte;
                count++;
                if(count == 4){
                    state = MyStateEnum.TAG_ID;
                    count = 0;
                }
                break;
            case MyStateEnum.TAG_ID:
                tagId[count] = readByte;
                count++;
                if(count == 4){
                    state = (int)MyStateEnum.DISTANCE;
                    count = 0;
                }
                break;
            case MyStateEnum.DISTANCE:
                distance[count] = readByte;
                count++;
                if(count == 4){
                    state = MyStateEnum.TAG_STATUS;
                    count = 0;
                }
                break;
            case MyStateEnum.TAG_STATUS:
                tagStatus = readByte;
                state = MyStateEnum.BATCH_SN;
                break;
            case MyStateEnum.BATCH_SN:
                batchSn = readByte;
                state = MyStateEnum.XOR_BYTE;
                break;
            case MyStateEnum.XOR_BYTE:
                xorByte = readByte;
                fieldDataDefinition = new FieldDataDefinition(messageHeader, packetLength, requestCommand, sequenceId, versionId,
                        anchorId, tagId, distance, tagStatus, batchSn, xorByte);
                //System.out.println(fieldDataDefinition);
                if(XorCheckTools.xorchrck(fieldDataDefinition).equals(xorByte)){
                    //System.out.println("校验数据正确");
                }else{
                    fieldDataDefinition = null;
                    //System.out.println("校验数据失败");
                }
            state = MyStateEnum.MESSAGE_HEADER;
                break;
            default:
                System.out.println("状态机出错了...");
        }
        return fieldDataDefinition;
    }
}
