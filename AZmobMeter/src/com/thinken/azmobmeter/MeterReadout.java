package com.thinken.azmobmeter;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import com.thinken.azmobmeter.driver.DriverUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MeterReadout extends Activity {

	DriverUtils driver_utils = new DriverUtils();
	//Strings
	private String meterType = "sl7000";
	private String serialNumber = "";
	private String fwVersion = "generic";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter_read);
		
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
			serialNumber = extras.getString("serialNumber");
			fwVersion = extras.getString("fwVersion");
		}
		
		
		TextView serial = (TextView)findViewById(R.id.txt_serialNumber);
		TextView fw = (TextView)findViewById(R.id.txt_firmwareVersion);
		
		serial.setText("Número de Série: "+serialNumber);
		fw.setText("Versão do Firmware: "+fwVersion);
		
		// Setup the list view
		final ListView newsEntryListView = (ListView) findViewById(R.id.list);
		final MeterObjectsEntryAdapter newsEntryAdapter = new MeterObjectsEntryAdapter(this,
				R.layout.meter_entry_list_item);
		newsEntryListView.setAdapter(newsEntryAdapter);
		// Populate the list, through the adapter
		for (final MeterObjectsEntry entry : getMeterObjectsEntries()) {
			newsEntryAdapter.add(entry);
		}
		
		
		newsEntryListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String selectedObject = newsEntryAdapter.getItem(position).getObjectName();
				Toast.makeText(getBaseContext(), selectedObject, Toast.LENGTH_LONG).show();
				
			}
		});
		

	}

	private List<MeterObjectsEntry> getMeterObjectsEntries() {
		// Let's setup some test data.
		// Normally this would come from some asynchronous fetch into a data
		// source
		// such as a sqlite database, or an HTTP request
		final List<MeterObjectsEntry> entries = new ArrayList<MeterObjectsEntry>();
		
		List<String[]> objects = new ArrayList<String[]>();
		
		try {
			objects = driver_utils.driver_getObjectsGroupsNames(meterType, fwVersion);
		} catch (Exception e) {
			// TODO: handle exception
			objects = driver_utils.driver_getObjectsGroupsNames("sl7000", "generic");
		}
		
		for (int i = 0; i < objects.size(); i++) {
			String[] object = new String[2];
			object = objects.get(i);
			entries.add(new MeterObjectsEntry(object[0], object[1] , i % 2 == 0 ? R.drawable.ic_read: R.drawable.ic_read));
		}
		
		return entries;
	}

	public void todo(View view) {
		Toast.makeText(getApplicationContext(), R.string.todo, 0).show();
	}

}
