package com.hxm.itemdecoration;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hxm.itemdecoration.decoration.GridItemDecoration;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = findViewById(R.id.recyclerView);

//        rv.setLayoutManager(new LinearLayoutManager(this));
//        LinearItemDecoration decoration = new LinearItemDecoration.Builder(this)
//                .colorRes(R.color.aaaa)
//                .marginLeft(SizeUtil.dp2px(15))
//                .marginRight(SizeUtil.dp2px(15))
//                .drawHeader(false)
//                .drawFooter(false)
//                .dividerSize(SizeUtil.dp2px(10))
//                .build();

//        TestDecoration decoration=new TestDecoration();

//        LinearItemDecoration decoration=new LinearItemDecoration(this);

        rv.setLayoutManager(new GridLayoutManager(this, 3));
        GridItemDecoration decoration = new GridItemDecoration.Builder(this)
                .size(SizeUtil.dp2px(10))
                .verColorRes(R.color.aaaa)
                .horColorRes(R.color.aaaa)
                .showAround(true)
                .marginLeft(40)
                .marginRight(60)
                .marginTop(50)
                .marginBottom(10)
                .build();
        rv.addItemDecoration(decoration);
        adapter = new ItemAdapter(this);
        rv.setAdapter(adapter);
    }
}
