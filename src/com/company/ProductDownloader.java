package com.company;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProductDownloader implements Runnable {
    String link;
    File out;

    public ProductDownloader(String link, File out)
    {
        this.link = link;
        this.out = out;
    }

    public void run() {
        try {
            URL url = new URL(link);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
//            double fileSize = (double)http.getContentLengthLong();
            BufferedInputStream in = new BufferedInputStream(http.getInputStream());
            FileOutputStream fos = new FileOutputStream(this.out);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
            byte[] buffer = new byte[1024];
//            double downloaded = 0.00;
            int read = 0;
//            double percentDownload = 0.00;
            while ((read = in.read(buffer, 0, 1024)) >= 0)
            {
                bout.write(buffer, 0, read);
//                downloaded += read;
//                percentDownload = (downloaded * 100) / fileSize;
//                String percent = String.format("%.4f", percentDownload);
//                System.out.println("Downloaded: " + percent + "% of a file.");
            }
            bout.close();
            in.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        // System.out.println("Download completed.");
    }
}

