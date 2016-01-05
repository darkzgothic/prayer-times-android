package com.metinkale.prayerapp.vakit.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.metinkale.prayer.R;
import com.metinkale.prayerapp.App;
import com.metinkale.prayerapp.Date;
import com.metinkale.prayerapp.Utils;
import com.metinkale.prayerapp.settings.Prefs;
import com.metinkale.prayerapp.vakit.NotificationPrefs;
import com.metinkale.prayerapp.vakit.times.MainHelper;
import com.metinkale.prayerapp.vakit.times.Times;
import com.metinkale.prayerapp.vakit.times.Vakit;

import java.util.Calendar;
import java.util.GregorianCalendar;

@SuppressLint("ClickableViewAccessibility")
public class MainFragment extends Fragment {

    private static final int ids[] = {R.id.imsaktime, R.id.gunestime, R.id.ogletime, R.id.ikinditime, R.id.aksamtime, R.id.yatsitime};
    private static final int idsNames[] = {R.id.imsak, R.id.gunes, R.id.ogle, R.id.ikindi, R.id.aksam, R.id.yatsi};
    private static Handler mHandler = new Handler();
    private View mView;
    private Times mTimes;
    private long mCity;
    private TextView mCountdown, mKerahat, mTitle;
    private boolean mHasTimes = false;
    private Runnable onSecond = new Runnable() {

        @Override
        public void run() {

            if (mTimes != null) {
                if (!mHasTimes) {
                    update();
                }
                checkKerahat();

                int next = mTimes.getNext();
                for (int i = 0; i < 6; i++) {
                    TextView time = (TextView) mView.findViewById(ids[i]);
                    ViewGroup parent = (ViewGroup) time.getParent();
                    if (i == next - 1) {
                        time.setBackgroundResource(R.color.indicator);
                        parent.getChildAt(parent.indexOfChild(time) - 1).setBackgroundResource(R.color.indicator);
                    } else {
                        time.setBackgroundColor(Color.TRANSPARENT);
                        parent.getChildAt(parent.indexOfChild(time) - 1).setBackgroundColor(Color.TRANSPARENT);
                    }
                }

                String left = mTimes.getLeft(next);
                mCountdown.setText(left);

            }

            if (mTitle.getText().toString().equals("gps")) {
                mTitle.setText(mTimes.getName());
            }

            mHandler.postDelayed(this, 1000);
        }

    };

    @Override
    public void onCreate(Bundle bdl) {
        super.onCreate(bdl);
        mCity = getArguments().getLong("city");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bdl) {
        mView = inflater.inflate(R.layout.vakit_fragment, container, false);
        this.setHasOptionsMenu(true);
        if (mCity == 0) {
            return mView;
        }

        mCountdown = (TextView) mView.findViewById(R.id.countdown);
        mTitle = (TextView) mView.findViewById(R.id.city);

        mKerahat = (TextView) mView.findViewById(R.id.kerahat);

        try {
            mTimes = MainHelper.getTimes(mCity);
        } catch (Exception e) {
            return new View(getActivity());
        }
        ImageView source1 = (ImageView) mView.findViewById(R.id.source1);
        ImageView source2 = (ImageView) mView.findViewById(R.id.source2);
        if (mTimes.getSource().resId != 0) {
            source1.setImageResource(mTimes.getSource().resId);
            source2.setImageResource(mTimes.getSource().resId);
        }
        mTitle.setText(mTimes.getName());

        update();

        if (Prefs.useArabic()) {
            View v = mView.findViewById(R.id.weightedFloatLayout1);
            for (int i = 0; i < idsNames.length; i++) {
                TextView tv = (TextView) v.findViewById(idsNames[i]);
                tv.setGravity(Gravity.LEFT);
                tv.setText(Vakit.getByIndex(i).getString());
            }
        }
        return mView;
    }

    private void update() {
        if (mTimes == null || mView == null) {
            return;
        }

        Calendar now = new GregorianCalendar();

        TextView datetv = (TextView) mView.findViewById(R.id.date);
        TextView hicritv = (TextView) mView.findViewById(R.id.hicri);

        Date h = new Date();

        hicritv.setText(h.format(true));
        datetv.setText(h.format(false));

        String[] daytimes = {mTimes.getTime(now, 0), mTimes.getTime(now, 1), mTimes.getTime(now, 2), mTimes.getTime(now, 3), mTimes.getTime(now, 4), mTimes.getTime(now, 5)};

        for (int i = 0; i < 6; i++) {

            TextView time = (TextView) mView.findViewById(ids[i]);
            if (!Prefs.use12H())
                time.setText(daytimes[i]);
            else
                time.setText(Utils.fixTimeForHTML(daytimes[i]));
            mHasTimes = !daytimes[i].equals("00:00");
        }

        mHandler.removeCallbacks(onSecond);
        mHandler.post(onSecond);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.vakit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.notification: {
                try {
                    Intent intent = new Intent(getActivity(), NotificationPrefs.class);
                    intent.putExtra("city", mCity);
                    startActivity(intent);
                    return true;
                } catch (Exception e) {
                    App.e(e);
                }
            }
            case R.id.refresh: {
                if (mTimes != null)
                    mTimes.refresh();
                break;
            }

            case R.id.share: {
                String txt = getString(R.string.shareTimes, mTimes.getName()) + ":";
                Calendar cal = Calendar.getInstance();
                String[] times = {mTimes.getTime(cal, 0), mTimes.getTime(cal, 1), mTimes.getTime(cal, 2), mTimes.getTime(cal, 3), mTimes.getTime(cal, 4), mTimes.getTime(cal, 5)};
                for (int i = 0; i < times.length; i++) {
                    txt += "\n   " + Vakit.getByIndex(i).getString() + ": " + times[i];
                }

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.vakit));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, txt);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share)));

            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected Times getTimes() {
        return mTimes;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.removeCallbacks(onSecond);
        mHandler.post(onSecond);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(onSecond);
    }

    void checkKerahat() {
        boolean k = mTimes.isKerahat();
        mKerahat.setVisibility(k ? View.VISIBLE : View.GONE);
    }

}