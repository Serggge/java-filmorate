package ru.yandex.practicum.filmorate.model;

public enum MpaRating {

    G ("G"),
    PG ("PG"),
    PG_13 ("PG-13"),
    R ("R"),
    NC_17 ("NC-17");
    private final String rating;

    private MpaRating(String rating) {
        this.rating = rating;
    }

    @Override
    public String toString(){
        return rating;
    }

}
