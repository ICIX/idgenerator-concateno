package com.icix;

import com.icix.model.IdGeneratorImpl;
import com.icix.model.RangeLimitException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IdGeneratorTests {

    @Autowired
    private IdGeneratorImpl idGenerator;

    @Test
    public void can_generate_id() throws RangeLimitException {
        List<String> list = idGenerator.generate(1);

        Assert.notEmpty(list);
    }
}
