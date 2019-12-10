package com.example.pickmeup.ViewModel;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MutableGenericResult<T> {

    @Nullable
    private T success;

    @NonNull
    private Integer failedError;

    @NonNull
    private Exception exceptionError;

    public MutableGenericResult(Integer failedError) {
        this.failedError = failedError;
    }

    public MutableGenericResult(Exception exceptionError) {
        this.exceptionError = exceptionError;
    }

    public MutableGenericResult(@Nullable T success) {
        this.success = success;
    }

    @Nullable
    public T getSuccess() {
        return success;
    }

    @NonNull
    public Integer getFailedError() {
        return failedError;
    }

    @NonNull
    public Exception getExceptionError(){
        return exceptionError;
    }
}
