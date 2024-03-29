package com.cityfleet.model;

import java.util.List;

/**
 * Created by vika on 21.04.16.
 */
public class PagesResult<T> {
    private int count;
    private int available;
    private String next;
    private String previous;
    private List<T> results;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<T> getResults() {
        return results;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
