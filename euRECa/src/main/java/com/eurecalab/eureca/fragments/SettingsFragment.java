package com.eurecalab.eureca.fragments;

import com.eurecalab.eureca.LoginActivity;
import com.eurecalab.eureca.R;
import com.eurecalab.eureca.constants.GenericConstants;
import com.eurecalab.eureca.core.GlobalState;
import com.eurecalab.eureca.core.User;
import com.eurecalab.eureca.ui.ColorSpinnerAdapter;
import com.eurecalab.eureca.ui.ThemeSwitcher;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SettingsFragment extends Fragment implements View.OnClickListener, OnConnectionFailedListener{
	private SwitchCompat reverseColor;
	private Button apply;
	private Spinner colorSpinner;
	private SpinnerAdapter colorSpinnerAdapter;
	private Button signOutButton;
	private TextView loggedUser;
	private GlobalState gs;
	private User user;
	private FragmentActivity parent;
	private GoogleApiClient mGoogleApiClient;
	private TextView appLicense;
	private TextView expiresIn;
	private Button upgrade;

	private SharedPreferences sharedPreferences;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container,
				false);
		
		reverseColor = (SwitchCompat) rootView.findViewById(R.id.reverseColor);
		apply = (Button) rootView.findViewById(R.id.apply);
		colorSpinner = (Spinner) rootView.findViewById(R.id.colorSpinner);
		signOutButton = (Button) rootView.findViewById(R.id.sign_out_button);
		loggedUser = (TextView) rootView.findViewById(R.id.logged_user);
		appLicense = (TextView) rootView.findViewById(R.id.app_license);
		upgrade = (Button) rootView.findViewById(R.id.upgrade);
		expiresIn = (TextView) rootView.findViewById(R.id.expires_in);

		parent = getActivity();

		gs = (GlobalState) parent.getApplication();
		user = gs.getAuthenticatedUser();
		loggedUser.setText(user.getDisplayName());

		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.build();

		mGoogleApiClient = new GoogleApiClient.Builder(parent)
				.enableAutoManage(parent, this /* OnConnectionFailedListener */)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.build();

		signOutButton.setOnClickListener(this);
		upgrade.setOnClickListener(this);

		StringBuilder license = new StringBuilder(getString(R.string.app_license));
		license.append(" ");

		boolean isPro = user.isProUser();
		if(!isPro){
			license.append(getString(R.string.free_version));
			upgrade.setVisibility(View.VISIBLE);
			expiresIn.setVisibility(View.GONE);
		}
		else {
			license.append(getString(R.string.pro_version));
			String expireDate = user.getProVersionExpireDate();
			if (!expireDate.equals(GenericConstants.DATE_INFINITE)) {
				try {
					Date expirationDate = GenericConstants.DATE_FORMATTER.parse(expireDate);
					Date now = new Date();
					long diff = expirationDate.getTime() - now.getTime();//as given

					long days = TimeUnit.MILLISECONDS.toDays(diff);

					StringBuilder expiresInSB = new StringBuilder();
					expiresInSB.append(getString(R.string.expires_in)).append(" ");
					expiresInSB.append(days).append(" ");

					if (days == 1) {
						expiresInSB.append(getString(R.string.day));
					} else {
						expiresInSB.append(getString(R.string.days));
					}
					expiresIn.setText(expiresInSB.toString());
					upgrade.setText(getString(R.string.extend));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			else{
				expiresIn.setVisibility(View.GONE);
				upgrade.setVisibility(View.GONE);
			}
		}

		appLicense.setText(license.toString());

		TypedArray colorArray = getResources().obtainTypedArray(R.array.theme_colors);
		int [] colors = new int[colorArray.length()];
		
		int defaultColor = ContextCompat.getColor(getActivity(), R.color.color_primary_red);
		
		for (int i = 0; i < colors.length; i++) {
			colors[i] = colorArray.getColor(i, defaultColor);
		}
		
		colorArray.recycle();
		
		colorSpinnerAdapter = new ColorSpinnerAdapter(getActivity(), colors);
		
		colorSpinner.setAdapter(colorSpinnerAdapter);
		
		apply.setOnClickListener(this);
		
		sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		int color = sharedPreferences.getInt(getString(R.string.saved_color), R.color.color_primary_red);
		boolean reverse = sharedPreferences.getBoolean(getString(R.string.saved_reverse), false);
		
		int position = -1;
		for (int i = 0; i < colors.length; i++) {
			if(colors[i] == color){
				position = i;
				break;
			}
		}
		
		colorSpinner.setSelection(position);
		
		reverseColor.setChecked(reverse);
		
		return rootView;
	}

	@Override
	public void onClick(View v) {
		if(v.equals(apply)){
			boolean reverse = reverseColor.isChecked();
			
			int color = (Integer) colorSpinner.getSelectedItem();
			
			new ThemeSwitcher(getActivity()).changeToTheme(color, reverse);
			
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt(getString(R.string.saved_color), color);
			editor.putBoolean(getString(R.string.saved_reverse), reverse);
			editor.commit();
		}
		else if(v.equals(signOutButton)){
			Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
					new ResultCallback<Status>() {
						@Override
						public void onResult(Status status) {
							Intent intent = new Intent(getActivity(), LoginActivity.class);
							getActivity().startActivity(intent);
							getActivity().finish();
						}
					});
			gs.setAuthenticatedUser(null);
		}
		else if(v.equals(upgrade)){
			//TODO: gestire upgrade a pro
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}
}
