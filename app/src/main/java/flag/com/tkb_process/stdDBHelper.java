package flag.com.tkb_process;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.FileInputStream;
import android.content.Context;

class StdDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Class";
    private static final int DATABASE_VERSION = 1;

    private Context context;

    public StdDBHelper(Context context, String name,
                       SQLiteDatabase.CursorFactory factory, int version){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE Course(_Name Text primary key, "+
                "total_course int , total_minus int , note Text)");
        db.execSQL("CREATE TABLE Course_sub(_Name Text, process Text, _date Text, Y_M Text,"+
                "complete Text , score int , note Text)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int Version){
        db.execSQL("DROP TABLE IF EXISTS Data");
        onCreate(db);
    }
}

