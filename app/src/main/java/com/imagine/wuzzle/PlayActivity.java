package com.imagine.wuzzle;

import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imagine.wuzzle.GamePad.OnWinListener;

public class PlayActivity extends ActionBarActivity {
	public class Word {
		public String word;
		public String hint;
	}

	private int square = 3;
	private ArrayList<Word> words = new ArrayList<Word>();;
	GamePad gp;
	GameNote gn;
	LinearLayout l;
	TextView tv;
	private int level =1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String label = getIntent().getStringExtra("TYPE");
		getSupportActionBar().setTitle(label);
		String d = this.getString(R.string.data);
		try {
			JSONObject o = new JSONObject(d);
			JSONArray rr = o.getJSONArray("branches");

			for (int x = 0; x < rr.length(); x++) {
				JSONObject p = rr.getJSONObject(x);
				JSONArray r = p.getJSONArray("words");
				if (p.getString("name").contentEquals(label)) {
					for (int x1 = 0; x1 < r.length(); x1++) {
						JSONObject ww = r.getJSONObject(x1);
						Word w = new Word();
						w.word = ww.getString("word");
						w.hint = ww.getString("hint");
						words.add(w);
					}
					break;
				}
			}
		} catch (Exception e) {
			Toast.makeText(this, "Invalid Data source", Toast.LENGTH_LONG)
					.show();
		}

		setContentView(R.layout.activity_game);
		l = (LinearLayout) findViewById(R.id.container);
		tv = (TextView) findViewById(R.id.thint);
		tv.setVisibility(View.GONE);
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv.setVisibility(View.GONE);
				getSupportActionBar().setTitle(label);
			}
		});
		gn = (GameNote) findViewById(R.id.gamenote);

		gp = (GamePad) findViewById(R.id.gamepad);
		gp.setGameNote(gn);
		gn.setGamePad(gp);
		gp.setOnWinListener(new OnWinListener() {

			public void run() {

				words.remove(0);
				if (words.size() > 0) {

					Collections.shuffle(words);
					Word nextword = words.get(0);
					startGame(nextword);

				} else {

					Runnable r = new Runnable() {

						@Override
						public void run() {
							try {
								Log.d("GamePad:", "Sleeping for 2000ms");
								Thread.sleep(2000);
								finish();
							} catch (Exception e) {
								Log.d("GamePad:",
										"Timeout Exception was caught on Main Init");
							}
							Log.d("Game:", "Sleeping for 2000ms complete");
						}
					};
					Handler h = new Handler();
					h.post(r);

				}

			}
		});
		Collections.shuffle(words);
		Word nextword = words.get(0);
		startGame(nextword);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (!tv.isShown()) {
			getSupportActionBar().setTitle("Wuzzle Hint");
			tv.setVisibility(View.VISIBLE);
		} else {
			getSupportActionBar().setTitle(getIntent().getStringExtra("TYPE"));
			tv.setVisibility(View.GONE);
		}
		return true;
	}

	void startGame(Word w) {
		Animation shake = new TranslateAnimation(0, 5, 0, 0);
		shake.setInterpolator(new CycleInterpolator(5));
		shake.setDuration(400);
		String word = w.word;
		if (w.hint == null) {
			w.hint = "No hint available";
		}
		tv.setText(w.hint + ". Tap to close");
		l.startAnimation(shake);
		square = 0;
		while (true) {
			if ((square * square) < word.length()) {
				square++;
			} else {
				break;
			}
		}
        if(square<3){
            square =3;
        }
		gn.setData(word);
		gn.setSquare();
		gp.setData(word);
		gp.setSquare(square);
	}
}
