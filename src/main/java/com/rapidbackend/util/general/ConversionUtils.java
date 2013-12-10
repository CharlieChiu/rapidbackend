package com.rapidbackend.util.general;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.rapidbackend.core.model.DbRecord;

/**
 * Note: Joins are ported from apache commons StringUtils 
 * @author chiqiu
 *
 */
public class ConversionUtils {
    
    /**
     * convert a Integer collection to int array
     * @param collection
     * @return if one value in collection is null, the value in int[] will be 0
     */
    public static int[] integerCollectionToIntArray(Collection<Integer> collection){
        if(collection.size()>0){
            int[] res = new int[collection.size()];
            int i = 0;
            for(Integer k:collection){
                if(i<res.length){
                    if(k!=null){
                        res[i++]=k;
                    }else {
                        res[i++]=0;
                    }
                }
            }
            return res;
        }else {
            return new int[0];
        }
    }
    /**
     * 
     * @param collection
     * @return return all records 'ID' property in an array
     */
    public static int[] socialDbRecordCollectionIdToIntArray(Collection<?> collection){
        if(collection.size()>0){
            int[] res = new int[collection.size()];
            int i = 0;
            
            for(Object o:collection){
                DbRecord k = (DbRecord)o;
                if(i<res.length){
                    if(k!=null){
                        res[i++]=k.getId();
                    }else {
                        res[i++]=0;
                    }
                }
            }
            return res;
        }else {
            return new int[0];
        }
    }
    public static String DefaultEncoding = "UTF-8";
    /**
     * 
     * @param collection
     * @return
     */
    public static byte[][] integerCollectionToByteArray(Collection<Integer> collection){
        try {
            final byte[][] data = new byte[collection.size()][];
            int j = 0;
            for(Integer i:collection){
                if(i==null){
                    throw new IllegalArgumentException("input integer collection should not contain null values");
                }
                String val = Integer.toString(i);
                byte[] bin = val.getBytes(DefaultEncoding);
                data[j++] = bin;
            }
            return data;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * 
     * @param collection
     * @return
     */
    public static byte[][] integerCollectionToByteArrayWithPrefix(Collection<Integer> collection,String prefix){
        try {
            final byte[][] data = new byte[collection.size()][];
            int j = 0;
            for(Integer i:collection){
                if(i==null){
                    throw new IllegalArgumentException("input integer collection should not contain null values");
                }
                String val = prefix +Integer.toString(i);
                byte[] bin = val.getBytes(DefaultEncoding);
                data[j++] = bin;
            }
            return data;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static String[] intArrayToStringArray(int[] ids){
        String[] res = new String[ids.length];
        int j=0;
        
        for(int i: ids){
            res[j++] = Integer.toString(i);
        }
        return res;
    }
    
    public static String[] intArrayToStringArray(int[] ids,String prefix){
        String[] res = new String[ids.length];
        int j=0;
        
        for(int i: ids){
            res[j++] = prefix+i;
        }
        return res;
    }
    
    
    /**
     * converts an string collection to int array
     * @param collection
     * @return if one value in collection is null, the value in int[] will be 0
     */
    public static int[] stringCollentionToIntArray(Collection<String> collection){
        if(collection.size()>0){
            int[] res = new int[collection.size()];
            int i = 0;
            for(String k:collection){
                if(i<res.length){
                    if(k!=null){
                        res[i++]=Integer.parseInt(k);
                    }else {
                        res[i++]=0;
                    }
                }
            }
            return res;
        }else {
            return null;
        }
    }
    public static final String EMPTY = "";
    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * A <code>null</code> separator is the same as an empty String ("").
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * StringUtils.join(null, *)                = null
     * StringUtils.join([], *)                  = ""
     * StringUtils.join([null], *)              = ""
     * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringUtils.join(["a", "b", "c"], null)  = "abc"
     * StringUtils.join(["a", "b", "c"], "")    = "abc"
     * StringUtils.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @param startIndex the first index to start joining from.  It is
     * an error to pass in an end index past the end of the array
     * @param endIndex the index to stop joining from (exclusive). It is
     * an error to pass in an end index past the end of the array
     * @return the joined String, <code>null</code> if null array input
     */
    public static String join(int[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }

        // endIndex - startIndex > 0:   Len = NofStrings *(len(firstString) + len(separator))
        //           (Assuming that all Strings are roughly equally long)
        int bufSize = (endIndex - startIndex);
        if (bufSize <= 0) {
            return EMPTY;
        }

        //bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length())
                        //+ separator.length());
        bufSize *= 8+ separator.length();
        
        StringBuffer buf = new StringBuffer(bufSize);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            //if (array[i] != null) {
            buf.append(array[i]);
            //}
        }
        return buf.toString();
    }
    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * A <code>null</code> separator is the same as an empty String ("").
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * StringUtils.join(null, *)                = null
     * StringUtils.join([], *)                  = ""
     * StringUtils.join([null], *)              = ""
     * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringUtils.join(["a", "b", "c"], null)  = "abc"
     * StringUtils.join(["a", "b", "c"], "")    = "abc"
     * StringUtils.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @return the joined String, <code>null</code> if null array input
     */
    public static String join(int[] array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }
    
    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * StringUtils.join(null, *)               = null
     * StringUtils.join([], *)                 = ""
     * StringUtils.join([null], *)             = ""
     * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
     * StringUtils.join(["a", "b", "c"], null) = "abc"
     * StringUtils.join([null, "", "a"], ';')  = ";;a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @param separator  the separator character to use
     * @param startIndex the first index to start joining from.  It is
     * an error to pass in an end index past the end of the array
     * @param endIndex the index to stop joining from (exclusive). It is
     * an error to pass in an end index past the end of the array
     * @return the joined String, <code>null</code> if null array input
     * @since 2.0
     */
    public static String join(int[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int bufSize = (endIndex - startIndex);
        if (bufSize <= 0) {
            return EMPTY;
        }

        bufSize *= (8 + 1);
        StringBuffer buf = new StringBuffer(bufSize);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            //if (array[i] != null) {
            buf.append(array[i]);
            //}
        }
        return buf.toString();
    }
    public static String join(int[] array, char separator) {
        if (array == null) {
            return null;
        }

        return join(array, separator, 0, array.length);
    }
    
    
    public static void main(String[] arg){
        int[] a = {1,2,3,34};
        System.out.print(join(a,"a"));
    }
}
