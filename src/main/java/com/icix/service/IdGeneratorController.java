package com.icix.service;

import com.icix.model.IdGeneratorImpl;
import com.icix.model.RangeLimitException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/id")
public class IdGeneratorController {

    private Logger logger = Logger.getLogger(IdGeneratorController.class);

    @Autowired
    protected IdGeneratorImpl idGenerator;

    @RequestMapping(method= RequestMethod.GET)
    public @ResponseBody
    List<String> generateId(@RequestParam(value="amount", required=false, defaultValue="1") int amount) throws RangeLimitException {

        return idGenerator.generate(amount);
    }

    @ResponseStatus(value= HttpStatus.SERVICE_UNAVAILABLE, reason="limit-reached.exception")  // 503
    @ExceptionHandler(RangeLimitException.class)
    public void onAmqpException(RangeLimitException e) {
        logger.error(e.toString());
    }
}
