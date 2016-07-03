package com.devtau.recyclerviewlib;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import com.devtau.recyclerviewlib.util.Constants;
import com.devtau.recyclerviewlib.util.Util;
/**
 * Клиент передает указанные параметры конструктора и реализует интерфейс RVHelperInterface
 * класс хранимого объекта должен:
 * 1 - переопределить методы equals() и hashCode() - для корректного удаления элемента из списка
 * 2 - реализовать Parcelable
 */
public class RVHelper<T extends Parcelable> {
    private ItemFragment itemFragment;

    public RVHelper(Builder<T> builder) {
        itemFragment = new ItemFragment();
        Bundle args = new Bundle();

        args.putParcelableArrayList(ItemFragment.ARG_ITEMS_LIST, builder.itemsList);
        args.putInt(ItemFragment.ARG_COLUMN_COUNT, builder.columnCount);
        args.putInt(ItemFragment.ARG_LIST_ITEM_LAYOUT_ID, builder.listItemLayoutId);
        args.putInt(ItemFragment.ARG_INDEX_OF_SORT_METHOD, builder.indexOfSortMethod);
        args.putSerializable(ItemFragment.ARG_COMPARATORS, builder.comparators);
        args.putStringArrayList(ItemFragment.ARG_COMPARATORS_NAMES, builder.comparatorsNames);

        itemFragment.setArguments(args);
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



    public static class Builder<T extends Parcelable>{
        private ArrayList<T> itemsList; //обязательный параметр. нет дефолта
        //все параметры ниже не обязательны
        private int columnCount = Constants.DEFAULT_COLUMN_COUNT;
        private int listItemLayoutId = Constants.DEFAULT_LIST_ITEM_LAYOUT;
        //три параметра ниже для использования сортировки
        private HashMap<Integer, Comparator> comparators;//нет дефолта
        private ArrayList<String> comparatorsNames;//дефолт назначается в конструкторе
        private int indexOfSortMethod = Constants.DEFAULT_SORT_BY;

        private Builder(Context context) {
            comparatorsNames = Util.getDefaultComparatorsNames(context);
        }

        public static <T extends Parcelable>Builder<T> start(Context context) {
            return new Builder<>(context);
        }

        public Builder setList(ArrayList<T> itemsList) {
            this.itemsList = itemsList;
            return this;
        }

        public Builder withColumnCount(int columnCount) {
            this.columnCount = columnCount;
            return this;
        }

        public Builder withListItemLayoutId(int listItemLayoutId) {
            this.listItemLayoutId = listItemLayoutId;
            return this;
        }

        public Builder withSortAndAdd(HashMap<Integer, Comparator> comparators,
                                      ArrayList<String> comparatorsNames, int indexOfSortMethod) {
            this.comparators = comparators;
            this.comparatorsNames = comparatorsNames;
            this.indexOfSortMethod = indexOfSortMethod;

            //TODO: действия по добавлению SortAndAddFragment
            return this;
        }

        public RVHelper<T> build() {
            return new RVHelper(this);
        }
    }
}
