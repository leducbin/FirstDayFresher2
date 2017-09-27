package com.ldb.bin.firstdayfresher;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.exoplayer.DefaultLoadControl;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.LoadControl;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.chunk.Format;
import com.google.android.exoplayer.hls.DefaultHlsTrackSelector;
import com.google.android.exoplayer.hls.HlsChunkSource;
import com.google.android.exoplayer.hls.HlsMasterPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylistParser;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.hls.PtsTimestampAdjusterProvider;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.ManifestFetcher;
import com.google.android.exoplayer.util.PlayerControl;
import com.google.android.exoplayer.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class VideoPlay extends AppCompatActivity implements ManifestFetcher.ManifestCallback<HlsPlaylist>,
        ExoPlayer.Listener,HlsSampleSource.EventListener, AudioManager.OnAudioFocusChangeListener{
    private String TAG = MainActivity.class.getSimpleName();
    private SurfaceView surface;
    private ExoPlayer player;
    private PlayerControl playerControl;
    private String video_url;
    private Handler mainHandler;
    ProgressDialog pDialog;
    private AudioManager am;
    private String userAgent;
    private ManifestFetcher<HlsPlaylist> playlistFetcher;
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int MAIN_BUFFER_SEGMENTS = 254;
    public static final int TYPE_VIDEO = 0;
    private TextView txt_playState;
    private TrackRenderer videoRenderer;
    private MediaCodecAudioTrackRenderer audioRenderer;
    private int h = 0;
    private int k = 0;
    private TextView textView;
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

        Intent intent_1 = getIntent();
        String id_video = intent_1.getStringExtra("url");
        String id_ep = intent_1.getStringExtra("id"); // we init buttons and listners

        textView = (TextView) findViewById(R.id.close);
        surface = (SurfaceView) findViewById(R.id.surface_view);

        Get_video get_video = new Get_video();
        get_video.setId_vi(id_video);
        get_video.setId_ep(id_ep);
        get_video.execute();


    }

    private class Get_video extends AsyncTask<Void, Void, Void>
    {
        private String id_vi,id_ep;
        private String reponse_se;

        public String getReponse_se() {
            return reponse_se;
        }

        public void setReponse_se(String reponse_se) {
            this.reponse_se = reponse_se;
        }

        public String getId_vi() {
            return id_vi;
        }

        public void setId_vi(String id_vi) {
            this.id_vi = id_vi;
        }

        public String getId_ep() {
            return id_ep;
        }

        public void setId_ep(String id_ep) {
            this.id_ep = id_ep;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler(VideoPlay.this);
            // Making a request to url and getting response
            this.reponse_se = sh.makeServiceCall("http://api.danet.vn/products/"+id_vi+"/playback/streams?device_id=undefined&device_type=web&variant=HD&episode_id="+id_ep);

//            String url =  "http://www.danet.vn/api/products/"+this.id_vi+"/playback/streams?device_id=undefined&device_type=web&variant=HD&episode_id="+id_ep;
//            Log.e(TAG, "url " + url);
//            RequestQueue queue = Volley.newRequestQueue(VideoPlay.this);
//            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET,url, null,
//                    new Response.Listener<JSONObject>()
//                    {
//
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            // display response
//                           Log.e(TAG, response.toString());
//                        }
//                    },
//                    new Response.ErrorListener()
//                    {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.d("Error.Response", String.valueOf(error));
//                        }
//                    }
//            );
//            queue.add(getRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
                try {
                    JSONObject json_ob = new JSONObject(reponse_se);
                    JSONObject streams = json_ob.getJSONObject("streams");
                    JSONObject hd = streams.getJSONObject("hd");
                    String video_url = hd.getString("src");//video url
                    player = ExoPlayer.Factory.newInstance(2);
                    playerControl = new PlayerControl(player); // we init player
                    am = (AudioManager) VideoPlay.this.getApplicationContext().getSystemService(Context.AUDIO_SERVICE); // for requesting audio
                    mainHandler = new Handler(); //handler required for hls
                    userAgent = Util.getUserAgent(VideoPlay.this, "VideoPlay"); //useragent required for hls
                    HlsPlaylistParser parser = new HlsPlaylistParser(); // init HlsPlaylistParser
                    playlistFetcher = new ManifestFetcher<>(video_url, new DefaultUriDataSource(VideoPlay.this, userAgent),
                            parser); // url goes here, useragent and parser
                    playlistFetcher.singleLoad(mainHandler.getLooper(), VideoPlay.this); //with 'this' we'll implement ManifestFetcher.ManifestCallback<HlsPlaylist>
                    //listener with it will come two functions
                    surface.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(h == 0)
                            {
                                playerControl.start();
                                h = 1;
                            }
                            else
                            {
                                playerControl.pause();
                                h=0;
                            }
                        }
                    });
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            playerControl.pause();
                            VideoPlay.this.finish();
                        }
                    });
                } catch (JSONException e) {

                }
        }
    }

    //inside onSingleManifest we'll code to play hls
    @Override
    public void onSingleManifest(HlsPlaylist manifest) {
        LoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(BUFFER_SEGMENT_SIZE));
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        PtsTimestampAdjusterProvider timestampAdjusterProvider = new PtsTimestampAdjusterProvider();
        boolean haveSubtitles = false;
        boolean haveAudios = false;
        if (manifest instanceof HlsMasterPlaylist) {
            HlsMasterPlaylist masterPlaylist = (HlsMasterPlaylist) manifest;
            haveSubtitles = !masterPlaylist.subtitles.isEmpty();

        }
        // Build the video/id3 renderers.
        DataSource dataSource = new DefaultUriDataSource(this, bandwidthMeter, userAgent);
        HlsChunkSource chunkSource = new HlsChunkSource(true /* isMaster */, dataSource, manifest,
                DefaultHlsTrackSelector.newDefaultInstance(this), bandwidthMeter,
                timestampAdjusterProvider, HlsChunkSource.ADAPTIVE_MODE_SPLICE);
        HlsSampleSource sampleSource = new HlsSampleSource(chunkSource, loadControl,
                MAIN_BUFFER_SEGMENTS * BUFFER_SEGMENT_SIZE, mainHandler, this, TYPE_VIDEO);
        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(this, sampleSource,
                MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                MediaCodecSelector.DEFAULT);
        this.videoRenderer = videoRenderer;
        this.audioRenderer = audioRenderer;
        pushSurface(false); // here we pushsurface
        player.prepare(videoRenderer,audioRenderer); //prepare
        player.addListener(this); //add listener for the text field
        if (requestFocus())
            player.setPlayWhenReady(true);
    }
    public boolean requestFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                am.requestAudioFocus(VideoPlay.this, AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
    }
    private void pushSurface(boolean blockForSurfacePush) {
        if (videoRenderer == null) {return;}
        if (blockForSurfacePush) {
            player.blockingSendMessage(
                    videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface.getHolder().getSurface());
        } else {
            player.sendMessage(
                    videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface.getHolder().getSurface());
        }
    }

    @Override
    public void onSingleManifestError(IOException e) {

    }
    // I'll upload this code on drive then just extarct it and understand ok
    //lets check
    // also watch my videos with my daughter
    //thanks!!!
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        String text = "";
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                break;
            case ExoPlayer.STATE_IDLE:
                text += "idle";
                break;
            case ExoPlayer.STATE_PREPARING:
                text += "preparing";
                break;
            case ExoPlayer.STATE_READY:
                text += "ready";
                break;
            default:
                text += "unknown";
                break;
        }

        //for the text feild
    }

    @Override
    public void onPlayWhenReadyCommitted() {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onLoadStarted(int sourceId, long length, int type, int trigger, Format format, long mediaStartTimeMs, long mediaEndTimeMs) {

    }

    @Override
    public void onLoadCompleted(int sourceId, long bytesLoaded, int type, int trigger, Format format, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs) {

    }

    @Override
    public void onLoadCanceled(int sourceId, long bytesLoaded) {

    }

    @Override
    public void onLoadError(int sourceId, IOException e) {

    }

    @Override
    public void onUpstreamDiscarded(int sourceId, long mediaStartTimeMs, long mediaEndTimeMs) {

    }

    @Override
    public void onDownstreamFormatChanged(int sourceId, Format format, int trigger, long mediaTimeMs) {

    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

}
