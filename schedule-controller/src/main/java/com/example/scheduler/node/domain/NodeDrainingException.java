package com.example.scheduler.node.domain;

/** The Node lock confirmed Drain before the OS start invocation. */
public final class NodeDrainingException extends IllegalStateException {
    public NodeDrainingException(String nodeId) {
        super("Node " + nodeId + " is DRAINING; ProcessBuilder.start was not invoked");
    }
}
