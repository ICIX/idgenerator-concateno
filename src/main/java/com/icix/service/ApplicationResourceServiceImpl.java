package com.icix.service;


import com.icix.model.ResponseData;
import com.icix.model.TokenAcquiringException;
import com.icix.model.TradingPartnerInfo;
import com.icix.model.messaging.Envelope;
import com.icix.model.messaging.Message;
import com.icix.model.messaging.MessageType;
import com.icix.repository.ApplicationResourceRepository;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ApplicationResourceServiceImpl implements ApplicationResourceService {

    private final Logger logger = Logger.getLogger(RecipientsServiceImpl.class);
    private final ApplicationResourceRepository applicationResourceRepository;

    @Autowired
    public ApplicationResourceServiceImpl(ApplicationResourceRepository applicationResourceRepository) {
        this.applicationResourceRepository = applicationResourceRepository;
    }

    @Override
    public byte[] findByKey(String key) {
        return applicationResourceRepository.findByKey(key).get(0).getValue();
    }
}
