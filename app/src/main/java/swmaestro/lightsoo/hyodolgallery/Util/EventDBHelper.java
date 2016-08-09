package swmaestro.lightsoo.hyodolgallery.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventDBHelper extends SQLiteOpenHelper {

    public EventDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, "event.db", null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
