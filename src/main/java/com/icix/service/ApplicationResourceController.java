package com.icix.service;

import com.icix.service.ApplicationResourceServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/resource")
public class ApplicationResourceController {

    private Logger logger = Logger.getLogger(IdGeneratorController.class);

    @Autowired
    protected ApplicationResourceServiceImpl appResource;

    @RequestMapping(method= RequestMethod.GET)
    public @ResponseBody
    byte[] getResource(@RequestParam(value="key", required=true, defaultValue="PRIVATE_KEY_V1") String key) {

        return appResource.findByKey(key);
    }
}
