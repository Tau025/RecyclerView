package com.devtau.recyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import com.devtau.recyclerview.database.DataSource;
import com.devtau.recyclerview.database.sources.DummyItemsSource;
import com.devtau.recyclerview.model.DummyItem;
import com.devtau.recyclerview.model.DummyItemComparators;
import com.devtau.recyclerview.util.Util;

import com.devtau.recyclerviewlib.RVHelper;
import com.devtau.recyclerviewlib.RVHelperInterface;
import com.devtau.recyclerviewlib.MyItemRVAdapter.ViewHolder;
import com.devtau.recyclerviewlib.util.Constants;
/**
 * Пример использования библиотеки RVHelper клиентом
 */
public class MainActivity extends AppCompatActivity implements
        RVHelperInterface<DummyItem> {
    private static final String ARG_INDEX_OF_SORT_METHOD = "indexOfSortMethod";
    private RVHelper rvHelper;
    //рекомендуется хранить ссылку на dataSource, если таблиц больше одной
    private DummyItemsSource dummyItemsSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dummyItemsSource = new DataSource(this).getDummyItemsSource();
        ArrayList<DummyItem> itemsList = dummyItemsSource.getItemsList();

        HashMap<Integer, Comparator> comparators = DummyItemComparators.getComparatorsMap();
        ArrayList<String> comparatorsNames = DummyItemComparators.getComparatorsNames(this);
        int indexOfSortMethod = Constants.DEFAULT_SORT_BY;
        if(savedInstanceState != null) {
            indexOfSortMethod = savedInstanceState.getInt(ARG_INDEX_OF_SORT_METHOD);
        }
        rvHelper = RVHelper.Builder.<DummyItem> start(this).setList(itemsList, comparators)
                .withColumnCount(1)
                .withListItemLayoutId(R.layout.list_item)
                .withSortSpinner(comparatorsNames, indexOfSortMethod)
                .withAddButton()
                .build();

        rvHelper.addItemFragmentToLayout(this, R.id.rv_helper_placeholder);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder) {
        //здесь выбираем, какие поля хранимого объекта отобразятся в каких частях CardView
        final DummyItem item = (DummyItem) holder.getItem();

        ((TextView) holder.getView().findViewById(R.id.price)).setText(String.valueOf(item.getPrice()));
        ((TextView) holder.getView().findViewById(R.id.description)).setText(item.getDescription());
        String dateString = Util.getStringDateTimeFromCal(item.getDate());
        ((TextView) holder.getView().findViewById(R.id.date)).setText(dateString);
        ImageButton btnDelete = ((ImageButton) holder.getView().findViewById(R.id.btnDelete));

        //здесь устанавливаем слушатели
        holder.getView().setOnClickListener(view -> onListItemClick(item, 0));
        btnDelete.setOnClickListener(view -> onListItemClick(item, 1));
    }

    private void onListItemClick(DummyItem item, int clickedActionId) {
        switch (clickedActionId) {
            case 0://клик по строке. просто покажем тост, по чему мы кликнули
                String msg = "You selected item " + item.toString();
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                break;
            case 1://запрос на удаление
                rvHelper.removeItemFromList(item);
                dummyItemsSource.remove(item);
                break;
        }
    }

    @Override
    public void onAddNewItemDialogResult(List<String> newItemParams) {
        //создадим из полученных данных новый хранимый объект
        int price = 0;
        try {
            price = Integer.parseInt(newItemParams.get(0));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.NumberFormatException, Toast.LENGTH_SHORT).show();
        }
        DummyItem newItem = new DummyItem(Calendar.getInstance(), price, newItemParams.get(1));
        newItem.setId(dummyItemsSource.create(newItem));//сохраним его в бд
        if(rvHelper != null) {
            rvHelper.addItemToList(newItem);//добавим его в лист
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_INDEX_OF_SORT_METHOD, rvHelper.getIndexOfSortMethod());
        super.onSaveInstanceState(outState);
    }
}
