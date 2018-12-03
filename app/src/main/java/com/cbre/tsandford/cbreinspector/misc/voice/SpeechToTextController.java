package com.cbre.tsandford.cbreinspector.misc.voice;
import android.os.AsyncTask;

import com.cbre.tsandford.cbreinspector.misc.Utils;
import com.ibm.watson.developer_cloud.http.ServiceCallback;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

import java.io.File;
import java.io.IOException;

public class SpeechToTextController{
    // This is using IBM's Watson Speech-To-Text mService
    // API reference: https://www.ibm.com/watson/developercloud/speech-to-text/api/v1/java.html?java#authentication
    // The API key used is for the Lite Version which allows free usage <100minutes / month

    private static final String API_KEY = "3wneMAvKIMfxw6yJI4II5ymJFjiOxE1mSpXP5Lu88Z_I";
    private static final String BASE_URL = "gateway-syd.watsonplatform.net";
    private static final String SPEECH_MODEL = "en-GB_NarrowbandModel";
    private static final String URL = "https://" + BASE_URL + "/speech-to-text/api";
    private static final String WEB_SOCKET_URL = "wss://" + BASE_URL + "/speech-to-text/api/v1/recognize";

    SpeechToText mService;
    ServiceCallback mServiceCallback;
    BaseRecognizeCallback mBaseRecogCallback;


    public SpeechToTextController(){
        IamOptions options = new IamOptions.Builder()
                .apiKey(API_KEY)
                .build();
        mService = new SpeechToText(options);
    }

    // executes async speech-to-text request
    // todo finish this and make it work
    public String getTranscription(File f) {
        if(mServiceCallback != null){
            try {
                mService.setEndPoint(URL);

                final RecognizeOptions options = new RecognizeOptions.Builder()
                        .audio(f)
                        .contentType(getContentType(f))
                        .model(SPEECH_MODEL)
                        .build();

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mService.recognize(options).enqueue(mServiceCallback);
                    }
                });

                t.start();

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getTranscriptionAlternative(File f){
        if(mBaseRecogCallback != null){
            try {
                mService.setEndPoint(WEB_SOCKET_URL);

                final RecognizeOptions options = new RecognizeOptions.Builder()
                        .audio(f)
                        .contentType(getContentType(f))
                        .model(SPEECH_MODEL)
                        .build();


                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mService.recognizeUsingWebSocket(options, mBaseRecogCallback);
                    }
                });
                t.start();

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getContentType(File f){
        String extension = Utils.GetFileExtension(f).toLowerCase();
        switch (extension){
            case ".mp3":
                return RecognizeOptions.ContentType.AUDIO_MP3;
            case ".mpeg":
                return RecognizeOptions.ContentType.AUDIO_MPEG;
            case ".wav":
                return RecognizeOptions.ContentType.AUDIO_WAV;
            case ".webm":
                return RecognizeOptions.ContentType.AUDIO_WEBM;
            case ".flac":
                return RecognizeOptions.ContentType.AUDIO_FLAC;
        }
        return "";
    }

    public void setServiceCallback(ServiceCallback serviceCallback){
        mServiceCallback = serviceCallback;
    }

    public void setBaseRecogCallback(BaseRecognizeCallback baseRecogCallback){
        mBaseRecogCallback = baseRecogCallback;
    }

}
