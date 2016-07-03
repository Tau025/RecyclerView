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
        RVHelperInterface {
    private static final String ARG_INDEX_OF_SORT_METHOD = "indexOfSortMethod";
    private RVHelper rvHelper;
    private RVHelper rvHelper2;
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
        rvHelper = RVHelper.Builder.<DummyItem> start(this, R.id.rv_helper_placeholder).setList(itemsList, comparators)
                .withColumnCount(1)
                .withListItemLayoutId(R.layout.list_item)
                .withSortSpinner(comparatorsNames, indexOfSortMethod)
                .withAddButton()
                .build();
        rvHelper.addItemFragmentToLayout(this, R.id.rv_helper_placeholder);

        rvHelper2 = RVHelper.Builder.<DummyItem> start(this, R.id.rv_helper_placeholder2).setList(itemsList, comparators)
                .withColumnCount(2)
                .withListItemLayoutId(R.layout.list_item)
                .withSortSpinner(comparatorsNames, indexOfSortMethod)
                .withAddButton()
                .build();
        rvHelper2.addItemFragmentToLayout(this, R.id.rv_helper_placeholder2);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, int rvHelperId) {
        //здесь выбираем, какие поля хранимого объекта отобразятся в каких частях CardView
        final DummyItem item = (DummyItem) holder.getItem();

        ((TextView) holder.getView().findViewById(R.id.price)).setText(String.valueOf(item.getPrice()));
        ((TextView) holder.getView().findViewById(R.id.description)).setText(item.getDescription());
        String dateString = Util.getStringDateTimeFromCal(item.getDate());
        ((TextView) holder.getView().findViewById(R.id.date)).setText(dateString);
        ImageButton btnDelete = ((ImageButton) holder.getView().findViewById(R.id.btnDelete));

        //здесь устанавливаем слушатели
        holder.getView().setOnClickListener(view -> onListItemClick(item, 0, rvHelperId));
        btnDelete.setOnClickListener(view -> onListItemClick(item, 1, rvHelperId));
    }

    private void onListItemClick(DummyItem item, int clickedActionId, int rvHelperId) {
        switch (clickedActionId) {
            case 0://клик по строке. просто покажем тост, по чему мы кликнули
                String msg = "You selected item " + item.toString();
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                break;
            case 1://запрос на удаление
                if(rvHelperId == R.id.rv_helper_placeholder) {
                    dummyItemsSource.remove(item);
                    if(rvHelper != null) rvHelper.removeItemFromList(item);
                } else
                if(rvHelperId == R.id.rv_helper_placeholder2) {
                    //в реальности классом объектов второго листа может быть совсем не DummyItem
                    dummyItemsSource.remove(item);
                    if(rvHelper2 != null) rvHelper2.removeItemFromList(item);
                }
                break;
        }
    }

    @Override
    public void onAddNewItemDialogResult(List<String> newItemParams, int rvHelperId) {
        //создадим из полученных данных новый хранимый объект
        int price = 0;
        try {
            price = Integer.parseInt(newItemParams.get(0));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.NumberFormatException, Toast.LENGTH_SHORT).show();
        }

        switch (rvHelperId) {
            case R.id.rv_helper_placeholder:
                DummyItem newItem = new DummyItem(Calendar.getInstance(), price, newItemParams.get(1));
                newItem.setId(dummyItemsSource.create(newItem));//сохраним его в бд
                if(rvHelper != null) {
                    rvHelper.addItemToList(newItem);//добавим его в лист
                }
                break;

            case R.id.rv_helper_placeholder2:
                //в реальности классом объектов второго листа может быть совсем не DummyItem
                DummyItem newItemOther = new DummyItem(Calendar.getInstance(), price, newItemParams.get(1));
                newItemOther.setId(dummyItemsSource.create(newItemOther));//сохраним его в бд
                if(rvHelper2 != null) {
                    rvHelper2.addItemToList(newItemOther);//добавим его в лист
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_INDEX_OF_SORT_METHOD, rvHelper.getIndexOfSortMethod());
        super.onSaveInstanceState(outState);
    }
}
