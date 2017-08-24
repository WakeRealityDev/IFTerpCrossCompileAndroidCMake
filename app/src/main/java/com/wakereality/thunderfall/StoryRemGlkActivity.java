package com.wakereality.thunderfall;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.wakereality.fictionengines.engineevents.DataFromEngineEvent;

import com.wakereality.fictionengines.engineevents.DataToEngineEvent;
import com.wakereality.fictionengines.engineinterface.NativeFictionEngineProcess;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Apache 2.0 license on this source file
 * (c) Copyright 2017, Stephen A Gutknecht, All Rights Reserved
 *
 * This code is largely reused from the previously published Thunderstrike app that was
 *    released on February 11, 2017. https://github.com/WakeRealityDev/Thunderstrike
 *
 * */
public class StoryRemGlkActivity extends AppCompatActivity {

    protected NativeFictionEngineProcess engineProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateLayoutSpecific();

        /*
        File permission is optional for this app, as this example extracts the included Glulx story from assets into
            the cache, so write and read permission to the /sdcard/ path is not required.
            CURRENTLY Hard-coded to always say good
         */
        boolean storagePermissionGood = true;
        // storagePermissionGood = UserRuntimePermission.verifyStoragePermissions(this);

        if (storagePermissionGood) {
            engineProcess = new NativeFictionEngineProcess();
            engineProcess.prepEngine(getApplicationContext());
            engineProcess.launchEngine(getApplicationContext());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        redrawEngineOutput();
    }


    // This serves to change Threads to the GUI update thread and to pass data from library to app.
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIncomingRemGlkStanza(DataFromEngineEvent event) {
        primitiveProcessingRemGlkStanza(event.dataJSON);
    };


    /*
    CODE from open source ThunderStrike app
     */
    protected ViewGroup rootView0;
    protected TextView topStatusTextView0;
    protected TextView storyOutputRawTextView0;
    protected TextView remGlkInfoOutputTextView0;
    protected GlkInputForWindow inputForSingleGlkWindow = new GlkInputForWindow();
    protected EditText inputEditText0;
    protected int clearScreenCount = 0;
    protected int inputLastGenSend = -1;
    protected int redrawCount = 0;
    protected int thunderWordRemoteEngineStateCode = -1;
    protected int remGlkUpdateGeneration = -1;

    private class GlkInputForWindow {
        public int windowId;
        public int gen;
        public boolean lineInputMode;
        public int maxlen;

        @Override
        public String toString() {
            return "win " + windowId + " gen " + gen + " line? " + lineInputMode;
        }
    }

    protected void postRemGlkInputFromPlayerToThunderword(String inputText) {
        JSONObject tempJSONObject = new JSONObject();
        int thisGen = inputForSingleGlkWindow.gen;
        try {
            // Tip: RemGlk documentation describes these fields and their format.
            tempJSONObject.put("type",   (inputForSingleGlkWindow.lineInputMode) ? "line" : "char");
            tempJSONObject.put("gen",    thisGen);
            tempJSONObject.put("window", inputForSingleGlkWindow.windowId);
            // ToDo: in char mode, use RemGlk special key names
            String inputTextModified = inputText;
            if (inputForSingleGlkWindow.lineInputMode) {
                inputTextModified = inputText.replace("\n", "");
            }
            tempJSONObject.put("value", inputTextModified);
        } catch (JSONException e) {
            Log.e("RemoteSimple", "JSONException formulating input", e);
        }

        if (inputLastGenSend == thisGen) {
            Log.w("RemoteSimple", "[dataToEngine] DUPLICATE_SEND_FAILURE, skip");
        } else {
            inputLastGenSend = thisGen;
            Log.i("RemoteSimple", "send " + tempJSONObject.toString() + " " + inputForSingleGlkWindow.windowId + " " + thisGen);
            DataToEngineEvent playerResponseToEngine = new DataToEngineEvent(tempJSONObject);
            EventBus.getDefault().post(playerResponseToEngine);
        }
    }


    protected void onCreateLayoutSpecific() {
        setContentView(R.layout.activity_thunderword_remote_simple);
        rootView0 = (ViewGroup) findViewById(R.id.activity_main);

        topStatusTextView0 = (TextView) rootView0.findViewById(R.id.topStatusTextView0);
        storyOutputRawTextView0 = (TextView) rootView0.findViewById(R.id.storyOutputRawTextView0);
        remGlkInfoOutputTextView0 =  (TextView) rootView0.findViewById(R.id.remGlkInfoOutputTextView0);
        inputEditText0 = (EditText) rootView0.findViewById(R.id.inputEditText0);
        inputEditText0.addTextChangedListener(new TextWatcher() {
            private CharSequence latestText = "";

            @Override
            public void beforeTextChanged(CharSequence input, int start, int before, int count) {}

            @Override
            public void onTextChanged(CharSequence input, int start, int before, int count) {
                latestText = input;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (inputForSingleGlkWindow.lineInputMode) {
                    // Strategy for line-only input mode is to stay in activity until [enter] key.
                    if (latestText.toString().endsWith("\n")) {
                        postRemGlkInputFromPlayerToThunderword(latestText.toString());
                        editable.clear();
                    }
                } else {
                    // ToDo: there is no conversion of RemGlk codes for special keys.
                    if (latestText.toString().length() > 0) {
                        postRemGlkInputFromPlayerToThunderword(latestText.toString());
                        editable.clear();
                    }
                }
            }
        });
    }


    public void redrawEngineOutput() {
        redrawCount++;
        topStatusTextView0.setText("");
        topStatusTextView0.append("Redraw #" + redrawCount);
        topStatusTextView0.append(" Engine State: " + thunderWordRemoteEngineStateCode);
        topStatusTextView0.append(" RemGlk Gen: " + remGlkUpdateGeneration);
        if (inputForSingleGlkWindow.lineInputMode) {
            inputEditText0.setHint("player input here (Glk line mode)");
        } else {
            inputEditText0.setHint("player input here (Glk character mode)");
        }
    }


    public void setupForNewStoryIncoming() {
        // clear output on screen.
        storyOutputRawTextView0.setText("");
        remGlkInfoOutputTextView0.setText("");
        // Reset input generation.
        inputLastGenSend = -1;
        remGlkUpdateGeneration = -1;
    }



    public void primitiveProcessingRemGlkUpdate(JSONObject jsonObject) {
        // raw debug:
        // storyOutputRawTextView0.append(jsonObject.toString() + "\n");

        int onUpdateGeneration = -1;
        try {

            if (jsonObject.has("gen")) {
                onUpdateGeneration = jsonObject.getInt("gen");
                remGlkUpdateGeneration = onUpdateGeneration;
            }

            if (jsonObject.has("windows")) {
                // ToDo: process Glk windows
            }

            if (jsonObject.has("content")) {
                JSONArray storyContentArray = jsonObject.getJSONArray("content");
                for (int topContentArrayIndex = 0; topContentArrayIndex < storyContentArray.length(); topContentArrayIndex++) {
                    JSONObject contentEntry = storyContentArray.getJSONObject(topContentArrayIndex);
                    Log.d("RemoteSimple", "contentEntry " + contentEntry.toString());
                    // Sample JSON from story Counterfeit Monkey version 6:
                    // {"id":24,"clear":true,"text":[{"append":true},{},{},{"content":[{"style":"normal","text":"Can you hear me? >> "}]}]}
                    if (contentEntry.has("clear")) {
                        if (contentEntry.getBoolean("clear")) {
                            clearScreenCount++;
                            storyOutputRawTextView0.setText("[clear screen #" + clearScreenCount + "]\n");
                        }
                    }
                    if (contentEntry.has("text")) {
                        JSONArray contentEntryTextArray = contentEntry.getJSONArray("text");
                        boolean appendMode = false;
                        for (int textForWindowIndex = 0; textForWindowIndex < contentEntryTextArray.length(); textForWindowIndex++) {
                            JSONObject windowContentTextParagraph = contentEntryTextArray.getJSONObject(textForWindowIndex);
                            // RemGlk has "content" field name at various levels
                            if (windowContentTextParagraph.has("content")) {
                                JSONArray singleContentTextFinalArray = windowContentTextParagraph.getJSONArray("content");
                                for (int singleTextIndex = 0; singleTextIndex < singleContentTextFinalArray.length(); singleTextIndex++) {
                                    JSONObject singleContentText = singleContentTextFinalArray.getJSONObject(singleTextIndex);
                                    if (singleContentText.has("text")) {
                                        String contentEntryStoryText = singleContentText.getString("text");
                                        // ToDo: smarter logic about newline, due to prompts and input echo backed.
                                        storyOutputRawTextView0.append(contentEntryStoryText + "\n");
                                    }
                                }

                            }
                            // Blank lines, blank paragraphs. See above "sample JSON from story Counterfeit Monkey".
                            if (windowContentTextParagraph.toString().equals("{}"))
                            {
                                storyOutputRawTextView0.append("\n");
                            }
                            if (windowContentTextParagraph.has("append"))
                            {
                                if (windowContentTextParagraph.getBoolean("append")) {
                                    // ToDo: value goes unchecked, the formating behavior / feature isn't implemented
                                    appendMode = true;
                                }
                            }
                        }
                    }
                }
            }

            if (jsonObject.has("input")) {
                JSONArray storyInputArray = jsonObject.getJSONArray("input");
                for (int inputArrayIndex = 0; inputArrayIndex < storyInputArray.length(); inputArrayIndex++) {
                    JSONObject inputEntry = storyInputArray.getJSONObject(inputArrayIndex);
                    Log.d("RemoteSimple", "inputEntry " + inputEntry.toString());
                    // Sample JSON from story Counterfeit Monkey version 6:
                    // {"id":24,"gen":1,"type":"line","maxlen":256}
                    if (inputEntry.has("type")) {
                        inputForSingleGlkWindow.lineInputMode = inputEntry.getString("type").equals("line");
                        if (inputEntry.has("gen")) {
                            inputForSingleGlkWindow.gen = inputEntry.getInt("gen");
                        }
                        if (inputEntry.has("id")) {
                            inputForSingleGlkWindow.windowId = inputEntry.getInt("id");
                        }

                        remGlkInfoOutputTextView0.append("input: " + inputForSingleGlkWindow.toString() + "\n");
                    }
                }
            }

        } catch (JSONException e) {
            Log.w("RemoteSimple", "Error processing JSON", e);
        }
    }

    /*
    It's labeled "primitive" because some would consider JSONObject/JSONArray field-level parsing
       as non-performant and tedious. Further, it's primitive in the sense that it skips RemGlk
       data from Thunderword about text colors, Glk style hints, Glk windows, Text Grids, images,
       and only seeks out the plain-text of a story in the Text Buffer windows.

       This is where the JSON processing starts, the head of each incoming stanza from RemGlk.
     */
    public void primitiveProcessingRemGlkStanza(JSONObject jsonStanza) {
        try {
            if (jsonStanza.has("type")) {
                // Some of these are non-standard types that Thunderword has added to the standard
                //   RemGlk types.
                switch (jsonStanza.getString("type")) {
                    case "error":
                        break;
                    case "update":
                        primitiveProcessingRemGlkUpdate(jsonStanza);
                        break;
                    case "blorbstatus":
                    case "remglk_exit":
                    case "remglk_status":
                        // ToDo: Warning, this name could change given the CAPS / inconsistent prefix.
                    case "RemGlk_debug":
                    case "remglk_debug":
                    case "EngineError":
                    case "blorberror":
                    case "debug_zcolors":
                        remGlkInfoOutputTextView0.append(jsonStanza.toString() + "\n");
                        break;
                    default:
                        remGlkInfoOutputTextView0.append("UNMATCHED_00: " + jsonStanza.toString() + "\n");
                        break;
                }
            } else {
                remGlkInfoOutputTextView0.append("NO_TYPE_00: " + jsonStanza.toString() + "\n");
            }
        } catch (JSONException e) {
            Log.w("RemoteSimple", "Error processing JSON", e);
        }

        redrawEngineOutput();
    }

}
