package com.fanfan.novel.utils.permiss;

import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.seabreeze.log.Print;

/**
 * Created by Administrator on 2018/3/14/014.
 */

public class CheckPermission {

    public static final int STATE_RECORDING = -1;
    public static final int STATE_NO_PERMISSION = -2;
    public static final int STATE_SUCCESS = 1;

    /**
     * 设定录音来源于同方向的相机麦克风相同，若相机无内置相机或无法识别，则使用预设的麦克风
     * MediaRecorder.AudioSource.DEFAULT                默认音频源
     * MediaRecorder.AudioSource.MIC                    设定录音来源为主麦克风。
     * MediaRecorder.AudioSource.VOICE_CALL             设定录音来源为语音拨出的语音与对方说话的声音
     * MediaRecorder.AudioSource.VOICE_COMMUNICATION    摄像头旁边的麦克风
     * MediaRecorder.AudioSource.VOICE_DOWNLINK         下行声音
     * MediaRecorder.AudioSource.VOICE_RECOGNITION      语音识别
     * MediaRecorder.AudioSource.VOICE_UPLINK           上行声音
     */
    public static int audioSource = MediaRecorder.AudioSource.DEFAULT;

    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    // 采样率：音频的采样频率，每秒钟能够采样的次数，采样率越高，音质越高。
    // 给出的实例是44100、22050、11025但不限于这几个参数。例如要采集低质量的音频就可以使用4000、8000等低采样率。
    public static int sampleRateInHz = 44100;

    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    // 声道设置：android支持双声道立体声和单声道。MONO单声道，STEREO立体声
    public static int channelConfig = AudioFormat.CHANNEL_IN_MONO;

    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    // 编码制式和采样大小：采集来的数据当然使用PCM编码(脉冲代码调制编码，即PCM编码。PCM通过抽样、量化、编码三个步骤将连续变化的模拟信号转换为数字编码。)
    // android支持的采样大小16bit 或者8bit。当然采样大小越大，那么信息量越多，音质也越高，现在主流的采样大小都是16bit，在低质量的语音传输的时候8bit足够了。
    public static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;


    public static int getRecordState() {
        //通过调用jni函数native_get_min_buff_size取得buffer大小
        int minBuffer = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, (minBuffer * 100));
        short[] point = new short[minBuffer];
        int readSize = 0;
        try {
            audioRecord.startRecording();//检测是否可以进入初始化状态
        } catch (Exception e) {
            if (audioRecord != null) {
                audioRecord.release();
                audioRecord = null;
            }
            return STATE_NO_PERMISSION;
        }
        //根据开始录音判断是否有录音权限
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            //6.0以下机型都会返回此状态，故使用时需要判断bulid版本
            //检测是否在录音中
            recyclerRecord(audioRecord);
            Print.e("录音机被占用");
            return STATE_RECORDING;
        } else {
            //检测是否可以获取录音结果
            readSize = audioRecord.read(point, 0, point.length);
            if (readSize <= 0) {
                recyclerRecord(audioRecord);
                Print.e("录音的结果为空");
                return STATE_NO_PERMISSION;
            } else {
                recyclerRecord(audioRecord);
                return STATE_SUCCESS;
            }
        }
    }

    private static void recyclerRecord(AudioRecord audioRecord) {
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

    public synchronized static boolean isCameraUseable(int cameraID) {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open(cameraID);
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            e.printStackTrace();
            canUse = false;
        } finally {
            if (mCamera != null) {
                mCamera.release();
            } else {
                canUse = false;
            }
            mCamera = null;
        }
        return canUse;
    }
}
