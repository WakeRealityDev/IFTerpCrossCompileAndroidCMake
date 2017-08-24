package com.wakereality.fictionengines.engineinterface;

import android.content.Context;
import android.util.Log;

import com.wakereality.fictionengines.CopyAssetHelper;
import com.wakereality.fictionengines.engineevents.DataFromEngineEvent;
import com.wakereality.fictionengines.engineevents.DataToEngineEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by adminsag on 8/20/17.
 * Design goals:
 *    1. Allow multiple concurrent instances of this class, multiple engines running at the same time.
 *
 * Executable binaries can be run from the APK libs that Android mounts, using no additonal storage to copy outside
 *   the APK.
 *
 * Reading asset files from NDK code
 *    pipe support can read given a FileDescriptor
 *       https://stackoverflow.com/questions/8877494/how-to-load-a-video-filelocated-in-assets-folder-in-ndk
 *
 * Thoughts:
 *     the binary executables in an apk can be run from any app! A broadcast message could be sent out with available engines and their binary path.
 *     Would run out of process, not be memory leaks/etc in this app.  This would eliminate the need to cross-compile your self, multiple CPU binaries, and shrink your APK.
 *     ToDo: how would a intercepting binary work? Would it make sense to launch /bin/sh and pipe?  For example, a profanity filter or a macro tag expanding app that rewrites the JSON.
 *
 *     For sake of app testing simplicity, the story data file 'Mid the sagebrush and the cactus by Victor Gijsbers is included.
 *       Victor's story has a license, GNU General Purpose License (GPL) version 3, that allows redistribution.
 *     Tip for SagebrushAndCactus as a testing story:
 *         1. "help" brings up a menu
 *         2. keys: space key selects item on menu, n is down-arrow, p is up-arrow, q is quit
 *
 */

public class NativeFictionEngineProcess {

    public NativeFictionEngineProcess() {
        EventBus.getDefault().register(this);
    }

    protected Process engineProcess;
    protected InputStream inputStreamFromEngine;
    protected BufferedReader readerFromEngine;
    protected DataOutputStream outputStreamToEngine;
    protected Thread dataFromEngineThread;

    protected String engineAppBinaryPath = "../lib/x86/lib_git_app.so";
    protected File storyDataFile;
    protected String storyDataFilePath = "/sdcard/storyGames/storyGames_TechTest0/Bronze.gblorb";
    protected String dataOutRemGlkInit0 = "{ \"type\": \"init\", \"gen\": 0, \"metrics\": { \"width\":720, \"height\":400, \"charwidth\":9, \"charheight\":16 }, \"support\": [ \"timer\", \"hyperlinks\", \"graphics\", \"graphicswin\" ] }";

    protected int stanzaCount = 0;


    public boolean prepEngine(Context context) {
        engineAppBinaryPath = context.getApplicationInfo().nativeLibraryDir + File.separator + "lib_app_git.so";
        engineAppBinaryPath = context.getApplicationInfo().nativeLibraryDir + File.separator + "lib_app_glulxe.so";
        // Will use the cache folder, which the user can tell Android to clear to reclaim the consumed space.
        storyDataFile = new File(context.getCacheDir(), "Mid_the_Sagebrush_and_the_Cactus.gblorb");
        if (! storyDataFile.exists()) {
            boolean goodFileCopy = CopyAssetHelper.copyAssetFile(context.getAssets(), "stories/Mid_the_Sagebrush_and_the_Cactus.gblorb", storyDataFile);
            if (!goodFileCopy) {
                Log.e("EngineProcess", "Copy file from assets failed " + storyDataFile.toString());
                return false;
            }
        }
        storyDataFilePath = storyDataFile.getPath();
        return true;
    }


    public boolean launchEngine(Context context) {
        try {
            String[] runProcessPathAndArguments = new String[] {engineAppBinaryPath, storyDataFilePath};
            ProcessBuilder processBuilder = new ProcessBuilder(runProcessPathAndArguments);
            processBuilder.directory(new File(context.getApplicationInfo().dataDir));
            String outDirectory =  "???";
            if (processBuilder.directory() != null) {
                outDirectory = processBuilder.directory().toString();
            }
            Log.i("EngineProcess", "process launch: " + Arrays.toString(runProcessPathAndArguments) + " dir: " + outDirectory);
            engineProcess = processBuilder.start();
            outputStreamToEngine = new DataOutputStream(engineProcess.getOutputStream());
            outputStreamToEngine.writeBytes(dataOutRemGlkInit0 + "\n");
            outputStreamToEngine.flush();

            inputStreamFromEngine = engineProcess.getInputStream();
            readerFromEngine = new BufferedReader(new InputStreamReader(inputStreamFromEngine));

            dataFromEngineThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    processDataFromEngine();
                }
            });
            dataFromEngineThread.setName("DataFromEngine");
            dataFromEngineThread.start();
        }
        catch (Exception e) {
            Log.e("EngineProcess", "Exception", e);
        }
        return true;
    }

    /*
    Note: will run loop, advise you call from dedicated thread.
     */
    protected void processDataFromEngine() {
        final StringBuilder totalStanza = new StringBuilder(4096);

        try {
            String inputLine = readerFromEngine.readLine();
            // StringBuilder faster than StringBuffer, no need for locking.
            while (inputLine != null) {
                Log.d("EngineProcess", "[DataFromEngine] " + inputLine);
                // RemGlk stanza ends with an empty line.
                if (inputLine.isEmpty()) {
                    Log.i("EngineProcess", "[DataFromEngine] END OF STANZA");
                    // Log.i("EngineProcess", "[DataFromEngine] TOTAL raw: " + totalStanza.toString());
                    try {
                        JSONObject totalStanzaJSON = new JSONObject(totalStanza.toString());
                        stanzaCount++;
                        Log.i("EngineProcess", "[DataFromEngine] TOTAL parsed, stanza " + stanzaCount + ": " + totalStanzaJSON.toString());
                        EventBus.getDefault().post(new DataFromEngineEvent(totalStanzaJSON));
                        totalStanza.setLength(0);
                    } catch (JSONException e) {
                        Log.e("EngineProcess", "[DataFromEngine] JSONException", e);
                    }
                } else {
                    totalStanza.append(inputLine);
                }
                inputLine = readerFromEngine.readLine();
            }
        }
        catch (Exception e) {
            Log.e("EngineProcess", "Exception", e);
        }
    }


    @SuppressWarnings("unused")
    // Sending is quick, payloads are small. Will send on the thread that called, allowing a synchronous behavior.
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onIncomingRemGlkStanza(DataToEngineEvent event) {
        try {
            if (outputStreamToEngine != null) {
                outputStreamToEngine.writeBytes(event.dataJSON.toString() + "\n");
                outputStreamToEngine.flush();
            }
        }
            catch (Exception e) {
            Log.e("EngineProcess", "Exception", e);
        }
    };
}
