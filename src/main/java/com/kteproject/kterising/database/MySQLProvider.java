package com.kteproject.kterising.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;

public class MySQLProvider implements SQLProvider {

    private final Plugin plugin;
    private HikariDataSource source;

    public MySQLProvider(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        String host = plugin.getConfig().getString("database.mysql.host");
        int port = plugin.getConfig().getInt("database.mysql.port");
        String database = plugin.getConfig().getString("database.mysql.database");
        String user = plugin.getConfig().getString("database.mysql.user");
        String pass = plugin.getConfig().getString("database.mysql.password");
        boolean ssl = plugin.getConfig().getBoolean("database.mysql.ssl");

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + ssl);
        cfg.setUsername(user);
        cfg.setPassword(pass);
        cfg.setMaximumPoolSize(plugin.getConfig().getInt("database.mysql.pool-size"));
        cfg.setPoolName("KteRising-MySQL");

        source = new HikariDataSource(cfg);
    }

    @Override
    public Connection getConnection() throws Exception {
        return source.getConnection();
    }

    @Override
    public void shutdown() {
        if (source != null) source.close();
    }
}
