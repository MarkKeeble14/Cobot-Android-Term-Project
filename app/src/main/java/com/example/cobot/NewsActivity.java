package com.example.cobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NewsActivity extends AppCompatActivity {
    private static final String STATS_URL = "https://api.covid19api.com/summary";

    // Static string variables
    static String totalConfirmed, newConfirmed, totalDeaths, newDeaths, totalRecovered, newRecovered;
    static String arrayData;
    static ArrayList<CountryData> countryDataList = new ArrayList<CountryData>();

    // UI Views
    private ProgressBar progressBar;

    private String TAG = MainActivity.class.getSimpleName();

    public static String KEY = "&apikey=63734d6ff1b144a082ddfc7ec4df8d67";
    public String date = null;
    public String url = null;
    public static ArrayList<Article> articleList;
    public static ArrayList<Article> searchedArticleList;

    EditText stringInput = null;

    ListView articleListView;
    EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // init UI Views
        progressBar = findViewById(R.id.progressBar);
        articleListView = findViewById(R.id.article_list_view);
        searchText = findViewById(R.id.search_words);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    ArrayAdapter<Article> arrayAdapter = new ArticlesAdapter(NewsActivity.this, articleList);
                    try {
                        ListView listArticles = articleListView;
                        listArticles.setAdapter(arrayAdapter);
                        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent i = new Intent(NewsActivity.this, ArticleDetailsActivity.class);

                                i.putExtra("articleTitle", articleList.get(position).getTitle());
                                i.putExtra("articleAuthor", articleList.get(position).getAuthor());
                                i.putExtra("articleURL", articleList.get(position).getUrl());
                                i.putExtra("articlePublishDate", articleList.get(position).getPublishedAt());
                                i.putExtra("articleContent", articleList.get(position).getContent());

                                startActivity(i);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    searchedArticleList = new ArrayList<Article>();
                    for (Article art : articleList) {
                        if (containsTerm(art.getTitle(), s) || containsTerm(art.getContent(), s)) {
                            searchedArticleList.add(art);
                        }
                    }

                    ArrayAdapter<Article> arrayAdapter = new ArticlesAdapter(NewsActivity.this, searchedArticleList);
                    try {
                        ListView listArticles = articleListView;
                        listArticles.setAdapter(arrayAdapter);
                        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent i = new Intent(NewsActivity.this, ArticleDetailsActivity.class);

                                i.putExtra("articleTitle", searchedArticleList.get(position).getTitle());
                                i.putExtra("articleAuthor", searchedArticleList.get(position).getAuthor());
                                i.putExtra("articleURL", searchedArticleList.get(position).getUrl());
                                i.putExtra("articlePublishDate", searchedArticleList.get(position).getPublishedAt());
                                i.putExtra("articleContent", searchedArticleList.get(position).getContent());

                                startActivity(i);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            private boolean containsTerm(String text, CharSequence string) {
                String track = "";
                int trackAt = 0;
                text = text.toLowerCase();
                string = string.toString().toLowerCase();
                for (int i = 0; i < text.length() - 1; i++) {
                    if (track.compareTo(string.toString()) == 0)
                        return true;
                    if (text.charAt(i) == string.charAt(trackAt)) {
                        track += string.charAt(trackAt);
                        trackAt++;
                    } else {
                        trackAt = 0;
                        track = "";
                    }
                }
                return false;
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        progressBar.setVisibility(View.GONE);

        loadHomeData();

        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.nav_news:
                        break;
                    case R.id.nav_chat:
                        Intent iChat = new Intent(NewsActivity.this, ChatActivity.class);
                        startActivity(iChat);
                        break;
                    case R.id.nav_auth:
                        Intent iAuth = new Intent(NewsActivity.this, AuthenticationActivity.class);
                        startActivity(iAuth);
                        break;
                }
                return false;
            }
        });

        Calendar c = Calendar.getInstance();
        populate();
        c.add(Calendar.DATE, (7 * -1));
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        date = "&from=" + curFormater.format(c.getTime());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHomeData();
    }

    private void loadHomeData() {
        // show progress
        progressBar.setVisibility(View.VISIBLE);

        // JSON String request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, STATS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response received, handle response
                handleResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // some error occurred, hide progress, show error message
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NewsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // add request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(NewsActivity.this);
        requestQueue.add(stringRequest);
    }

    public void handleResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject globalJo = jsonObject.getJSONObject("Global");

            // get data from it
            newConfirmed = globalJo.getString("NewConfirmed");
            totalConfirmed = globalJo.getString("TotalConfirmed");
            newDeaths = globalJo.getString("NewDeaths");
            totalDeaths = globalJo.getString("TotalDeaths");
            newRecovered = globalJo.getString("NewRecovered");
            totalRecovered = globalJo.getString("TotalRecovered");

            // Load data
            JSONObject jsonObject2 = new JSONObject(response);
            JSONArray jsonArray = jsonObject2.getJSONArray("Countries");

            // Change json array to gson
            Gson gson = new Gson();

            for (int i = 0; i < jsonArray.length(); i++) {
                String aData = gson.toJson(jsonArray.get(i));
                String name = parseFor("Country", aData);
                String tc = parseFor("TotalConfirmed", aData);
                String nc = parseFor("NewConfirmed", aData);
                String td = parseFor("TotalDeaths", aData);
                String nd = parseFor("NewDeaths", aData);
                String tr = parseFor("TotalRecovered", aData);
                String nr = parseFor("NewRecovered", aData);
                CountryData cd = new CountryData(name.substring(1, name.length()),
                        tc.substring(0, tc.length() - 1),
                        nc.substring(0, nc.length() - 1),
                        td.substring(0, td.length() - 1),
                        nd.substring(0, nd.length() - 1),
                        tr.substring(0, tr.length() - 1),
                        nr.substring(0, nr.length() - 1));
                countryDataList.add(cd);
            }

            //  hide progess
            progressBar.setVisibility(View.GONE );
        }
        catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(NewsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String parseFor(String lookingFor, String parse) {
        String res = "";
        String track = "";
        int trackAt = 0;
        String lowercaseLookingFor = lookingFor.toLowerCase();
        String lowercaseParse = parse.toLowerCase();
        for (int i = 0; i < lowercaseParse.length(); i++) {
            if (lowercaseParse.charAt(i) == lowercaseLookingFor.charAt(trackAt)) {
                trackAt++;
                track += lowercaseParse.charAt(i);
            } else {
                trackAt = 0;
                track = "";
            }
            if ((track.length() - lowercaseLookingFor.length()) == 0) {
                int startAt = i + 3;
                while (lowercaseParse.charAt(startAt) != '\"' || startAt < i + 4) {
                    res += lowercaseParse.charAt(startAt);
                    startAt++;
                }
                return res;
            }
        }
        return "err";
    }

    public static CountryData getCountriesData(String country) {
        for (CountryData cd : countryDataList) {
            if (cd.getName().equalsIgnoreCase(country))
                return cd;
        }
        return null;
    }

    public void processButtonPress(View v) {
        String queries = null;
        if ((queries = stringInput.getText().toString()).isEmpty()) {
            Toast.makeText(NewsActivity.this, getString(R.string.empty_query_warning),
                    Toast.LENGTH_LONG).show();
        }
        else {
            url = buildURL(queries);
            new AsyncHTTPTask().execute();
        }
    }

    private String buildURL(String queries) {
        String baseOfURL =  "https://newsapi.org/v2/everything?q=";
        baseOfURL = baseOfURL.concat(queries);
        baseOfURL = baseOfURL.concat(date);
        baseOfURL = baseOfURL.concat(KEY);

        Log.d("Full URL: ", baseOfURL);
        Log.d(TAG, "QUERIES: " + queries + ", " + " DATE: " + date + ", KEY: " + KEY + ", FULL URL: " + baseOfURL);

        return baseOfURL;
    }

    private class AsyncHTTPTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HTTPHandler sh = new HTTPHandler();
            String jsonStr = null;

            // Making a request to url and getting response
            jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                articleList = parseResult(jsonStr);
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Intent myIntent = new Intent(NewsActivity.this, ArticleSelectionActivity.class);
            NewsActivity.this.startActivity(myIntent);
        }
    }

    private void populate() {
        articleList = new ArrayList<Article>();
        articleList.add(new Article("Ian Holliday", "Vancouver police shut down party, issue $2,300 ticket to host for breaking COVID-19 rules",
                "https://bc.ctvnews.ca/vancouver-police-shut-down-party-issue-2-300-ticket-to-host-for-breaking-covid-19-rules-1.5209014",
                "November 28, 2020", "VANCOUVER -- Vancouver police say they issued a $2,300 ticket to a downtown resident for violating B.C.'s COVID-19 rules early Saturday morning.\n" +
                        "\n" +
                        "Vancouver Police Department spokesperson Const. Jason Doucette told CTV News Vancouver officers were called to an apartment on Howe Street near Davie Street around 1 a.m. for a report of a party taking place inside.\n" +
                        "\n" +
                        "Doucette said officers followed loud music and voices to the apartment in question, where they found \"at least 16 people inside.\"Guests at the party ranged in age from 13 to 34, Doucette said, noting that three underage girls who did not live at the address where the party was being held were \"safely turned over to a parent.\"\n" +
                        "\n" +
                        "Police shut down the party and wrote the resident a ticket for hosting the gathering.\n" +
                        "\n" +
                        "Public health orders put in place across B.C. last week prohibit social gatherings with people from outside a \"core household\" group. The rules are intended to prevent the spread of the coronavirus.\n" +
                        "\n" +
                        "B.C. set a record for new cases on Friday, recording 911 in a 24-hour period."));
        articleList.add(new Article("Nathan Martin", "COVID-19: Alberta marks record one-day high with 1,731 new active cases; five more deaths",
                "https://edmontonjournal.com/news/local-news/covid-19-alberta-records-1731-new-active-cases-and-five-more-deaths", "November 29, 2020",
                "Alberta set a new single-day record with 1,731 new cases of COVID-19 on Friday, bringing the total number of confirmed cases in the province to 54,836, with 14,931 active cases.\n" +
                        "\n" +
                        "There are also 10 more people in hospital, bringing the total to 415, with two more people in ICU, bringing the total number of COVID-19 patients in intensive care to 88." +
                "As of Friday, the Edmonton Public Schools recorded cases at 11 schools in the city. Two cases were announced at Avalon School and single case was linked to Westminster School, Rio Terrace School, W.P. Wagner School, Thelma Chalifoux School, Tevie Miller School, Queen Elizabeth School, Mee-Yah-Noh School, Lillian Osborne School, Ross Sheppard School and Dunluce School.\n" +
                        "\n" +
                        "Across Canada, there are 60,666 active cases of COVID-19 and 11,894 deaths related to COVID-19."));
        articleList.add(new Article("Amanda Connolly", "Support for mandatory coronavirus vaccine keeps falling even as cases spike",
                "https://globalnews.ca/news/7488523/coronavirus-covid-19-vaccine-canada-mandatory-ipsos/", "November 28, 2020",
                "Support among Canadians for mandatory vaccination against the coronavirus continues to fall even as new infections explode across the country and public health officials urge people to stay at home.\n" +
                        "\n" +
                        "Polling done exclusively by Ipsos for Global News shows a drop in support for a mandatory vaccine since the beginning of the month, when it stood at 61 per cent.\n" +
                        "\n" +
                        "That support now stands at 59 per cent, a total drop of 13 percentage points since May 2020.\n" +
                        "\n" +
                        "“We know when we start going through the concerns that people have, they’re very worried about anything that has been rushed or they might perceive has been rushed. They’re very worried about anything that might have side effects associated with it,” said Darrell Bricker, CEO of Ipsos Public Affairs.\n" +
                        "\n" +
                        "“They haven’t heard enough reassurance, I would say, to convince them that the concerns that they have are being dealt with appropriately or validly. And as a result of that, what we do is we end up in a situation where the more time goes on, the more worried we get.”"));
        articleList.add(new Article("Zoe Demarco", "Rate of active COVID-19 cases now higher in BC than Ontario and Quebec",
                "https://dailyhive.com/vancouver/british-columbia-more-active-covid-19-cases-ontario-and-quebec", "November 28 2020",
                "British Columbia now has a higher rate of active COVID-19 cases than Ontario and Quebec, according to the latest epidemiological data.\n" +
                        "\n" +
                        "On November 27, British Columbia had 181 active virus cases per 100,000 people. Quebec had 135 active cases per 100,000 people, and Ontario had 91 active cases per 100,000 people.\n" +
                        "\n" +
                        "Ontario and Quebec have been Canada’s hotspots throughout the pandemic, each having seen over 100,000 cases to date." + "On Thursday, BC health officials reported 911 new COVID-19 cases and 11 new deaths. It’s the highest single-day count the province has seen since the start of the pandemic.\n" +
                        "\n" +
                        "Dr. Bonnie Henry recently implemented a number of province-wide health and safety measures intended to stop the rapid spread of the virus.\n" +
                        "\n" +
                        "Under the orders, which will be in place until at least December 7, gatherings are not allowed with anyone outside your household and community gatherings are banned.\n" +
                        "\n" +
                        "Masks are mandatory in indoor public spaces, including retail stores. Indoor HIIT, spin, and yoga classes have been suspended.\n" +
                        "\n" +
                        "“We’re in a critical time right now, and we’re seeing far too many people infected with this virus,” Henry told a press briefing."));
        articleList.add(new Article("Elena Shepert", "B.C. has more active COVID-19 cases per capita than Ontario and Quebec",
                "https://www.vancouverisawesome.com/coronavirus-covid-19-local-news/bc-has-more-active-covid-19-cases-per-capita-cases-than-ontario-and-quebec-3135433",
                "November 27 2020",
                "As the number of new coronavirus (COVID-19) cases continues to climb in B.C., the province currently has a higher per capita rate of active cases than Canada's two most populous provinces, Ontario and Quebec. \n" +
                        "\n" +
                        "With this in mind, B.C.'s per capita rate of active cases falls far short of the highest. As of Nov. 27, Manitoba has the highest per capita rate in Canada, with 647 per 100,000; B.C. has 181. \n" +
                        "\n" +
                        "Following Manitoba, Nunavut has the highest rate of active cases, with 389. Alberta has the third-highest rate, at 325, and Saskatchewan follows with 278. \n" +
                        "\n" +
                        "B.C. has 181 active cases per 100,000 people, while Quebec has 135. Ontario has 91. \n" +
                        "\n" +
                        "However, when it comes to total cases of the virus, Ontario has seen a staggering 111,216 and Quebec has seen 138,163."));
        articleList.add(new Article("Ian Holliday", "Publicly funded COVID-19 tests in B.C. come back positive more often, data shows",
                "https://bc.ctvnews.ca/publicly-funded-covid-19-tests-in-b-c-come-back-positive-more-often-data-shows-1.5208830", "November 28 2020",
                "VANCOUVER -- As COVID-19 cases in B.C. have surged, so has the percentage of tests for the coronavirus that are coming back positive.\n" +
                        "\n" +
                        "While this correlation is not surprising, a change to the way the provincial government reports testing data helps to highlight just how dramatic the increase in the rate of positive tests has been.\n" +
                        "\n" +
                        "During her latest update on the pandemic Friday, provincial health officer Dr. Bonnie Henry announced that the province would now be reporting two different types of test-positivity rates."));
        articleList.add(new Article("David Lao", "Canada surpasses 360K coronavirus cases as Quebec, Akbert break daily infection records",
                "https://globalnews.ca/news/7490677/coronavirus-canada-update-nov-28/", "November 28 2020",
                "Canada added 5,757 new cases of the novel coronavirus on Saturday, as well as 82 more deaths.\n" +
                        "\n" +
                        "The country’s total confirmed cases of COVID-19 now stands at 364,501, though 290,693 of those patients have since recovered. A total of 11,976 have died from the virus in Canada, while over 14,407,000 tests have been administered.\n" +
                        "\n" +
                        "READ MORE: Support for mandatory coronavirus vaccine keeps falling even as cases spike: Ipsos\n" +
                        "\n" +
                        "Saturday’s data paints a limited snapshot of the virus’ spread across Canada however, as British Columbia and both the Yukon and Northwest Territories do not release updated COVID-19 testing data on the weekend.\n" +
                        "\n" +
                        "As new cases of the virus surge in communities across the country, new Ipsos polling released Saturday suggested Canadians were also moving away from the idea of mandatory vaccinations.\n" +
                        "\n" +
                        "According to the poll, 59 per cent of Canadians agreed that COVID-19 vaccinations should be compulsory — a decrease of 13 points since July.\n" +
                        "\n" +
                        "CEO of Ipsos Public Affairs Darrell Bricker has since told Global News that the drop over the last several months was due to a number of reasons which include the perception of the vaccine being rushed as well as its potential side effects."));
        articleList.add(new Article("Jordan Armstrong & Jon Azpiri", "'For me, it is personal': B.C. doctor makes heartfelt appeal after death of 3 COVID-19 patients",
                "https://globalnews.ca/news/7489302/bc-doctor-covid-19-appeal/", "November 28 2020", "A B.C. doctor is offering a glimpse of what it likes on the front lines during the second wave of the COVID-19 pandemic.\n" +
                "\n" +
                "Dr. Kevin McLeod works in the COVID-19 ward at North Vancouver’s Lions Gate Hospital. Last weekend, three of his patients died.\n" +
                "\n" +
                "“People with COVID, they worsen very quickly,” McLeod said.\n" +
                "\n" +
                "“So those deaths … people were on a little bit of oxygen, maybe two litres. I would look at them and think, you know, this person’s going to get through it and eight hours later they’re dead.”"));
        articleList.add(new Article("The Visual and Data Journalism Team @ BBC News", "COVID-19 pandemic: Tracking the global coronavirus outbreak",
                "https://www.bbc.com/news/world-51235105", "November 27 2020",
                "Amid warnings that healthcare systems are being pushed to breaking point, World Health Organization Director General Tedros  Adhanom Ghebreyesus says positive news from vaccine trials means the \"light at the end of this long, dark tunnel is growing brighter\".\n" +
                        "\n" +
                        "But he warned against allowing the poorest and most vulnerable to be \"trampled in the stampede\" to get inoculated.\n" +
                        "\n" +
                        "As populations await vaccine roll-out, cases remain high across a number of regions of the world." + "The US Centers for Disease Control and Prevention (CDC) urged Americans to avoid travel for this week's Thanksgiving holiday to reduce the risk of infection.\n" +
                        "\n" +
                        "The outbreak has had a devastating impact on the US economy, although there are now some signs it is recovering."));
        articleList.add(new Article("Richard Horton", "Offline: Europe and COVID-19-struggling with tragedy",
                "https://www.thelancet.com/journals/lancet/article/PIIS0140-6736(20)32530-7/fulltext", "November 28, 2020",
                "The Institute for Health Metrics and Evaluation (IHME) at the University of Washington, Seattle, USA, publishes weekly reports on progress to control the COVID-19 pandemic in Europe. They make grim reading. The latest iteration, dated Nov 19, predicts that daily deaths from COVID-19 will continue to rise in the coming weeks, reaching a peak of over 7000 deaths per day around mid-January. Hospitals will be stretched to breaking point from December through to the end of February. COVID-19 is currently the second leading cause of death in the region—29 858 weekly deaths (ischaemic heart disease killed 44 253 people across Europe during the same period). The effective reproduction number, R, remains above 1 in most countries. There is still a large susceptible population—IHME estimates that only 7% of Europeans have been infected with the coronavirus so far. European publics remain resistant to public health advice. Mask use is less than 50% in Sweden, Norway, Denmark, Finland, the Netherlands, Belarus, Bulgaria, Croatia, and Latvia. Europe is struggling with tragedy. But the crisis Europe faces is not only about health—it is about politics too."));
        articleList.add(new Article("Lorne Cook and Virgina Mayo", "EU says first COVID-19 vaccinations possible by Christmas",
                "https://www.ctvnews.ca/health/coronavirus/eu-says-first-covid-19-vaccinations-possible-by-christmas-1.5203832", "November 25, 2020",
                "BRUSSELS -- Vaccinations against the coronavirus could start in the 27 European Union nations by Christmas and member countries must urgently prepare their logistical chains to cope with the rollout of hundreds of millions of doses of the vaccines, according to a top EU official.\n" +
                        "\n" +
                        "Hailing the likelihood that “there's finally light at the end of the tunnel,” European Commission President Ursula von der Leyen told EU lawmakers Wednesday that “the first European citizens might already be vaccinated before the end of December.”\n" +
                        "\n" +
                        "The commission, the EU's executive arm, has agreements with six potential vaccine suppliers and is working on a seventh contract. The deals allow it to purchase over 1.2 billion doses, more than double the population of the bloc, which stands at around 460 million people. Some vaccines would require two doses to be effective."));

        ArrayAdapter<Article> arrayAdapter = new ArticlesAdapter(this, articleList);
        try {
            ListView listArticles = articleListView;
            listArticles.setAdapter(arrayAdapter);
            articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(NewsActivity.this, ArticleDetailsActivity.class);

                    i.putExtra("articleTitle", articleList.get(position).getTitle());
                    i.putExtra("articleAuthor", articleList.get(position).getAuthor());
                    i.putExtra("articleURL", articleList.get(position).getUrl());
                    i.putExtra("articlePublishDate", articleList.get(position).getPublishedAt());
                    i.putExtra("articleContent", articleList.get(position).getContent());

                    startActivity(i);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Article> parseResult(String result) {
        JSONObject response = null;
        ArrayList<Article> res = new ArrayList<Article>();

        try {
            response = new JSONObject(result);
            JSONArray articles = response.optJSONArray("articles");

            for (int i = 0; i < articles.length(); i++) {

                JSONObject article = articles.optJSONObject(i);
                String author = article.optString("author");
                String title = article.optString("title");
                String URL = article.optString("url");
                String publishedAt = article.optString("publishedAt");
                String content = article.optString("content");

                Article curArticle = new Article(author, title, URL, publishedAt, content);
                res.add(curArticle);
            }
            return res;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}