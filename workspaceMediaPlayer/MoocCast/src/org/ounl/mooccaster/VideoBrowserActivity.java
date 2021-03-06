/*
 * Copyright (C) 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ounl.mooccaster;

import org.ounl.mooccaster.R;
import org.ounl.mooccaster.settings.CastPreference;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.sample.castcompanionlibrary.cast.VideoCastManager;
import com.google.sample.castcompanionlibrary.cast.callbacks.IVideoCastConsumer;
import com.google.sample.castcompanionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.sample.castcompanionlibrary.widgets.MiniController;

public class VideoBrowserActivity extends ActionBarActivity {
	

	private static final String TAG = "VideoBrowserActivity";
	private VideoCastManager mCastManager;
	private IVideoCastConsumer mCastConsumer;
	private MiniController mMini;
	private MenuItem mediaRouteMenuItem;
	boolean mIsHoneyCombOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	private Toolbar mToolbar;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate() is called");

		VideoCastManager.checkGooglePlayServices(this);
		setContentView(R.layout.video_browser);

		mCastManager = CastApplication.getCastManager(this);

		// -- Adding MiniController
		mMini = (MiniController) findViewById(R.id.miniController1);
		mCastManager.addMiniController(mMini);

		mCastConsumer = new VideoCastConsumerImpl() {

			@Override
			public void onFailed(int resourceId, int statusCode) {

			}

			@Override
			public void onConnectionSuspended(int cause) {
				Log.d(TAG, "onConnectionSuspended() was called with cause: "
						+ cause);
				org.ounl.mooccaster.utils.Utils.showToast(
						VideoBrowserActivity.this,
						R.string.connection_temp_lost);
			}

			@Override
			public void onConnectivityRecovered() {
				org.ounl.mooccaster.utils.Utils.showToast(
						VideoBrowserActivity.this,
						R.string.connection_recovered);
			}

			@Override
			public void onCastDeviceDetected(final RouteInfo info) {
				if (!CastPreference.isFtuShown(VideoBrowserActivity.this)
						&& mIsHoneyCombOrAbove) {
					CastPreference.setFtuShown(VideoBrowserActivity.this);

					Log.d(TAG, "Route is visible: " + info);
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							if (mediaRouteMenuItem.isVisible()) {
								Log.d(TAG,
										"Cast Icon is visible: "
												+ info.getName());
								showFtu();
							}
						}
					}, 1000);
				}
			}
		};

		setupActionBar();

		mCastManager.reconnectSessionIfPossible(this, false);

	}

	private void setupActionBar() {
		Log.d(TAG, "setupActionBar() is called");

		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mToolbar.setLogo(R.drawable.actionbar_logo_castvideos);
		mToolbar.setTitle("");
		setSupportActionBar(mToolbar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		Log.d(TAG, "onCreateOptionsMenu() is called");

		getMenuInflater().inflate(R.menu.main, menu);

		mediaRouteMenuItem = mCastManager.addMediaRouterButton(menu,
				R.id.media_route_menu_item);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected() is called");

		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent i = new Intent(VideoBrowserActivity.this,
					CastPreference.class);
			startActivity(i);
			break;
		}
		return true;
	}

	/**
	 * The getActionView() method used in this method requires API 11 or above.
	 * If one needs to extend this below that version, one possible solution
	 * could be using reflection and such.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void showFtu() {
		Menu menu = mToolbar.getMenu();
		View view = menu.findItem(R.id.media_route_menu_item).getActionView();
		if (view != null && view instanceof MediaRouteButton) {
			new ShowcaseView.Builder(this).setTarget(new ViewTarget(view))
					.setContentTitle(R.string.touch_to_cast).build();
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (mCastManager.onDispatchVolumeKeyEvent(event,
				CastApplication.VOLUME_INCREMENT)) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.d(TAG, "onResume() is called");
		mCastManager = CastApplication.getCastManager(this);
		if (null != mCastManager) {
			mCastManager.addVideoCastConsumer(mCastConsumer);
			mCastManager.incrementUiCounter();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause() is called");

		mCastManager.decrementUiCounter();
		mCastManager.removeVideoCastConsumer(mCastConsumer);


	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy is called");
		if (null != mCastManager) {
			mMini.removeOnMiniControllerChangedListener(mCastManager);
			mCastManager.removeMiniController(mMini);
			mCastManager.clearContext(this);
		}
		super.onDestroy();
	}

	@Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);

		Log.d(TAG, "onNewIntent() is called");

	}

}
