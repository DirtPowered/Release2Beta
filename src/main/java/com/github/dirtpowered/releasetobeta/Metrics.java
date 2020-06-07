package com.github.dirtpowered.releasetobeta;

import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.zip.GZIPOutputStream;

/**
 * bStats collects some data for plugin authors.
 * Check out https://bStats.org/ to learn more about bStats!
 */
class Metrics {
    private static final int B_STATS_VERSION = 1;
    private static final String URL = "https://bStats.org/submitData/server-implementation";
    private static boolean logFailedRequests = true;
    private static ReleaseToBeta releaseToBeta;
    private final String name = "Release2Beta";
    private final String serverUUID;
    private final List<CustomChart> charts = new ArrayList<>();

    private Metrics(String serverUUID, boolean logFailedRequests, ReleaseToBeta releaseToBeta) {
        this.serverUUID = serverUUID;
        Metrics.releaseToBeta = releaseToBeta;
        Metrics.logFailedRequests = logFailedRequests;

        startSubmitting();
    }

    private static void sendData(JsonObject data) throws Exception {
        if (data == null)
            throw new IllegalArgumentException("Data cannot be null!");

        HttpsURLConnection connection = (HttpsURLConnection) new URL(URL).openConnection();

        byte[] compressedData = compress(data.toString());

        connection.setRequestMethod("POST");
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Connection", "close");
        connection.addRequestProperty("Content-Encoding", "gzip"); // We gzip our request
        connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
        connection.setRequestProperty("Content-Type", "application/json"); // We send our data in JSON format
        connection.setRequestProperty("User-Agent", "MC-Server/" + B_STATS_VERSION);

        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.write(compressedData);
        outputStream.flush();
        outputStream.close();

        connection.getInputStream().close();
    }

    private static byte[] compress(final String str) throws IOException {
        if (str == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(outputStream);
        gzip.write(str.getBytes(StandardCharsets.UTF_8));
        gzip.close();
        return outputStream.toByteArray();
    }

    private void addCustomChart(CustomChart chart) {
        if (chart == null) {
            throw new IllegalArgumentException("Chart cannot be null!");
        }
        charts.add(chart);
    }

    private void startSubmitting() {
        final Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                submitData();
            }
        }, 1000 * 60 * 5, 1000 * 60 * 30);
    }

    private JsonObject getPluginData() {
        JsonObject data = new JsonObject();

        data.addProperty("pluginName", name);
        data.addProperty("id", 7777);
        data.addProperty("metricsRevision", B_STATS_VERSION);
        JsonArray customCharts = new JsonArray();
        for (CustomChart customChart : charts) {
            JsonObject chart = customChart.getRequestJsonObject();
            if (chart == null) {
                continue;
            }
            customCharts.add(chart);
        }

        data.add("customCharts", customCharts);
        return data;
    }

    private JsonObject getServerData() {
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String osVersion = System.getProperty("os.version");
        int coreCount = Runtime.getRuntime().availableProcessors();

        JsonObject data = new JsonObject();

        data.addProperty("serverUUID", serverUUID);

        data.addProperty("osName", osName);
        data.addProperty("osArch", osArch);
        data.addProperty("osVersion", osVersion);
        data.addProperty("coreCount", coreCount);

        return data;
    }

    private void submitData() {
        final JsonObject data = getServerData();

        JsonArray pluginData = new JsonArray();
        pluginData.add(getPluginData());
        data.add("plugins", pluginData);

        try {
            sendData(data);
        } catch (Exception e) {
            if (logFailedRequests) {
                releaseToBeta.getLogger().warning("Could not submit stats of " + name);
            }
        }
    }

    public static abstract class CustomChart {

        final String chartId;

        CustomChart(String chartId) {
            if (chartId == null || chartId.isEmpty()) {
                throw new IllegalArgumentException("ChartId cannot be null or empty!");
            }
            this.chartId = chartId;
        }

        private JsonObject getRequestJsonObject() {
            JsonObject chart = new JsonObject();
            chart.addProperty("chartId", chartId);
            try {
                JsonObject data = getChartData();
                if (data == null)
                    return null;

                chart.add("data", data);
            } catch (Throwable t) {
                if (logFailedRequests) {
                    releaseToBeta.getLogger().warning("Failed to get data for custom chart with id " + chartId);
                }
                return null;
            }
            return chart;
        }

        protected abstract JsonObject getChartData() throws Exception;

    }

    public static class SimplePie extends CustomChart {

        private final Callable<String> callable;

        SimplePie(String chartId, Callable<String> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() throws Exception {
            JsonObject data = new JsonObject();
            String value = callable.call();
            if (value == null || value.isEmpty())
                return null;

            data.addProperty("value", value);
            return data;
        }
    }

    public static class SingleLineChart extends CustomChart {

        private final Callable<Integer> callable;

        SingleLineChart(String chartId, Callable<Integer> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JsonObject getChartData() throws Exception {
            JsonObject data = new JsonObject();
            int value = callable.call();
            if (value == 0)
                return null;

            data.addProperty("value", value);
            return data;
        }
    }

    static class R2BMetrics {

        R2BMetrics(ReleaseToBeta releaseToBeta) {
            Metrics metrics = new Metrics(R2BConfiguration.metricsUniqueId, true, releaseToBeta);

            metrics.addCustomChart(new Metrics.SingleLineChart("players", releaseToBeta.getBootstrap()::getOnline));
            metrics.addCustomChart(new Metrics.SimplePie("version", R2BConfiguration.version::name));
            metrics.addCustomChart(new Metrics.SimplePie("platform", releaseToBeta.getBootstrap().getPlatform()::name));
        }
    }
}