package com.devtau.recyclerviewlib;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import com.devtau.recyclerviewlib.util.Constants;
import com.devtau.recyclerviewlib.util.Logger;
import java.util.List;
/**
 * Фрагмент для опционально добавляемых контролов сортировки и вставки новой записи в список
 */
public class SortAndAddFragment<T extends Parcelable> extends Fragment implements
        AddNewItemDF.onAddNewItemDFListener {
    private OnSortAndAddFragmentListener listener;
    private Spinner spnSort;

    //Обязательный пустой конструктор
    public SortAndAddFragment() { }
    //мы не можем использовать статический метод newInstance() для создания фрагмента с дженериками

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

        SortBy sortBy = Constants.DEFAULT_SORT_BY;
        if (getArguments() != null) {
            sortBy = (SortBy) getArguments().getSerializable(ItemFragment.ARG_SORT_BY);
        }

        initControls(rootView, sortBy);
        return rootView;
    }

    private void initControls(View rootView, SortBy sortBy) {
        spnSort = (Spinner) rootView.findViewById(R.id.spnSort);
        Button btnAdd = (Button) rootView.findViewById(R.id.btnAdd);

        if(spnSort != null && btnAdd != null) {
            spnSort.setAdapter(new SpinnerAdapter(rootView.getContext()));
            spnSort.setSelection(sortBy.getId());
            spnSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    listener.onSpinnerItemSelected(SortBy.getById(spnSort.getSelectedItemPosition()));
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


    public interface OnSortAndAddFragmentListener {
        void onSpinnerItemSelected(SortBy selectedSortBy);
        void onAddNewItemDialogResult(List<String> newItemParams);
    }



    public class SpinnerAdapter extends ArrayAdapter<SortBy> {
        public SpinnerAdapter(Context context) {
            super(context, 0, SortBy.values());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CheckedTextView text = (CheckedTextView) convertView;
            if (text == null) {
                text = (CheckedTextView) LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_spinner_dropdown_item,  parent, false);
            }
            text.setText(getItem(position).getDescriptionId());
            return text;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            CheckedTextView text = (CheckedTextView) convertView;
            if (text == null) {
                text = (CheckedTextView) LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_spinner_dropdown_item,  parent, false);
            }
            text.setText(getItem(position).getDescriptionId());
            return text;
        }
    }
}
