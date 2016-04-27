package com.cityfleet.model;

import java.util.ArrayList;
import java.util.List;

public class AddContactsBody {
    private List<String> contacts = new ArrayList<String>();

    public AddContactsBody(List<String> contacts) {
        this.contacts = contacts;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }
}
