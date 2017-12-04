package com.rathierry.mychatapplication.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.rathierry.mychatapplication.R;
import com.rathierry.mychatapplication.model.UserDetails;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends BaseActivity {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private LinearLayout layout;
    private ImageView sendButton;
    private EditText messageArea;
    private ScrollView scrollView;
    private Firebase reference1, reference2;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // variable
        layout = (LinearLayout) this.findViewById(R.id.layout1);
        sendButton = (ImageView) this.findViewById(R.id.sendButton);
        messageArea = (EditText) this.findViewById(R.id.messageArea);
        scrollView = (ScrollView) this.findViewById(R.id.scrollView);

        // toolBar
        SpannableString spannableString = new SpannableString(getResources().getString(R.string.activity_chat_toolbar_title));
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_grey_blue)), 0,
                getResources().getString(R.string.activity_chat_toolbar_title).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(spannableString);

        // firebase
        Firebase.setAndroidContext(this);
        reference1 = new Firebase(rootUrlUserMessage + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase(rootUrlUserMessage + UserDetails.chatWith + "_" + UserDetails.username);

        // event onClick
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if (!messageText.equals("")/* && !TextUtils.isEmpty(messageText)*/) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);

                    messageArea.setText("");

                    // check if no view has focus:
                    if (sendButton != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(sendButton.getWindowToken(), 0);
                    }
                } else {
                    showCustomMessage(getResources().getString(R.string.activity_chat_error_title),
                            getResources().getString(R.string.activity_chat_error_message));
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if (userName.equals(UserDetails.username)) {
                    addMessageBox("You> " + message, 1);
                } else {
                    addMessageBox(UserDetails.chatWith + ":-\n" + message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    private void addMessageBox(String message, int type) {
        TextView textView = new TextView(ChatActivity.this);
        textView.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        textView.setLayoutParams(lp);

        if (type == 1) {
            textView.setBackgroundResource(R.drawable.rounded_corner1);
        } else {
            textView.setBackgroundResource(R.drawable.rounded_corner2);
        }

        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

}