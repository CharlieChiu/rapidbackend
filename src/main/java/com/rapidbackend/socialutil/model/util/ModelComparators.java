package com.rapidbackend.socialutil.model.util;

import java.util.Comparator;

import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;

public class ModelComparators {
    public static CompareFeedById CompareFeedById = new CompareFeedById();
    public static CompareFeedByCreateDate CompareFeedByCreateDate =new CompareFeedByCreateDate();
    public static CompareUserById CompareUserById = new CompareUserById();
    public static class CompareFeedById implements Comparator<FeedContentBase>{
        @Override
        public int compare(FeedContentBase a,FeedContentBase b){
            return a.getId()-b.getId();
        }
    }
    
    public static class CompareFeedByCreateDate implements Comparator<FeedContentBase>{
        @Override
        public int compare(FeedContentBase a,FeedContentBase b){
            return a.getCreated()-b.getCreated()>0?1:-1;
        }
    }
    
    public static class CompareUserById implements Comparator<UserBase>{
        @Override
        public int compare(UserBase a,UserBase b){
            return a.getId()-b.getId()>0?1:-1;
        }
    }
}
