package flag.com.tkb_process.ui.dashboard;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;

import flag.com.tkb_process.MySQLiteHelper;
import flag.com.tkb_process.R;

public class DashboardFragment extends Fragment {
    private SQLiteDatabase db;
    MySQLiteHelper dbHelper;
    Spinner spinner2;
    String Name,Note;
    int Count;
    LinearLayout LL=null;
    ArrayList<String> items =new ArrayList<>();
    private DashboardViewModel dashboardViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                dbHelper = new MySQLiteHelper(getActivity(),"Course_sub",null,1);
                db = dbHelper.getWritableDatabase();

                final TextView change =(TextView)getView().findViewById(R.id.textView16);
                change.setOnClickListener( new  View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        change();
                    }
                });


                //課程選單
                spinner2 = getView().findViewById(R.id.course);
                Cursor course=db.rawQuery("SELECT _Name FROM Course",null);
                course.moveToFirst();
                for(int i=0;i<course.getCount();i++){
                    items.add(course.getString(0));
                    course.moveToNext();
                }
                Name="請選擇課程: ";
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
                spinner2.setAdapter(adapter);
                spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(!items.get(position).equals(Name)){
                            Name=items.get(position);
                            //看板顯示
                            Cursor course=db.rawQuery("SELECT * FROM Course  WHERE _Name = '"+Name+"'",null);
                            course.moveToFirst();
                            TextView title=(TextView)getView().findViewById(R.id.textView19) ;
                            title.setText(Name);
                            TextView count=(TextView)getView().findViewById(R.id.textView20) ;
                            count.setText("總堂數: "+course.getString(1)+"堂");
                            Count=Integer.parseInt(course.getString(1));
                            TextView note=(TextView)getView().findViewById(R.id.textView22) ;
                            note.setText("備註: "+course.getString(3));
                            Note=course.getString(3);
                            if(course.getString(3).equals(""))note.setText("備註: 無");

                            //顯示修課紀錄
                            LinearLayout L = (LinearLayout)getView().findViewById(R.id.LL) ;
                            if(LL!=null)L.removeView(LL);
                            LL=new LinearLayout(getActivity());
                            LL.setOrientation(LinearLayout.VERTICAL);
                            L.addView(LL, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            Cursor c=db.rawQuery("SELECT * FROM Course_sub  WHERE _Name = '"+Name+"' ORDER BY process DESC",null);
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
                        }


                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });
        return root;
    }

    public void produce(final String Name, final String date,final  String process,final  String complete){
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
                TextView DDate=(TextView)v2.findViewById(R.id.textView12);
                DDate.setText("上課日期:  "+date);
                Cursor c=db.rawQuery("SELECT * FROM Course_sub WHERE process= '"+process+"' AND _Name = '"+Name+"' AND complete = '"+complete+"'" ,null);
                c.moveToFirst();
                TextView score=(TextView)v2.findViewById(R.id.textView14);
                score.setText("學習成效:  "+c.getString(5)+"/5");
                TextView note=(TextView)v2.findViewById(R.id.textView17);
                note.setText(c.getString(6));
                if(note.getText().toString().equals(""))note.setText("你並沒有留下紀錄喔。");


                new AlertDialog.Builder(getActivity())
                        .setView(v2)
                        .setIcon(R.drawable.star)
                        .setTitle(Name+" |  "+process+"  "+complete)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });
    }
    public void change(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View v = inflater.inflate(R.layout.course_add, null);
        EditText N_Name=(EditText)v.findViewById(R.id.C_name);
        N_Name.setText(Name);
        N_Name.setEnabled(false);
        EditText time=(EditText)v.findViewById(R.id.C_time);
        time.setText(Integer.toString(Count));
        EditText note=(EditText)v.findViewById(R.id.C_note);
        note.setText(Note);
        new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.star)
                .setTitle("修改課程內容")
                .setView(v)
                .setNegativeButton("取消更改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("確定更改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText c_Name=(EditText)v.findViewById(R.id.C_name);
                        EditText c_time=(EditText)v.findViewById(R.id.C_time);
                        String Digit = "[0-9]+";
                       if(!c_time.getText().toString().matches(Digit)){
                            new AlertDialog.Builder(getActivity())
                                    .setIcon(R.drawable.ic_launcher_background)
                                    .setTitle("Message")
                                    .setMessage("總堂數必須填數字呦(*´▽`*)")
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }
                        else {
                            EditText c_note=(EditText)v.findViewById(R.id.C_note);
                            ContentValues cv = new ContentValues();
                            cv.put("total_course", Integer.parseInt(c_time.getText().toString()));
                            cv.put("total_minus", 0);
                            cv.put("note", c_note.getText().toString());
                            db.update("Course",  cv,"_Name = '"+Name+"'",null);

                           //看板顯示
                           Cursor course=db.rawQuery("SELECT * FROM Course  WHERE _Name = '"+Name+"'",null);
                           course.moveToFirst();
                           TextView title=(TextView)getView().findViewById(R.id.textView19) ;
                           title.setText(Name);
                           TextView count=(TextView)getView().findViewById(R.id.textView20) ;
                           count.setText("總堂數: "+course.getString(1)+"堂");
                           Count=Integer.parseInt(course.getString(1));
                           TextView note=(TextView)getView().findViewById(R.id.textView22) ;
                           note.setText("備註: "+course.getString(3));
                           Note=course.getString(3);
                           if(course.getString(3).equals(""))note.setText("備註: 無");
                        }
                    }
                })
                .show();
    }
}
