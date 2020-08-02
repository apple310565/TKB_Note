package flag.com.tkb_process.ui.home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.Calendar;


import flag.com.tkb_process.MySQLiteHelper;
import flag.com.tkb_process.R;



public class HomeFragment extends Fragment {
    Spinner spinner2,spinner;
    String Y_M,complete,Name;
    private SQLiteDatabase db;
    MySQLiteHelper dbHelper;
    ArrayList<String> items =new ArrayList<>();
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                dbHelper = new MySQLiteHelper(getActivity(),"Course_sub",null,1);
                db = dbHelper.getWritableDatabase();

                //選日期
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                Y_M= String.valueOf(year) + "/" + String.valueOf(month + 1);
                String dateTime = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
                TextView date = getView().findViewById(R.id.date);
                date.setText(dateTime);

                Button Datepick =(Button)getView().findViewById(R.id.datepicker);
                Datepick.setOnClickListener( new  View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePicker();
                    }
                });

                //課程選單
                spinner2 = getView().findViewById(R.id.course);
                /*final String[] items = new String[]{
                        "請選擇課程: ", "作業系統", " 線性代數", "演算法","離散數學","資料結構","計算機組織"
                };*/
                Cursor course=db.rawQuery("SELECT _Name FROM Course",null);
                items.add("請選擇課程: ");
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
                        Log.v("item", (String) parent.getItemAtPosition(position));
                        Name=items.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });


                //進度選單
                spinner = getView().findViewById(R.id.pro_spinner);
                final String[] items2 = new String[]{
                        "完成", "其他"
                };

                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items2);
                spinner.setAdapter(adapter2);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        complete=items2[position];
                        if(items2[position].equals("其他")){
                            TextView tv = getView().findViewById(R.id.textView3);
                            tv.setTextColor(Color.parseColor("#000000"));
                            EditText e_lse = getView().findViewById(R.id.editText2);
                            e_lse.setEnabled(true);
                        }
                        else {
                            TextView tv = getView().findViewById(R.id.textView3);
                            tv.setTextColor(Color.parseColor("#AEAEAE"));
                            EditText e_lse = getView().findViewById(R.id.editText2);
                            e_lse.setText("");
                            e_lse.setEnabled(false);
                        }
                        Log.v("items2", (String) parent.getItemAtPosition(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                //新增課程
                final Button course_add =(Button)getView().findViewById(R.id.course_add);
                course_add.setOnClickListener( new  View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add();
                    }
                });

                //清除
                final Button remove =(Button)getView().findViewById(R.id.button3);
                remove.setOnClickListener( new  View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remove();
                    }
                });

                //送出
                final Button submit =(Button)getView().findViewById(R.id.button4);
                submit.setOnClickListener( new  View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText Pro=getView().findViewById(R.id.editText);
                        String process=Pro.getText().toString();
                        TextView Date=getView().findViewById(R.id.date);
                        String date=Date.getText().toString();
                        SeekBar SB=getView().findViewById(R.id.seekBar);
                        int score=SB.getProgress();
                        TextView Note=getView().findViewById(R.id.editText3);
                        String note=Note.getText().toString();
                        EditText EE=getView().findViewById(R.id.editText2);
                        String ee=EE.getText().toString();
                        if(spinner2.getSelectedItemPosition()==0){
                            new AlertDialog.Builder(getActivity())
                                    .setIcon(R.drawable.ic_launcher_background)
                                    .setTitle("Message")
                                    .setMessage("要記得選擇課程喔")
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }
                        else if(process.equals("")){
                            new AlertDialog.Builder(getActivity())
                                    .setIcon(R.drawable.ic_launcher_background)
                                    .setTitle("Message")
                                    .setMessage("堂數要記得填寫喔")
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }
                        else if(complete.equals("其他")&&ee.equals("")){
                            new AlertDialog.Builder(getActivity())
                                    .setIcon(R.drawable.ic_launcher_background)
                                    .setTitle("Message")
                                    .setMessage("若進度選擇其他，後面的要記得填寫。")
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }
                        else{
                            submit(process,date,score,note);
                            new AlertDialog.Builder(getActivity())
                                    .setIcon(R.drawable.ic_launcher_background)
                                    .setTitle("Message")
                                    .setMessage("學習記錄新增成功")
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            remove();
                                        }
                                    })
                                    .show();

                        }
                    }
                });

            }
        });

        return root;



    }

    public void datePicker() {
        TextView date = getView().findViewById(R.id.date);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(getView().getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String dateTime = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(dayOfMonth);
                Y_M=String.valueOf(year) + "/" + String.valueOf(month + 1);
                TextView date = getView().findViewById(R.id.date);
                date.setText(dateTime);
            }
        }, year, month, day).show();
    }

    public void  remove(){
        spinner.setSelection(0);
        spinner2.setSelection(0);
        SeekBar SB = getView().findViewById(R.id.seekBar);
        SB.setProgress(0);
        EditText ET = getView().findViewById(R.id.editText3);
        ET.setText("");
        EditText ET2 = getView().findViewById(R.id.editText);
        ET2.setText("");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dateTime = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
        TextView date = getView().findViewById(R.id.date);
        date.setText(dateTime);
    }

    public void submit(String process,String date,int score,String note){
        try {
            ContentValues cv = new ContentValues();
            cv.put("_Name", Name);
            cv.put("process", process);
            if (complete.equals("完成")) cv.put("complete", complete);
            else {
                EditText EE = getView().findViewById(R.id.editText2);
                String ee = EE.getText().toString();
                cv.put("complete", ee);
            }
            cv.put("date", date);
            cv.put("score", score);
            cv.put("note", note);
            cv.put("Y_M", Y_M);
             db.insert("Course_sub", null, cv);
        }catch (Exception e) {
            e.printStackTrace();
            new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_launcher_background)
                    .setTitle("Error")
                    .setMessage(e.toString())
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
            //Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }
    public void add(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View v = inflater.inflate(R.layout.course_add, null);
        new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_launcher_background)
                .setTitle("新增課程")
                .setView(v)
                .setNegativeButton("取消新增", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("確定新增", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    EditText c_Name=(EditText)v.findViewById(R.id.C_name);
                    EditText c_time=(EditText)v.findViewById(R.id.C_time);
                        String Digit = "[0-9]+";
                    if(c_Name.getText().toString().equals("")){
                        new AlertDialog.Builder(getActivity())
                                .setIcon(R.drawable.ic_launcher_background)
                                .setTitle("Message")
                                .setMessage("課程名稱不可為空喔")
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    }
                    else if(!c_time.getText().toString().matches(Digit)){
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
                        cv.put("_Name", c_Name.getText().toString());
                        cv.put("total_course", Integer.parseInt(c_time.getText().toString()));
                        cv.put("total_minus", 0);
                        cv.put("note", c_note.getText().toString());
                        db.insert("Course", null, cv);

                        Cursor course=db.rawQuery("SELECT _Name FROM Course",null);
                        items.clear();
                        items.add("請選擇課程: ");
                        course.moveToFirst();
                        for(int i=0;i<course.getCount();i++){
                            items.add(course.getString(0));
                            course.moveToNext();
                        }
                    }

                    }
                })
                .show();
    }
}
