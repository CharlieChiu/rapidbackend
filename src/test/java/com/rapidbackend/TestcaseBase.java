package com.rapidbackend;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.ObjectUtils;
import org.junit.Assert;

import com.carrotsearch.randomizedtesting.annotations.TestGroup;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakAction;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakAction.Action;

/**
 * TestBase Class for most of our test cases.
 * @author chiqiu
 *
 */
@ThreadLeakAction(value={Action.WARN})
public abstract class TestcaseBase extends Assert{
    
    
    public static String FATAL_ERROR = "RapidbackendFatalError";
    public static void print(String value){
        System.out.println(value);
    }
    public static void print(float value){
        System.out.println(value);
    }
    public static void print(boolean bool){
        System.out.println(bool+"");
    }
    public static void print(long val){
        System.out.println(val+"");
    }
    public static void print(int val){
        System.out.println(val+"");
    }
    public static <T> void print(List<T> value){
        for(Object o : value){
            if(o==null){
                System.out.println(ObjectUtils.identityToString(o));
            }else{
                System.out.println(o.toString());
            }
            
        }
        
    }
    public static void print(byte[] array){
        System.out.println(new String(array));
    }
    public static void print(Object[] array){
        for(Object o : array){
            if(o==null){
                System.out.println(ObjectUtils.identityToString(o));
            }else{
                System.out.println(o.toString());
            }
            
        }
    }
    public static void print(int... array){
        for(int o : array){
            System.out.println(o);
        }
    }
    
    /**
     * 
     * @param random
     * @param max
     * @param exclude
     * @return
     */
    public static int getRandomInt(Random random, int max, int exclude){
        int res = 0;
        while(res==0){
            int rand = random.nextInt(max);
            if(rand != exclude){
                res = rand;
            }
        }
        return res;
    }
    /**
     * 
     * @param random
     * @param max
     * @param exclusive
     * @return
     */
    public static int getRandomInt(Random random, int max, HashSet<Integer> exclusive){
        int res = 0;
        while(res==0){
            int rand = random.nextInt(max);
            if(!exclusive.contains(rand)){
                res = rand;
                exclusive.add(rand);
            }
        }
        return res;
    }
    
    /**
     * Annotated method should be called for installation only
     * @author chiqiu
     *
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD,ElementType.TYPE})
    @Inherited
    @TestGroup(enabled=false)
    public @interface Install {
        /** Additional description, if needed. */
        String value() default "";
    }
    
    /**
     * Annotated method should be called for performance test only
     * @author chiqiu
     *
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD,ElementType.TYPE})
    @Inherited
    @TestGroup(enabled=false)
    public @interface Performance {
        /** Additional description, if needed. */
        String value() default "";
    }
}
