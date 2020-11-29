package com.example.cobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ArticleDetailsActivity extends AppCompatActivity {
    ImageView iv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);

        TextView author = findViewById(R.id.article_author);
        author.setText(getString(R.string.written_by) + getIntent().getExtras().getString("articleAuthor"));

        TextView title = findViewById(R.id.article_title);
        title.setText(getIntent().getExtras().getString("articleTitle"));

        TextView date = findViewById(R.id.article_publish_date);
        date.setText(getString(R.string.published_on) + getIntent().getExtras().getString("articlePublishDate"));

        TextView content = findViewById(R.id.article_content);
        content.setText(getIntent().getExtras().getString("articleContent"));
    }
}