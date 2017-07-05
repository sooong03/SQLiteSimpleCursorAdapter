package kr.anima.xd.s.sqlitesimplecursoradapter;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.v7.appcompat.R.styleable.AlertDialog;

public class MainActivity extends AppCompatActivity {

    EditText ed_name, ed_num;
    ListView listView;
    SimpleCursorAdapter cursorAdapter;


    SQLiteDatabase db;
    String tableName="Member";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ed_name= (EditText) findViewById(R.id.ed_name);
        ed_num= (EditText) findViewById(R.id.ed_num);
        listView= (ListView) findViewById(R.id.view_list);

        listView.setOnItemClickListener(clickListener);
        listView.setOnItemLongClickListener(longClickListener);

        // db create
        // SQLiteOpenHelper.class : db파일과 테이블이 한 번에 생성됨
        // 테이블마다 db가 만들어짐 == 하나의 db, 하나의 테이블
        // 테이블과 같은 이름으로 db 파일을 만들어서 작업함
        DBOpnner opnner=new DBOpnner(this, tableName, null, 1);

        // 읽고 쓰기 다 됨
        // db=opnner.getReadableDatabase(); 메모리 부족 시 db 읽어들임을 포기
        // writable 은 메모리 부족하면 필요없는걸 지우고 무조건 db를 열기기
       db=opnner.getWritableDatabase();

    }
    public void clickInsert(View v){
        String name=ed_name.getText().toString();
        int imgNum=0;
        try {
            imgNum=Integer.parseInt(ed_num.getText().toString());
        } catch (Exception e){}
        ed_name.setText("");
        ed_num.setText("");

        // column 개수가 많거나 숫자와 문자를 오가게 된다면 오히려 contentValues가 더 편함
        ContentValues values=new ContentValues();
        values.put("name", name); // column name, column value
        values.put("img", R.drawable.one_3+imgNum);
        long rowId=db.insert(tableName, null, values);
        Toast.makeText(this, "save to"+rowId, Toast.LENGTH_SHORT).show();

    }

    public void clickSelectAll(View v){

        //Cursor cursor=db.rawQuery("select * from "+tableName, null);
        // 그 조건이 필요 없으면 null
        // 그럼 다 가지고 옴
        // orderBy : 정렬순서
        Cursor cursor=db.query(true, tableName, null, null, null, null, null, null, null);
        if(cursor==null) return; // 테이블 존재하지 않으면 리턴

        // 대량의 데이터인 cursor 의 데이터를 listview_item.xml 모양의 뷰 객체로 생성
        // from 과 to 의 갯수는 일대일 대응이 되어야 함
        // SimpleAdapter 는 Bitmap 자원관리를 안 해줌. 이미지 사용하면 겁나 오래 걸림
        // 전화번호부가 실제로 SimpleAdapter로 만들어져있음
        //cursorAdapter=new SimpleCursorAdapter(this, R.layout.listview_item, cursor, new String[]{"name", "img"}, new int[]{R.id.txt_name, R.id.img_img}, 0);
        cursorAdapter=new MyCursorAdapter(this, R.layout.listview_item, cursor, new String[]{"name", "img"}, new int[]{R.id.txt_name, R.id.img_img}, 0);
        listView.setAdapter(cursorAdapter);

//        StringBuffer buffer=new StringBuffer();
//        while (cursor.moveToNext()){
//            int id=cursor.getInt(cursor.getColumnIndex("_id"));
//            String name=cursor.getString(cursor.getColumnIndex("name"));
//            int imgid=cursor.getInt(cursor.getColumnIndex("img"));
//
//            buffer.append("id : "+id+" name : "+name+" imgId : "+imgid+"\n");
//        }
//        Toast.makeText(this, buffer.toString(), Toast.LENGTH_LONG).show();
    }

    public void clickSelectByName(View v){

        String name=ed_name.getText().toString();
        // 이름정렬 아스키번호 기준 오름차순
        Cursor cursor=db.query(true, tableName, new String[]{"_id", "name", "img"}, "name=? or name=?", new String[]{name, "bbb"}, null, null, "name asc", null);
        if(cursor==null) return; // 테이블 존재하지 않으면 리턴

        cursorAdapter=new MyCursorAdapter(this, R.layout.listview_item, cursor, new String[]{"_id", "name", "img"}, new int[]{R.id.txt_name, R.id.img_img}, 0);
        listView.setAdapter(cursorAdapter);

//        StringBuffer buffer=new StringBuffer();
//        while (cursor.moveToNext()){
//            String name1=cursor.getString(cursor.getColumnIndex("name"));
//            int imgid=cursor.getInt(cursor.getColumnIndex("img"));
//
//            buffer.append(" name : "+name1+" imgId : "+imgid+"\n");
//        }
//        Toast.makeText(this, buffer.toString(), Toast.LENGTH_LONG).show();
    }

    // listView item click
    AdapterView.OnItemClickListener clickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//            Cursor cursor=cursorAdapter.getCursor();
            Cursor cursor= (Cursor) cursorAdapter.getItem(position);

            String name=cursor.getString(cursor.getColumnIndex("name"));
            int imgId=cursor.getInt(cursor.getColumnIndex("img"));

            Intent intent=new Intent(MainActivity.this, ItemActivity.class);

            intent.putExtra("name", name);
            intent.putExtra("img", imgId);

            CircleImageView imgView= (CircleImageView) view.findViewById(R.id.img_img);

            // Activity 옵션 담당 객체
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                // Main 의 서클뷰와
                // Item 의 IMG로 별명이 붙은 ImageView와 연결함
                ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, new Pair<View, String>(imgView, "IMG"));
                startActivity(intent, options.toBundle());

            } else {
                startActivity(intent);
            }


        }
    };

    AdapterView.OnItemLongClickListener longClickListener=new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


            final int index=position;

            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("DELETE?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // position 으로 지우려고 하면 화면에 보이는 것과 실제 db의 순서가 다를 수 있기 때문에
                    // id 또는 다른 걸로 지우려고 해야함
                    // 선택한 것의 id를 찾아서 지울것
                    Cursor cursor= (Cursor) cursorAdapter.getItem(index);
                    int _id=cursor.getInt(cursor.getColumnIndex("_id"));

                    // 삭제된 개수를 리턴값으로 받음
                    int num=db.delete(tableName, "_id=?", new String[]{_id+""});
                    Toast.makeText(MainActivity.this, "Delete "+num+"EA", Toast.LENGTH_LONG).show();

                    // 화면갱신
                    cursor=db.rawQuery("select * from "+tableName, null);
                    cursorAdapter.changeCursor(cursor);
                }
            });

            // null 주면 아무것도 안 하고 걍 꺼짐
            builder.setNegativeButton("CANCLE", null);

            builder.create().show();


            return true; // 롱클릭 다음에 클릭이 발동하는 걸 막음
        }
    };


} // class Main
