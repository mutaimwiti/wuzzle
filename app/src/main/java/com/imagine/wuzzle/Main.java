package com.imagine.wuzzle;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.imagine.wuzzle.data.DataItem;
import com.imagine.wuzzle.data.DataSource;

public class Main extends ActionBarActivity implements
        AdapterView.OnItemClickListener {

    public static final String TYPE = "TYPE";
    private DataSource mDataSource;
    private ListView mListView;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mAdView = (AdView) getLayoutInflater().inflate(R.layout.ads,null);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        Runnable r = new Runnable() {

            @Override
            public void run() {
                setContentView(R.layout.activity_main);
                mListView = (ListView) findViewById(R.id.listView);
                mListView.addFooterView(mAdView, null, false);
                mDataSource = new DataSource(Main.this);
                mListView.setAdapter(new ChoiceAdapter());
                mListView.setOnItemClickListener(Main.this);
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 3000);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        DataItem item = (DataItem) mListView.getItemAtPosition(position);

        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra(TYPE, item.getLabel());
        startActivity(intent);

    }

    private class ChoiceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDataSource.getCount();
        }

        @Override
        public DataItem getItem(int position) {
            return mDataSource.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(Main.this,
                        R.layout.list_item_layout, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            DataItem item = getItem(position);

            final Drawable drawable = item.getDrawable();
            holder.imageView.setImageDrawable(drawable);
            holder.textView.setText(item.getLabel());

            // if navigation is supported, show the ">" navigation icon
            if (item.getNavigationInfo() != DataSource.NO_NAVIGATION) {
                holder.textView.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        getResources().getDrawable(
                                R.drawable.ic_action_next_item), null);
            } else {
                holder.textView.setCompoundDrawablesWithIntrinsicBounds(null,
                        null, null, null);
            }

            // fix for animation not playing for some below 4.4 devices
            if (drawable instanceof AnimationDrawable) {
                holder.imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        ((AnimationDrawable) drawable).stop();
                        ((AnimationDrawable) drawable).start();
                    }
                });
            }

            return convertView;
        }
    }

    private static class ViewHolder {

        private ImageView imageView;

        private TextView textView;

        private ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.imageView);
            textView = (TextView) view.findViewById(R.id.textView);
        }
    }
}
