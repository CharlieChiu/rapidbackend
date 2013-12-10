package com.rapidbackend.util.general;

public class Tuple<L,R> {
    final L left;
    R right;
    
    public Tuple(L left,R right){
        this.left = left;
        this.right = right;
    }
    
    public R getRight() {
        return right;
    }

    public void setRight(R right) {
        this.right = right;
    }

    public L getLeft() {
        return left;
    }
    
    
}
