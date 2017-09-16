package com.chargebee.android.sdk.models;

import com.chargebee.*;
import com.chargebee.android.sdk.internal.*;
import com.chargebee.android.sdk.filters.*;
import com.chargebee.android.sdk.filters.enums.SortOrder;
import com.chargebee.android.sdk.internal.HttpUtil.Method;
import com.chargebee.android.sdk.models.enums.*;
import org.json.*;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;

public class Download extends Resource<Download> {

    //Constructors
    //============

    public Download(String jsonStr) {
        super(jsonStr);
    }

    public Download(JSONObject jsonObj) {
        super(jsonObj);
    }

    // Fields
    //=======

    public String downloadUrl() {
        return reqString("download_url");
    }

    public Timestamp validTill() {
        return reqTimestamp("valid_till");
    }

    // Operations
    //===========


}
