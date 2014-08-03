package com.thinken.azmobmeter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MeterReadout extends Activity {
	
	ArrayAdapter<String> listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter_read);

	}

	public void todo(View view) {
		Toast.makeText(getApplicationContext(), R.string.todo, 0).show();
	}

}
