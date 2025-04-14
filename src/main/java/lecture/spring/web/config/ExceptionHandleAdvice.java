package lecture.spring.web.config;

import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.logging.Logger;

@ControllerAdvice
public class ExceptionHandleAdvice {
    private final Logger logger = Logger.getLogger(getClass().getName());

    @ExceptionHandler(BindException.class)
    public String handleBindException(BindException ex) {
        logger.info("BindException");
        logger.info(ex.getBindingResult().getAllErrors().toString());
        return "redirect:/error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        logger.info("MethodArgumentNotValidException");
        logger.info(ex.getBindingResult().getAllErrors().toString());
        return "redirect:/error";
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public String handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        logger.info("HandlerMethodValidationException");
        logger.info(ex.getParameterValidationResults().toString());
        return "redirect:/error";
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolationException(ConstraintViolationException ex) {
        logger.info("ConstraintViolationException");
        logger.info(ex.getConstraintViolations().toString());
        return "redirect:/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex) {
        logger.info("Exception");
        logger.info(ex.getMessage());
        return "redirect:/error";
    }
}
