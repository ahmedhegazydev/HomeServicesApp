package com.ahmed.homeservices.fire_utils;

import com.ahmed.homeservices.constants.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class RefBase {

    public static DatabaseReference refUser() {
        return FirebaseDatabase.getInstance()
                .getReference(Constants.APP_FIREBASE_DATABASE_REF)
                .child(Constants.USERS);
    }

    public static DatabaseReference refUser(String userId) {
        return FirebaseDatabase.getInstance()
                .getReference(Constants.APP_FIREBASE_DATABASE_REF)
                .child(Constants.USERS)
                .child(userId);

    }

    public static DatabaseReference refCategoriesForService() {
        return FirebaseDatabase.getInstance()
                .getReference(Constants.APP_FIREBASE_DATABASE_REF)
                .child(Constants.CATEGORY);
    }


}
