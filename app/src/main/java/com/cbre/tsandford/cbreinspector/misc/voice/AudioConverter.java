package com.cbre.tsandford.cbreinspector.misc.voice;

import android.content.Context;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;

/**
 * A wrapper class for FFmpeg. See link for GitHub repo
 * @author  Todd Sandford
 * @link    https://github.com/WritingMinds/ffmpeg-android-java
 */

public class AudioConverter {

    public interface OnFinishCallBackHandler{
        void Success();
        void Failure();
    }

    public enum AudioFormat {
        AAC,
        MP3,
        M4A,
        WMA,
        WAV,
        FLAC;

        public String getFormat() {
            return name().toLowerCase();
        }
    }

    OnFinishCallBackHandler _callbackHandler;

    private File _outputFile;
    private FFmpeg _ffmpeg;
    static String TAG = "AudioConverter";

    public AudioConverter(Context context){
        _ffmpeg = FFmpeg.getInstance(context);
        loadBinary();
    }

    public File getOutputFile(){
        return _outputFile;
    }

    public void convert(File originalFile, AudioFormat outputFormat){
        _outputFile = getConvertedFile(originalFile, outputFormat);
        final String[] cmd = new String[]{"-y", "-i", originalFile.getPath(), _outputFile.getPath()};

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                startExecution(cmd);
            }
        });
        t.start();
    }

    private void startExecution(String[] command){
        try {
            _ffmpeg.execute(command, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.d(TAG, "FFmpeg successfully converted file");
                    if(_callbackHandler != null){
                        _callbackHandler.Success();
                    }
                }

                @Override
                public void onProgress(String message) {
                    Log.d(TAG, "FFmpeg is currently trying to convert file");
                }

                @Override
                public void onFailure(String message) {
                    Log.d(TAG, "FFmpeg failed to convert file");
                    if(_callbackHandler != null){
                        _callbackHandler.Failure();
                    }
                }

                @Override
                public void onStart() {
                    Log.d(TAG, "FFmpeg starting to convert file");
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "FFmpeg finishing up conversion of file");
                }
            });
        }catch(Exception e){
            Log.d(TAG, "FFmpeg failed. Error: " + e.getMessage());
        }
    }

    private void loadBinary(){
        if(_ffmpeg != null){
            try {
                _ffmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                    @Override
                    public void onFailure() {
                        Log.d(TAG, "FFmpeg failed to load binary");
                    }

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "FFmpeg successfully loaded binary");
                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }
                });
            } catch (FFmpegNotSupportedException e){
                Log.d(TAG, "FFmpeg not supported by device. Error: " + e.getMessage());
            } catch (Exception e){
                Log.d(TAG, "FFmpeg binary load failed. Error: " + e.getMessage());
            }
        }
    }

    private static File getConvertedFile(File originalFile, AudioFormat format){
        String[] f = originalFile.getPath().split("\\.");
        String filePath = originalFile.getPath().replace(f[f.length - 1], format.getFormat());
        return new File(filePath);
    }

    public void setCallbackHandler(OnFinishCallBackHandler handler){
        _callbackHandler = handler;
    }

}
