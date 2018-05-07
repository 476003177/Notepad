package com.example.hao.notepad.db;

import android.content.ContentValues;
import android.util.Log;
import android.widget.Toast;

import com.example.hao.notepad.ui.AddDiaryActivity;
import com.example.hao.notepad.ui.MainActivity;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
/**
 * Created by 梁 on 2018/5/5.
 */
public class DiaryDatabaseBmobHelper{
    public DiaryData data;

    //构造函数
    public DiaryDatabaseBmobHelper(DiaryData data){
        this.data=(DiaryData)data.clone();
    }
    public DiaryDatabaseBmobHelper(ContentValues values){
        this.data=new DiaryData();
        this.data.setDate((String)values.get("date"));
        this.data.setTitle((String)values.get("title"));
        this.data.setContent((String)values.get("content"));
        this.data.setTag((String)values.get("tag"));
    }

    public DiaryData getData() {
        return data;
    }

    public void setData(DiaryData data) {
        this.data = data;
    }

    //设置data的id
    public void setDataID(String id){
        this.data.setObjectId(id);
//        this.data.setObjectId(id);
        Log.e("到1", "setDataID: "+this.data.getObjectId());
    }
    //添加数据
    public void AddData(){
        this.data.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e==null){
                    Log.i("bmob","添加成功");
                }else{
                    Log.i("bmob","添加失败"+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    //删除数据
    public void DeleteData(){
        Find_setId();
        this.data.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","删除成功");
                }else{
                    Log.i("bmob","删除失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    //修改数据
    public void UpdateData(DiaryData data){
        Find_setId();
        data.update(this.data.getObjectId(), new UpdateListener() {//objectID为需要获取数据的id
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","更新成功");
                }else{
                    Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    //匹配条件查询，返回ID
    public void Find_setId(){
        final boolean go[]=new boolean[1];
        go[0]=false;
        BmobQuery<DiaryData> query = new BmobQuery<DiaryData>();
//        query.addWhereEqualTo("title",this.data.getTitle());
        query.addWhereEqualTo("tag",this.data.getTag());
//        query.addWhereEqualTo("date",this.data.getDate());
        query.findObjects(new FindListener<DiaryData>() {
            @Override
            public void done(List<DiaryData> object, BmobException e) {
                if(e==null){
                    String deleteId=object.get(0).getObjectId();
                    Log.i("bmob","查询id成功："+deleteId);
                    setDataID(deleteId);
                }else{
                    Log.i("bmob","查询id失败："+e.getMessage()+","+e.getErrorCode());
                }
                go[0]=true;
            }
        });
        while (!go[0]){
            try {
                wait(1000);
            }catch(Exception e) {
                e.printStackTrace();
            }
        };
        Log.e("到", "Find_setId: "+this.data.getObjectId());
    }
}
