package com.devtau.recyclerviewlib;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;
import com.devtau.recyclerviewlib.util.Constants;
import com.devtau.recyclerviewlib.util.Logger;
import com.devtau.recyclerviewlib.util.Util;
/**
 * Фрагмент для опционально добавляемых контролов сортировки и вставки новой записи в список
 */
public class SortAndAddFragment extends Fragment implements
        AddNewItemDF.onAddNewItemDFListener {
    private OnSortAndAddFragmentListener listener;
    private Spinner spnSort;

    //Обязательный пустой конструктор
    public SortAndAddFragment() { }

    @Override
    public void onAttach(Context context) {
        //если фрагмент является вложенным, context - это активность, держащая фрагмент-родитель, а не сам родитель
        Logger.d("SortAndAddFragment.onAttach()");
        super.onAttach(context);
        try {
            //проверим, реализован ли нужный интерфейс родительским фрагментом или активностью
            listener = (OnSortAndAddFragmentListener) getParentFragment();
            if (listener == null) {
                listener = (OnSortAndAddFragmentListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnSortAndAddFragmentListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d("SortAndAddFragment.onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_sort_and_add, container, false);

        int indexOfSortMethod = Constants.DEFAULT_SORT_BY;
        ArrayList<String> comparatorsNames = Util.getDefaultComparatorsNames(getContext());
        if (getArguments() != null) {
            indexOfSortMethod = getArguments().getInt(ItemFragment.ARG_INDEX_OF_SORT_METHOD);
            comparatorsNames = getArguments().getStringArrayList(ItemFragment.ARG_COMPARATORS_NAMES);
        }

        initControls(rootView, indexOfSortMethod, comparatorsNames);
        return rootView;
    }

    private void initControls(View rootView, int indexOfSortMethod, ArrayList<String> comparatorsNames) {
        spnSort = (Spinner) rootView.findViewById(R.id.spnSort);
        Button btnAdd = (Button) rootView.findViewById(R.id.btnAdd);

        if(spnSort != null && btnAdd != null) {
            spnSort.setAdapter(new SpinnerAdapter(rootView.getContext(), comparatorsNames));
            spnSort.setSelection(indexOfSortMethod);
            spnSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    listener.onSpinnerItemSelected(spnSort.getSelectedItemPosition());
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {/*NOP*/}
            });

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AddNewItemDF().show(getChildFragmentManager(), AddNewItemDF.FRAGMENT_TAG);
                }
            });
        }
    }

    @Override
    public void onDetach() {
        Logger.d("SortAndAddFragment.onDetach()");
        super.onDetach();
        listener = null;
    }

    @Override
    public void onAddNewItemDialogResult(List<String> newItemParams) {
        listener.onAddNewItemDialogResult(newItemParams);
    }


    //интерфейс для общения SortAndAddFragment со своим родителем
    public interface OnSortAndAddFragmentListener {
        void onSpinnerItemSelected(int indexOfSortMethod);
        void onAddNewItemDialogResult(List<String> newItemParams);
    }


    //адаптер спиннера. здесь можно настроить отображение его строк в свернутом и развернутом виде
    public class SpinnerAdapter extends ArrayAdapter<String> {
        ArrayList<String> comparatorsNames;

        public SpinnerAdapter(Context context, ArrayList<String> comparatorsNames) {
            super(context, 0, comparatorsNames);
            this.comparatorsNames = comparatorsNames;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CheckedTextView text = (CheckedTextView) convertView;
            if (text == null) {
                text = (CheckedTextView) LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_spinner_dropdown_item,  parent, false);
            }
            text.setText(getItem(position));
            return text;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            CheckedTextView text = (CheckedTextView) convertView;
            if (text == null) {
                text = (CheckedTextView) LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_spinner_dropdown_item,  parent, false);
            }
            text.setText(getItem(position));
            return text;
        }
    }
}
