package mywins.theandroiddev.com.mediacollections;

import java.io.Serializable;

/**
 * Created by jakub on 17.10.17.
 */

public class Audio implements Serializable {
    private long id;
    private String title;
    private int position;
    private String path;


    public Audio(String title, String path) {

        this.id = -1;
        this.position = -1;
        this.title = title;
        this.path = path;
    }

    public static Audio createFromAssets(String assetName) {
        return new Audio(assetName, assetName);
    }

    public static Audio createFromAssets(String title, String assetName) {
        return new Audio(title, assetName);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}