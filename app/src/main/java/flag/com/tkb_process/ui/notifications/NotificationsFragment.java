package flag.com.tkb_process.ui.notifications;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;

import flag.com.tkb_process.MySQLiteHelper;
import flag.com.tkb_process.R;

public class NotificationsFragment extends Fragment {
    private SQLiteDatabase db;
    MySQLiteHelper dbHelper;
    int flag=1;
    int[] Mon={31,28,31,30,31,30,31,31,30,31,30,31};
    int now=0;
    com.github.mikephil.charting.charts.LineChart lineChart=null;
    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                dbHelper = new MySQLiteHelper(getActivity(),"Course_sub",null,1);
                db = dbHelper.getWritableDatabase();
                Cursor c=db.rawQuery("SELECT * FROM Course_sub ORDER BY date DESC",null);
                c.moveToFirst();
                int t=c.getCount();
                for(int i=0;i<t;i++){
                    String Name=c.getString(0);
                    String process=c.getString(1);
                    String date=c.getString(2);
                    String complete=c.getString(4);
                    produce(Name,date,process,complete);
                    c.moveToNext();
                }

                //設定目標達成度
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                String Y_M= String.valueOf(year) + "/" + String.valueOf(month + 1);
                c=db.rawQuery("SELECT Y_M FROM Course_sub WHERE complete = '完成' AND Y_M = '"+Y_M+"'",null);
                c.moveToFirst();
                int total=20;
                //db.execSQL("drop table if exists Mon_goal");
                db.execSQL("CREATE TABLE IF NOT EXISTS Mon_goal(goal int,id int)");

                Cursor G=db.rawQuery("SELECT * FROM Mon_goal",null);
                G.moveToFirst();
                if(G.getCount()==0){
                    ContentValues cv = new ContentValues();
                    cv.put("goal", 20);
                    cv.put("id", 1);
                    db.insert("Mon_goal", null, cv);
                    G=db.rawQuery("SELECT * FROM Mon_goal",null);
                }
                now=c.getCount(); G.moveToFirst();
                total=Integer.parseInt(G.getString(0));
                TextView tv=(TextView)getView().findViewById(R.id.tv1);
                tv.setText("本月當前修課數/目標修課數:   "+Integer.toString(c.getCount())+"/"+Integer.toString(total));
                TextView tv2=(TextView)getView().findViewById(R.id.textView18);
                tv2.setText("這個月還剩下"+Integer.toString(Mon[month]-day)+"天");

                final TextView change =(TextView)getView().findViewById(R.id.change);
                change.setOnClickListener( new  View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        change_click();
                    }
                });

                //設定圖表
                lineChart=getView().findViewById(R.id.lineChart);
                graph();

                //設定歷史紀錄可收放
                LinearLayout L=(LinearLayout)getView().findViewById(R.id.L5);
                L.setOnClickListener( new  View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        history_click();
                    }
                });

            }
        });
        return root;
    }
    public void produce(final String Name, final String date,final  String process,final  String complete){
        LinearLayout LL=(LinearLayout)getView().findViewById(R.id.LL);
        LinearLayout Loout = new LinearLayout(getActivity());
        Loout.setPadding(15,3,15,3);
        LL.addView(Loout, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout Lout = new LinearLayout(getActivity());
        Lout.setPadding(2,4,2,4);
        Lout.setBackgroundColor(Color.parseColor("#C5C5C5"));
        Loout.addView(Lout, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout Lin = new LinearLayout(getActivity());
        Lin.setPadding(5,5,5,5);
        Lin.setBackgroundColor(Color.parseColor("#FFFFFF"));
        Lin.setOrientation(LinearLayout.VERTICAL);
        Lout.addView(Lin,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        final TextView name = new TextView(getActivity());
        name.setGravity(Gravity.CENTER);
        name.setText(Name+" |  ");name.setTextSize(18);
        LinearLayout L1 = new LinearLayout(getActivity());
        L1.setBackgroundColor(Color.parseColor("#C5C5C5"));
        LinearLayout L2 = new LinearLayout(getActivity());
        L2.setOrientation(LinearLayout.HORIZONTAL);
        final TextView Date = new TextView(getActivity());
        TextView Process = new TextView(getActivity());
        TextView Complete = new TextView(getActivity());
        Date.setText(date+"  |  ");Date.setTextSize(18);Date.setGravity(Gravity.CENTER);
        Process.setText(process+"  |  ");Process.setTextSize(18);Process.setGravity(Gravity.CENTER);
        Complete.setText(complete);Complete.setTextSize(18);Complete.setGravity(Gravity.CENTER);
        L2.addView(name);L2.addView(Date);L2.addView(Process);L2.addView(Complete);
        //Lin.addView(name,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        //Lin.addView(L1,LinearLayout.LayoutParams.MATCH_PARENT,2);
        Lin.addView(L2,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);


        Lin.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View v2 = inflater.inflate(R.layout.course_d, null);
                TextView v_N=(TextView)v2.findViewById(R.id.v_Name);
                v_N.setText(Name);
                TextView pro=(TextView)v2.findViewById(R.id.textView10);
                pro.setText(process);
                TextView com=(TextView)v2.findViewById(R.id.textView11);
                com.setText(complete);
                if(!complete.equals("完成")){
                    com.setTextSize(14);
                }
                TextView DDate=(TextView)v2.findViewById(R.id.textView12);
                DDate.setText("上課日期:  "+date);
                Cursor c=db.rawQuery("SELECT * FROM Course_sub WHERE date= '"+date+"' AND _Name = '"+Name+"' AND complete = '"+complete+"'" ,null);
                c.moveToFirst();
                TextView score=(TextView)v2.findViewById(R.id.textView14);
                score.setText("學習成效:  "+c.getString(5)+"/5");
                TextView note=(TextView)v2.findViewById(R.id.textView17);
                note.setText(c.getString(6));
                if(note.getText().toString().equals(""))note.setText("你並沒有留下紀錄喔。");


                new AlertDialog.Builder(getActivity())
                        .setView(v2)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });
    }
    public void history_click(){
        if(flag==1&&lineChart!=null){
            LinearLayout LL = (LinearLayout)getView().findViewById(R.id.linearLayout2);
            LL.removeView(lineChart);
            flag=0;
        }
        else {
            lineChart = new com.github.mikephil.charting.charts.LineChart(getActivity());
            LinearLayout LL = (LinearLayout)getView().findViewById(R.id.linearLayout2);
            lineChart.setBackgroundColor(Color.WHITE);
            LL.addView(lineChart, LinearLayout.LayoutParams.MATCH_PARENT,400);
            graph();
            flag=1;
        }
    }
    public void graph(){
        //  設定圖表內容
        //設定數據
        Cursor tmp=db.rawQuery("SELECT Y_M FROM Course_sub WHERE complete = '完成' ORDER BY Y_M",null);

        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<String> label = new ArrayList<>();
        tmp.moveToFirst();
        int t=tmp.getCount();
        String pre="";
        if(t!=0)pre=tmp.getString(0);
        int times=0,j=0;
        for(int i=0;i<t;i++){
            if(tmp.getString(0).equals(pre)){
                times++;
            }
            else {
                values.add(new Entry(j, times));
                label.add(pre+"月");
                j++;times=0;pre=tmp.getString(0);
            }
            tmp.moveToNext();
        }
        values.add(new Entry(j, times));
        label.add(pre+"月");j=j+1;
        //多個點連成一條線（LineDataSet）
        final LineDataSet set;
        // greenLine
        set = new LineDataSet(values, "每月完成課堂數");
        set.setMode(LineDataSet.Mode.LINEAR);//類型為折線
        set.setColor(getResources().getColor(R.color.colorPrimary));//線的顏色
        set.setLineWidth(1.5f);//線寬
        set.setCircleColor(getResources().getColor(R.color.colorPrimaryDark));//圓點顏色
        set.setCircleRadius(6);//圓點大小
        set.setDrawValues(true);//不顯示座標點對應Y軸的數字(預設顯示)
        set.setValueTextSize(15);
        LineData data = new LineData(set);
        lineChart.setData(data);//一定要放在最後
        lineChart.invalidate();//繪製圖表
        //設置標籤
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X軸標籤顯示位置(預設顯示在上方，分為上方內/外側、下方內/外側及上下同時顯示)
        xAxis.setTextSize(12);//X軸標籤大小
        xAxis.setLabelCount(j);//X軸標籤個數
        xAxis.setValueFormatter(new IndexAxisValueFormatter(label));
        YAxis leftAxis = lineChart.getAxisLeft();//獲取左側的軸線
        leftAxis.setAxisMinimum(0);//Y軸標籤最小值
        YAxis rightAxis = lineChart.getAxisRight();//獲取右側的軸線
        rightAxis.setEnabled(false);//不顯示右側Y軸
    }
    public void change_click(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View v2 = inflater.inflate(R.layout.change_goal, null);
        new AlertDialog.Builder(getActivity())
                .setView(v2)
                .setIcon(R.drawable.ic_launcher_background)
                .setTitle("修改目標修課數")
                .setNegativeButton("取消修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("確定修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText et=(EditText)v2.findViewById(R.id.editText4);
                        String Digit = "[0-9]+";
                        if(!et.getText().toString().matches(Digit)){
                            new AlertDialog.Builder(getActivity())
                                    .setIcon(R.drawable.ic_launcher_background)
                                    .setTitle("Message")
                                    .setMessage("目標修課數必須填數字呦(*´▽`*)")
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }
                        else {
                            ContentValues cv = new ContentValues();
                            cv.put("goal", Integer.parseInt(et.getText().toString()));
                            db.update("Mon_goal",cv,"id = 1",null);
                            Cursor G=db.rawQuery("SELECT * FROM Mon_goal",null);
                            G.moveToFirst();
                            int total=Integer.parseInt(G.getString(0));
                            TextView tv=(TextView)getView().findViewById(R.id.tv1);
                            tv.setText("本月當前修課數/目標修課數:   "+Integer.toString(now)+"/"+Integer.toString(total));
                        }
                    }
                })
                .show();
    }
}
