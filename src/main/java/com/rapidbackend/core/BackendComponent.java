package com.rapidbackend.core;


import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;

/**
 * A component means one data service which can be separated to 
 * different machines
 * @author chiqiu
 *
 */
public abstract class BackendComponent {
    public abstract void handleRequest(CommandRequest request,CommandResponse response); 
}