package com.devtau.recyclerviewlib;

public enum SortBy {
    //TODO: настройте варианты сортировки
    FIRST_FRESH(0, R.string.first_fresh),
    FIRST_OLD(1, R.string.first_old),
    FIRST_HIGHER_PRICE(2, R.string.first_higher_price),
    FIRST_LOWER_PRICE(3, R.string.first_lower_price),
    ALPHABETICAL(4, R.string.alphabetical),
    REV_ALPHABETICAL(5, R.string.rev_alphabetical);

    //здесь нужен int, т.к. индекс в спиннере это int, а не long
    private final int id;
    private final int captionId;

    SortBy(int id, int captionId) {
        this.id = id;
        this.captionId = captionId;
    }

    public static SortBy getById(int id) {
        for (SortBy x : SortBy.values()) {
            if (x.id == id) return x;
        }
        throw new IllegalArgumentException();
    }

    public int getId() {
        return id;
    }

    public int getDescriptionId() {
        return captionId;
    }
}
