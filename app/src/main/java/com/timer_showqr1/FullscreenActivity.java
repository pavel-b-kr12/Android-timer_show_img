package com.timer_showqr1;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements View.OnClickListener {

	private long startCountDownTimerHideQR_d = 10000;

	private long timeCountInMilliSeconds = 1 * 60000;
	private CountDownTimer countDownTimerHideQR;

	public void shoqQR(View view) {
		((View)textViewTime).setAlpha(0);
		((View)imageViewQR).setAlpha(1);

		startCountDownTimerHideQR();

	}

	private enum TimerStatus {
		STARTED,
		STOPPED
	}

	private TimerStatus timerStatus = TimerStatus.STOPPED;

	private ProgressBar progressBarCircle;
	private EditText editTextMinute;
	private TextView textViewTime;
	private TextView text_info;
	private ImageView imageViewReset;
	private ImageView imageViewStartStop;
	private ImageView imageViewQR;
	private CountDownTimer countDownTimer;

	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * Some older devices needs a small delay between UI widget updates
	 * and a change of the status and navigation bar.
	 */
	private static final int UI_ANIMATION_DELAY = 300;
	private final Handler mHideHandler = new Handler();
	private View mContentView;
	private final Runnable mHidePart2Runnable = new Runnable() {
		@SuppressLint("InlinedApi")
		@Override
		public void run() {
			// Delayed removal of status and navigation bar

			// Note that some of these constants are new as of API 16 (Jelly Bean)
			// and API 19 (KitKat). It is safe to use them, as they are inlined
			// at compile-time and do nothing on earlier devices.
			mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		}
	};
	private View mControlsView;
	private final Runnable mShowPart2Runnable = new Runnable() {
		@Override
		public void run() {
			// Delayed display of UI elements
			ActionBar actionBar = getSupportActionBar();
			if (actionBar != null) {
				actionBar.show();
			}
			mControlsView.setVisibility(View.VISIBLE);
		}
	};
	private boolean mVisible;
	private final Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			hide();
		}
	};
	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			switch (motionEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (AUTO_HIDE) {
						delayedHide(AUTO_HIDE_DELAY_MILLIS);
					}
					break;
				case MotionEvent.ACTION_UP:
					view.performClick();
					break;
				default:
					break;
			}
			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);

		initViews();
		// method call to initialize the listeners
		initListeners();



		mVisible = true;
		mControlsView = findViewById(R.id.fullscreen_content_controls);
		mContentView = findViewById(R.id.fullscreen_content);

		// Set up the user interaction to manually show or hide the system UI.
//		mContentView.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				toggle();
//			}
//		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		//findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
	}

	private void initViews() {
		progressBarCircle = (ProgressBar) findViewById(R.id.progressBarCircle);
		setProgressBarValues();

		editTextMinute = (EditText) findViewById(R.id.editTextMinute);
		textViewTime = (TextView) findViewById(R.id.textViewTime);
		text_info = (TextView) findViewById(R.id.text_info);
		imageViewReset = (ImageView) findViewById(R.id.imageViewReset);
		imageViewStartStop = (ImageView) findViewById(R.id.imageViewStartStop);
		imageViewQR = (ImageView) findViewById(R.id.imageViewQR);
	}

	/**
	 * method to initialize the click listeners
	 */
	private void initListeners() {
		imageViewReset.setOnClickListener(this);
		imageViewStartStop.setOnClickListener(this);
	}

	/**
	 * implemented method to listen clicks
	 *
	 * @param view
	 */
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.imageViewReset:
				reset();
				break;
			case R.id.imageViewStartStop:
				startStop();
				break;
		}
	}

	/**
	 * method to reset count down timer
	 */
	private void reset() {
		stopCountDownTimer();
		startCountDownTimer();
	}


	/**
	 * method to start and stop count down timer
	 */
	private void startStop() {
		if (timerStatus == TimerStatus.STOPPED) {

			// call to initialize the timer values
			setTimerValues();
			// call to initialize the progress bar values
			setProgressBarValues();
			// showing the reset icon
			imageViewReset.setVisibility(View.VISIBLE);
			// changing play icon to stop icon
			imageViewStartStop.setImageResource(R.drawable.icon_stop);
			// making edit text not editable
			editTextMinute.setEnabled(false);
			// changing the timer status to started
			timerStatus = TimerStatus.STARTED;
			// call to start the count down timer
			startCountDownTimer();

		} else {

			// hiding the reset icon
			imageViewReset.setVisibility(View.GONE);
			// changing stop icon to start icon
			imageViewStartStop.setImageResource(R.drawable.icon_start);
			// making edit text editable
			editTextMinute.setEnabled(true);
			// changing the timer status to stopped
			timerStatus = TimerStatus.STOPPED;
			stopCountDownTimer();

		}

	}

	/**
	 * method to initialize the values for count down timer
	 */
	private void setTimerValues() {
		int time = 0;
		if (!editTextMinute.getText().toString().isEmpty()) {
			// fetching value from edit text and type cast to integer
			time = Integer.parseInt(editTextMinute.getText().toString().trim());
		} else {
			// toast message to fill edit text
			Toast.makeText(getApplicationContext(), getString(R.string.message_minutes), Toast.LENGTH_LONG).show();
		}
		// assigning values after converting to milliseconds
		timeCountInMilliSeconds = time * 60 * 1000;
	}

	/**
	 * method to start count down timer
	 */
	private void startCountDownTimer() {

		countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
				progressBarCircle.setProgress((int) (1000*millisUntilFinished / timeCountInMilliSeconds));
			}

			@Override
			public void onFinish() {

				textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));

				((View)textViewTime).setAlpha(1); //text "press to show image"
				((View)imageViewQR).setAlpha(0.02f);
				textViewTime.setText("сканувати\n       QR");
				text_info.setVisibility(View.INVISIBLE);

				// call to initialize the progress bar values
				setProgressBarValues();
				// hiding the reset icon
				//imageViewReset.setVisibility(View.GONE);
				// changing stop icon to start icon
				imageViewStartStop.setImageResource(R.drawable.icon_start);
				// making edit text editable
				//editTextMinute.setEnabled(true);
				// changing the timer status to stopped
				timerStatus = TimerStatus.STOPPED;
			}

		}.start();
		countDownTimer.start();
	}

	/**
	 * method to stop count down timer
	 */
	private void stopCountDownTimer() {
		countDownTimer.cancel();
	}

	/**
	 * method to set circular progress bar values
	 */
	private void setProgressBarValues() {
		progressBarCircle.setMax( 1000);
		progressBarCircle.setProgress( 1000);
	}


	/**
	 * method to convert millisecond to time format
	 *
	 * @param milliSeconds
	 * @return HH:mm:ss time formatted string
	 */
	private String hmsTimeFormatter(long milliSeconds) {

		String hms = String.format("%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(milliSeconds),
				TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
				TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

		return hms;
	}

	private void startCountDownTimerHideQR() {

		countDownTimerHideQR = new CountDownTimer(startCountDownTimerHideQR_d, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {

				//textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

				progressBarCircle.setProgress((int) (1000*millisUntilFinished / startCountDownTimerHideQR_d));

			}

			@Override
			public void onFinish() {

				textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
				// call to initialize the progress bar values
				setProgressBarValues();

				((View)textViewTime).setAlpha(1);
				((View)imageViewQR).setAlpha(0.02f);
				text_info.setVisibility(View.VISIBLE);

				//imageViewReset.setVisibility(View.GONE);
				// changing stop icon to start icon
				//imageViewStartStop.setImageResource(R.drawable.icon_start);
				// making edit text editable
				//editTextMinute.setEnabled(true);
				// changing the timer status to stopped
				timerStatus = TimerStatus.STOPPED;

				startCountDownTimer();
			}

		}.start();
		countDownTimerHideQR.start();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	private void toggle() {
		if (mVisible) {
			hide();
		} else {
			show();
		}
	}

	private void hide() {
		// Hide UI first
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}
		mControlsView.setVisibility(View.GONE);
		mVisible = false;

		// Schedule a runnable to remove the status and navigation bar after a delay
		mHideHandler.removeCallbacks(mShowPart2Runnable);
		mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
	}

	private void show() {
		// Show the system bar
		mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
		mVisible = true;

		// Schedule a runnable to display UI elements after a delay
		mHideHandler.removeCallbacks(mHidePart2Runnable);
		mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
	}

	/**
	 * Schedules a call to hide() in delay milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	public void back(View view) {
		// this.onBackPressed(); //https://stackoverflow.com/questions/11747420/call-the-hardware-back-button-programmatically
		this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
		//super.finish();
		this.finish();
	}
}