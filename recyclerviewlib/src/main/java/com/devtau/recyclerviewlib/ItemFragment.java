package com.devtau.recyclerviewlib;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.devtau.recyclerviewlib.util.Constants;
import com.devtau.recyclerviewlib.util.Logger;
import com.devtau.recyclerviewlib.util.Util;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
/**
 * Фрагмент обобщающий сортировку, вставку новой записи с отображением самого списка
 */
public class ItemFragment<T extends Parcelable> extends Fragment implements
        SortAndAddFragment.OnSortAndAddFragmentListener,
        RVFragment.OnRVFragmentListener<T> {
    public static final String ARG_ITEMS_LIST = "itemsList";
    public static final String ARG_COLUMN_COUNT = "columnCount";
    public static final String ARG_LIST_ITEM_LAYOUT_ID = "listItemLayoutId";
    public static final String ARG_INDEX_OF_SORT_METHOD = "indexOfSortMethod";
    public static final String ARG_COMPARATORS = "comparators";
    public static final String ARG_COMPARATORS_NAMES = "comparatorsNames";
    private RVHelperInterface listener;
    private int indexOfSortMethod = Constants.DEFAULT_SORT_BY;
    private HashMap<Integer, Comparator> comparators;

    private SortAndAddFragment sortAndAddFragment;
    private RVFragment rvFragment;

    //Обязательный пустой конструктор
    public ItemFragment() { }
    //мы не можем использовать статический метод newInstance() для создания фрагмента с дженериками

    @Override
    public void onAttach(Context context) {
        //если фрагмент является вложенным, context - это активность, держащая фрагмент-родитель, а не сам родитель
        Logger.d("ItemFragment.onAttach()");
        super.onAttach(context);
        try {
            //проверим, реализован ли нужный интерфейс родительским фрагментом или активностью
            listener = (RVHelperInterface) getParentFragment();
            if (listener == null) {
                listener = (RVHelperInterface) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RVHelperInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d("ItemFragment.onCreateView()");
        View view = inflater.inflate(R.layout.fragment_rv_helper, container, false);

        //рекомендовано читать аргументы из бандла не здесь, а в onCreate, но тогда нам нужно создавать
        //все переменные на уровне фрагмента, а не локальные для метода, которые мы сразу отдаем в конструкторы
        ArrayList<T> itemsList = new ArrayList<>();
        int columnCount = Constants.DEFAULT_COLUMN_COUNT;
        int listItemLayoutId = Constants.DEFAULT_LIST_ITEM_LAYOUT;
        ArrayList<String> comparatorsNames = Util.getDefaultComparatorsNames(getContext());

        if (getArguments() != null) {
            itemsList = getArguments().getParcelableArrayList(ARG_ITEMS_LIST);
            columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            listItemLayoutId = getArguments().getInt(ARG_LIST_ITEM_LAYOUT_ID);
            indexOfSortMethod = getArguments().getInt(ARG_INDEX_OF_SORT_METHOD);
            comparators = (HashMap<Integer, Comparator>) getArguments().getSerializable(ARG_COMPARATORS);
            comparatorsNames = getArguments().getStringArrayList(ARG_COMPARATORS_NAMES);
        }

        sortAndAddFragment = createSortAndAddFragment(indexOfSortMethod, comparatorsNames);
        rvFragment = createRVFragment(itemsList, columnCount, listItemLayoutId, indexOfSortMethod);
        addFragmentToLayout(R.id.sort_and_add_placeholder, sortAndAddFragment);
        addFragmentToLayout(R.id.recycler_view_placeholder, rvFragment);

        return view;
    }

    public SortAndAddFragment createSortAndAddFragment(int indexOfSortMethod, ArrayList<String> comparatorsNames) {
        SortAndAddFragment fragment = new SortAndAddFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX_OF_SORT_METHOD, indexOfSortMethod);
        args.putStringArrayList(ARG_COMPARATORS_NAMES, comparatorsNames);
        fragment.setArguments(args);
        return fragment;
    }

    public RVFragment createRVFragment(ArrayList<T> itemsList, int columnCount,
                                       int listItemLayoutId, int indexOfSortMethod) {
        RVFragment fragment = new RVFragment();
        Bundle args = new Bundle();

        args.putParcelableArrayList(ARG_ITEMS_LIST, itemsList);
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_LIST_ITEM_LAYOUT_ID, listItemLayoutId);
        args.putInt(ARG_INDEX_OF_SORT_METHOD, indexOfSortMethod);

        fragment.setArguments(args);
        return fragment;
    }

    private void addFragmentToLayout(int placeholderID, Fragment fragment){
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(placeholderID, fragment);
        ft.commit();
    }


    public void addItemToList(T item) {
        rvFragment.addItemToList(item, comparators.get(indexOfSortMethod));
    }

    public void removeItemFromList(T item) {
        rvFragment.removeItemFromList(item);
    }

    //переназначает лист адаптера
    public void setList(ArrayList<T> itemsList){
        rvFragment.setList(itemsList);
    }


    @Override
    public void onDetach() {
        Logger.d("ItemFragment.onDetach()");
        super.onDetach();
        listener = null;
    }


    //метод необходим для savedInstanceState
    public int getIndexOfSortMethod() {
        return indexOfSortMethod;
    }


    //сортирует лист
    //этот же метод используется и для обработки запросов на сортировку извне RVHelper
    @Override
    public void onSpinnerItemSelected(int indexOfSortMethod) {
        this.indexOfSortMethod = indexOfSortMethod;
        rvFragment.sort(comparators.get(indexOfSortMethod));
    }

    @Override
    public void onAddNewItemDialogResult(List<String> newItemParams) {
        listener.onAddNewItemDialogResult(newItemParams);
    }

    //этот фрагмент содержит вложенный фрагмент списка
    //можно было бы настроить прямую реализацию интерфейса взаимодействия с его дочерним списком активностью
    //однако ItemFragment может быть частью другого фрагмента, ссылку на который мы не можем получить
    @Override
    public void onBindViewHolder(MyItemRVAdapter.ViewHolder holder) {
        listener.onBindViewHolder(holder);
    }

    @Override
    public Comparator provideComparator(int indexOfSortMethod) {
        return comparators.get(indexOfSortMethod);
    }
}
