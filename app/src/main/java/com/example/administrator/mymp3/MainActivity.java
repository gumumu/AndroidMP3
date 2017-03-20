package com.example.administrator.mymp3;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public android.os.Handler handler = new android.os.Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if(musicService.getState()==1) tvLoad.setVisibility(View.VISIBLE);
            else tvLoad.setVisibility(View.GONE);

            if(musicService.mp.isPlaying()) {
                bt1.setText("PAUSE");
            } else {
                bt1.setText("START");
            }

            int Nowtimes = musicService.mp.getCurrentPosition();
            int Fintimes = musicService.mp.getDuration();
            Calendar mycalendar1 = Calendar.getInstance();
            Calendar mycalendar2 = Calendar.getInstance();
            mycalendar1.setTimeInMillis(Nowtimes);
            mycalendar2.setTimeInMillis(Fintimes);
            nowMin=mycalendar1.get(Calendar.MINUTE);
            nowSec=mycalendar1.get(Calendar.SECOND);
            finMin=mycalendar2.get(Calendar.MINUTE);
            finSec=mycalendar2.get(Calendar.SECOND);

            tvNow.setText(String.format("%d:%02d",nowMin,nowSec));
            tvFin.setText(String.format("%d:%02d",finMin,finSec));

            tvPin.setText(strs[musicService.getIndex()]);

            sb.setMax(finMin*60+finSec);
            sb.setProgress(nowMin*60+nowSec);
            sb.setSecondaryProgress((int)(musicService.getBuf()/100.0*(finMin*60+finSec)));

            if(sb.getProgress()==sb.getMax()&&musicService.mp.isPlaying()&&musicService.getState()==2) musicService.nextMusic();

            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        musicService.mp.seekTo(seekBar.getProgress()*1000);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            handler.postDelayed(runnable, 100);
        }
    };


    MusicService musicService=new MusicService();;
    int nowMin=0,nowSec=0,finMin=0,finSec=0;
    TextView tvPin,tvNow,tvFin,tvLoad;
    SeekBar sb;
    ListView lv;
    Button bt1,bt2,bt3;
    private static final String[] strs = new String[]{
            "Eminem - Not Afraid_audio",
            "The Internet Millionaires' Club",
            "You're Never Over",
            "不要认为自己没有用",
            "对面的女孩看过来"
    };

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicService = ((MusicService.MyBinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
        }
    };
    private void bindServiceConnection() {
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        startService(intent);
        bindService(intent, sc, this.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        if(musicService.getState()==1) tvLoad.setVisibility(View.VISIBLE);
        else tvLoad.setVisibility(View.GONE);

        if(musicService.mp.isPlaying()) {
            bt1.setText("PAUSE");
        } else {
            bt1.setText("START");
        }

        int Nowtimes = musicService.mp.getCurrentPosition();
        int Fintimes = musicService.mp.getDuration();
        Calendar mycalendar1 = Calendar.getInstance();
        Calendar mycalendar2 = Calendar.getInstance();
        mycalendar1.setTimeInMillis(Nowtimes);
        mycalendar2.setTimeInMillis(Fintimes);
        nowMin=mycalendar1.get(Calendar.MINUTE);
        nowSec=mycalendar1.get(Calendar.SECOND);
        finMin=mycalendar2.get(Calendar.MINUTE);
        finSec=mycalendar2.get(Calendar.SECOND);

        tvNow.setText(String.format("%d:%02d",nowMin,nowSec));
        tvFin.setText(String.format("%d:%02d",finMin,finSec));

        tvPin.setText(strs[musicService.getIndex()]);

        sb.setMax(finMin*60+finSec);
        sb.setProgress(nowMin*60+nowSec);
        sb.setSecondaryProgress((int)(musicService.getBuf()/100.0*(finMin*60+finSec)));
        handler.post(runnable);
        super.onResume();
        Log.d("hint", "handler post runnable");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bt1 = (Button)findViewById(R.id.bt1);
        bt2 = (Button)findViewById(R.id.bt2);
        bt3 = (Button)findViewById(R.id.bt3);

        tvLoad = (TextView)findViewById(R.id.textView);

        tvLoad.setVisibility(View.VISIBLE);
        bindServiceConnection();

        //now = musicService.mp.getCurrentPosition()/60000;
        //fin = musicService.mp.getDuration()/60000;

        sb = (SeekBar)this.findViewById(R.id.sb);
        /*sb.setMax((int)fin*60000);
        sb.setProgress((int)now*60000);

        if(musicService.mp.isPlaying()) {
            bt1.setText("PAUSE");
        } else {
            bt1.setText("START");
        }*/

        tvPin = (TextView)findViewById(R.id.tvPin);
        //tvPin.setText(strs[0]);
        tvNow = (TextView)findViewById(R.id.tvNow);
        //tvNow.setText(nf.format(now));
        tvFin = (TextView)findViewById(R.id.tvFin);
        //tvFin.setText(nf.format(fin));

        lv = (ListView)findViewById(R.id.lv);
        lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,strs));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tvPin.setText(strs[i]);
                tvLoad.setVisibility(View.VISIBLE);
                musicService.popMusic(i);
            }
        });
    }

    public void onClick(View v){
        switch (v.getId()) {
            case R.id.bt1://when it is play
                musicService.playOrPause();
                break;
            case R.id.bt2://when it is next
                tvLoad.setVisibility(View.VISIBLE);
                musicService.nextMusic();
                break;
            case R.id.bt3://when it is last
                tvLoad.setVisibility(View.VISIBLE);
                musicService.preMusic();
                break;
            case R.id.bt4://when it is quit
                MainActivity.this.finish();
                onDestroy();
                System.exit(0);
            default:
                break;
        }
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        unbindService(sc);
        stopService(new Intent(MainActivity.this,MusicService.class));
        super.onDestroy();
    }
}
