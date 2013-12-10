package com.rapidbackend.core.request.util;

import java.util.ArrayList;

import com.rapidbackend.core.request.CommandParam;

public class ParamList extends ArrayList<CommandParam> {

    private static final long serialVersionUID = -5280382939903721642L;
    
    public ParamList appendParam(CommandParam element){
        add(element);
        return this;
    }

}
