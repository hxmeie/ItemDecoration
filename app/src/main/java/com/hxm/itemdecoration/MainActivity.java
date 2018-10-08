package com.hxm.itemdecoration;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hxm.itemdecoration.decoration.LinearItemDecoration;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = findViewById(R.id.recyclerView);
//        rv.setLayoutManager(new GridLayoutManager(this, 2));

        rv.setLayoutManager(new LinearLayoutManager(this));
        LinearItemDecoration decoration=new LinearItemDecoration.Builder(this)
                .colorRes(R.color.aaaa)
//                .marginLeft(SizeUtil.dp2px(15))
//                .marginRight(SizeUtil.dp2px(15))
                .drawHeader(false)
                .drawFooter(false)
                .dividerHeight(SizeUtil.dp2px(10))
                .build();

//        TestDecoration decoration=new TestDecoration();

//        LinearItemDecoration decoration=new LinearItemDecoration(this);
        rv.addItemDecoration(decoration);
        adapter = new ItemAdapter(this);
        rv.setAdapter(adapter);
    }
}
