package com.rapidbackend.util.general;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.Validate;
import org.springframework.util.ReflectionUtils;

/**
 * 
 * @author chiqiu
 *
 */
public class ReflectionTools extends ReflectionUtils{
	/**
	 * util method to get all declared fields in one class excluding inherited ones
	 * @param clazz
	 * @param type filter fileds with the specified class type
	 * @return
	 */
	public static List<Field> getDeclaredFields(Class clazz, Class type){
		Validate.notNull(clazz,"target class type is null");
		Validate.notNull(type,"field type is null ");
		List<Field> result = new ArrayList<Field>();
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields){
			if(type.equals(field.getType())){
				result.add(field);
			}
		}
		return result;
	}
	/**
	 * util method to get all declared fields in one class excluding inherited ones
	 * @param clazz
	 * @param name filter fileds with the specified name
	 * @return
	 */
	public static List<Field> getDeclaredFields(Class clazz, String name){
		Validate.notNull(clazz,"target class type is null");
		Validate.notNull(name,"field name is null ");
		List<Field> result = new ArrayList<Field>();
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields){
			if(name.equals(field.getName())){
				result.add(field);
			}
		}
		return result;
	}
	/**
	 * 
	 * @param clazz
	 * @param name field name, case sensitive
	 * @return
	 */
	public static Field getDeclaredField(Class clazz, String name){
        Validate.notNull(clazz,"target class type is null");
        Validate.notNull(name,"field name is null ");
        List<Field> result = new ArrayList<Field>();
        Field[] fields = clazz.getDeclaredFields();
        Field f = null;
        for(Field field : fields){
            if(name.equals(field.getName())){
                f = field;
            }
        }
        return f;
    }
	
	/**
	 * Get all fields traces from this class to all its super classes
	 * @param clazz
	 * @param stop stop adding fields from this class and it's superclasses
	 * @return
	 */
	public static Field[] getAllFields(Class<?> clazz,Class<?> stop) {
        Class<?> searchType = clazz;
        HashMap<String, Field> allFields = new HashMap<String, Field>();
        while (!stop.equals(searchType) && searchType != null) {
            Field[] fields = searchType.getDeclaredFields();
            for (Field field : fields) {
                if(!allFields.containsKey(field.getName())){
                    allFields.put(field.getName(), field);
                }
            }
            searchType = searchType.getSuperclass();
        }
        return allFields.values().toArray(new Field[0]);
    }
	/*
	public static Field[] getAllFields(Class<?> clazz,Class<?> stop)
	  {
	      List<Class<?>> classes = getAllSuperclasses(clazz,stop);
	      classes.add(clazz);
	      return getAllFields(classes);
	  }*/
	
	public static Field findFiled(Class<?> clazz, String fieldName){
	    Field result = null;
	    Field[] fields = getAllFields(clazz,Object.class);
	    if(fields!=null && fields.length>0){
	        for(Field f: fields){
	            if(f.getName().equals(fieldName)){
	                result = f;
	                break;
	            }
	        }
	    }
	    
	    return result;
	}
	
	
	/**
	 * 
	 * @param classes
	 * @return
	 */
	private static Method[] getAllMethods(List<Class<?>> classes){
	    Set<Method> methods = new TreeSet<Method>();
	    for(Class<?> clazz:classes){
	        methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
	    }
	    return methods.toArray(new Method[methods.size()]);
	}
	
	
	private static Field[] getAllFields(List<Class<?>> classes)
	  {
	      Set<Field> fields = new HashSet<Field>();
	      for (Class<?> clazz : classes)
	      {
	          fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
	      }

	      return fields.toArray(new Field[fields.size()]);
	  }
	
	public static List<Class<?>> getAllSuperclasses(Class<?> clazz,Class<?> stop)
	  {
	      List<Class<?>> classes = new ArrayList<Class<?>>();

	      Class<?> superclass = clazz.getSuperclass();
	      while (superclass != null && superclass!=stop)
	      {
	          classes.add(superclass);
	          superclass = superclass.getSuperclass();
	      }

	      return classes;
	  }
	
	public static void main(String[] args) throws Exception{
		Field[] fields = getAllFields(Class.forName("com.rapidbackend.socialutil.model.Group"),Object.class);
		for(Field field : fields){
			System.out.println(field.getName());
		}
		
	}
}
