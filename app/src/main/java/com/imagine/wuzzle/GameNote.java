package com.imagine.wuzzle;

import java.util.*;

import com.imagine.textdrawable.TextDrawable;
import com.imagine.textdrawable.util.ColorGenerator;

import android.annotation.SuppressLint;
import android.content.Context;

import android.graphics.Color;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GameNote extends LinearLayout {

	private TextDrawable.IBuilder mDrawableBuilder;

	private ArrayList<ListData> mDataList = new ArrayList<ListData>();
	public GamePad gp;

	public GameNote(Context context) {
		super(context);
	}

	public GameNote(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressLint("NewApi")
	public GameNote(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@SuppressLint("NewApi")
	public GameNote(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);

	}

	private int count;
	public String word;
	int position = 0;

	public int getCount() {
		return mDataList.size();
	}

	public void loadData() {
		int dc = (count * count) - word.length();
		Log.d("Data Left", String.valueOf(dc));
		int c = word.length();
		mDataList.clear();

		for (int x = 0; x < c; x++) {
			mDataList.add(new ListData(String.valueOf(word.charAt(x))));
		}

	}

	public void setData(String word) {

		this.word = word;
		int dc = (count * count) - word.length();
		Log.d("Data Left", String.valueOf(dc));

		word = word.toUpperCase();

		mDataList.clear();
		int c = word.length();
		word = "";
		for (int x = 0; x < c; x++) {
			word += " ";
		}
		this.word = word;

	}

	void setSquare() {
		mDrawableBuilder = TextDrawable.builder().round();
		position = 0;
		this.count = word.length();
		loadData();
		Display display = ((WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay();
		int w = 0;
		DisplayMetrics m = new DisplayMetrics();
		display.getMetrics(m);
		w = m.widthPixels;
		int sw = w / count;

		this.removeAllViews();
		LinearLayout l = new LinearLayout(getContext());

		l.setOrientation(LinearLayout.HORIZONTAL);
		l.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		l.getLayoutParams().height = sw;

		loadPad(l, 0, sw);
		this.addView(l);

	}

	void update() {
		int c = word.length();
		word = "";
		for (int x = 0; x < c; x++) {
			word += mDataList.get(x).data;
		}
		Log.d("Data:", word);
		setSquare();
	}

	public void loadPad(LinearLayout l, int y, int w) {
		for (int x = 0; x < count; x++) {

			View v = getPad(position, w);
			l.addView(v);
			position++;
		}
	}

	public ListData getItem(int position) {

		return mDataList.get(position);

	}

	View getPad(final int position, int w) {

		View pad = View.inflate(getContext(), R.layout.grid_item_layout, null);
		ViewHolder holder = new ViewHolder(pad);
		ListData item = getItem(position);
		item.v = holder;
		item.position = position;
		// provide support for selected state
		updateCheckedState(item);
		holder.imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				ListData data = getItem(position);
				if (!data.data.contentEquals(" ")) {
					for (int x = getCount() - 1; x >= position; x--) {
						gp.uncheck(getItem(x).data);
						getItem(x).data = " ";
						ListData d = getItem(x);
						d.setChecked(!d.isChecked);
					}
				}
				update();
			}
		});
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, w, 1);

		pad.setLayoutParams(lp);
		int p = (int) (w * 0.04);

		pad.setPadding(p, p, p, p);
		return pad;
	}

	private void updateCheckedState(ListData item) {
		item.v.imageView.setImageDrawable(mDrawableBuilder.build(
				String.valueOf(item.data.charAt(0)), 0xff616161));
		getItem(item.position).data = String.valueOf(item.data.charAt(0));
	}

	public static class ListData {

		public String data;
		public ViewHolder v;
		public int position;
		public boolean isChecked;

		public ListData(String data) {
			this.data = data;
		}

		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}
	}

	class ViewHolder {

		private ImageView imageView;

		ViewHolder(View view) {
			imageView = (ImageView) view.findViewById(R.id.imageView);

		}
	}

	public void setGamePad(GamePad gp) {
		this.gp = gp;
	}
}
