package net.runelite.client.plugins.vmswimshamer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DiscordWebhook {

    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    private void sendFile(OutputStream out, String name, InputStream in, String fileName) throws IOException {
        String o = "Content-Disposition: form-data; name=\"" + URLEncoder.encode(name,"UTF-8")
                + "\"; filename=\"" + URLEncoder.encode(fileName,"UTF-8") + "\"\r\n\r\n";
        out.write(o.getBytes(StandardCharsets.UTF_8));
        byte[] buffer = new byte[2048];
        for (int n = 0; n >= 0; n = in.read(buffer))
            out.write(buffer, 0, n);
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private static String format(Date date)
    {
        synchronized (TIME_FORMAT)
        {
            return TIME_FORMAT.format(date);
        }
    }

    private void sendField(OutputStream out, String name, String field) throws IOException {
        String o = "Content-Disposition: form-data; name=\""
                + URLEncoder.encode(name,"UTF-8") + "\"\r\n\r\n";
        out.write(o.getBytes(StandardCharsets.UTF_8));
        out.write(URLEncoder.encode(field,"UTF-8").getBytes(StandardCharsets.UTF_8));
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    public void SendWebhook(ByteArrayOutputStream screenshotOutput, String fileName, String discordUrl) throws IOException {
        fileName += (fileName.isEmpty() ? "" : " ") + format(new Date());
        fileName = fileName.replace('+', ' ');
        URL url = new URL(discordUrl);
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        String boundary = UUID.randomUUID().toString();
        byte[] boundaryBytes =
                ("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8);
        byte[] finishBoundaryBytes =
                ("--" + boundary + "--").getBytes(StandardCharsets.UTF_8);
        http.setRequestProperty("Content-Type",
                "multipart/form-data; charset=UTF-8; boundary=" + boundary);

        http.setChunkedStreamingMode(0);

        try(OutputStream out = http.getOutputStream()) {
            out.write(boundaryBytes);
            sendField(out, "content", fileName);
            out.write(boundaryBytes);
            try(InputStream file = new ByteArrayInputStream(screenshotOutput.toByteArray());) {
                sendFile(out, "file", file, "temp.png");
            }

            out.write(finishBoundaryBytes);
        }

    }
}
