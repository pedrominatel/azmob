package com.thinken.azmobmeter;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MeterReadout extends Activity {

	ArrayAdapter<String> listAdapter;

	ListView list;
	String[] web = { "Google Plus", "Twitter", "Windows", "Bing", "Itunes",
			"Wordpress", "Drupal" };
	Integer[] imageId = { R.drawable.ic_read, R.drawable.ic_read,
			R.drawable.ic_read, R.drawable.ic_read, R.drawable.ic_read,
			R.drawable.ic_read, R.drawable.ic_read };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meter_read);

		// Setup the list view
		final ListView newsEntryListView = (ListView) findViewById(R.id.list);
		final NewsEntryAdapter newsEntryAdapter = new NewsEntryAdapter(this,
				R.layout.news_entry_list_item);
		newsEntryListView.setAdapter(newsEntryAdapter);
		// Populate the list, through the adapter
		for (final NewsEntry entry : getNewsEntries()) {
			newsEntryAdapter.add(entry);
		}

	}

	private List<NewsEntry> getNewsEntries() {
		// Let's setup some test data.
		// Normally this would come from some asynchronous fetch into a data
		// source
		// such as a sqlite database, or an HTTP request
		final List<NewsEntry> entries = new ArrayList<NewsEntry>();
		for (int i = 1; i < 50; i++) {
			entries.add(new NewsEntry("Test Entry " + i, "Anonymous Author "
					+ i, new GregorianCalendar(2011, 11, i).getTime(),
					i % 2 == 0 ? R.drawable.ic_read
							: R.drawable.ic_read));
		}
		return entries;
	}

	public void todo(View view) {
		Toast.makeText(getApplicationContext(), R.string.todo, 0).show();
	}

}
