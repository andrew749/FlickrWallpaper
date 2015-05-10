package com.andrew749.flickrwallpaper;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URL;
import java.util.List;

/**
 * Created by andrewcodispoti on 2015-05-09.
 */
public class FlickrResult implements LinkFollowingCallback{
    private String imageName;
    private URL url=null;
    private long id;
    private static String REST_ENDPOINT = "https://api.flickr.com/services/rest/";

    //simple model for a result containing the image and the url of the image.
    public FlickrResult(String name,long id){
        this.imageName=name;
        this.id=id;
        FollowUrl linkFollower=new FollowUrl(this);
        linkFollower.execute(id);
    }
    public long getId(){
        return id;
    }
    public void setId(long id){
        this.id=id;
    }

    public URL getUrl(){
        return  url;
    }

    public String getName(){
        return  imageName;
    }

    @Override
    public void doneFollowing(URL url) {
        this.url=url;
    }

    private class FollowUrl extends AsyncTask<Long, Void,URL>{
        LinkFollowingCallback callback;
        private class SizesParent{
            SizesResult result;
        }
        private class SizesResult{
            List<Size> imageSizes;
        }
        public class Size{
            String label;
            int height,width;
            String source;
        }
        public FollowUrl(LinkFollowingCallback callback) {
            this.callback=callback;
        }

        @Override
        protected URL doInBackground(Long... longs) {

            String queryParameter = "?method=flickr.photos.getSizes&api_key=6c30fdb8388402770932f08d6e367939&format=json&nojsoncallback=1&photo_id="+longs[0];
            Gson gson=new GsonBuilder().create();
            URL largestImage=null;
            try {
                String json=FlickrSearcher.readUrl(new URL(REST_ENDPOINT+queryParameter));
                SizesParent sizesParent=gson.fromJson(json, SizesParent.class);
                SizesResult sizesResult=sizesParent.result;
                List<Size> sizes=sizesResult.imageSizes;
                for(Size size:sizes){
                    largestImage=new URL(size.source);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return largestImage;
        }

        @Override
        protected void onPostExecute(URL url) {
            super.onPostExecute(url);
            callback.doneFollowing(url);
        }
    }

}
