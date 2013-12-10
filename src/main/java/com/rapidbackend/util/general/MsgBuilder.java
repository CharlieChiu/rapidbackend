package com.rapidbackend.util.general;
public class MsgBuilder {
    StringBuilder sb = new StringBuilder(16);
    public MsgBuilder $(String s){
        sb.append(s);
        return this;
    }
    @Override
    public String toString(){
        return sb.toString();
    }
}
