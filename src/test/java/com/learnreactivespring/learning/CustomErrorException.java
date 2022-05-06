package com.learnreactivespring.learning;

public class CustomErrorException extends Throwable {

    CustomErrorException(String msg){
        super(msg);
    }
}
