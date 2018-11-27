package com.brucezhu.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 业务对象
 * 实现序列化接口,方便JVM将PO类序列化到磁盘,或者通过流的方式进行传输
 */
public class BaseDomain implements Serializable
{
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
