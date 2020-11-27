package com.example.cobot;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class OldChatListAdapter extends ArrayAdapter<Message> {

    private Activity context;
    private List<Message> messageList;

    public OldChatListAdapter(Activity context, List<Message> messageList) {
        super(context, R.layout.old_chat_listview, messageList);
        this.context = context;
        this.messageList = messageList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.old_chat_listview, null, true);
        TextView sender = listViewItem.findViewById(R.id.senderTextView);
        TextView dateTime = listViewItem.findViewById(R.id.dateTextView);
        TextView content = listViewItem.findViewById(R.id.contentTextView);
        Message msg = messageList.get(position);

        sender.setText("Sender: " + msg.getSender());
        dateTime.setText("Date: " + msg.getDate() + " " + msg.getTime());
        content.setText("Msg: " + msg.getContent());

        return listViewItem;
    }
}
