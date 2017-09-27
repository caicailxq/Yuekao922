package com.bawei.yuekao922;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bawei.yuekao922.Bean.Qbean;
import com.bawei.yuekao922.Dao.SQLiteDao;
import com.google.gson.Gson;
import com.limxing.xlistview.view.XListView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements XListView.IXListViewListener {
    // 默认是日间模式
    private int theme = R.style.AppTheme;
    private ViewPager pager;
    private String[] urls = new String[] {
            "https://img10.360buyimg.com/da/jfs/t4747/277/1368712300/170619/35098d7f/58f038e0N9b3a0ca5.jpg",
            "https://img14.360buyimg.com/da/jfs/t4915/21/1427207714/81116/b005bb06/58f08963Ndb295b3c.jpg",
            "https://img13.360buyimg.com/da/jfs/t4651/104/2867456043/68336/99da4c16/58f41eaeN5b614a63.jpg" };

    private XListView xlv;
    private int index=1;
    private int qndex = 0;
    private boolean isLast;
    private Myapter adapter;
    private ArrayList<Qbean.DataBean> datas;
    private SQLiteDao dao;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pager.setCurrentItem(qndex);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 判断是否有主题存储
        if(savedInstanceState != null){
            theme = savedInstanceState.getInt("theme");
            setTheme(theme);
        }
        setContentView(R.layout.activity_main);
        pager= (ViewPager) findViewById(R.id.pager);
        autoPlay();
         pager.setAdapter(new MyPagerAdapter());
        dao=new SQLiteDao(this);
       xlv= (XListView) findViewById(R.id.xlv);
        xlv.setPullRefreshEnable(true);
        xlv.setPullLoadEnable(true);
        xlv.setXListViewListener(this);

        datas=dao.findData();
        adapter=new Myapter(datas);
        xlv.setAdapter(adapter);
      doGet();



    }

    private void autoPlay() {

        new Thread(){

            @Override
            public void run() {
                super.run();

                while (true){

                    try{Thread.sleep(3000);}catch (Exception e){e.printStackTrace();}
                    //页面索引++
                    index++;
                    handler.sendEmptyMessage(0);
                }


            }
        }.start();

    }

    private void doGet() {
        RequestParams params=new RequestParams("http://api.expoon.com/AppNews/getNewsList/type/1/p/1");
        x.http().get(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson=new Gson();
                Qbean qbean = gson.fromJson(result, Qbean.class);
                List<Qbean.DataBean> list = qbean.getData();
                adapter=new Myapter(list);
                xlv.setAdapter(adapter);//加载网络数据



                if (datas.size()==0){//如果数据库里没数据
                    for (Qbean.DataBean lists:list){
                        boolean b = dao.addData(lists.getNews_title(), lists.getPic_url                                 ());//把请求到的数据添加到数据库
                        if (b){
                            Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this, "添加失敗", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public boolean onCache(String result) {
                return false;
            }
        });

    }

    public void Riye(View view){
        theme = (theme == R.style.AppTheme) ? R.style.NightAppTheme : R.style.AppTheme;
        MainActivity.this.recreate();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("theme", theme);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        theme = savedInstanceState.getInt("theme");
    }

    @Override
    public void onRefresh() {
        ++index;
        doGet();
        isLast=true;
        xlv.stopRefresh(true);
    }

    @Override
    public void onLoadMore() {
        ++index;
        doGet();
        isLast=false;
        xlv.stopLoadMore();
    }



    class MyPagerAdapter extends PagerAdapter {


        private ImageLoader imageLoader;
       private DisplayImageOptions options;



        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //产生一个显示条目
            final ImageView imageview = new ImageView(MainActivity.this);
            imageLoader = ImageLoader.getInstance();
            File file= new File(Environment.getExternalStorageDirectory(),"Bwei");
            if(!file.exists())
                file.mkdirs();

            ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(MainActivity.this)
                    .diskCache(new UnlimitedDiskCache(file))
                    .build();

            imageLoader.init(configuration);

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.mipmap.ic_launcher)
                    .cacheOnDisk(true)
                    .build();
            ImageLoader.getInstance().displayImage(urls[position % urls.length],imageview,options);
            container.addView(imageview);

            return imageview;
        }
    }


   class Myapter extends BaseAdapter{
     public List<Qbean.DataBean> list;
    private DisplayImageOptions options;
       public Myapter(List<Qbean.DataBean> list) {
           this.list = list;
           options = new DisplayImageOptions.Builder()
                   .cacheInMemory(true)//是否内存缓存
                   .cacheOnDisk(true)//是否sdcard缓存
                   .build();
       }

       @Override
       public int getCount() {
           return list.size();
       }

       @Override
       public Object getItem(int i) {
           return list.get(i);
       }

       @Override
       public long getItemId(int i) {
           return i;
       }
       @Override
       public int getItemViewType(int position) {
           if(position %2==0){
               return 0;
           }
           else{
               return 1;
           }
       }

       @Override
       public int getViewTypeCount() {
           return 2;
       }

       @Override
       public View getView(int i, View view, ViewGroup viewGroup) {
           int type = getItemViewType(i);
           switch (type){
               case 0:
                   if(view==null){
                       view=View.inflate(MainActivity.this,R.layout.item1,null);

                   }
                   ImageView i1=view.findViewById(R.id.i1);
                   ImageView i2=view.findViewById(R.id.i2);
                   ImageLoader.getInstance().displayImage(list.get(i).getPic_url(),i1,options);
                   ImageLoader.getInstance().displayImage(list.get(i).getPic_url(),i2,options);
                   break;
               case 1:

                   if(view==null){
                       view=View.inflate(MainActivity.this,R.layout.item2,null);

                   }
                   ImageView i3=view.findViewById(R.id.i3);
                   ImageView i4=view.findViewById(R.id.i4);
                   ImageView i5=view.findViewById(R.id.i5);
                   ImageView i6=view.findViewById(R.id.i6);
                   ImageLoader.getInstance().displayImage(list.get(i).getPic_url(),i3,options);
                   ImageLoader.getInstance().displayImage(list.get(i).getPic_url(),i4,options);
                   ImageLoader.getInstance().displayImage(list.get(i).getPic_url(),i5,options);
                   ImageLoader.getInstance().displayImage(list.get(i).getPic_url(),i6,options);
                   break;

           }

           return view;
       }
   }
}
