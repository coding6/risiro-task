package org.example;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SumTask {
    private Integer a;

    private Integer b;


    public Integer getA() {
        return a;
    }

    public Integer getB() {
        return b;
    }

    public SumTask(Integer a, Integer b) {
        this.a = a;
        this.b = b;
    }

}
