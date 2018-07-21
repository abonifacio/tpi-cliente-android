package github.com.abonifacio.microfonoremoto.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import github.com.abonifacio.microfonoremoto.dispositivos.Dispositivo;

/**
 * Created by Augusto on 21/7/2018.
 */

public class PCMRecorder {

    private int sampleRate = 44100;
    private boolean stereo;
    private int sampleSize;

    private int mBufferSize;
    private AudioRecord mAudioRecorder;
    private Thread mThread;
    private boolean isRecording = false;
    private ByteConsumer mByteConsumer;

    public PCMRecorder(Dispositivo d,final ByteConsumer consumer){
        this.mByteConsumer = consumer;
        this.sampleRate = d.getSampleRate();
        this.stereo = d.isStereo();
        this.sampleSize = d.getSampleSize();
        int pcmChannels = d.isStereo() ? AudioFormat.CHANNEL_IN_STEREO : AudioFormat.CHANNEL_IN_MONO;
        int pcmSampleSize = d.getSampleSize()== 8 ? AudioFormat.ENCODING_PCM_8BIT : AudioFormat.ENCODING_PCM_16BIT;
        this. mBufferSize = AudioRecord.getMinBufferSize(sampleRate,pcmChannels,pcmSampleSize);
        this.mAudioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,pcmChannels,pcmSampleSize,mBufferSize);
    }

    public boolean start(){
        if(mBufferSize<1) return false;
        this.mAudioRecorder.startRecording();
        this.mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte data[] = new byte[mBufferSize];
                int readStatus = 0;
                while(isRecording){
                    readStatus = mAudioRecorder.read(data, 0, mBufferSize);
                    if(AudioRecord.ERROR_INVALID_OPERATION != readStatus){
                        mByteConsumer.onBytes(data,mBufferSize);
                    }
                }
            }
        });
        this.isRecording = true;
        this.mThread.start();
        return true;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public boolean isStereo() {
        return stereo;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public void destroy(){
        this.isRecording = false;
        if(this.mAudioRecorder!=null) this.mAudioRecorder.stop();
    }

    public interface ByteConsumer{
        public void onBytes(byte[] data, int size);
    }
}
