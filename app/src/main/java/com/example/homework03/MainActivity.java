/* Groups1 23
Dhawala Bhagawat
Niveditha Gopalkrishna
*/


package com.example.homework03;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public  EditText minimumValue;
    public  EditText maximumValue;
    public  EditText avgValue;
    public  SeekBar seekBar;
    public EditText selectComplexity;
    int getVal;
    Handler handler;
    ProgressDialog progress;
    ExecutorService threadpool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        threadpool = Executors.newFixedThreadPool(2); //threadpool with 2 threads

        selectComplexity = findViewById(R.id.et_selectCompl);
        seekBar = findViewById(R.id.pb_async);
        minimumValue = findViewById(R.id.et_min_val);
        maximumValue = findViewById(R.id.et_max_val);
        avgValue = findViewById(R.id.et_min_avg);
        Button generate_btn = findViewById(R.id.bt_generate);
        seekBar.setMax(10);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override

            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                getVal = i;
                selectComplexity.setText(getVal+"");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar.setProgress(0);
        progress = new ProgressDialog(MainActivity.this);


        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                switch (message.what)
                {
                    case doWork.STATUS_START:

                        progress.setMessage("Upadting Progress");
                        progress.setMax(100);
                        progress.setProgressStyle(progress.STYLE_SPINNER);
                        progress.setCancelable(false);
                        progress.show();
                        break;

                    case doWork.STATUS_STOP:
                        progress.dismiss();
                        double min = message.getData().getDouble(doWork.MIN);
                        double max = message.getData().getDouble(doWork.MAX);
                        double avg = message.getData().getDouble(doWork.AVG);
                        minimumValue.setText(min+"");
                        maximumValue.setText(max+"");
                        avgValue.setText(avg+"");
                        break;



                }
                return false;
            }

        });

        generate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                threadpool.execute(new doWork());

            }
        });

    }
    class doWork implements Runnable{


        static final int STATUS_START = 0x00;
        static final int STATUS_STOP= 0x02;
        static final String PROGRESS_KEY= "Progress";
        static final String MIN= "MaxVal";
        static final String MAX= "MinVal";
        static final String AVG= "Avg";
        @Override
        public void run() {

            //Start Progress
            Message startmessage = new Message();
            startmessage.what = STATUS_START;
            handler.sendMessage(startmessage);

            HeavyWork heavyWork = new HeavyWork();
            ArrayList<Double> listNum =  heavyWork.getArrayNumbers(getVal);
            Collections.sort(listNum);
            Log.d("demo", listNum.toString());
            double sum=0.0;
            for(int i =0;i<listNum.size(); i++)
            {
                sum = listNum.get(i)+sum;
            }
            Double avg=(sum)/listNum.size();
            Message stopmessage = new Message();

            //End Progress
            stopmessage.what = STATUS_STOP;
            Bundle bundle = new Bundle();
            bundle.putDouble(MIN,listNum.get(0));
            bundle.putDouble(MAX,listNum.get(listNum.size()-1));
            bundle.putDouble(AVG,avg);
            stopmessage.setData(bundle);
            handler.sendMessage(stopmessage);
        }
    }

}

