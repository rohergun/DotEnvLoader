package io.github.rohergun.dotenv;

import java.io.IOException;

public class DotLoaderException extends RuntimeException{

    public  DotLoaderException(String message, Throwable cause){
        super(message, cause);
    }
}
