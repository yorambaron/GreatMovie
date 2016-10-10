package com.yboweb.bestmovie;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yboweb.bestmovie.androidnavigationdrawerexample.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

;


public class ScrollingActivity extends AppCompatActivity {

    static LinearLayout topLinearLayout;
    private CustomPagerAdapter mGridAdapter;
    //private ArrayList<ImageItem> mGridData;
    static private ProgressBar mProgressBar;
    ViewPager mViewPager;
    private String imageUrl;
    private String rating;
    private String voting;
    private String title;
    private String mId;
    private String mapString;
    private String imagesList;
    private Activity activity = this;
    private static String  omdbId = null;
    private static String  shareTitle = null;
    private Handler mHandler;
    MovieDetails movieDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);


        // EventBus.getDefault().register(this);
        Intent intentExtras = getIntent();
        Bundle extraBundle = intentExtras.getExtras();


        Bundle args = getIntent().getExtras();
        mId = args.getString("id");

        imageUrl = args.getString("image_url");
        rating = args.getString("rating");
        voting = args.getString("voting");
        title = args.getString("title");
        mapString = args.getString("map");
        imagesList = args.getString("images");

        Log.d("Scrolling", "image_url:" + args.getString("image_url") + " rating:" + args.getString("rating") + " voting:" + args.getString("voting") + " title:" + args.getString("title"));
        Log.d("scrolling", "Map:" + args.getString("map"));
        Log.d("scrolling", "Images:" + args.getString("images"));

        final Activity activity = this;


        int idInt = Integer.parseInt(mId);

        ImdbConstants imdbConstants = ImdbConstants.getInstance();
        String url = imdbConstants.getImagesUrl(idInt);



            /* mGridView = (ListView) findViewById(R.id.scroll_detail_id1); */
        mViewPager = (ViewPager) findViewById(R.id.scroll_detail_id);
        mProgressBar = (ProgressBar) findViewById(R.id.detail_progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        //Initialize with empty data
        mGridAdapter = new CustomPagerAdapter(activity);


        topLinearLayout = new LinearLayout(this);


        Log.d("DET_IMAGES", "URL:" + url);
        final RowImagesInputData m = new RowImagesInputData(url, activity, idInt, this, null, imdbConstants.GET_DETAILS);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        topLinearLayout.setLayoutParams(params);
        topLinearLayout.setOrientation(LinearLayout.HORIZONTAL);


        if (mapString != null && imagesList != null) {


            // We have the data in memory (favorites).
            // This part draws the horizontal picture images


            List<String> items = Arrays.asList(imagesList.split("\\s*,\\s*"));
            for (int i = 0; i < items.size(); i++) {
                String fullImageURL = items.get(i);
                ImageItem item = new ImageItem();
                item.setImage(fullImageURL);
                item.setTitle("title");
                item.setVoting("voting");
                item.setRating("rating");
                item.setType(ImageItem.SCROLL_HORIZONTAL_ITEM);
                mGridAdapter.addItem(item);


            }

            mViewPager.setClipToPadding(false);
            mViewPager.setPadding(0, 0, 0, 0);
            mViewPager.setAdapter(mGridAdapter);

            mProgressBar.setVisibility(View.INVISIBLE);

            GetDetails getDetails = new GetDetails();
            HashMap<String, String> map = new Gson().fromJson(mapString, new TypeToken<HashMap<String, String>>() {
            }.getType());
            Log.d("Scrolling", "Number of items:" + items.size());
            LinearLayout layout = (LinearLayout) activity.findViewById(R.id.scroll_root_id);

            // This part draws the rest of the page.
            getDetails.drawDetails(layout, this, map, items.size(), items);


        } else {



            Thread background = new Thread(new Runnable() {



                // After call for background.start this run method call

                public void run() {
                    try {
                        Looper.prepare();

                        String SetServerString = "Hello world I am here";
                        threadMsg(SetServerString);

                    } catch (Throwable t) {
                        // just end the background thread

                        Log.i("Animation", "Thread  exception " + t);
                    }
                }


                private void threadMsg(String msg) {

                    if (!msg.equals(null) && !msg.equals("")) {
                        Message msgObj = handler.obtainMessage();

                        ReadURL imageUrl = new ReadURL();


                        Boolean binary = Looper.myLooper() == Looper.getMainLooper();
                        Log.d("threadMsg", "before readUrl" + "Mainthread is:" +binary);

                        String url = m.getURL();

                        List<Map<String, String>> myResult = new ArrayList<Map<String, String>>();

                        int mContent = m.getType();
                        ReadURL readURL = new ReadURL();
                        StringBuffer readBuffer = readURL.Read(url);

                        try {
                            JSONObject jsonBuffer = new JSONObject(readBuffer.toString());
                            String mContentJson;

                            ImdbConstants  imdbConstObject = ImdbConstants.getInstance();

                            if(mContent == imdbConstObject.GET_DETAILS) {
                                mContentJson = "backdrops";
                            }  else {
                                mContentJson = "profiles";
                            }

                            JSONArray resultsArray = jsonBuffer.getJSONArray(mContentJson);


                            Log.d("MostPopular", "Returned." + resultsArray.length());

                             movieDetails = new MovieDetails(resultsArray, m);


                        } catch (JSONException e) {
                            Log.e("JSON: ", e.getMessage(), e);
                            e.printStackTrace();
                        }






                        Bundle b = new Bundle();

                       b.putString("message","Test message") ;
                        msgObj.setData(b);
                        handler.sendMessage(msgObj);
                    }
                }

                // Define the Handler that receives messages from the thread and update the progress
                private final Handler handler = new Handler() {

                    public void handleMessage(Message msg) {


                        String aResponse = msg.getData().getString("message");


                        if ((null != aResponse)) {

                            try {

                                Boolean binary = Looper.myLooper() == Looper.getMainLooper();
                                Log.d("threadMsg", "handleMessage" + "Mainthread is:" + binary);


                                Log.d("threadMsg", "filename:" + aResponse);

                                GetImages getImages = new GetImages();
                                Log.d("ScrollingActivity", "Main: Calling to displayView");
                                getImages.displayView(movieDetails);

                                //EventBus.getDefault().post(new MessageEvent("Hello everyone!"));
                                //Log.d("onMessageEvent", "event posted");




                            } catch(Exception e) {
                                e.getMessage();

                            }

                        }
                        else
                        {

                            // ALERT MESSAGE
                            Toast.makeText(
                                    getBaseContext(),
                                    "No Response From Server.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                };

            });
            // Start Thread
            background.start();  //After call start method thread called run Method
        };




                //GetImages getImages = new GetImages();
                // getImages.execute(m);

            }




    // Main thread recieve data from the background thread
    /*
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Toast.makeText(getApplicationContext().getApplicationContext(), event.message, Toast.LENGTH_SHORT).show();
        Log.d("onMessageEvent", "event recieved");
    }
*/
    /*
    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    */
    void doJob() {
        Runnable runnable = new Runnable() {
            public void run() {
                Message msg = mHandler.obtainMessage();
                Bundle bundle = new Bundle();

                bundle.putString("myKey", "Hello String");
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        };

        Thread mythread = new Thread(runnable);
        mythread.start();
    }

    private  String getOneImage(String fileName, String size) {
        final   String SINGLE_IMAGE_url = "http://image.tmdb.org/t/p/";
        return(SINGLE_IMAGE_url + size + fileName);
    }

    void  addItem(ImageItem imageItem) {
        mGridAdapter.addItem(imageItem);
    }



    public static void setId(String id) {
         omdbId =  id;
    }

    public static void setTitle(String title) {
        shareTitle =  title;
    }

    void setGridData() {
        mViewPager.setAdapter(mGridAdapter);
        mGridAdapter.set_propop(0.9f);
        mProgressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling, menu);

        /*
        MenuItem item = (MenuItem) menu.findItem(R.id.save);
        item.setEnabled(false);
        */
        return(super.onCreateOptionsMenu(menu));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if(id == R.id.share) {


            prepareShareIntent();



            //startActivity(Intent.createChooser(sharingIntent, "Share via"));

        }

        if (id == R.id.favorite) {

            Context context = getApplicationContext();
            CharSequence text = "Movie saved";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            ArrayList<ImageItem> mGridData  = mGridAdapter.getData();
            Log.d("Action", "Got save");
            int size = mGridData.size();

            // Map to String
            Map<String, String> myMap = GetDetails.getMap();
            JSONObject obj = new JSONObject(myMap);
            String mapString = obj.toString();

            // String to map
            HashMap<String,String> map = new Gson().fromJson(mapString, new TypeToken<HashMap<String, String>>() {
            }.getType());
            Log.d("Action", "Succeeded?");


            String imagesString = "";
            for(int i = 0; i < mGridData.size(); i++) {
                if(i == 0)
                    imagesString =  mGridData.get(i).getImage() ;
                else
                    imagesString = imagesString + "," +  mGridData.get(i).getImage();
            }


            Log.d("Action", "Image Length: " + String.valueOf(size));
            Log.d("Action", "Details Length: " + myMap.size());

            Log.d("Action:", "Image:" + imageUrl);
            Log.d("Action:", "Title:" + title);
            Log.d("Action:", "Rating:" + rating);
            Log.d("Action:", "Voting:" + voting);
            Log.d("Action:", "id:" + id);

            DBHandler handler = new DBHandler(activity.getApplicationContext());
            MovieRow mRow = new MovieRow(mId, title, rating, voting, imageUrl, mapString, imagesString);
            if(handler.searchMoviebyId(mId) != null)
                handler.updateMovie(mRow);
            else
                handler.addMovie(mRow);
            MovieRow mRow1 = handler.searchMoviebyId(mId);


            Log.d("Action", "still alive");

        }
            return super.onOptionsItemSelected(item);
        }


    void setProgressBarInactive() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }



    static LinearLayout getLayout() {
        return (topLinearLayout);
    }

    public void  prepareShareIntent() {
        // Fetch Bitmap Uri locally

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);

        ImageView imageX = mGridAdapter.getData().get(0).getImageVIew();
        Bitmap bmp = ((BitmapDrawable) imageX.getDrawable()).getBitmap();


        Uri bmpUri = null;
        try {

            // AndroidNavDrawerActivity.verifyStoragePermissions();
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        sharingIntent.setAction(Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Come see: " + shareTitle + " at: http://www.imdb.com/title/" + omdbId );
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Great movie: " + shareTitle);


        sharingIntent.setType("image/*");

        startActivity(Intent.createChooser(sharingIntent, "Share via"));


/*
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bmp)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
*/
        
        // Log.d("ShareDialog", "Sharing to Facebook" + Settings.getApplicationSignature(activity));
        /*
        ShareDialog shareDialog = new ShareDialog(this);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Hello Facebook")
                    .setContentDescription(
                            "The 'Hello Facebook' sample  showcases simple Facebook integration")
                    .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                    .build();

            shareDialog.show(linkContent, ShareDialog.Mode.NATIVE);
        }
        */

    }



    private void sharePhotoToFacebook(){
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption("Give me my codez or I will ... you know, do that thing you don't like!")
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, null);

    }

}



