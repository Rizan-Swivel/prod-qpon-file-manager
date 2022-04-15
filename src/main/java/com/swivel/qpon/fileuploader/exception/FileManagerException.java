package com.swivel.qpon.fileuploader.exception;

/**
 * ImageUploaderException - Parent exception of the service
 */
public class FileManagerException extends RuntimeException {

    /**
     * ImageUploaderException with error message.
     *
     * @param errorMessage error message
     */
    public FileManagerException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * ImageUploaderException with error message and throwable error
     *
     * @param errorMessage error message
     * @param error        error
     */
    public FileManagerException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
