package com.rapidbackend.core.request;

import java.io.UnsupportedEncodingException;

public abstract class SeqCommandParam extends CommandParam{
    public abstract String toStringEncoded() throws UnsupportedEncodingException;
}
