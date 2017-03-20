package com.example.administrator.mymp3;
import java.io.IOException;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;//music play class
//import android.net.Uri;
import android.os.Binder;
//import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Administrator on 2017/3/14.Service class include the option for music play
 */

public class MusicService extends Service {
    boolean ha = false;
    private int buf = 0;
    public final IBinder binder = new MyBinder();
    public class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
    private String[] musicDir = new String[]{
            "http://mpianatra.com/Courses/files/Eminem - Not Afraid_audio.mp3",
            "http://mpianatra.com/Courses/files/The Internet Millionaires' Club.mp3",
            "http://mpianatra.com/Courses/files/You're Never Over.mp3",
            "http://mpianatra.com/Courses/files/不要认为自己没有用.mp3",
            "http://mpianatra.com/Courses/files/对面的女孩看过来.mp3"};
    private int musicIndex = 0;
    private int musicState = 0;//1 is loading，2 is have loaded
    public static MediaPlayer mp = new MediaPlayer();//声明一个MediaPlayer对象
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    //create service
    @Override
    public void onCreate(){
            try{
                musicIndex = 0;


                mp.setDataSource(musicDir[musicIndex]);

                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

                musicState = 1;
                mp.prepareAsync();
                mp.seekTo(0);
                ha=false;
            }catch(IllegalStateException e){
                Log.v("hint","加载异常，请检查网络连接");
                e.printStackTrace();
            }catch (IOException e){
                Log.v("hint","加载异常，请检查网络连接");
                e.printStackTrace();
            }

        mp.setLooping(false);
        /*mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mp){
                if(mp.isPlaying()) nextMusic();
            }
        });*/
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // loaded
                musicState = 2;
            }
        });

       mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
              @Override
              public boolean onError(MediaPlayer mp, int what, int extra) {
                  if(!mp.isPlaying()&&mp!=null&&ha) {
                      mp.start();
                  }
                  return false;
               }
             });

        //buffer
        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                buf = i;
            }
        });
        super.onCreate();
    }




    @Override
    public void onDestroy(){

        if(mp != null){
            mp.stop();//停止播放
            mp.release();//释放资源
            mp = null;//把player对象设置为null
        }
        super.onDestroy();
    }


    /*@Override
    public int onStartCommand(Intent intent,int flags,int startId){
        Bundle b = intent.getExtras();
        int op = b.getInt("start");
        switch (op){
            case 1:
                play();
                break;
            case 2:
                pause();
                break;
            default:
                break;
        }
        return super.onStartCommand(intent,flags,startId);
    }*/


    //pause or play
    public void playOrPause() {
        ha = true;
        if(mp.isPlaying()&&mp!=null){
            mp.pause();
        } else if(!mp.isPlaying()&&mp!=null) {
            mp.start();
        }
        else;
    }

    public void stop() {
        ha = false;
        if(mp != null) {
            mp.stop();
            try {
                musicState = 1;
                mp.prepareAsync();
                mp.seekTo(0);
            }catch(Exception e){
                Log.v("hint","加载异常，请检查网络连接");
                e.printStackTrace();
            }
        }
    }

    public void nextMusic() {
        ha = true;
        if(mp != null && musicIndex <=4) {
            mp.stop();
            try {
                mp.reset();
                mp.setDataSource(musicDir[(musicIndex+1)%5]);
                musicIndex=(musicIndex+1)%5;
                musicState = 1;
                mp.prepareAsync();
                mp.seekTo(0);
                mp.start();
            } catch(IllegalStateException e){
                Log.v("hint","加载异常，请检查网络连接");
                e.printStackTrace();
            }catch (IOException e){
                Log.v("hint","加载异常，请检查网络连接");
                e.printStackTrace();
            }
        }
    }

    public void preMusic() {
        ha = true;
        if (mp != null && musicIndex >= 0) {
            mp.stop();
            try {
                mp.reset();
                musicIndex--;
                if(musicIndex<0) musicIndex=4;
                mp.setDataSource(musicDir[musicIndex]);
                musicState = 1;
                mp.prepareAsync();
                mp.seekTo(0);
                mp.start();
            } catch(IllegalStateException e){
                Log.v("hint","加载异常，请检查网络连接");
                e.printStackTrace();
            }catch (IOException e){
                Log.v("hint","加载异常，请检查网络连接");
                e.printStackTrace();
            }
        }
    }

    public void popMusic(int n){
        ha = true;
        if (mp != null && n >= 0 && n<=4) {
            mp.stop();
            try {
                mp.reset();
                musicIndex = n;
                mp.setDataSource(musicDir[musicIndex]);
                musicState = 1;
                mp.prepareAsync();
                mp.seekTo(0);
                mp.start();
            } catch(IllegalStateException e){
                Log.v("hint","加载异常，请检查网络连接");
                e.printStackTrace();
            }catch (IOException e){
                Log.v("hint","加载异常，请检查网络连接");
                e.printStackTrace();
            }
        }
    }

    public int getIndex(){
        return musicIndex;
    }

    public int getState(){return musicState;}

    public int getBuf(){return buf;}
}
