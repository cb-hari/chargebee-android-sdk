/*
 * Copyright (c) 2011 chargebee.com
 * All Rights Reserved.
 */

package com.chargebee.android.sdk.exceptions;

import com.chargebee.android.sdk.APIException;
import org.json.*;


public class InvalidRequestException extends APIException{

    public InvalidRequestException(int httpStatusCode,JSONObject jsonObj) throws Exception {
        super(httpStatusCode,jsonObj);
    }
    
}
