package kr.anima.xd.s.sqlitesimplecursoradapter;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ItemActivity extends AppCompatActivity {

    ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        img= (ImageView) findViewById(R.id.img);

        Intent intent=getIntent();
        String name=intent.getStringExtra("name");
        int imgId=intent.getIntExtra("img", R.drawable.one_ace);

        getSupportActionBar().setTitle(name);
        Glide.with(this).load(imgId).into(img);

        // ImageView 에게 별명 부여
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            img.setTransitionName("IMG");
        }

        // 뒤로가기 버튼 누르면 Main으로 돌아가기
        // manifests 에서 itemActivity의 부모를 MainActivity로 설정해야 실행가능
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 이 때 UP버튼으로 뒤로가기를 실행하면
        // MainActivity를 종료했다가 새롭게 Activity를 실행함 == 새롭게 갱신됨
        // UP 버튼은 사용하되, MainActivity가 새롭게 시작하지는 않게 설정

    }

    // 제목줄에 있는 옵션메뉴 아이템을 클릭 했을 때 호출되는 메소드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                // 그냥 finish는 애니메이션 전환효과를 볼 수가 없음
//                finish();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
                return true;
        }

        return super.onOptionsItemSelected(item); // Super로 가는 순간 Activity 갱신됨

    }

    // Activity 결과값 받을 때 자동 호출
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case 10:
                if(resultCode==RESULT_OK){ // 사진선택 ok
                    Uri uri=data.getData();
                    Glide.with(this).load(uri).into(img);

                    // 변경사항 db update
                    String name=getSupportActionBar().getTitle().toString();
                    DBOpnner opnner=new DBOpnner(this, "member", null, 1);
                    SQLiteDatabase db=opnner.getWritableDatabase();

                    ContentValues values=new ContentValues();
                    values.put("name", name);
                    // 선택한 이미지를 넣어야 맞는데, 지금은 꼬여있어서..
                    // 실제 선택은 기기 안에 있는 이미지이기 때문에 Uri
                    // 업뎃되는 이미지는 Uri를 string으로 바꿀 수 없어서 임의로 바뀔 이미지를 넣어둠..
                    values.put("img", R.drawable.one_zoro);

                    // 총 몇개의 row 가 업뎃되었는지 그 개수를 리턴함
                    int num=db.update("member", values, "name=?", new String[]{name});
                    Toast.makeText(this, num+"EA Chaneged", Toast.LENGTH_LONG).show();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void clickChange(View v){

        // 사진선택화면
        // 19 ver 이상일 때 이하와 같은 코드로 작성
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, 10);

    }

}
