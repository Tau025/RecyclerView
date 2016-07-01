package com.devtau.recyclerviewlib;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.devtau.recyclerviewlib.util.Constants;
import com.devtau.recyclerviewlib.util.Logger;
import java.util.ArrayList;
/**
 * Фрагмент для самого списка
 */
public class RVFragment<T extends Parcelable> extends Fragment {
    private OnRVFragmentListener listener;
    private MyItemRVAdapter adapter;
    private RecyclerView recyclerView;

    //Обязательный пустой конструктор
    public RVFragment() { }
    //мы не можем использовать статический метод newInstance() для создания фрагмента с дженериками

    @Override
    public void onAttach(Context context) {
        //если фрагмент является вложенным, context - это активность, держащая фрагмент-родитель, а не сам родитель
        Logger.d("RVFragment.onAttach()");
        super.onAttach(context);
        try {
            //проверим, реализован ли нужный интерфейс родительским фрагментом или активностью
            listener = (OnRVFragmentListener) getParentFragment();
            if (listener == null) {
                listener = (OnRVFragmentListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnRVFragmentListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d("RVFragment.onCreateView()");
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        //рекомендовано читать аргументы из бандла не здесь, а в onCreate, но тогда нам нужно создавать
        //все переменные на уровне фрагмента, а не локальные для метода, которые мы сразу отдаем в адаптер
        ArrayList<T> itemsList = new ArrayList<>();
        int columnCount = Constants.DEFAULT_COLUMN_COUNT;
        int listItemLayoutId = Constants.DEFAULT_LIST_ITEM_LAYOUT;
        SortBy sortBy = Constants.DEFAULT_SORT_BY;

        if (getArguments() != null) {
            itemsList = getArguments().getParcelableArrayList(ItemFragment.ARG_ITEMS_LIST);
            columnCount = getArguments().getInt(ItemFragment.ARG_COLUMN_COUNT);
            listItemLayoutId = getArguments().getInt(ItemFragment.ARG_LIST_ITEM_LAYOUT_ID);
            sortBy = (SortBy) getArguments().getSerializable(ItemFragment.ARG_SORT_BY);
        }


        // установим адаптер списка
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (columnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
            }
            adapter = new MyItemRVAdapter(itemsList, listItemLayoutId, sortBy, listener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    //метод публичный, т.к. при работе с бд _id хранимого объекта создается только после
    //вставки записи в бд, а к ней у списка доступа нет
    public void addItemToList(T item, SortBy sortBy) {
        int position = adapter.addItemToList(item, sortBy);
        recyclerView.scrollToPosition(position);
    }



    //методы, вызываемые, если команда на сортировку/удаление/переназначечение поступает извне списка
    //обычно такие команды генерируются внутри
    public void sort(SortBy sortBy) {
        adapter.sortAndNotify(sortBy);
    }

    public void removeItemFromList(T item) {
        adapter.removeItemFromList(item);
    }

    public void setList(ArrayList<T> itemsList){
        adapter.setList(itemsList);
    }

    @Override
    public void onDetach() {
        Logger.d("RVFragment.onDetach()");
        super.onDetach();
        listener = null;
    }

    public interface OnRVFragmentListener<T extends Parcelable> {
        //здесь действие не обрабатывается, а лишь пробрасывается дальше
        void onListItemClick(T item, int clickedActionId);
    }
}
