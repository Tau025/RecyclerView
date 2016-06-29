package com.devtau.recyclerview.database;

import android.content.Context;
import com.devtau.recyclerview.database.sources.DummyItemsSource;
import com.devtau.recyclerview.model.DummyItem;
import java.util.GregorianCalendar;

public class DataSource {
    private DummyItemsSource itemsSource;

    public DataSource(Context context) {
        itemsSource = new DummyItemsSource(context);

        if (itemsSource.getItemsCount() == 0) {
            populateDB();
        }
    }

    public DummyItemsSource getDummyItemsSource() {
        return itemsSource;
    }

    private void populateDB() {
        itemsSource.create(new DummyItem(new GregorianCalendar(2016, 5, 26, 14, 0), 2000, "DummyItem one"));//месяц +1
        itemsSource.create(new DummyItem(new GregorianCalendar(2016, 5, 27, 10, 10), 500, "DummyItem two"));
        itemsSource.create(new DummyItem(new GregorianCalendar(2016, 5, 28, 8, 40), 1000, "DummyItem three"));
        itemsSource.create(new DummyItem(new GregorianCalendar(2016, 6, 1, 10, 0), 800, "DummyItem four"));
        itemsSource.create(new DummyItem(new GregorianCalendar(2016, 6, 3, 12, 30), 150, "DummyItem five"));
        itemsSource.create(new DummyItem(new GregorianCalendar(2016, 6, 5, 9, 20), 400, "DummyItem six"));
    }
}
