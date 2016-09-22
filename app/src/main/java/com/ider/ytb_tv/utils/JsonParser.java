package com.ider.ytb_tv.utils;


import com.ider.ytb_tv.data.AppEntry;
import com.ider.ytb_tv.data.Movie;
import com.ider.ytb_tv.data.PlayList;
import com.ider.ytb_tv.data.ResourceEntry;
import com.ider.ytb_tv.ui.fragment.MySearchFragment;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ider-eric on 2016/8/12.
 */
public class JsonParser {

    public static ArrayList<ResourceEntry> parseForApp(String json) {
        if(json == null) {
            return null;
        }
        ArrayList<ResourceEntry> apps = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(json);
            JSONArray array = root.getJSONObject("datalist").getJSONArray("list");
            Utils.log(MySearchFragment.TAG, "arrays.length = " + array.length());
            for(int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                AppEntry app = new AppEntry();
                app.id = item.getInt("id");
                app.name = item.getString("name");
                app.pkgname = item.getString("package");
                app.vername = item.getJSONObject("file").getString("vername");
                app.vercode = item.getJSONObject("file").getInt("vercode");
                app.iconurl = item.getString("icon");
                app.graphics = item.getString("graphic");
                apps.add(app);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.log(MySearchFragment.TAG, "apps.size = " + apps.size());
        return apps;
    }


    public static ArrayList parsePopular(String json) {

        if(json == null) {
            return null;
        }

        ArrayList<Movie> list = new ArrayList<>();

        try {
            JSONObject rootJson = new JSONObject(json);
            JSONArray items = rootJson.getJSONArray("items");
            for(int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                Movie movie = new Movie();
                movie.setId(item.getString("id"));
                movie.setTitle(item.getJSONObject("snippet").getString("title"));
                try {
                    movie.setCardImageUrl(item.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url"));
                    movie.setBgUrl(item.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                movie.setDuration(formatTime(item.getJSONObject("contentDetails").getString("duration")));
                list.add(movie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String getNextPageToken(String json) {
        try {
            JSONObject rootJson = new JSONObject(json);
            return rootJson.getString("nextPageToken");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getTotalResult(String json) {
        try {
            JSONObject rootJson = new JSONObject(json);
            return rootJson.getJSONObject("pageInfo").getInt("totalResults");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static ArrayList<Movie> parseEditorChoice(String json) {
        if(json == null) {
            return null;
        }
        ArrayList<Movie> list = new ArrayList<>();
        try{
            JSONObject rootJson = new JSONObject(json);
            JSONArray items = rootJson.getJSONArray("items");
            for(int i = 0; i < items.length(); i++) {
                JSONObject itemSnippet = items.getJSONObject(i).getJSONObject("snippet");
                Movie movie = new Movie();
                movie.setId(itemSnippet.getJSONObject("resourceId").getString("videoId"));
                movie.setTitle(itemSnippet.getString("title"));
                try {
                    movie.setCardImageUrl(itemSnippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url"));
                    movie.setBgUrl(itemSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                list.add(movie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public static ArrayList parseRelateOrSearchVideos(String json) {
        ArrayList<ResourceEntry> list = new ArrayList<>();
        try {
            JSONObject rootJson = new JSONObject(json);
            JSONArray items = rootJson.getJSONArray("items");
            for(int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                Movie movie = new Movie();
                movie.setId(item.getJSONObject("id").getString("videoId"));
                movie.setTitle(item.getJSONObject("snippet").getString("title"));
                try {
                    movie.setCardImageUrl(item.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url"));
                    movie.setBgUrl(item.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("high").getString("url"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                list.add(movie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Map parseEditorChoiceId(String json) {
        if(json == null) {
            return null;
        }
        try {
            JSONObject rootJson = new JSONObject(json);
            JSONArray items = rootJson.getJSONArray("items");
            for(int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String title = item.getJSONObject("snippet").getString("title");
                if(title.toLowerCase().contains("ider")) {
                    Map map = new HashMap();
                    map.put("id", item.getString("id"));
                    map.put("itemCount", item.getJSONObject("contentDetails").getInt("itemCount"));
                    return map;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ArrayList<ResourceEntry> parsePlaylist(String json) {
        ArrayList<ResourceEntry> list = new ArrayList<>();
        try {

            JSONObject rootJson = new JSONObject(json);
            JSONArray items = rootJson.getJSONArray("items");
            for(int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                PlayList playlist = new PlayList();
                playlist.setId(item.getJSONObject("id").getString("playlistId"));
                playlist.setTitle(item.getJSONObject("snippet").getString("title"));
                list.add(playlist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public static ArrayList acceptVideosDuration(ArrayList list, String json) {
        try {
            JSONObject rootJson = new JSONObject(json);
            JSONArray items = rootJson.getJSONArray("items");
            for(int i = 0; i < items.length(); i++) {
                ((Movie)list.get(i)).setDuration(formatTime(items.getJSONObject(i).getJSONObject("contentDetails").getString("duration")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Movie getFullDescriptionVideo(String json, Movie movie) {
        try {
            JSONObject rootJson = new JSONObject(json);
            JSONArray items = rootJson.getJSONArray("items");
            String fullDes = items.getJSONObject(0).getJSONObject("snippet").getString("description");
            String description;
            if(!fullDes.contains("\n")) {
                description = fullDes;
            } else {
                String[] dess = fullDes.split("\n");
                description = dess.length > 1 ? dess[0] + "\n" + dess[1] : dess[0];
            }

            movie.setDescription(description);
            movie.setPublishAt(formatPublishTime(items.getJSONObject(0).getJSONObject("snippet").getString("publishedAt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movie;
    }

    private static String formatTime(String ptTime) {
        int ptH, ptM, ptS;

        if(ptTime.contains("H") && ptTime.contains("M") && ptTime.contains("S")) {
            ptH = Integer.parseInt(ptTime.substring(2, ptTime.indexOf("H")));
            ptM = Integer.parseInt(ptTime.substring(ptTime.indexOf("H")+1, ptTime.indexOf("M")));
            ptS = Integer.parseInt(ptTime.substring(ptTime.indexOf("M")+1, ptTime.indexOf("S")));
            return ptH + ":" + ptM + ":" + formatNum(ptS);
        } else if (!ptTime.contains("H") && ptTime.contains("M") && ptTime.contains("S")) {
            ptM = Integer.parseInt(ptTime.substring(2, ptTime.indexOf("M")));
            ptS = Integer.parseInt(ptTime.substring(ptTime.indexOf("M")+1, ptTime.indexOf("S")));
            return ptM + ":" + formatNum(ptS);
        } else if (!ptTime.contains("H") && !ptTime.contains("M") && ptTime.contains("S")) {
            ptS = Integer.parseInt(ptTime.substring(2, ptTime.indexOf("S")));
            return "00:" + formatNum(ptS);
        } else if (!ptTime.contains("H") && ptTime.contains("M") && !ptTime.contains("S")) {
            ptM = Integer.parseInt(ptTime.substring(2, ptTime.indexOf("M")));
            return ptM + ":" + "00";
        } else if (ptTime.contains("H") && !ptTime.contains("M") && ptTime.contains("S")) {
            ptH = Integer.parseInt(ptTime.substring(2, ptTime.indexOf("H")));
            ptS = Integer.parseInt(ptTime.substring(ptTime.indexOf("H")+1, ptTime.indexOf("S")));
            return ptH + ":00:" + formatNum(ptS);
        } else if (ptTime.contains("H") && !ptTime.contains("M") && !ptTime.contains("S")) {
            ptH = Integer.parseInt(ptTime.substring(2, ptTime.indexOf("H")));
            return ptH + ":00:00";
        } else if (ptTime.contains("H") && ptTime.contains("M") && !ptTime.contains("S")) {
            ptH = Integer.parseInt(ptTime.substring(2, ptTime.indexOf("H")));
            ptM = Integer.parseInt(ptTime.substring(ptTime.indexOf("H")+1, ptTime.indexOf("M")));
            return ptH + ":" + ptM + ":" + "00";
        }
        return "--:--";

    }

    private static String formatNum(int msh) {
        return msh >= 10 ? String.valueOf(msh) : "0" + msh;
    }

    private static String formatPublishTime(String publishAt) {
        publishAt = publishAt.substring(0, publishAt.lastIndexOf("T"));
        return publishAt;
    }


}
