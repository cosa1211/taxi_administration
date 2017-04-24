package com.cosicervin.administration.server.database;

import android.provider.BaseColumns;

/**
 * Created by ervincosic on 19/04/2017.
 */
public final class ServerDataContract {

    private ServerDataContract(){}

    public static class ServerData implements BaseColumns{
        public static final String TABLE_NAME = "ServerData";
        public static final String SERVER_URL = "ServerURL";
        public static final String SERVER_TOKEN = "ServerToken";
    }

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + ServerData.TABLE_NAME + " ("+ ServerData._ID +
            " INTEGER PRIMARY KEY," + ServerData.SERVER_URL + " TEXT," + ServerData.SERVER_TOKEN + " TEXT);";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ServerData.TABLE_NAME;

}
