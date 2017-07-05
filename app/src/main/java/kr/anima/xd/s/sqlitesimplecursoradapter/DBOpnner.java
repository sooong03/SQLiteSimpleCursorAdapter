package kr.anima.xd.s.sqlitesimplecursoradapter;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alfo6-10 on 7/5/2017.
 */

public class DBOpnner extends SQLiteOpenHelper {

    String tableName;

    public DBOpnner(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        tableName=name;
    }

    public DBOpnner(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    // 생성자로 전달받은 테이블이름을 가진 테이블이 존재하지 않으면
    // 자동으로 실행되는 메소드 == 맨 처음 1회 호출됨 == 테이블이 존재하지 않을때 호출됨
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+tableName+"("
                +"_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                +"name TEXT NOT NULL, "
                +"img INTEGER)");
    }

    // 버젼 번호를 다르게 했을 때 실행되는 메소드
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+tableName); // 혹시 테이블이 존재하면 지울 것
        onCreate(db);
    }
}
