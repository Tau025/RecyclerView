package com.devtau.recyclerview.model;

import android.content.Context;
import android.content.res.Resources;
import com.devtau.recyclerview.R;
import java.util.ArrayList;
import java.util.Comparator;
/**
 * Компараторы необходимы, если вы собираетесь сортировать лист объектов этого класса
 * ответ <0 говорит о том, что сравнение не прошло проверку и нужна перестановка
 */
public class DummyItemComparators {
    public static Comparator<DummyItem> FIRST_FRESH = (first, second) -> {
        long firstLong = first.getDate().getTimeInMillis();
        long secondLong = second.getDate().getTimeInMillis();
        int result;
        if (secondLong < firstLong) result = -1;
        else if (firstLong == secondLong) result = 0;
        else result = 1;
        return result;
    };
    public static Comparator<DummyItem> FIRST_OLD = (first, second) -> {
        long firstLong = first.getDate().getTimeInMillis();
        long secondLong = second.getDate().getTimeInMillis();
        int result;
        if (firstLong < secondLong) result = -1;
        else if (firstLong == secondLong) result = 0;
        else result = 1;
        return result;
    };
    public static Comparator<DummyItem> FIRST_HIGHER_PRICE =
            (first, second) -> second.getPrice() - first.getPrice();
    public static Comparator<DummyItem> FIRST_LOWER_PRICE =
            (first, second) -> first.getPrice() - second.getPrice();
    public static Comparator<DummyItem> ALPHABETICAL =
            (first, second) -> first.getDescription().compareTo(second.getDescription());
    public static Comparator<DummyItem> REV_ALPHABETICAL =
            (first, second) -> second.getDescription().compareTo(first.getDescription());

    //альтернатива без лямбды
//        public static Comparator<DummyItem> FIRST_LOWER_PRICE = new Comparator<DummyItem>() {
//            @Override
//            public int compare(DummyItem first, DummyItem second) {
//                return first.getPrice() - second.getPrice();
//            }
//        };

    public static Comparator provideComparator(int indexOfSortMethod) {
        switch (indexOfSortMethod) {
            case 0: return FIRST_FRESH;
            case 1: return FIRST_OLD;
            case 2: return FIRST_HIGHER_PRICE;
            case 3: return FIRST_LOWER_PRICE;
            case 4: return ALPHABETICAL;
            case 5: return REV_ALPHABETICAL;
            default: return FIRST_FRESH;
        }
    }

    public static ArrayList<String> getComparatorsNames(Context context) {
        Resources res = context.getResources();
        ArrayList<String> comparatorsNames = new ArrayList<>();
        comparatorsNames.add(res.getString(R.string.first_fresh));
        comparatorsNames.add(res.getString(R.string.first_old));
        comparatorsNames.add(res.getString(R.string.first_higher_price));
        comparatorsNames.add(res.getString(R.string.first_lower_price));
        comparatorsNames.add(res.getString(R.string.alphabetical));
        comparatorsNames.add(res.getString(R.string.rev_alphabetical));
        return comparatorsNames;
    }
}
