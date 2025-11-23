package com.kteproject.kterising.database;

import java.sql.Connection;

public interface SQLProvider {
    void init() throws Exception;
    Connection getConnection() throws Exception;
    void shutdown();
}
