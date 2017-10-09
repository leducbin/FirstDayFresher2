package com.ldb.bin.firstdayfresher;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VideoPlay extends AppCompatActivity implements VideoRendererEventListener{
    private SurfaceView surface;
    private String video_url;
    private Handler mainHandler;
    ProgressDialog pDialog;
    private AudioManager am;
    private String userAgent;
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int MAIN_BUFFER_SEGMENTS = 254;
    public static final int TYPE_VIDEO = 0;
    private TextView txt_playState;
    private int h = 0;
    private int k = 0;
    private TextView textView;
    private static final String TAG = "MainActivity";
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private SharedPreferences sharedPreferences;
    private ImageView close_video;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "VideoPlay on create");
        setContentView(R.layout.activity_video_play); // we import surface
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        close_video = (ImageView) findViewById(R.id.close);
        close_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent_1 = getIntent();
        String id_video = intent_1.getStringExtra("url");
        String id_ep = intent_1.getStringExtra("id"); // we init buttons and listners
        sharedPreferences = getSharedPreferences("dataLogin",MODE_PRIVATE);

// 1. Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

// 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

// 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        simpleExoPlayerView = new SimpleExoPlayerView(this);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);

//Set media controller
        simpleExoPlayerView.setUseController(true);
        simpleExoPlayerView.requestFocus();

// Bind the player to the view.
        simpleExoPlayerView.setPlayer(player);


// I. ADJUST HERE:
//CHOOSE CONTENT: LiveStream / SdCard

//LIVE STREAM SOURCE: * Livestream links may be out of date so find any m3u8 files online and replace:
// url data = "http://api.danet.vn/products/"+id_vi+"/playback/streams?device_id=undefined&device_type=web&variant=HD&episode_id="+id_ep;
        final String token_user = sharedPreferences.getString("accessToken","");
        String url_getvideo = "http://api.danet.vn/products/"+id_video+"/playback/streams?device_id=undefined&device_type=web&variant=HD&episode_id="+id_ep;
        RequestQueue requestQueue = Volley.newRequestQueue(VideoPlay.this);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url_getvideo,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            JSONObject reponese_js = new JSONObject(response);
                            JSONObject streams = reponese_js.getJSONObject("streams");
                            JSONObject hd = streams.getJSONObject("hd");
                            String src = hd.getString("src");

                        Uri mp4VideoUri =Uri.parse(src); //Radnom 540p indian channel





//Measures bandwidth during playback. Can be null if not required.
                        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
//Produces DataSource instances through which media data is loaded.
                        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(VideoPlay.this, Util.getUserAgent(VideoPlay.this, "exoplayer"), bandwidthMeterA);
//Produces Extractor instances for parsing the media data.
                        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();


// II. ADJUST HERE:

//This is the MediaSource representing the media to be played:
//FOR SD CARD SOURCE:
//        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);

//FOR LIVESTREAM LINK:
                        MediaSource videoSource = new HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null);
                        final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);

// Prepare the player with the source.
                        player.prepare(loopingSource);

                        player.addListener(new ExoPlayer.EventListener() {
                            @Override
                            public void onTimelineChanged(Timeline timeline, Object manifest) {
                                Log.v(TAG, "Listener-onTimelineChanged...");
                            }

                            @Override
                            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                                Log.v(TAG, "Listener-onTracksChanged...");
                            }

                            @Override
                            public void onLoadingChanged(boolean isLoading) {
                                Log.v(TAG, "Listener-onLoadingChanged...isLoading:"+isLoading);
                            }

                            @Override
                            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                                Log.v(TAG, "Listener-onPlayerStateChanged..." + playbackState);
                            }

                            @Override
                            public void onRepeatModeChanged(int repeatMode) {
                                Log.v(TAG, "Listener-onRepeatModeChanged...");
                            }

                            @Override
                            public void onPlayerError(ExoPlaybackException error) {
                                Log.v(TAG, "Listener-onPlayerError...");
                                player.stop();
                                player.prepare(loopingSource);
                                player.setPlayWhenReady(true);
                            }

                            @Override
                            public void onPositionDiscontinuity() {
                                Log.v(TAG, "Listener-onPositionDiscontinuity...");
                            }

                            @Override
                            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                                Log.v(TAG, "Listener-onPlaybackParametersChanged...");
                            }
                        });

                        player.setPlayWhenReady(true); //run file/link when ready to play.
                        player.setVideoDebugListener(VideoPlay.this); //for listening to resolution change and  outputing the resolution
                        } catch (JSONException e) {

                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Movideo-Auth", token_user);

                return params;
            }
        };
        requestQueue.add(postRequest);
//        Uri mp4VideoUri =Uri.parse("http://81.7.13.162/hls/ss1/index.m3u8"); //random 720p source
//        Uri mp4VideoUri =Uri.parse("FIND A WORKING LINK ABD PLUg INTO HERE"); //PLUG INTO HERE<------------------------------------------


    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Log.v(TAG, "onVideoSizeChanged ["  + " width: " + width + " height: " + height + "]");
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }




//-------------------------------------------------------ANDROID LIFECYCLE---------------------------------------------------------------------------------------------

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()...");
        player.release();
    }

}
