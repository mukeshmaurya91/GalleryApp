package com.maurya91.gallerylibrary.data;

import java.util.Date;

/**
 * Created by Mukesh Kumar Maurya on 13-07-2016 in project Gallery App.
 */
public class Image {
    //public static boolean isImageList = false;
    private String bucketName;
    private String imageUri;
    private String imageName;
    private Date dateTaken;
    private int totalCount;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "Image{" +
                "bucketName='" + bucketName + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", imageName='" + imageName + '\'' +
                ", dateTaken=" + dateTaken +
                ", totalCount=" + totalCount +
                '}';
    }
}
