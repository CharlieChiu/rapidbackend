package com.rapidbackend.util.comm.redis.util;

import java.util.ArrayList;
import java.util.List;

/**
 * stores the key value 
 * @author chiqiu
 *
 */
public class KeyValueList<T> {
	protected List<T> key;
	protected List<T> value;
	
	
	public KeyValueList(List<T> keys){
		if(keys.size()>0){
			this.key = keys;
		}
	}
	public KeyValueList(List<T> keys,List<T> values){
		if(keys.size()>0){
			this.key = keys;
			this.value = values;
		}
	}
	public void updateValue(List<T> values){
		if(values.size()>0){
			this.value = values;
		}
	}
	public int size(){
		if(key!=null){
			return key.size();
		}else{
			return 0;
		}
	}
	/**
	 * 
	 * @return keys which does not have a value
	 */
	public List<T> getMissedKeys(){
		if(value==null){
			return key;
		}
		
		List<T> result = new ArrayList<T>();
		int idx = 0;
		
		for(;idx<key.size();idx++){
			try{
				if(value.get(idx)==null ){
					result.add(key.get(idx));
				}
			}catch(ArrayIndexOutOfBoundsException e){
				break;
			}
		}
		/**
		 * keep on rolling to the end, this should not run
		 */
		for(;idx<key.size();idx++){
			result.add(key.get(idx));
		}
		return result;
	}
	/**
	 * check if the value size equal to key size
	 * @return
	 */
	public boolean check(){
		return key!=null && value!=null && key.size()==value.size();
	}
}
