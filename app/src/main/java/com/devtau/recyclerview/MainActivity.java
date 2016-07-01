package com.devtau.recyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.devtau.recyclerview.database.DataSource;
import com.devtau.recyclerview.database.sources.DummyItemsSource;
import com.devtau.recyclerview.model.DummyItem;
import com.devtau.recyclerviewlib.ItemFragment;
import com.devtau.recyclerviewlib.MyRecyclerViewHelper;
import com.devtau.recyclerviewlib.SortBy;
import com.devtau.recyclerview.util.Constants;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ItemFragment.OnItemFragmentListener<DummyItem> {
    private static final String SORT_BY_EXTRA = "SortBy";
    private MyRecyclerViewHelper myRecyclerViewHelper;
    //рекомендуется хранить ссылку на dataSource, если таблиц больше одной
    private DummyItemsSource dummyItemsSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dummyItemsSource = new DataSource(this).getDummyItemsSource();
        ArrayList<DummyItem> itemsList = dummyItemsSource.getItemsList();

        SortBy sortBy = Constants.DEFAULT_SORT_BY;
        if(savedInstanceState != null) {
            sortBy = (SortBy) savedInstanceState.getSerializable(SORT_BY_EXTRA);
        }
        myRecyclerViewHelper = new MyRecyclerViewHelper(itemsList, 1, R.layout.list_item, sortBy);
        myRecyclerViewHelper.addItemFragmentToLayout(this, R.id.rv_helper_placeholder);
    }


    @Override
    public void onListItemClick(DummyItem item) {
        String msg = "You selected item " + item.toString();
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onListItemClickDelete(DummyItem item) {
        //обратите внимание, что удаление из списка обрабатывается внутри списка
        //клиенту нужно только передать инструкцию в бд
        dummyItemsSource.remove(item);
    }

    @Override
    public void onAddNewItemDialogResult(List<String> newItemParams) {
        //создадим из полученных данных новый хранимый объект
        DummyItem newItem = new DummyItem(
                Calendar.getInstance(),
                Integer.parseInt(newItemParams.get(0)),
                newItemParams.get(1));
        newItem.setId(dummyItemsSource.create(newItem));//сохраним его в бд
        if(myRecyclerViewHelper != null) {
            myRecyclerViewHelper.addItemToList(newItem);//добавим его в лист
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SORT_BY_EXTRA, myRecyclerViewHelper.getSortByState());
        super.onSaveInstanceState(outState);
    }
}
