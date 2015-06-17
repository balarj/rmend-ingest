package com.brajagopal.rmend.data.beans;

import java.util.Map;

/**
 * @author <bxr4261>
 */
public class SocialTag extends BaseContent {


    @Override
    public void process(Map<String, ? extends Object> _value) {
        //TODO: All processing goes here
        System.out.println("Works");
    }

    @Override
    public BaseContent getInstance() {
        return new SocialTag();
    }
}
