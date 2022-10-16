package com.mammb.code.jsonstruct.entity;

import com.mammb.code.jsonstruct.processor.CodeTemplate;

public interface Entity {

    void writeTo(CodeTemplate code, String key);

}
