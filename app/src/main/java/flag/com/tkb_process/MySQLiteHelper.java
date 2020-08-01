package flag.com.tkb_process;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private Context context;

    public MySQLiteHelper(Context context, String name,
                          SQLiteDatabase.CursorFactory factory, int version) {

        super(context, "Blossom.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE Course(_Name Text primary key, "+
                "total_course int , total_minus int , note Text)");
        db.execSQL("CREATE TABLE Course_sub(_Name Text, process Text, date Text, Y_M Text,"+
                "complete Text , score int , note Text,primary key(_Name,process,complete))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
        // TODO Auto-generated method stub
        db.execSQL("drop table if exists diary");
        onCreate(db);
    }
}

