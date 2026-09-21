package com.fanren;

import com.fanren.store.DataStore;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * 启动入口：纯 JDK（com.sun.net.httpserver），零外部依赖，无需 Maven/Gradle。
 *
 * 运行（Windows 用 run.bat，macOS/Linux 用 run.sh）：
 *   javac -d out src/com/fanren/Main.java src/com/fanren/ApiHandler.java \
 *            src/com/fanren/Json.java src/com/fanren/HttpError.java \
 *            src/com/fanren/TxtImporter.java \
 *            src/com/fanren/model/Book.java src/com/fanren/model/Chapter.java \
 *            src/com/fanren/store/DataStore.java
 *   java  -cp out com.fanren.Main
 *
 * 可选 JVM 参数：
 *   -Dserver.port=8080           监听端口
 *   -Ddata.file=data/fanren.txt  正版文本路径（相对工作目录）
 */
public class Main {

    public static void main(String[] args) throws Exception {
        int port = Integer.getInteger("server.port", 8080);
        String dataFile = System.getProperty("data.file", "data/fanren.txt");

        DataStore store = DataStore.build(dataFile);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api", new ApiHandler(store));
        // 用线程池处理并发请求（默认单线程，读小说并发不高也够，但仍开个小池）
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println("========================================");
        System.out.println(" 凡人修仙传 · 阅读后端已启动");
        System.out.println(" 地址: http://localhost:" + port + "/api");
        System.out.println(" 书单:   GET /api/books");
        System.out.println(" 目录:   GET /api/books/fanren/chapters?offset=0&limit=50");
        System.out.println(" 章节:   GET /api/chapters/fanren-1");
        System.out.println(" 搜索:   GET /api/search?q=韩立");
        System.out.println("========================================");
    }
}
