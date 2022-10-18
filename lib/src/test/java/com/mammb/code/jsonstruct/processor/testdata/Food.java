package com.mammb.code.jsonstruct.processor.testdata;

import com.mammb.code.jsonstruct.JsonStruct;
import java.util.List;

@JsonStruct
public record Food(String name, List<String> materials) {
}
