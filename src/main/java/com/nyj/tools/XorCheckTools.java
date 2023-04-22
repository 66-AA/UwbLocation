package com.nyj.tools;

import com.nyj.fielddatadefinition.FieldDataDefinition;

/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/18 13:58
 * 异或校验
 */
public class XorCheckTools {
    public static Integer xorchrck(FieldDataDefinition fieldLengthDefinition){
        int n = 35 ^ 50 ^ 1;
        n = single_XOR_check(fieldLengthDefinition.getSequenceId(),n);
        n = single_XOR_check(fieldLengthDefinition.getVersionId(),n);
        n = single_XOR_check(fieldLengthDefinition.getAnchorId(),n);
        n = single_XOR_check(fieldLengthDefinition.getTagId(),n);
        n = single_XOR_check(fieldLengthDefinition.getDistance(),n);
        Integer tagStatus = fieldLengthDefinition.getTagStatus();
        Integer batchSn = fieldLengthDefinition.getBatchSn();
        n = n ^ tagStatus ^ batchSn;
        //System.out.println("n=" + n);
        return n;

    }
    public static Integer single_XOR_check(Integer[] state,int n){
        for(int i = 0;i < state.length;i++){
            n = n ^ state[i];
        }
        return n;
    }
}

