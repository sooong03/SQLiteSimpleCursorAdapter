package kr.anima.xd.s.sqlitesimplecursoradapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by alfo6-10 on 7/5/2017.
 */

public class MyCursorAdapter extends SimpleCursorAdapter {

    public MyCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    // 뷰의 값을 연결할 때 자동으로 실행되는 메소드
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //super.bindView(view, context, cursor);

        TextView txt_name= (TextView) view.findViewById(R.id.txt_name);
        CircleImageView img_img= (CircleImageView) view.findViewById(R.id.img_img);

        // bindView가 호출될 때
        // Cursor는 이미 한 칸 움직여서 준비상태. 바로 값을 받아오면 됨
        txt_name.setText(cursor.getString(cursor.getColumnIndex("name")));
        int imgId=cursor.getInt(cursor.getColumnIndex("img"));
        Glide.with(context).load(imgId).into(img_img);
    }

}
