package com.springboot.dailyclock.wechat.mp.model;

/**
 * Created by weishiyao on 2018/1/14.
 * 多媒体工具类
 */
public class FileMeta {

    private String fileName;
    private String fileSize;
    private String fileType;
    private String savePath;

    private byte[] bytes;

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFileSize() {
        return fileSize;
    }
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
    public String getFileType() {
        return fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public byte[] getBytes() {
        return bytes;
    }
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }
}
