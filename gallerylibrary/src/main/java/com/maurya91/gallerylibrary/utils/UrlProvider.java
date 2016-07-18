package com.maurya91.gallerylibrary.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.maurya91.gallerylibrary.data.Image;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mukesh Kumar Maurya on 13-07-2016 in project Gallery App.
 */
public class UrlProvider {
    private Context mContext;
    private String[] mPROJECTION =
            new String[] {
                    Media.BUCKET_ID,
                    Media.BUCKET_DISPLAY_NAME,
                    Media.DATE_TAKEN,
                    Media.DATA,
                    Media.TITLE
            };
    private String mSortOrder= " DESC";
//            { "bucket_id", "bucket_display_name", "datetaken", "_data" };
    private final Uri mUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    public UrlProvider(Context mContext) {
        this.mContext = mContext;
    }
    public ArrayList<Image> imagesInAlbum(String bucketName)
    {
        Cursor cursor=null;
        ArrayList<Image> list = new ArrayList<>();
        try
        {
            cursor = getCursor(Media.BUCKET_DISPLAY_NAME+" = \"" + bucketName + "\"");
            int dataColumn = cursor.getColumnIndex(Media.DATA);
            int titleColumn=cursor.getColumnIndex(Media.TITLE);
                if ((cursor != null) && (cursor.moveToFirst()))

                {
                    do
                    {
                        Image image = new Image();
                        String data = cursor.getString(dataColumn);
                        String title =cursor.getString(titleColumn);
                        image.setImageUri(data);
                        image.setImageName(title);
                        list.add(image);
                    } while (cursor.moveToNext());

                }
                cursor.close();
                return list;
        }
        catch (Exception paramString)
        {
            paramString.printStackTrace();
        }
        return null;
    }
    private Cursor getCursor(String selection)
    {
        return this.mContext.getContentResolver().query(this.mUri, null, selection, null, Media.DATE_TAKEN +mSortOrder);
    }

    private int photoCountByAlbum(String bucketName)
    {
        Cursor cursor = null;
        String str = null;
        try
        {
            cursor= getCursor(Media.BUCKET_DISPLAY_NAME+" = \"" + bucketName + "\"");
            if (cursor.getCount() > 0)
            {
                return cursor.getCount();
            }
        }
        catch (Exception e)
        {
                e.printStackTrace();
        }
        finally
        {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }
    public ArrayList<Image> albumList()
    {
        ArrayList<Image> albumList = new ArrayList<Image>();
        String selection= "1) GROUP BY 1,(2";
        String sortOrder="MAX("+ Media.DATE_TAKEN+") DESC";
        try
        {
            Cursor cursor = this.mContext.getContentResolver().query(this.mUri, this.mPROJECTION, selection, null, sortOrder);
            if ((cursor != null) && (cursor.moveToFirst()))
            {
                int bucketDisplayNameColumn = cursor.getColumnIndex(Media.BUCKET_DISPLAY_NAME);
                int dataColumn = cursor.getColumnIndex(Media.DATA);
                int titleColumn=cursor.getColumnIndex(Media.TITLE);
                do
                {
                    String bucketName = cursor.getString(bucketDisplayNameColumn);
                    String data = cursor.getString(dataColumn);
                    String title =cursor.getString(titleColumn);
                    if ((bucketName != null) && (bucketName.length() > 0))
                    {
                        Image image = new Image();
                        image.setBucketName(bucketName);
                        image.setImageUri(data);
                        image.setImageName(title);
                        image.setTotalCount(photoCountByAlbum(bucketName));
//                        Log.d("ZZZZZZZZZZ","Image:::>"+image.toString());
                        albumList.add(image);
                    }
                } while (cursor.moveToNext());
            }
            if (cursor != null) {
                cursor.close();
            }
            return albumList;
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
        return albumList;
    }
}
