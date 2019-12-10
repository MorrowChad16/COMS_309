package com.example.pickmeup.Data;

/**
 * A generic class that holds a result success w/ data or an error exception.
 */
public class Result<T> {
    // hide the private constructor to limit subclass types (Success, Error)
    private Result() {}

    // Success sub-class
    public final static class Success<T> extends Result {
        private T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }
    }

    // Success sub-class
    public final static class FailedError extends Result {
        private Integer error;

        public FailedError(Integer error) {
            this.error = error;
        }

        public Integer getFailedError() {
            return this.error;
        }
    }

    // Error sub-class
    public final static class ExceptionError extends Result {
        private java.lang.Exception error;

        public ExceptionError(java.lang.Exception error) {
            this.error = error;
        }

        public java.lang.Exception getExceptionError() {
            return this.error;
        }
    }

    @Override
    public String toString() {
        if (this instanceof Result.Success) {
            Result.Success success = (Result.Success) this;
            return "Success[data=" + success.getData().toString() + "]";
        } else if (this instanceof Result.ExceptionError) {
            Result.ExceptionError error = (Result.ExceptionError) this;
            return "Error[exception=" + error.getExceptionError().toString() + "]";
        }
        return "";
    }
}
