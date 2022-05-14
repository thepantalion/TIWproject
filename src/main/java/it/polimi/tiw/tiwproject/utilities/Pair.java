package it.polimi.tiw.tiwproject.utilities;

public class Pair<A, B> {
    private final A _1;
    private B _2;

    public Pair(A _1, B _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public A get_1() {
        return _1;
    }

    public B get_2() {
        return _2;
    }

    public void set_2(B _2){
        this._2 = _2;
    }
}
