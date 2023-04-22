package com.nyj.fielddatadefinition;

import lombok.Data;

/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/6 11:00
 *
 * 字段数据定义
 */
@Data
public class FieldDataDefinition {
    private Integer[] messageHeader = new Integer[4];
    private Integer[] packetLength = new Integer[4];
    private Integer[] requestCommand = new Integer[4];
    private Integer[] sequenceId = new Integer[4];
    private Integer[] versionId = new Integer[4];
    private Integer[] anchorId = new Integer[4];
    private Integer[] tagId = new Integer[4];
    private Integer[] distance = new Integer[4];
    private Integer tagStatus;
    private Integer batchSn;
    private Integer xorByte;

    public FieldDataDefinition(Integer[] messageHeader, Integer[] packetLength,
                               Integer[] requestCommand, Integer[] sequenceId, Integer[] versionId,
                               Integer[] anchorId, Integer[] tagId, Integer[] distance, Integer tagStatus,
                               Integer batchSn, Integer xorByte) {
        this.messageHeader = messageHeader;
        this.packetLength = packetLength;
        this.requestCommand = requestCommand;
        this.sequenceId = sequenceId;
        this.versionId = versionId;
        this.anchorId = anchorId;
        this.tagId = tagId;
        this.distance = distance;
        this.tagStatus = tagStatus;
        this.batchSn = batchSn;
        this.xorByte = xorByte;
    }
    public FieldDataDefinition(){

    }
}
