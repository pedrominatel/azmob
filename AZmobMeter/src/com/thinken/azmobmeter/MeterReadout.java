package com.thinken.azmobmeter;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import com.thinken.azmobmeter.driver.DriverUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MeterReadout extends Activity {

	DriverUtils driver_utils = new DriverUtils();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter_read);

		// Setup the list view
		final ListView newsEntryListView = (ListView) findViewById(R.id.list);
		final MeterObjectsEntryAdapter newsEntryAdapter = new MeterObjectsEntryAdapter(this,
				R.layout.meter_entry_list_item);
		newsEntryListView.setAdapter(newsEntryAdapter);
		// Populate the list, through the adapter
		for (final MeterObjectsEntry entry : getMeterObjectsEntries()) {
			newsEntryAdapter.add(entry);
		}

	}

	private List<MeterObjectsEntry> getMeterObjectsEntries() {
		// Let's setup some test data.
		// Normally this would come from some asynchronous fetch into a data
		// source
		// such as a sqlite database, or an HTTP request
		final List<MeterObjectsEntry> entries = new ArrayList<MeterObjectsEntry>();
		
		List<String[]> objects = new ArrayList<String[]>();
		
		objects = driver_utils.driver_getObjectsGroupsNames("sl7000", "0721");
		
		for (int i = 1; i < objects.size(); i++) {
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
