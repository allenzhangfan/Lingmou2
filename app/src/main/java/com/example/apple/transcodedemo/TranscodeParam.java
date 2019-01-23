package com.example.apple.transcodedemo;

/**
 * Created by apple on 2017/5/31.
 */

public class TranscodeParam {

    //是否旋转，0表示不旋转，保持视频的默认方向， 90表示设置输出视频的旋转方向为90°
    public int autorotate = 0;

    //分辨率宽, 如：720
    public int width = 0;

    //分辨率高, 如：1280
    public int height = 0;

    //音频采样率，如：8000，相当于 8KHz
    public int samplerate = 0;

    //视频码率，如：1700，相当于 1650Kbps
    public int videobitrate = 0;

    //视频帧率，如：24
    public int videoframerate = 0;

    //音频码率，如：11，相当于 11Kbps
    public int audiobitrate = 0;

    //压缩速度设置（压缩速度越快，视频质量越低； 1-10,由慢到快，默认为8）
    public int preset = 0;

    //输入文件路径 (必填，128字节以内)
    public String srcpath = null;

    //输出文件路径 (必填，128字节以内)
    public String dstpath = null;

}
