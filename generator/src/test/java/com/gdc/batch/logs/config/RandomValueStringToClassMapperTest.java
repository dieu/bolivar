package com.gdc.batch.logs.config;

import org.junit.Test;
import org.junit.Before;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import com.gdc.batch.logs.random.RandomValue;
import com.gdc.batch.logs.random.RandomNumber;
import com.gdc.batch.logs.random.RandomIp;
import com.gdc.batch.logs.random.RandomLocalIp;

/**
 * Created by vkatson
 * Date: 05.03.2009
 * Time: 12:55:15
 */

public class RandomValueStringToClassMapperTest {

    private StringToClassMapper<RandomValue> classMapper;

    @Before
    public void setUp() {
        classMapper = new RandomValueStringToClassMapper();
    }

    @Test
    public void testThatCorrectInstanceIsReturnedByKeyD() throws IllegalAccessException, InstantiationException {
        String key = "d4";
        RandomNumber instance = (RandomNumber) classMapper.getInstanceByKey(key);
        assertThat(instance, is(new RandomNumber(4)));
    }

    @Test
    public void testThatCorrectInstanceIsReturnedByKeyIp() throws IllegalAccessException, InstantiationException {
        String key = "ip";
        RandomIp instance = (RandomIp) classMapper.getInstanceByKey(key);
        assertThat(instance, is(new RandomIp()));
    }

    @Test
    public void testThatCorrectInstanceIsReturnedByKeyLocalIp() throws IllegalAccessException, InstantiationException {
        String key = "localIp";
        RandomLocalIp instance = (RandomLocalIp) classMapper.getInstanceByKey(key);
        assertThat(instance, is(new RandomLocalIp()));
    }

    @Test(expected = Exception.class)
    public void testThatInstantiationByWrongKeyThrowsException() throws IllegalAccessException, InstantiationException {
        String key = "wrong key";
        RandomValue instance = classMapper.getInstanceByKey(key);
    }


}
