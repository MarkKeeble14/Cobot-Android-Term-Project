package com.example.cobot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


//A function for the details of an article
public class ArticleDetailsActivity extends AppCompatActivity {
    ImageView iv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);

        //Set text view to the article author
        TextView author = findViewById(R.id.article_author);
        author.setText(getString(R.string.written_by) + getIntent().getExtras().getString("articleAuthor"));

        //Set text view for the article title
        TextView title = findViewById(R.id.article_title);
        title.setText(getIntent().getExtras().getString("articleTitle"));

        //Set text view for the article date
        TextView date = findViewById(R.id.article_publish_date);
        date.setText(getString(R.string.published_on) + getIntent().getExtras().getString("articlePublishDate"));

        //Set text view for the article content
        TextView content = findViewById(R.id.article_content);
        content.setText(getIntent().getExtras().getString("articleContent"));
    }
}