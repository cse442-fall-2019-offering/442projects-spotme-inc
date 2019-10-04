package edu.buffalo.cse.cse442f19.spotme

import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_chat_screen.*
import kotlinx.android.synthetic.main.content_chat_screen.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import android.view.View.TEXT_ALIGNMENT_TEXT_END
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.view.inputmethod.EditorInfo


class ChatActivity : AppCompatActivity() {

    /**
     * For ANANYA to read:
     * Use addSenderChatBubble to add a user's own chat bubble as a message
     * Use addChatBubble to add a response message bubble.
     *
     * Preferably in the database, we should only store the latest 15 messages or something so as to not flood it
     * with old messages.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_screen)
        setSupportActionBar(toolbar)

        enterMessage.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){

                sendButtonClicked();
                true
            } else {
                false
            }
        }

        sendButton.setOnClickListener {
            sendButtonClicked();
        };

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
    }

    fun sendButtonClicked() {

        var userMessage = enterMessage.text.toString();

        if (!userMessage.isBlank()) {

            addSenderChatBubble(userMessage);
            addChatBubble("Wow, that\'s so cool!");
        }
    }

    fun addSenderChatBubble(text: String) {

        var chatLayout = LinearLayout(this);

        var chatBubble = TextView(this);
        var timeStamp = TextView(this);

        chatBubble.text = text;
        chatBubble.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);

        val currentTime = LocalDateTime.now();

        timeStamp.text = currentTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
        timeStamp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
        timeStamp.textAlignment = TEXT_ALIGNMENT_TEXT_END;

        //Clear enter message text
        enterMessage.setText("");

        //Add chat to the main layout
        chatLayout.addView(chatBubble);

        scrollViewMainLayout.addView(chatLayout);
        scrollViewMainLayout.addView(timeStamp);

        //Scroll to the end of the chat
        scrollView4.post {
            scrollView4.fullScroll(View.FOCUS_DOWN)
        };

        chatBubble.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        chatBubble.setPadding(10, 10, 50, 0);
        timeStamp.setPadding(0, 0, 50, 0);

        chatLayout.gravity = Gravity.RIGHT;
    }

    fun addChatBubble(text: String) {
        var chatLayout = LinearLayout(this);

        var chatBubble = TextView(this);
        var timeStamp = TextView(this);

        var profile = ImageButton(this);

        chatBubble.setText(text);
        chatBubble.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);

        profile.setBackgroundResource(R.drawable.match_avatar);

        val currentTime = LocalDateTime.now();

        timeStamp.setText(currentTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
        timeStamp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);

        //Clear enter message text
        enterMessage.setText("");

        //Add chat to the main layout
        chatLayout.addView(profile);
        chatLayout.addView(chatBubble);

        scrollViewMainLayout.addView(chatLayout);
        scrollViewMainLayout.addView(timeStamp);

        //Scroll to the end of the chat
        scrollView4.post {
            scrollView4.fullScroll(View.FOCUS_DOWN)
        };

        //chatBubble.getLayoutParams().width = 1000;
        profile.getLayoutParams().width = 200;
        profile.getLayoutParams().height = 200;

        chatBubble.setLayoutParams(
            android.widget.LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        );
        chatBubble.setPadding(10, 10, 10, 0);
    }
}
