package com.swivel.qpon.fileuploader.controller;

import com.swivel.qpon.fileuploader.domain.response.ResponseDto;
import com.swivel.qpon.fileuploader.enums.ErrorResponseStatusType;
import com.swivel.qpon.fileuploader.enums.SuccessResponseStatusType;
import com.swivel.qpon.fileuploader.wrapper.ErrorResponseWrapper;
import com.swivel.qpon.fileuploader.wrapper.ResponseWrapper;
import com.swivel.qpon.fileuploader.wrapper.SuccessResponseWrapper;
import com.swivel.qpon.fileuploader.configurations.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Controller
 */
public class Controller {

    protected static final String MULTIPART_FORM_DATA = "multipart/form-data";
    protected static final String APPLICATION_JSON = "application/json";
    protected static final String APPLICATION_JSON_UTF_8 = "application/json;charset=UTF-8";
    private final Translator translator;

    @Autowired
    public Controller(Translator translator) {
        this.translator = translator;
    }

    /**
     * This method creates the empty data response for bad request.
     *
     * @param errorResponseStatusType errorResponseStatusType
     * @return bad request error response
     */
    protected ResponseEntity<ResponseWrapper> getBadRequestError(ErrorResponseStatusType errorResponseStatusType) {
        ResponseWrapper responseWrapper =
                new ErrorResponseWrapper(errorResponseStatusType,
                        translator.toLocale(errorResponseStatusType.getCodeString(errorResponseStatusType.getCode())),
                        null);
        return new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST);
    }

    /**
     * This method creates the empty data response for bad request.
     *
     * @param errorResponseStatusType errorResponseStatusType
     * @param data                    responseDto
     * @return bad request error response
     */
    protected ResponseEntity<ResponseWrapper> getBadRequestError(ErrorResponseStatusType errorResponseStatusType,
                                                                 ResponseDto data) {
        ResponseWrapper responseWrapper =
                new ErrorResponseWrapper(errorResponseStatusType,
                        translator.toLocale(errorResponseStatusType.getCodeString(errorResponseStatusType.getCode())),
                        data);
        return new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST);
    }


    /**
     * This method creates the empty data response for internal server error.
     *
     * @return internal server error response
     */
    protected ResponseEntity<ResponseWrapper> getInternalServerError() {
        ResponseWrapper responseWrapper =
                new ErrorResponseWrapper(ErrorResponseStatusType.INTERNAL_SERVER_ERROR,
                        translator.toLocale(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                                .getCodeString(ErrorResponseStatusType.INTERNAL_SERVER_ERROR.getCode())),
                        null);
        return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * This method creates empty data response for success response
     *
     * @return ok
     */
    protected ResponseEntity<ResponseWrapper> getSuccessResponse(SuccessResponseStatusType successResponseStatusType,
                                                                 ResponseDto responseDto) {
        ResponseWrapper responseWrapper = new SuccessResponseWrapper(successResponseStatusType,
                translator.toLocale(successResponseStatusType.getCode()),
                responseDto);
        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }

}
