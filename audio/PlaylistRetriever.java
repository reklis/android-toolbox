package com.cibotechnology.audio;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PlaylistRetriever {

    /**
     * Parses the PLS file format
     */
    public PlaylistRetriever() {
    }

    private URL url;

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    private ArrayList<String> playlistItems;

    public ArrayList<String> getPlaylistItems() {
        return playlistItems;
    }

    public void setPlaylistItems(ArrayList<String> playlistItems) {
        this.playlistItems = playlistItems;
    }

    public void fetchAndParse(String playlistUrlString) {
        HttpURLConnection urlConnection = null;
        BufferedInputStream is = null;

        try {
            URL playlistUrl = new URL(playlistUrlString);

            if (!playlistUrl.equals(this.getUrl())) {
                urlConnection = (HttpURLConnection) playlistUrl.openConnection();
                is = new BufferedInputStream(urlConnection.getInputStream());

                readStream(is);

                this.setUrl(playlistUrl);
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (null != urlConnection) {
                urlConnection.disconnect();
            }
        }
    }

    public void readStream(BufferedInputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            ArrayList<String> list = new ArrayList<String>();
            String line = "";
            while ((line = reader.readLine()) != null) {
                String[] kvp = line.split("=");
                if (2 == kvp.length) {
                    if (kvp[0].startsWith("File")) {
                        list.add(kvp[1]);
                    }
                }
            }
            setPlaylistItems(list);
        } finally {
            reader.close();
        }
    }

    public String getFirstItem() {
        ArrayList<String> list = getPlaylistItems();
        if ((null != list) && (0 != list.size())) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
