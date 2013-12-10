package com.rapidbackend.cache.local;

import java.util.BitSet;
/**
 * Record every item's status change in a bit
 * @author chiqiu
 *
 */
public class BitStatusCache extends LocalJvmCache{
    protected BitSet bits = new BitSet();
    
    /**
     * return false by default, <br>
     * If this bit is set, then we can use the cached value in local LRU cache as the latest value
     * @param itemIndex
     * @return true if the status is not changed
     */
    public boolean isDataUnchanged(int itemIndex){
        return bits.get(itemIndex);
    }
    /**
     * after every reading, mark the item as unchanged<BR>
     * If this bit is set, then we can use the cached value in local LRU cache as the latest value
     * @return
     */
    public void markAsUnchangedAfterReading(int itemIndex){
        bits.set(itemIndex, true);
    }
    
    public void markAsChangedAfterWriting(int itemIndex){
        bits.set(itemIndex, false);
    }
}
