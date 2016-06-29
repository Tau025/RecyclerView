package com.devtau.recyclerview.recycler_view_frag;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.devtau.recyclerview.model.DummyItem;
import java.util.ArrayList;
/**
 * Клиент передает указанные параметры конструктора,
 * выбирает класс хранимого объекта и настраивает onBindViewHolder()
 * при переносе в новый проект не забудьте взять R.layout.fragment_recycler_view
 */
public class MyRecyclerView {
    private ItemFragment itemFragment;

    public MyRecyclerView(ArrayList<DummyItem> itemsList, int columnCount, int listItemLayoutId,
                          SortBy sortBy) {
        itemFragment = ItemFragment.newInstance(itemsList, columnCount, listItemLayoutId, sortBy);
    }

    public void addItemFragmentToLayout(AppCompatActivity activity, int placeholderId){
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(placeholderId, itemFragment);
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.commit();
    }


    //метод публичный, т.к. при работе с бд _id хранимого объекта создается только после
    //вставки записи в бд, а к ней у списка доступа нет
    public void addItemToList(DummyItem item) {
        itemFragment.addItemToList(item);
    }


    //метод необходим для savedInstanceState
    public SortBy getSortByState() {
        return itemFragment.getSortByState();
    }


    //методы, вызываемые, если команда на сортировку/удаление/переназначечение поступает извне списка
    //обычно такие команды генерируются внутри
    public void sort(SortBy sortBy) {
        itemFragment.sortFromOutside(sortBy);
    }

    public void removeItemFromList(DummyItem item) {
        itemFragment.removeItemFromList(item);
    }

    public void setList(ArrayList<DummyItem> itemsList){
        itemFragment.setList(itemsList);
    }
}
