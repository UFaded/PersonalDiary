package com.example.sonata.personaldiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import android.widget.AdapterView.OnItemClickListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    private SQLiteHelper db;
    private ListView listview;
    private final static String SharedPreferencesFileName="config";

    //插入请求
    private static final int INSERT_REQUESTCODE = 1;

    //插入结果
    private static final int INSERT_RESULTCODE = 1;

    //修改请求
    private static final int UPDATE_REQUESTCODE = 2;

    //修改结果
    private static final int UPDATE_RESULTCODE = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new SQLiteHelper(this,"diary.db", null, 1);
        listview = (ListView) findViewById(R.id.list);
        query();
        registerForContextMenu(listview); //为每个项创建上下文菜单

        listview.setOnItemClickListener(new OnItemClickListener()
        {
            //单击调用
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ListView list_View = (ListView)adapterView;
                HashMap<String,String> map = (HashMap<String,String>)list_View.getItemAtPosition(position);

                Diary diary = new Diary();
                String str = String.valueOf(map.get("id"));
                int _id = Integer.parseInt(str);
                diary.setId(_id);
                diary.setTitle(map.get("title"));
                diary.setData(map.get("data"));
                diary.setText(map.get("text"));
                diary.setBitmap(map.get("bitmap"));

                Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                intent.putExtra("update_diary",diary);
                startActivityForResult(intent,UPDATE_REQUESTCODE);
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView list_View = (ListView)parent;
                HashMap<String,String> map = (HashMap<String,String>)list_View.getItemAtPosition(position);
                String str = String.valueOf(map.get("id"));
                final int _id = Integer.parseInt(str);

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示");
                builder.setMessage("您确定要删除吗？");
                builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.deleteDiary(_id);
                        query();
                        showMessage("删除成功");
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
                return true;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INSERT_REQUESTCODE:
                if (resultCode == INSERT_RESULTCODE) { // 插入请求
                    if (data != null) {
                        //接收对象
                        Diary diary = (Diary) data.getSerializableExtra("insert_diary");
                        db.addDiary(diary);
                        query();
                        showMessage("添加成功");
                    } else {
                        showMessage("添加失败");
                    }
                }
            case UPDATE_REQUESTCODE:
                if (resultCode == UPDATE_RESULTCODE)
                    if (data != null)
                    {
                        Diary diary = (Diary) data.getSerializableExtra("update_diary");
                        int id = diary.getId();
                        Log.e("mytag", String.valueOf(id));
                        if(db.queryDiaryById(diary.getId())!=null)
                        {
                            db.updateDiary(diary);
                            query();
                            showMessage("修改成功");
                        }
                        else
                        {
                            showMessage("修改失败");
                        }
                    }
                break;
            }
        }

    protected void query()
    {
        List<Map<String,Object>> items = db.queryAllDiary(); //查询所有的Diary
        SimpleAdapter adapter=new SimpleAdapter(this,items,R.layout.activity_list_item,new String[]{"title","data","text"},new int[]{R.id.Title,R.id.Data,R.id.Text});
        listview.setAdapter(adapter);

    }

    protected void querySearch(String title)
    {
        List<Map<String,Object>> items = db.queryDiaryByTitle(title); //查询所有的Diary
        SimpleAdapter adapter=new SimpleAdapter(this,items,R.layout.activity_list_item,new String[]{"title","data","text"},new int[]{R.id.Title,R.id.Data,R.id.Text});
        listview.setAdapter(adapter);
    }

    private void showMessage(String msg)
    {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.addNew:
                final Intent intent = new Intent(this,Main2Activity.class);
                startActivityForResult(intent,INSERT_REQUESTCODE);
                break;
            case R.id.search:
                final View view = getLayoutInflater().inflate(R.layout.activity_search,null);
                final AlertDialog.Builder dialog2 = new AlertDialog.Builder(MainActivity.this);
                dialog2.setTitle("搜索");
                dialog2.setView(view);
                dialog2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText search = (EditText) view.findViewById(R.id.search);
                        String title = search.getText().toString();
                        querySearch(title);
                        showMessage("查询成功");
                    }
                });
                dialog2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog2.show();
                break;
                default:
        }
        return true;
    }

}
