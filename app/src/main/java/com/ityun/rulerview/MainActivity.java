package com.ityun.rulerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public RulerView rulerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rulerview = findViewById(R.id.rulerview);
        rulerview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        List<String> stringList = new ArrayList<>();
        stringList.add("480*320");
        stringList.add("640*480");
        stringList.add("720*480");
        stringList.add("960*640");
        stringList.add("1080*720");
        stringList.add("1280*960");
        rulerview.setDataString(stringList);
        rulerview.setCenterPointNum(10);
        rulerview.setPosition(2);
    }
}
