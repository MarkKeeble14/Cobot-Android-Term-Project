package com.example.cobot;

import android.os.Bundle;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ArticleSelectionActivity extends ListActivity {
    private ArrayList<Article> articleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        articleList = NewsActivity.articleList;

        ArrayAdapter<Article> arrayAdapter = new ArticlesAdapter(this, articleList);

        try {
            ListView listArticles = getListView();
            listArticles.setAdapter(arrayAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(this, ArticleDetailsActivity.class);

        i.putExtra("articleTitle", articleList.get(position).getTitle());
        i.putExtra("articleAuthor", articleList.get(position).getAuthor());
        i.putExtra("articleURL", articleList.get(position).getUrl());
        i.putExtra("articlePublishDate", articleList.get(position).getPublishedAt());
        i.putExtra("articleContent", articleList.get(position).getContent());

        startActivity(i);
    }
}