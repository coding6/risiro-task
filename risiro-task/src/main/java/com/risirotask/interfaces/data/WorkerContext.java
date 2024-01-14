package com.risirotask.interfaces.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class WorkerContext {

    private String taskId;

    private Map<String, Object> context;
}
