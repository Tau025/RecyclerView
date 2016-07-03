package com.devtau.recyclerviewlib;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
/**
 * Клиент передает указанные параметры конструктора и реализует интерфейс RVHelperInterface
 * класс хранимого объекта должен:
 * 1 - переопределить методы equals() и hashCode() - для корректного удаления элемента из списка
 * 2 - реализовать Parcelable
 */
public class RVHelper<T extends Parcelable> {
    private ItemFragment itemFragment;

    public RVHelper(ArrayList<T> itemsList, int columnCount, int listItemLayoutId,
                    int indexOfSortMethod, HashMap<Integer, Comparator> comparators,
                    ArrayList<String> comparatorsNames) {
        itemFragment = createItemFragment(itemsList, columnCount, listItemLayoutId, indexOfSortMethod,
                comparators, comparatorsNames);
    }

    public ItemFragment createItemFragment(ArrayList<T> itemsList, int columnCount, int listItemLayoutId,
                                           int indexOfSortMethod, HashMap<Integer, Comparator> comparators,
                                           ArrayList<String> comparatorsNames) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();

        args.putParcelableArrayList(ItemFragment.ARG_ITEMS_LIST, itemsList);
        args.putInt(ItemFragment.ARG_COLUMN_COUNT, columnCount);
        args.putInt(ItemFragment.ARG_LIST_ITEM_LAYOUT_ID, listItemLayoutId);
        args.putInt(ItemFragment.ARG_INDEX_OF_SORT_METHOD, indexOfSortMethod);
        args.putSerializable(ItemFragment.ARG_COMPARATORS, comparators);
        args.putStringArrayList(ItemFragment.ARG_COMPARATORS_NAMES, comparatorsNames);

        fragment.setArguments(args);
        return fragment;
    }

    public void addItemFragmentToLayout(AppCompatActivity activity, int placeholderId){
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(placeholderId, itemFragment);
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.commit();
    }


    //метод необходим для saveInstanceState
    public int getIndexOfSortMethod() {
        return itemFragment.getIndexOfSortMethod();
    }


    //вставляет новую строку в лист
    //метод публичный, т.к. при работе с бд _id хранимого объекта создается только после
    //вставки записи в бд, а к ней у списка доступа нет
    public void addItemToList(T item) {
        itemFragment.addItemToList(item);
    }


    //удаляет строку из листа
    //физическое удаление из бд - ответственность клиента, не входящая в функционал RVHelper
    public void removeItemFromList(T item) {
        itemFragment.removeItemFromList(item);
    }

    //переназначает лист адаптера
    //клиент сам проверяет, что к новому передаваемому листу могут быть применены старые компараторы
    public void setList(ArrayList<T> itemsList){
        itemFragment.setList(itemsList);
    }

    //сортирует лист
    //обычно эта команда генерируется внутри RVHelper выбором одного из вариантов в спиннере
    public void sort(int indexOfSortMethod) {
        itemFragment.onSpinnerItemSelected(indexOfSortMethod);
    }
}
