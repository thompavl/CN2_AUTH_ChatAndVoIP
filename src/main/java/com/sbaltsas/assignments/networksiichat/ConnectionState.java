package com.sbaltsas.assignments.networksiichat;

public enum ConnectionState {
    READY {
        @Override
        public String toString() {
            return "Ready";
        }
    },
    CONNECTED {
        @Override
        public String toString() {
            return "Connected";
        }
    },
    ON_CALL {
        @Override
        public String toString() {
            return "On call";
        }
    }
}
