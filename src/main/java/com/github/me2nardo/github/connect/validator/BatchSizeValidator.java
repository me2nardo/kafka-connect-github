package com.github.me2nardo.github.connect.validator;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

public class BatchSizeValidator implements ConfigDef.Validator {
    @Override
    public void ensureValid(String s, Object o) {
        Integer batchSize = (Integer) o;
        if (!(1 <= batchSize && batchSize <=100)){
            throw new ConfigException(s, o, "Batch Size must be a positive integer that's less or equal to 100");
        }
    }
}
