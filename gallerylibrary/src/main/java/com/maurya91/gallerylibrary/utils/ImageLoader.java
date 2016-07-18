package com.maurya91.gallerylibrary.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.ImageView;

import com.maurya91.gallerylibrary.R;

import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * Created by Mukesh Kumar Maurya on 16-07-2016 in project Gallery App.
 */
public class ImageLoader {

    private Bitmap mPlaceHolderBitmap;
    private Context mContext;
    private ImageCache mCache;
    private static final int REQ_HEIGHT=128;
    private static final int REQ_WIDTH=128;

    public ImageLoader(Context context) {
        this.mContext = context;
        setPlaceHolderImage();
        mCache= new ImageCache(context);
    }
private  void setPlaceHolderImage(){
    mPlaceHolderBitmap=BitmapFactory.decodeResource(mContext.getResources(),R.drawable.place_holder_image);
}
    public void loadBitmap(String path, ImageView imageView) {
        final Bitmap bitmap = mCache.getBitmapFromMemCache(path);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }else {
            if (cancelPotentialWork(path, imageView)) {
                final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(mContext.getResources(), mPlaceHolderBitmap, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(path);
            }
        }
    }
    public static boolean cancelPotentialWork(String data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.path;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == null || !bitmapData.equals(data)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }
    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String path = null;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            path=params[0];
            // Check disk cache in background thread
            Bitmap bitmap = mCache.getBitmapFromDiskCache(path);
            if (bitmap==null)
            bitmap= MkUtils.decodeSampledBitmapFromUrl( path, REQ_WIDTH, REQ_HEIGHT);
            mCache.addBitmapToMemoryCache(path, bitmap);
            return bitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable( Resources resources,Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super( resources,bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }
        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
}
