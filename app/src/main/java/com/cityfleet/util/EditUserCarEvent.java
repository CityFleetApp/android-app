package com.cityfleet.util;

import com.cityfleet.model.UserEditInfo;

/**
 * Created by vika on 31.03.16.
 */
public class EditUserCarEvent {
   private  UserEditInfo editInfo;

    public EditUserCarEvent(UserEditInfo editInfo) {
        this.editInfo = editInfo;
    }

    public UserEditInfo getEditInfo() {
        return editInfo;
    }
}
