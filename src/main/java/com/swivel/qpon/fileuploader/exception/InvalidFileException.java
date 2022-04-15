package com.swivel.qpon.fileuploader.exception;

/**
 * InvalidImageException Exception
 */
public class InvalidFileException extends FileManagerException {

    /**
     * InvalidImageException with error message.
     *
     * @param errorMessage error message
     */
    public InvalidFileException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * InvalidImageException with error message and throwable error
     *
     * @param errorMessage error message
     * @param error        error
     */
    public InvalidFileException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
