package com.rapidbackend.core;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * 
 * @author chiqiu
 *
 */
public class BackendRuntimeException extends RuntimeException
	implements Timestamped{
    /**
     * 
     */
    private static final long serialVersionUID = 5523728899400204176L;
    public static Integer InternalServerError = 500;
    public static Integer Unsupported = 501;
    public static Integer BadRequest = 400;
    
    public static final int CONTINUE = 100;
    
    // Field descriptor #3 I
    public static final int SWITCHING_PROTOCOLS = 101;
    
    // Field descriptor #3 I
    public static final int OK = 200;
    
    // Field descriptor #3 I
    public static final int CREATED = 201;
    
    // Field descriptor #3 I
    public static final int ACCEPTED = 202;
    
    // Field descriptor #3 I
    public static final int NON_AUTHORITATIVE_INFORMATION = 203;
    
    // Field descriptor #3 I
    public static final int NO_CONTENT = 204;
    
    // Field descriptor #3 I
    public static final int RESET_CONTENT = 205;
    
    // Field descriptor #3 I
    public static final int PARTIAL_CONTENT = 206;
    
    // Field descriptor #3 I
    public static final int MULTIPLE_CHOICES = 300;
    
    // Field descriptor #3 I
    public static final int MOVED_PERMANENTLY = 301;
    
    // Field descriptor #3 I
    public static final int MOVED_TEMPORARILY = 302;
    
    // Field descriptor #3 I
    public static final int FOUND = 302;
    
    // Field descriptor #3 I
    public static final int SEE_OTHER = 303;
    
    // Field descriptor #3 I
    public static final int NOT_MODIFIED = 304;
    
    // Field descriptor #3 I
    public static final int USE_PROXY = 305;
    
    // Field descriptor #3 I
    public static final int TEMPORARY_REDIRECT = 307;
    
    // Field descriptor #3 I
    public static final int BAD_REQUEST = 400;
    
    // Field descriptor #3 I
    public static final int UNAUTHORIZED = 401;
    
    // Field descriptor #3 I
    public static final int PAYMENT_REQUIRED = 402;
    
    // Field descriptor #3 I
    public static final int FORBIDDEN = 403;
    
    // Field descriptor #3 I
    public static final int NOT_FOUND = 404;
    
    // Field descriptor #3 I
    public static final int METHOD_NOT_ALLOWED = 405;
    
    // Field descriptor #3 I
    public static final int NOT_ACCEPTABLE = 406;
    
    // Field descriptor #3 I
    public static final int PROXY_AUTHENTICATION_REQUIRED = 407;
    
    // Field descriptor #3 I
    public static final int REQUEST_TIMEOUT = 408;
    
    // Field descriptor #3 I
    public static final int CONFLICT = 409;
    
    // Field descriptor #3 I
    public static final int GONE = 410;
    
    // Field descriptor #3 I
    public static final int LENGTH_REQUIRED = 411;
    
    // Field descriptor #3 I
    public static final int PRECONDITION_FAILED = 412;
    
    // Field descriptor #3 I
    public static final int REQUEST_ENTITY_TOO_LARGE = 413;
    
    // Field descriptor #3 I
    public static final int REQUEST_URI_TOO_LONG = 414;
    
    // Field descriptor #3 I
    public static final int UNSUPPORTED_MEDIA_TYPE = 415;
    
    // Field descriptor #3 I
    public static final int REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    
    // Field descriptor #3 I
    public static final int EXPECTATION_FAILED = 417;
    
    // Field descriptor #3 I
    public static final int INTERNAL_SERVER_ERROR = 500;
    
    // Field descriptor #3 I
    public static final int NOT_IMPLEMENTED = 501;
    
    // Field descriptor #3 I
    public static final int BAD_GATEWAY = 502;
    
    // Field descriptor #3 I
    public static final int SERVICE_UNAVAILABLE = 503;
    
    // Field descriptor #3 I
    public static final int GATEWAY_TIMEOUT = 504;
    
    // Field descriptor #3 I
    public static final int HTTP_VERSION_NOT_SUPPORTED = 505;
    
    protected Integer errorCode = null;
    
	public Integer getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
    protected long occurrenceTime;
    
    /**
     * Construct the exception with an error code for future error handling
     * @param errCode
     * @param err
     * @param throwable
     */
    public BackendRuntimeException(Integer errCode,String err,Throwable throwable){
    	super(err);
    	this.errorCode = errCode;
        this.occurrenceTime = System.currentTimeMillis();
        
        if(throwable instanceof BackendRuntimeException){
            BackendRuntimeException cause = (BackendRuntimeException)throwable;
            this.errorCode = cause.getErrorCode();
        }
        
    	initCause(throwable);
    }
    /**
     * Construct the exception with an error code for future error handling
     * @param errCode
     * @param err
     */
    public BackendRuntimeException(Integer errCode,String err){
    	super(err);
    	errorCode = errCode;
    	occurrenceTime = System.currentTimeMillis();
    }
    /**
     * 
     * @param errCode
     */
    public BackendRuntimeException(Integer errCode){
        errorCode = errCode;
        occurrenceTime = System.currentTimeMillis();
    }
    @Override
    public long getUnixTimestamp(){
    	return occurrenceTime;
    }
    /**
     * For now, return unix timestamp without assign a human readable format for performance concern
     */
    @Override
    public String getTimestamp(){
    	return occurrenceTime+"";
    }    
        
    private String detailedInfo = null;
    
    public String getDetailedInfo(){
        if(detailedInfo==null){
            StringBuffer sb = new StringBuffer("");
            sb.append(getClass().getName()).append(":").append(errorCode);
            sb.append(":");
            sb.append(getDetailedInfo(this));
            detailedInfo = sb.toString();
        }
        return detailedInfo;
    }
    
    public static String getDetailedInfo(Throwable t){
        ArrayList<String> info = render(t);
        StringBuilder sb = new StringBuilder();
        sb.append("timestamp:").append(System.currentTimeMillis()).append("\n") ;
        for(String s: info){
            sb.append(s);
        }
        return sb.toString();
    }
    
    public static ArrayList<String> render(final Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            throwable.printStackTrace(pw);
        } catch(RuntimeException ex) {
        }
        pw.flush();
        LineNumberReader reader = new LineNumberReader(
                new StringReader(sw.toString()));
        ArrayList<String> lines = new ArrayList<String>();
        try {
          String line = reader.readLine();
          while(line != null) {
            lines.add(line);
            line = reader.readLine();
          }
        } catch(IOException ex) {
            if (ex instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
            lines.add(ex.toString());
        }
        return lines;
    }
    

}
