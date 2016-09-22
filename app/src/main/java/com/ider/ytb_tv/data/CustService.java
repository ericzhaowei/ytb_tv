package com.ider.ytb_tv.data;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;

import com.ider.ytb_tv.ui.fragment.MySearchFragment;
import com.ider.ytb_tv.utils.JsonParser;
import com.ider.ytb_tv.utils.OkhttpManager;
import com.ider.ytb_tv.utils.PropertyReader;
import com.ider.ytb_tv.utils.Utils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ider-eric on 2016/8/11.
 */
public class CustService extends Service {

    private String TAG = "CustService";


    private static final String ID_YTB_IDER_CAHNNEL = "UCRD2n56QwpiI02MNigUpQFA";
    public static final String ID_YTB_KEY = "AIzaSyCMIntoeYd4PzFdLBZUrBgQjVbIeH1-wI0";
    private static String PAGER_TOKEN_STR = "&pageToken=%s";
    private static String YTB_API_BASE = "https://www.googleapis.com/youtube/v3/";
    private static String YTB_API_METHOD = "%s?";
    private static String YTB_API_PART = "part=%s&";
    private static String YTB_API_RESOURCE_ID = "id=%s&";
    private static String YTB_API_FIELDS = "fields=%s&";
    private static String YTB_API_KEY = "key=%s";


    private static String YTB_PLAYLIST_VIDEOS = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet,contentDetails,status&maxResults=%d%s&playlistId=%s&fields=items(snippet(position,title,resourceId/videoId,thumbnails(high/url,medium/url))),nextPageToken,pageInfo/totalResults&key=%s";
    private static String YTB_POPULAR_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails&chart=mostPopular&maxResults=15%s&regionCode=%s&videoCategoryId=17&fields=nextPageToken,items(id,contentDetails(duration),snippet(thumbnails(high/url,medium/url),title)),prevPageToken&key=%s";
    private static String YTB_PLAYLIST_URL = "https://www.googleapis.com/youtube/v3/playlists?part=snippet,contentDetails&channelId=%s&fields=items(id,snippet/title,contentDetails)&key=%s";
    private static String YTB_RELATE_VIDEOS = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10%s&relatedToVideoId=%s&type=video&fields=items(id/videoId,snippet(thumbnails(high/url,medium/url),title)),nextPageToken&key=%s";
    private static String YTB_SEARCH_PLAYLIST = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=15%s&q=%s&type=playlist&fields=items(id/playlistId,snippet/title),nextPageToken&key=%s";
    private static String YTB_SEARCH_VIDEO = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=15&order=viewCount%s&q=%s&type=video&videoDefinition=high&fields=items(id/videoId,snippet(thumbnails(high/url,medium/url),title)),nextPageToken&key=%s";

    private static String MARKET_SEARCH = "http://ws75.aptoide.com/api/7/apps/search/store_name/%s/query=%s/limit=8";
    private static String MARKET_ID = "ider-market";


    MyBinder mBinder = new MyBinder();

    public CustService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class MyBinder extends Binder {
        public CustService getService() {
            return CustService.this;
        }
    }



    public String getPopular(String pageId) {
        String pageToken;
        String region = PropertyReader.getString(this, "ro.product.locale.region");
        pageToken = pageId == null ? "" : String.format(PAGER_TOKEN_STR, pageId);
        String popularUrl = createPopularUrl(pageToken, region, ID_YTB_KEY);
        try {
            return OkhttpManager.getInstance().sendGet(popularUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public String getEditChoice(String pageId) {
        try {
            String plResult = getPlaylistInfo();
            Map map = JsonParser.parseEditorChoiceId(plResult);
            String playlist_id = (String) map.get("id");

            return retreiveVideosInPlaylist(playlist_id, pageId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String retreiveVideosInPlaylist(String playlistId, String pageId) {
        String pagerToken = pageId == null ? "" : String.format(PAGER_TOKEN_STR, pageId);
        String url = createEditorChoiceUrl(pagerToken, playlistId, ID_YTB_KEY);
        Utils.log(TAG, url);
        try {
            return OkhttpManager.getInstance().sendGet(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList setVideosDuration(ArrayList list) {
        try {
            String url = createVideosDurationUrl(list);
            String result = OkhttpManager.getInstance().sendGet(url);

            return JsonParser.acceptVideosDuration(list, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public Movie setVideoDescription(Movie movie) {
        try {
            String url = createVideoDescriptionUrl(movie.getId());
            Utils.log(TAG, url);
            String result = OkhttpManager.getInstance().sendGet(url);
            return JsonParser.getFullDescriptionVideo(result, movie);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movie;
    }


    public String getRelateVideos(String pageId, String videoId) {
        try{
            String pageToken;
            pageToken = pageId == null ? "" : String.format(PAGER_TOKEN_STR, pageId);
            String relateUrl = createRelateUrl(pageToken, videoId, ID_YTB_KEY);
            return OkhttpManager.getInstance().sendGet(relateUrl);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSearchPlaylist(String pageToken, String query) {
        pageToken = pageToken == null ? "" : String.format(PAGER_TOKEN_STR, pageToken);
        String url = String.format(YTB_SEARCH_PLAYLIST, pageToken, query, ID_YTB_KEY);
        try {
            return OkhttpManager.getInstance().sendGet(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSearchVideos(String pageToken, String query) {
        pageToken = pageToken == null ? "" : String.format(PAGER_TOKEN_STR, pageToken);
        String url = String.format(YTB_SEARCH_VIDEO, pageToken, query, ID_YTB_KEY);
        try{
            return OkhttpManager.getInstance().sendGet(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSearchApps(String query) {
        String url = String.format(MARKET_SEARCH, MARKET_ID, query);

        try {
            return OkhttpManager.getInstance().sendGet(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String getPlaylistInfo() throws IOException {
        String url = String.format(YTB_PLAYLIST_URL, ID_YTB_IDER_CAHNNEL, ID_YTB_KEY);
        return OkhttpManager.getInstance().sendGet(url);
    }

    private String createPopularUrl(String pagerToken, String region, String key) {
        return String.format(YTB_POPULAR_URL, pagerToken, region, key);
    }

    private String createEditorChoiceUrl(String pagerToken, String playlistId, String key) {
        return String.format(YTB_PLAYLIST_VIDEOS, 15, pagerToken, playlistId, key);
    }

    private String createRelateUrl(String pageToken, String videoId, String key) {
        return String.format(YTB_RELATE_VIDEOS, pageToken, videoId, key);
    }

    private String createVideosDurationUrl(ArrayList list) {
        StringBuffer ids = new StringBuffer();
        for(int i = 0; i < list.size(); i++) {
            ids.append(((Movie)list.get(i)).getId());
            if(i != list.size()-1) {
                ids.append(",");
            }
        }
        return YTB_API_BASE
                + String.format(YTB_API_METHOD, "videos")
                + String.format(YTB_API_PART, "contentDetails")
                + String.format(YTB_API_RESOURCE_ID, ids)
                + String.format(YTB_API_FIELDS, "items(id,contentDetails(duration))")
                + String.format(YTB_API_KEY, ID_YTB_KEY);
    }

    private String createVideoDescriptionUrl(String videoId) {
        return YTB_API_BASE
                + String.format(YTB_API_METHOD, "videos")
                + String.format(YTB_API_PART, "snippet")
                + String.format(YTB_API_RESOURCE_ID, videoId)
                + String.format(YTB_API_FIELDS, "items(id,snippet(publishedAt,description))")
                + String.format(YTB_API_KEY, ID_YTB_KEY);
    }


}
