package com.devtau.recyclerview.recycler_view_frag;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import com.devtau.recyclerview.R;
import com.devtau.recyclerview.model.DummyItem;
import com.devtau.recyclerview.util.Logger;
import java.util.Calendar;

public class CustomViewDF extends DialogFragment {
    public static final String FRAGMENT_TAG = "CustomViewDF";
    private EditText etPrice;
    private EditText etDescription;
    private onCustomViewDFListener listener;

    @Override
    public void onAttach(Context context) {
        //если фрагмент является вложенным, context - это активность, держащая фрагмент-родитель, а не сам родитель
        Logger.d("ItemFragment.onAttach()");
        super.onAttach(context);
        try {
            //проверим, реализован ли нужный интерфейс родительским фрагментом или активностью
            listener = (onCustomViewDFListener) getParentFragment();
            if (listener == null) {
                listener = (onCustomViewDFListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onCustomViewDFListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_custom_view, null);
        etPrice = (EditText) rootView.findViewById(R.id.etPrice);
        etDescription = (EditText) rootView.findViewById(R.id.etDescription);

        builder.setView(rootView)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(etPrice != null && etDescription != null) {
                            // TODO: настройте создание нового хранимого объекта
                            //подготовим компоненты и объединим их в новый объект
                            Calendar now = Calendar.getInstance();
                            int price = 0;
                            try {
                                price = Integer.parseInt(etPrice.getText().toString());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            String description = etDescription.getText().toString();
                            //new DummyItem невозможно реализовать дженериком
                            DummyItem newItem = new DummyItem(now, price, description);

                            //передадим собранный объект на обработку слушателю
                            listener.onAddNewItemDialogResult(newItem);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {/*NOP*/}
                });
        return builder.create();
    }

    public interface onCustomViewDFListener {
        // TODO: настройте интерфейс взаимодействия с диалогом
        void onAddNewItemDialogResult(DummyItem newItem);
    }
}