package com.imagine.wuzzle.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import com.imagine.wuzzle.R;

/**
 * @author amulya
 * @datetime 17 Oct 2014, 3:49 PM
 */
public class DataSource {

	public static final int NO_NAVIGATION = -1;

	private ArrayList<DataItem> mDataSource;
	private DrawableProvider mProvider;

	public DataSource(Context context) {
		mProvider = new DrawableProvider(context);
		mDataSource = new ArrayList<DataItem>();
		String d = context.getString(R.string.data);
		try {
			JSONObject o = new JSONObject(d);
			JSONArray r = o.getJSONArray("branches");

			for (int x = 0; x < r.length(); x++) {

				mDataSource.add(itemFromType(r.getJSONObject(x).getString(
						"name")));
			}
		} catch (Exception e) {
			Toast.makeText(context, "Invalid Data source", Toast.LENGTH_LONG)
					.show();
		}

	}

	public int getCount() {
		return mDataSource.size();
	}

	public DataItem getItem(int position) {
		return mDataSource.get(position);
	}

	private DataItem itemFromType(String label) {
		Drawable drawable = null;
		int type = DrawableProvider.SAMPLE_ROUND;
		//type = (int) (Math.random() * 11) + 1;
		String s = String.valueOf(label.charAt(0));
		String s1 = s;
		if (label.indexOf(" ") > -1) {
			s1 = String.valueOf(label.charAt(label.indexOf(" ") + 1));
		}
		switch (type) {
		case DrawableProvider.SAMPLE_RECT:

			drawable = mProvider.getRect(s);
			break;
		case DrawableProvider.SAMPLE_ROUND_RECT:

			drawable = mProvider.getRoundRect(s);
			break;
		case DrawableProvider.SAMPLE_ROUND:

			drawable = mProvider.getRound(s);
			break;
		case DrawableProvider.SAMPLE_RECT_BORDER:

			drawable = mProvider.getRectWithBorder(s);
			break;
		case DrawableProvider.SAMPLE_ROUND_RECT_BORDER:

			drawable = mProvider.getRoundRectWithBorder(s);
			break;
		case DrawableProvider.SAMPLE_ROUND_BORDER:

			drawable = mProvider.getRoundWithBorder(s);
			break;
		case DrawableProvider.SAMPLE_MULTIPLE_LETTERS:

			drawable = mProvider.getRectWithMultiLetter(s);

			break;
		case DrawableProvider.SAMPLE_FONT:

			drawable = mProvider.getRoundWithCustomFont(s);

			break;
		case DrawableProvider.SAMPLE_SIZE:

			drawable = mProvider.getRectWithCustomSize(s, s1);

			break;
		case DrawableProvider.SAMPLE_ANIMATION:

			drawable = mProvider.getRectWithAnimation(label);

			break;
		case DrawableProvider.SAMPLE_MISC:

			drawable = mProvider.getRect(s);

			break;
		}
		type = NO_NAVIGATION;
		// label = label.concat(String.valueOf(type));
		return new DataItem(label, drawable, type);
	}
}
