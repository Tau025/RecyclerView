package com.devtau.recyclerviewlib;

import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.devtau.recyclerviewlib.util.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 * Адаптер списка и вьюхолдер строки списка
 */
public class MyItemRVAdapter<T extends Parcelable> extends RecyclerView.Adapter<MyItemRVAdapter.ViewHolder> {
    private ArrayList<T> itemsList;
    private final int listItemLayoutId;
    private final RVFragment.OnRVFragmentListener listener;

    public MyItemRVAdapter(ArrayList<T> itemsList, int listItemLayoutId, Comparator comparator,
                           RVFragment.OnRVFragmentListener listener) {
        Logger.d("MyItemRVAdapter constructor");
        this.itemsList = itemsList;
        this.listItemLayoutId = listItemLayoutId;
        sort(comparator);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        Logger.d("MyItemRVAdapter.onBindViewHolder()");
        holder.item = itemsList.get(position);
        listener.onBindViewHolder(holder);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }



    //публичные методы редактирования хранимого списка
    public void setList(ArrayList<T> itemsList){
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    public int addItemToList(T item, Comparator comparator) {
        itemsList.add(item);
        sort(comparator);
        int position = itemsList.indexOf(item);
        notifyItemInserted(position);
        return position;
    }

    public void removeItemFromList(T item){
        //для корректного удаления элемента из списка реализуйте equals и hashCode у класса хранимого объекта
        int positionInList = itemsList.indexOf(item);
        itemsList.remove(item);
        if(positionInList != -1) {
            notifyItemRemoved(positionInList);
        }
    }

    public void sort(Comparator comparator) {
        Collections.sort(itemsList, comparator);
    }

    public void sortAndNotify(Comparator comparator) {
        sort(comparator);
        notifyDataSetChanged();
    }


    public class ViewHolder<T extends Parcelable> extends RecyclerView.ViewHolder {
        //пока ViewHolder является вложенным классом, адаптер имеет доступ к его private переменным
        private final View view;
        private T item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
        }

        public View getView() {
            return view;
        }

        public T getItem() {
            return item;
        }
    }
}
