package com.imagine.wuzzle;

import java.util.*;

import com.imagine.textdrawable.TextDrawable;
import com.imagine.textdrawable.util.ColorGenerator;

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

public class GamePad extends LinearLayout {

	private static int alphaIndex = 0;
	GameNote gn;
	private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
	private TextDrawable.IBuilder mDrawableBuilder;
	private int square = 4;
	private String word = "Wuzzle";
	int position = 0;
	private String actualword = "";
	private OnWinListener l;
	private ArrayList<ListData> mDataList = new ArrayList<ListData>();
	private boolean enabled = true;

	public GamePad(Context context) {
		super(context);

	}

	public GamePad(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public GamePad(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public GamePad(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);

	}

	String shuffle(String word) {
		String a[] = word.split("");
		List<String> ar = Arrays.asList(a);

		Collections.shuffle(ar);

		String r = "";
		int x = ar.size();
		for (int y = 0; y < x; y++) {
			r += ar.get(y);
		}
		return r;
	}

	String generateDontCares(int x) {
		char a[] = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
				'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
				'V', 'W', 'X', 'Y', 'Z' };
		String r = "";
		for (int y = 0; y < x; y++) {
			int z = (int) (Math.random() * 26);
			r += String.valueOf(a[z]);
		}

		return r;
	}

	public int getCount() {
		return mDataList.size();
	}

	public void loadData() {
		int dc = (square * square) - word.length();
		word += generateDontCares(dc);

		word = word.toUpperCase();

		word = shuffle(word);

		mDataList.clear();
		int c = word.length();
		for (int x = 0; x < c; x++) {
			mDataList.add(new ListData(String.valueOf(word.charAt(x))));
		}

	}

	public interface OnWinListener {

		public void run();

	}

	public void setData(String word) {

		this.word = word;
		actualword = word;
	}

	void setSquare(int square) {
		mDrawableBuilder = TextDrawable.builder().round();
		alphaIndex = 0;
		position = 0;
		this.square = square;
		loadData();
		Display display = ((WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay();

		int w = 0;
		DisplayMetrics m = new DisplayMetrics();
		display.getMetrics(m);

		w = m.widthPixels;

		int sw = w / square;
		this.setOrientation(LinearLayout.VERTICAL);
		this.removeAllViews();
		for (int x = 0; x < square; x++) {
			LinearLayout l = new LinearLayout(getContext());

			l.setOrientation(LinearLayout.HORIZONTAL);
			l.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));

			l.getLayoutParams().height = sw;

			loadPad(l, x, sw);
			this.addView(l);
		}
	}

	public void loadPad(LinearLayout l, int y, int w) {
		for (int x = 0; x < square; x++) {
			View v = getPad(position, w);
			l.addView(v);
			position++;
		}
	}

	public ListData getItem(int position) {
		return mDataList.get(position);
	}

	public void uncheck(String ch) {
		int c = getCount();
		Log.d("GamePad", "set out to deleting " + ch + " at unknown");
		for (int x = 0; x < c; x++) {
			if (getItem(x).data.contentEquals(ch) && getItem(x).isChecked) {
				getItem(x).isChecked = !getItem(x).isChecked;
				alphaIndex = x;
				Log.d("GamePad", "Deleting " + ch + " at " + String.valueOf(x));
				 updateCheckedState(getItem(x));
			}
		}
	}

	View getPad(final int position, int w) {

		View pad = View.inflate(getContext(), R.layout.grid_item_layout, null);
		final ViewHolder holder = new ViewHolder(pad);
		ListData item = getItem(position);
		item.v = holder;
		item.position = position;
		Log.d("GamePad", "Adding new Pad");
		updateCheckedState(item);
		holder.imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				ListData data = getItem(position);
				if (gn != null) {
					if (!data.isChecked && (alphaIndex == gn.getCount())) {
						return;
					}
				}
				data.setChecked(!data.isChecked);
				updateCheckedState(data);
				int c = gn.getCount();
				String w = "";
				for (int x = 0; x < c; x++) {
					w += gn.getItem(x).data;
				}
				w = w.trim().toLowerCase();
				if (w.contentEquals(actualword.toLowerCase())) {
					gameWon();
				}
			}
		});
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, w, 1);
		pad.setLayoutParams(lp);
		int p = (int) (w * 0.04);
		pad.setPadding(p, p, p, p);
		return pad;
	}

	void setOnWinListener(OnWinListener l) {
		this.l = l;
	}

	void gameWon() {
		this.removeAllViews();

		if (l != null) {
			l.run();
		}
	}

	public void updateCheckedState(ListData item) {
		if (!enabled) {
			return;
		}
		if (item.isChecked) {
			if (gn.getCount() == alphaIndex) {
				item.isChecked = !item.isChecked;
				return;
			}
			item.v.imageView.setImageDrawable(mDrawableBuilder.build(
					String.valueOf(item.data.charAt(0)), 0xff616161));

			getItem(item.position).data = String.valueOf(item.data.charAt(0));
			gn.getItem(alphaIndex).data = String.valueOf(item.data.charAt(0));
			alphaIndex++;

		} else {

			int c = gn.getCount() - 1;
			for (int x = c; x >= 0; x--) {
				if (gn.getItem(x).data.contentEquals(item.data)) {
					int y = Math.min(c - 1, x + 1);
					if (gn.getItem(y).data.contentEquals(" ") || (x == c)
							|| x == alphaIndex - 1) {
						gn.getItem(x).data = " ";
						alphaIndex = x;
						break;
					} else {
						item.isChecked = !item.isChecked;
						return;
					}

				}
			}

			TextDrawable drawable = mDrawableBuilder.build(
					String.valueOf(item.data.charAt(0)),
					mColorGenerator.getColor(item.data));
			item.v.imageView.setImageDrawable(drawable);
			item.v.view.setBackgroundColor(Color.TRANSPARENT);

		}

		gn.update();

	}

	private static class ListData {

		private String data;

		private boolean isChecked;
		public ViewHolder v;
		public int position;

		public ListData(String data) {
			this.data = data;
		}

		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}
	}

	class ViewHolder {

		private View view;

		private ImageView imageView;

		ViewHolder(View view) {
			this.view = view;
			imageView = (ImageView) view.findViewById(R.id.imageView);
		}
	}

	public void setGameNote(GameNote gn) {
		this.gn = gn;
	}

}
