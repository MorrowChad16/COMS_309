package com.example.pickmeup.httpServices;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NoCache;
import com.android.volley.toolbox.Volley;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;

/**
 * This class is a singleton that handles multiple Volley Request's by managing a Volley RequestQueue.  Subclasses are FakeRequestController and FakeNetwork
 * are for testing.
 */
public class RequestController {

    @SuppressLint("StaticFieldLeak")
    private static Context mCtx;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    @SuppressLint("StaticFieldLeak")
    private static RequestController mInstance;
    private FakeRequestQueue fakeRequestQueue;
    private boolean testMode;
    private Network mFakeNetwork;

    private RequestController(Context context) {
        mCtx = context;

        testMode = false;
        fakeRequestQueue = null;
        mFakeNetwork = null;

        mRequestQueue = getRequestQueue();
    }

    private void setImageLoader(){
        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {

                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    //for testing only
    private RequestController(){
        mCtx = null;
        mRequestQueue = null;
        testMode = false;
        fakeRequestQueue = null;
        mFakeNetwork = null;
        mImageLoader = null;

        setTestMode(true);
    }

    /**
     * This method returns the single instance of RequestController.
     * @param context App context required for calling Volley's newRequestQueue() method
     * @return RequestController object
     */
    public static synchronized RequestController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestController(context);
        }
        return mInstance;
    }

    /**
     * This method returns the single instance of RequestController without a Context object and sets the RequestController to "test mode" with
     * a fakeRequestController and FakeNetwork. Equivalent to calling getInstance(context) and calling setTestMode(true).
     * @return RequestController object
     */
    public static synchronized RequestController getTestInstance(){
        if(mInstance == null){
            mInstance = new RequestController();
        }

        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null && !testMode) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            setImageLoader();
            
            mRequestQueue.start();
        }


        if(testMode){
            return fakeRequestQueue;
        } else {
            return mRequestQueue;
        }

    }


    /**
     * Adds Request to RequestQueue.
     * @param req<T></T> Volley Request
     *
     */
    public <T> void addToRequestQueue(Request<T> req) {

      getRequestQueue().add(req);

    }

    /**
     * If the network is an instance of FakeNetwork then will update the response to be returned when volley Request is processed.
     * @param response String to simulate network response
     */
    public void setFakeNetworkResponse(String response) {
        if(!testMode ) {
            setTestMode(true);
        }
        if(mFakeNetwork instanceof FakeNetwork){
            ((FakeNetwork)mFakeNetwork).setDataToReturn(response.getBytes(StandardCharsets.UTF_8));
        }


    }

    /**
     *  If set to true then will set RequestController to use FakeRequestController and FakeNetwork, else will set RequestController to normal instance.
     * @param testMode boolean for turning on or off test mode.
     */
    //call when using getInstance(AppContext)
    public void setTestMode(boolean testMode){
       Network network;
        if(testMode){
            network =  new FakeNetwork();
        } else{
            network = null;
        }
        setTestMode(testMode, network);
    }

    /**
     * Calls SetTestMode(boolean testMode), but additionally takes a network object (like a mocked Network) that can be used.
     * @param testMode boolean for turning on or off test mode.
     * @param network Volley Network to be used with RequestQueue
     */
    public void setTestMode(boolean testMode, Network network){ //pass in mock network too
        this.testMode = testMode;
        mFakeNetwork = network;

        if(testMode){

            fakeRequestQueue = new FakeRequestQueue(network);
            fakeRequestQueue.start();

        } else{
            fakeRequestQueue.stop();

            mFakeNetwork = null;
            fakeRequestQueue = null;
        }
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

}
    //constructs a fake Volley RequestQueue

/**
 * This class provides a FakeRequestQueue that can substitute the regular RequestQueue. For mocking and testing only.
 */
class FakeRequestQueue extends RequestQueue {
        public FakeRequestQueue( Network network) {
        super(new NoCache(), network, 1,  new ExecutorDelivery(new Executor() {
            @Override
            public void execute(Runnable runnable) {
                runnable.run();
            }
        }));
        }
    }

    //constructs a fake server response

/**
 * A object that mocks Network responses.
 */
class FakeNetwork implements Network { //fake a network response
        private byte[] mDataToReturn = null;

        public void setDataToReturn(byte[] data) {
            mDataToReturn = data;
        }
            @Override
            public NetworkResponse performRequest(Request<?> request) throws VolleyError {
                return new NetworkResponse(mDataToReturn);
            }
}

