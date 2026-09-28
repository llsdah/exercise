package com.example.scheduler.execution.application.port;

import java.io.IOException;

/** OS invocation supplied to the durable launch fence. */
@FunctionalInterface
public interface ProcessStarter {
    Process start() throws IOException;
}
