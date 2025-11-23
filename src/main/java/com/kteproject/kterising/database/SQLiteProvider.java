package com.kteproject.kterising.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;

public class SQLiteProvider implements SQLProvider {

    private final Plugin plugin;
    private HikariDataSource source;

    public SQLiteProvider(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() throws Exception {
        String fileName = plugin.getConfig().getString("database.sqlite.file", "data.db");
        File dbFile = new File(plugin.getDataFolder(), fileName);

        if (!dbFile.exists()) {
            dbFile.getParentFile().mkdirs();
        }

        HikariConfig cfg = new HikariConfig();

        cfg.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
        cfg.setPoolName("KteRising-SQLite");

        cfg.setMaximumPoolSize(1);
        cfg.setMinimumIdle(1);
        cfg.setConnectionTestQuery(null);
        cfg.setIdleTimeout(60000L);
        cfg.setMaxLifetime(300000L);
        cfg.setConnectionTimeout(8000L);

        cfg.addDataSourceProperty("journal_mode", "WAL");
        cfg.addDataSourceProperty("synchronous", "NORMAL");
        cfg.addDataSourceProperty("busy_timeout", "5000");
        cfg.addDataSourceProperty("cache_size", "-64000");
        cfg.addDataSourceProperty("temp_store", "MEMORY");

        source = new HikariDataSource(cfg);
    }

    @Override
    public Connection getConnection() throws Exception {
        return source.getConnection();
    }

    @Override
    public void shutdown() {
        if (source != null) {
            source.close();
        }
    }
}
