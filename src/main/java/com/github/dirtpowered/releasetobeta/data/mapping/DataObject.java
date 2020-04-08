package com.github.dirtpowered.releasetobeta.data.mapping;

public class DataObject {
    private int from, to;

    DataObject(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
