package com.rapidbackend.socialutil.util;

import java.util.Comparator;
import java.util.TreeSet;

import com.rapidbackend.socialutil.model.reserved.FeedContentBase;

public class FeedComparator implements Comparator<FeedContentBase>{
    public int compare(FeedContentBase f1, FeedContentBase f2){
        return f2.getId() - f1.getId();
    }
    
    public boolean equals(Object another){
        return this == another;
    }
    
    public static void main(String[] agrs){
        FeedContentBase f1 = new FeedContentBase();
        f1.setId(1);
        
        FeedContentBase f2 = new FeedContentBase();
        f2.setId(2);
        
        FeedComparator comparator = new FeedComparator();
                
        TreeSet set = new TreeSet(comparator);
        set.add(f1);set.add(f2);
        
        for(Object object :set){
            FeedContentBase f = (FeedContentBase)object;
            System.out.println(f.getId());
        }
    }
}
