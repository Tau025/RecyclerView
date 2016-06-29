package com.devtau.recyclerview.recycler_view_frag;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
/**
 * Клиент передает указанные параметры конструктора,
 * выбирает класс хранимого объекта и настраивает onBindViewHolder()
 * при переносе в новый проект не забудьте взять R.layout.fragment_recycler_view
 */
public class MyRecyclerView<T extends Parcelable> {
    private ItemFragment itemFragment;

    public MyRecyclerView(ArrayList<T> itemsList, int columnCount, int listItemLayoutId,
                          SortBy sortBy) {
        itemFragment = createItemFragment(itemsList, columnCount, listItemLayoutId, sortBy);
    }

    public ItemFragment createItemFragment(ArrayList<T> itemsList, int columnCount,
                                           int listItemLayoutId, SortBy sortBy) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();

        args.putParcelableArrayList(ItemFragment.ARG_ITEMS_LIST, itemsList);
        args.putInt(ItemFragment.ARG_COLUMN_COUNT, columnCount);
        args.putInt(ItemFragment.ARG_LIST_ITEM_LAYOUT_ID, listItemLayoutId);
        args.putSerializable(ItemFragment.ARG_SORT_BY, sortBy);

        fragment.setArguments(args);
        return fragment;
    }

    public void addItemFragmentToLayout(AppCompatActivity activity, int placeholderId){
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(placeholderId, itemFragment);
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.commit();
    }


    //метод публичный, т.к. при работе с бд _id хранимого объекта создается только после
    //вставки записи в бд, а к ней у списка доступа нет
    public void addItemToList(T item) {
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

    public void removeItemFromList(T item) {
        itemFragment.removeItemFromList(item);
    }

    public void setList(ArrayList<T> itemsList){
        itemFragment.setList(itemsList);
    }
}
