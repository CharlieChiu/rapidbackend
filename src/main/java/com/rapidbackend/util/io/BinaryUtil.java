package com.rapidbackend.util.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
/**
 * 
 * @author chiqiu
 *
 */
public class BinaryUtil {
	
	
	public static byte[] toByteArray(int[] data) throws IOException{
		if(data==null || data.length==0){
			return null;
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		for(Integer i:data){
			dout.writeInt(i);
		}
		byte[] array = bout.toByteArray();
		return array;
	}
	
	public static byte[] toByteArray(long[] data) throws IOException{
		if(data==null || data.length==0){
			return null;
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		for(Long i:data){
			dout.writeLong(i);
		}
		byte[] array = bout.toByteArray();
		return array;
	}
	
}
