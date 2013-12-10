package com.rapidbackend.util.io;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

// Commons Logging imports
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Original written by apache tomcat(http://tomcat.apache.org). Ported here by chiqiu
 * @author chiqiu
 *
 */
public class GZIPUtils {
  
  private static final Logger LOG = LoggerFactory.getLogger(GZIPUtils.class);
  private static final int EXPECTED_COMPRESSION_RATIO= 5;
  private static final int BUF_SIZE= 4096;

  /**
   * Returns an gunzipped copy of the input array.  If the gzipped
   * input has been truncated or corrupted, a best-effort attempt is
   * made to unzip as much as possible.  If no data can be extracted
   * <code>null</code> is returned.
   */
  public static final byte[] unzipBestEffort(byte[] in) {
    return unzipBestEffort(in, Integer.MAX_VALUE);
  }

  /**
   * Returns an gunzipped copy of the input array, truncated to
   * <code>sizeLimit</code> bytes, if necessary.  If the gzipped input
   * has been truncated or corrupted, a best-effort attempt is made to
   * unzip as much as possible.  If no data can be extracted
   * <code>null</code> is returned.
   */
  public static final byte[] unzipBestEffort(byte[] in, int sizeLimit) {
    try {
      // decompress using GZIPInputStream 
      ByteArrayOutputStream outStream = 
        new ByteArrayOutputStream(EXPECTED_COMPRESSION_RATIO * in.length);

      GZIPInputStream inStream = 
        new GZIPInputStream ( new ByteArrayInputStream(in) );

      byte[] buf = new byte[BUF_SIZE];
      int written = 0;
      while (true) {
        try {
          int size = inStream.read(buf);
          if (size <= 0) 
            break;
          if ((written + size) > sizeLimit) {
            outStream.write(buf, 0, sizeLimit - written);
            break;
          }
          outStream.write(buf, 0, size);
          written+= size;
        } catch (Exception e) {
          break;
        }
      }
      try {
        outStream.close();
      } catch (IOException e) {
      }

      return outStream.toByteArray();

    } catch (IOException e) {
      return null;
    }
  }


  /**
   * Returns an gunzipped copy of the input array.  
   * @throws IOException if the input cannot be properly decompressed
   */
  public static final byte[] unzip(byte[] in) throws IOException {
    // decompress using GZIPInputStream 
    ByteArrayOutputStream outStream = 
      new ByteArrayOutputStream(EXPECTED_COMPRESSION_RATIO * in.length);

    GZIPInputStream inStream = 
      new GZIPInputStream ( new ByteArrayInputStream(in) );

    byte[] buf = new byte[BUF_SIZE];
    while (true) {
      int size = inStream.read(buf);
      if (size <= 0) 
        break;
      outStream.write(buf, 0, size);
    }
    outStream.close();

    return outStream.toByteArray();
  }

  /**
   * Returns an gzipped copy of the input array.
   */
  public static final byte[] zip(byte[] in) {
    try {
      // compress using GZIPOutputStream 
      ByteArrayOutputStream byteOut= 
        new ByteArrayOutputStream(in.length / EXPECTED_COMPRESSION_RATIO);

      GZIPOutputStream outStream= new GZIPOutputStream(byteOut);

      try {
        outStream.write(in);
      } catch (Exception e) {
        LOG.error("", e);
      }

      try {
        outStream.close();
      } catch (IOException e) {
          LOG.error("", e);
      }

      return byteOut.toByteArray();

    } catch (IOException e) {
        LOG.error("", e);
      return null;
    }
  }
  /**
   * 
   * @param data
   * @return
   * @throws IOException
   */
  public static final byte[] zip(long[] data) throws IOException {
  	ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		for(long l:data){
			dout.writeLong(l);
		}
		byte[] array = bout.toByteArray();
		return zip(array);
  }
  /**
   * 
   * @param in
   * @return
   * @throws IOException
   */
  public static final List<Long> unzipToLongArray(byte[] in) throws IOException{
	  byte[] unzipped =  unzip(in);
	  ByteBuffer buffer = ByteBuffer.wrap(unzipped);
	  List<Long> result = new ArrayList<Long>();
	  while(buffer.hasRemaining()){
		  result.add(buffer.getLong());
	  }
	  return result;
  }
}

