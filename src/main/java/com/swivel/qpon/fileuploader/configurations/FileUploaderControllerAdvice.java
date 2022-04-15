package com.swivel.qpon.fileuploader.configurations;

import com.swivel.qpon.fileuploader.enums.ErrorResponseStatusType;
import com.swivel.qpon.fileuploader.wrapper.ErrorResponseWrapper;
import com.swivel.qpon.fileuploader.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Handles the FileUploadBase$FileSizeLimitExceededException
 */
@ControllerAdvice
public class FileUploaderControllerAdvice extends ResponseEntityExceptionHandler {

    private final Translator translator;

    @Autowired
    public FileUploaderControllerAdvice(Translator translator) {
        this.translator = translator;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    ResponseEntity<ResponseWrapper> handleControllerException(HttpServletRequest request, Throwable ex) {
        ResponseWrapper responseWrapper =
                new ErrorResponseWrapper(ErrorResponseStatusType.EXCEEDED_FILE_SIZE,
                        translator.toLocale(ErrorResponseStatusType.EXCEEDED_FILE_SIZE
                                .getCodeString(ErrorResponseStatusType.EXCEEDED_FILE_SIZE.getCode())),
                        null);
        return new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST);
    }
}
