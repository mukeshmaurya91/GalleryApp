package com.maurya91.gallerylibrary.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maurya91.gallerylibrary.R;
import com.maurya91.gallerylibrary.data.Image;
import com.maurya91.gallerylibrary.utils.ImageLoader;
import com.maurya91.gallerylibrary.utils.OffsetDecoration;
import com.maurya91.gallerylibrary.utils.UrlProvider;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private static final int SPAN_COUNT = 2;
    private static final int SPACE_OFFSET = 2;
    private RecyclerView mRecyclerView;
    private GalleryRecyclerAdapter mAdapter;
    private static boolean isAlbum;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProgressBar= (ProgressBar) findViewById(R.id.progress_bar);
        // init RecyclerView
        mRecyclerView= (RecyclerView) findViewById(R.id.gallery_recycler_view);

        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this,SPAN_COUNT));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(SPAN_COUNT,StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.addItemDecoration(new OffsetDecoration(SPACE_OFFSET));
        mAdapter= new GalleryRecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        loadAlbumList();
    }
    private void loadAlbumList(){
        isAlbum=true;
        setTitle("Albums");
        new ImageUrlWorker(this).execute("");
    }
    private void loadImagesList(String bucket){
        isAlbum=false;
        setTitle(bucket);
        new ImageUrlWorker(this).execute(bucket);
    }
    private void setTitle(String title){
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(title);
            if (isAlbum)
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            else
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public void onBackPressed() {
        if (isAlbum) {
            super.onBackPressed();
        }else{
          loadAlbumList();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id ==android.R.id.home) {
            loadAlbumList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    class ImageUrlWorker extends AsyncTask<String,Void,ArrayList<Image>> {
        Context context;

        public ImageUrlWorker(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Image> doInBackground(String... params) {
            if (params[0].equalsIgnoreCase(""))
               return new UrlProvider(context).albumList();
            else
                return new UrlProvider(context).imagesInAlbum(params[0]);

        }

        @Override
        protected void onPostExecute(ArrayList<Image> imageList) {
            super.onPostExecute(imageList);
//            Log.d("ZZZZZZZZZZ","list:::>"+imageList.toString());
            mProgressBar.setVisibility(View.INVISIBLE);
           if (imageList!=null)
               mAdapter.updateList(imageList);

        }
    }
    class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.MyViewHolder>{
        List<Image> mImageList;//= new ArrayList<>();
       ImageLoader mLoader;// = new ImageLoader(GalleryActivity.this);
        Context mContext;

        public GalleryRecyclerAdapter(Context context) {
            this.mContext = context;
            mImageList= new ArrayList<>();
            mLoader = new ImageLoader(context);

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_layout,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            mLoader.loadBitmap(mImageList.get(position).getImageUri(), holder.imageView);
            if (isAlbum) {
                //show Text strip
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.albumNameText.setText(mImageList.get(position).getBucketName());
                int count =mImageList.get(position).getTotalCount();
                String countText=mContext.getResources().getQuantityString(R.plurals.image_count,count,count);
                holder.countText.setText(countText);
            }else{
                //hide the text strip
             holder.linearLayout.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mImageList==null?0:mImageList.size();
        }

        public void updateList(List<Image> imageList) {
            this.mImageList=imageList;
            notifyDataSetChanged();
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView albumNameText,countText;
            ImageView imageView;
            LinearLayout linearLayout;

            public MyViewHolder(View itemView) {
                super(itemView);
                albumNameText= (TextView) itemView.findViewById(R.id.album_name_text);
                countText= (TextView) itemView.findViewById(R.id.count_text);
                imageView= (ImageView) itemView.findViewById(R.id.image_view);
                linearLayout= (LinearLayout) itemView.findViewById(R.id.text_strip);
                imageView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (isAlbum) {
                    isAlbum=false;
                    loadImagesList(mImageList.get(getAdapterPosition()).getBucketName());
                }else {
                    Intent intent= new Intent(Intent.ACTION_VIEW);

                    intent.setDataAndType(Uri.parse("file://"+mImageList.get(getAdapterPosition()).getImageUri()),"image/*");
                    if (intent.resolveActivity(getPackageManager())!=null){
                        startActivity(intent);
                    }
                }
            }
        }
    }
}
