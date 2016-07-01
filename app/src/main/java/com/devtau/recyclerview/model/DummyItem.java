package com.devtau.recyclerview.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Comparator;
import com.devtau.recyclerview.database.tables.DummyItemsTable;
import com.devtau.recyclerview.util.Util;
import com.devtau.recyclerviewlib.SortBy;
import static com.devtau.recyclerview.database.tables.DummyItemsTable.*;

public class DummyItem implements Parcelable{
    private long id;
    private Calendar date;
    private int price;
    private String description;

    public DummyItem(Calendar date, int price, String description) {
        this.id = -1;//необходимо для использования автоинкремента id новой записи в sql
        this.date = date;
        this.price = price;
        this.description = description;
    }

    public DummyItem(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        date = null;
        try {
            date = Calendar.getInstance();
            String dateString = cursor.getString(cursor.getColumnIndex(DummyItemsTable.DATE));
            date.setTime(Util.dateFormat.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        price = cursor.getInt(cursor.getColumnIndex(PRICE));
        description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
    }

    private DummyItem(Parcel parcel) {
        id = parcel.readLong();
        date = Calendar.getInstance();
        date.setTimeInMillis(parcel.readLong());
        price = parcel.readInt();
        description = parcel.readString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass() || id == -1) return false;
        DummyItem that = (DummyItem) obj;
        if (that.id == -1) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id != -1 ? 31 * id : 0);
    }

    public void update(Calendar date, int price, String description) {
        this.date = date;
        this.price = price;
        this.description = description;
    }


    //геттеры
    public long getId() {
        return id;
    }

    public Calendar getDate() {
        return date;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }


    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DummyItem{" +
                "id=" + id +
                ", date=" + Util.dateFormat.format(date.getTime()) +
                ", price=" + price +
                ", description='" + description + '\'' +
                '}';
    }


    //Parcelable methods
    public static final Creator<DummyItem> CREATOR = new Creator<DummyItem>() {
        @Override
        public DummyItem createFromParcel(Parcel parcel) {
            return new DummyItem(parcel);
        }

        @Override
        public DummyItem[] newArray(int size) {
            return new DummyItem[size];
        }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(date.getTimeInMillis());
        parcel.writeInt(price);
        parcel.writeString(description);
    }



    //компараторы необходимы, если вы собираетесь сортировать лист объектов этого класса
    //ответ <0 говорит о том, что сравнение не прошло проверку и нужна перестановка
    public static class Comparators {
        public static Comparator<DummyItem> FIRST_FRESH = (first, second) -> {
            long firstLong = first.getDate().getTimeInMillis();
            long secondLong = second.getDate().getTimeInMillis();
            int result;
            if(secondLong < firstLong) result = -1;
            else if(firstLong == secondLong) result = 0;
            else result = 1;
            return result;
        };
        public static Comparator<DummyItem> FIRST_OLD = (first, second) -> {
            long firstLong = first.getDate().getTimeInMillis();
            long secondLong = second.getDate().getTimeInMillis();
            int result;
            if(firstLong < secondLong) result = -1;
            else if(firstLong == secondLong) result = 0;
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

        public static Comparator<DummyItem> getProperComparator(SortBy sortBy) {
            switch (sortBy) {
                case FIRST_FRESH: return FIRST_FRESH;
                case FIRST_OLD: return FIRST_OLD;
                case FIRST_HIGHER_PRICE: return FIRST_HIGHER_PRICE;
                case FIRST_LOWER_PRICE: return FIRST_LOWER_PRICE;
                case ALPHABETICAL: return ALPHABETICAL;
                case REV_ALPHABETICAL: return REV_ALPHABETICAL;
                default: return FIRST_FRESH;
            }
        }
    }
}
