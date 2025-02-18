package com.sharktank.interdepcollab.exception;

public class FileParentExistsException extends RuntimeException{
    public FileParentExistsException(String message) {
        super(message);
    }
}
