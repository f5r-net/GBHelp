package com.gb.gbhelp;

import java.io.Serializable;
import java.util.Objects;

public class FeedsModel implements Serializable, Comparable<FeedsModel> {
    private String title, summery,titleAR, summeryAR, timestamp, urls, fileName, urlsAR, fileNameAR, tag;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummery() {
        return summery;
    }

    public void setSummery(String summery) {
        this.summery = summery;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedsModel that = (FeedsModel) o;
        return Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "FeedsModel{" +
                "title='" + title + '\'' +
                ", summery='" + summery + '\'' +
                ", titleAR='" + titleAR + '\'' +
                ", summeryAR='" + summeryAR + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", urls='" + urls + '\'' +
                ", fileName='" + fileName + '\'' +
                ", urlsAR='" + urlsAR + '\'' +
                ", fileNameAR='" + fileNameAR + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }

    @Override
    public int compareTo(FeedsModel o) {
        if (this.getTag() != null && o.getTag() != null) {
            return compareTime(this,o);
        }else if (this.getTag() != null || o.getTag() != null){
            return -1;// larger
        }

        return compareTime(this,o);

    }
    private int compareTime(FeedsModel thisFeed,FeedsModel oFeed){
        long modified;
        modified = Long.parseLong(oFeed.getTimestamp()) - Long.parseLong(thisFeed.getTimestamp());
        if (modified > 0) {
            return 1;
        } else if (modified == 0) {
            return 0;
        } else {
            return -1;
        }
    }

    public String getUrlsAR() {
        return urlsAR;
    }

    public void setUrlsAR(String urlsAR) {
        this.urlsAR = urlsAR;
    }

    public String getFileNameAR() {
        return fileNameAR;
    }

    public void setFileNameAR(String fileNameAR) {
        this.fileNameAR = fileNameAR;
    }

    public String getSummeryAR() {
        return summeryAR;
    }

    public void setSummeryAR(String summeryAR) {
        this.summeryAR = summeryAR;
    }

    public String getTitleAR() {
        return titleAR;
    }

    public void setTitleAR(String titleAR) {
        this.titleAR = titleAR;
    }
}
