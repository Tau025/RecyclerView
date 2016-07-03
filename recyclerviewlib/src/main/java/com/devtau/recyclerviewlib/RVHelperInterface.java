package com.devtau.recyclerviewlib;

import android.os.Parcelable;
import java.util.List;
/**
 * Интерфейс общения хелпера с клиентом
 */
public interface RVHelperInterface<T extends Parcelable> {
    void onBindViewHolder(MyItemRVAdapter.ViewHolder holder);
    void onAddNewItemDialogResult(List<String> newItemParams);
}
