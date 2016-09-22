package com.ider.ytb_tv.data;

/**
 * Created by ider-eric on 2016/8/17.
 */
public class PlayList extends ResourceEntry {
    public String title;
    public String id;

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        return ((PlayList)o).id.equals(id);
    }
}
