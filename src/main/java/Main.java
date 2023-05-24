import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.net.ssl.*;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Main{


    private static String getAccessToken()
    {
        String at = "";
        try {
            URL url = new URL("https://clienttoken.spotify.com/v1/clienttoken");

            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();

            urlConnection.setRequestMethod("OPTIONS");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");

            InputStream inputStream = urlConnection.getInputStream();
            String output = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println(output);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String tokenRequest = "{\"client_data\":{\"client_version\":\"1.2.13.60.g6e472d1f\",\"client_id\":\"d8a5ed958d274c2e8ee717e6a4b0971d\",\"js_sdk_data\":{\"device_brand\":\"unknown\",\"device_model\":\"unknown\",\"os\":\"linux\",\"os_version\":\"unknown\",\"device_id\":\"a2a98ad37f582df8721dc50c90372d90\",\"device_type\":\"computer\"}}}";
/*        try {
            URL url = new URL("https://clienttoken.spotify.com/v1/clienttoken");

            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");

            urlConnection.setDoOutput(true);

            urlConnection.getOutputStream().write(tokenRequest.getBytes(StandardCharsets.UTF_8));

            InputStream inputStream = urlConnection.getInputStream();
            String output = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            JSONObject response = new JSONObject(output);
            at = response.getJSONObject("granted_token").getString("token");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return at;
    }

    private static String getFetchUrl(final String playlistId, int offset, int maxItems, boolean extension)
    {
        final String variables = "{\"uri\":\"spotify:playlist:" +
                playlistId +
                "\",\"offset\":"+offset+",\"limit\":"+maxItems+"}";
        final String extensions = "{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"c56c706a062f82052d87fdaeeb300a258d2d54153222ef360682a0ee625284d9\"}}";
        if(extension) {
            return "https://api-partner.spotify.com/pathfinder/v1/query?operationName=fetchPlaylistContents&variables=" +
                    URLEncoder.encode(variables, StandardCharsets.UTF_8) + "&extensions=" + URLEncoder.encode(extensions, StandardCharsets.UTF_8);
        }
        return "https://api-partner.spotify.com/pathfinder/v1/query?operationName=fetchPlaylistContents&variables=" +
                URLEncoder.encode(variables, StandardCharsets.UTF_8);
    }

    private static String getFetchUrl2(final String playlistId, int offset, int maxItems)
    {
        return "https://api.spotify.com/v1/playlists/"+playlistId;
    }

    private static String getBearerToken()
    {
        String bt = "";
        final String urlstr = "https://accounts.spotify.com/api/token";
        try {
            URL url = new URL(urlstr);

            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.setRequestProperty("authorization", "Basic " + Base64.getEncoder().encodeToString(("31hzddvcnmqgak3jpjgtcdbu6rjy:takaw82139").getBytes()));

            urlConnection.setDoOutput(true);

            urlConnection.getOutputStream().write("grant_type=client_credentials".getBytes(StandardCharsets.UTF_8));

            InputStream inputStream = urlConnection.getInputStream();
            String output = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println(output);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bt;
    }

    private static String getSiteUrl(final String playlistId)
    {
        return "https://open.spotify.com/playlist/"+playlistId;
    }

    private static String fetchHtml(final String urlstr)
    {
        try {
            URL url = new URL(urlstr);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            return new String(urlConnection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static void minimizeWindow(WebDriver driver) {
        // Get the current window size
        Dimension originalSize = driver.manage().window().getSize();

        // Set a small size to minimize the window
        Dimension minimizedSize = new Dimension(200, 200);
        driver.manage().window().setSize(minimizedSize);

        // Move the window to a corner of the screen
        Point cornerLocation = new Point(0, 0);
        driver.manage().window().setPosition(cornerLocation);

        // Maximize the window to restore its original size
        driver.manage().window().maximize();
    }

    private static String fetchPlaylistName(final String playlistId ,final String bearerToken)
    {
        try {

            String accessToken = getAccessToken();
            final String fetchurl = "https://api.spotify.com/v1/playlists/"+playlistId;
            URL url = new URL(fetchurl);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("client-token", accessToken);
            urlConnection.setRequestProperty("authorization", " Bearer " + bearerToken);

            String s = new String(urlConnection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(s);
            return jsonObject.getString("name");
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        return null;

    }

    private static List<Track> fetchPlaylistTracks(final String playlistId, final String bearerToken)
    {
        ArrayList<Track> tracks = new ArrayList<>();
        try {

            String accessToken = getAccessToken();
            final String fetchurl = "https://api.spotify.com/v1/playlists/"+playlistId+"/tracks";
            URL url = new URL(fetchurl);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("client-token", accessToken);
            urlConnection.setRequestProperty("authorization", " Bearer " + bearerToken);

            final String s = new String(urlConnection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            final JSONObject playlist = new JSONObject(s);
            final JSONArray items = playlist.getJSONArray("items");
            for(int i = 0; i < items.length(); i++) {
                final JSONObject track = items.getJSONObject(i);
                String title = track.getJSONObject("track").getString("name");
                JSONArray artists = track.getJSONObject("track").getJSONArray("artists");
                final StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (int a = 0; a < artists.length(); a++) {
                    final String author = artists.getJSONObject(a).getString("name");
                    if(!first)
                    {
                        sb.append(", ");
                    }
                    sb.append(author);
                    first = false;
                }
                tracks.add(new Track(title,sb.toString(),0));
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        return tracks;

    }

    public static void minimizeWindowsWithTitleContaining(String keyword) {
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window.getType() == Window.Type.NORMAL) {
                window.setSize(1,1);
            }
        }
    }
    private static List<Track> getPlaylist(final String playlistId, int scrollCounts)
    {
        String fetchUrl = getSiteUrl(playlistId);

        String html2 = fetchHtml(fetchUrl);

        Document document2 = Jsoup.parse(html2);
        Element ele = document2.getElementById("session");
        String session = ele.html();
        JSONObject tokens = new JSONObject(session);

        final String accessToken = tokens.getString("accessToken");

        String fetchedPlaylistName = fetchPlaylistName(playlistId, accessToken);

        List<Track> fetchedPlaylistTracks = fetchPlaylistTracks(playlistId, accessToken);

        return fetchedPlaylistTracks;
    }

    private static Trackinfo querySong(final String id) throws IOException {


        final String fetchUrl = "https://songbpm.com/searches/" +id;

        String s = fetchHtml(fetchUrl);

        int a = s.indexOf(">Duration<");
        int b = s.indexOf("</",a+11 );
        int c = s.indexOf(">BPM<");
        int d = s.indexOf("</",c+6 );
        String duration = "0:0";
        if(b>a) {
            duration = s.substring(a, b);
            int i = duration.length()-1;
            while (i >= 0 && duration.charAt(i) != '>')
            {
                i--;
            }
            duration = duration.substring(i+1);
        }
        String bpm = "0";
        if(d>c) {
            bpm = s.substring(c, d);
            int t = bpm.length()-1;
            while (t >= 0 && bpm.charAt(t) != '>')
            {
                t--;
            }
            bpm = bpm.substring(t+1);
        }


        int dur = -1;
        if(duration.contains(":"))
        {
            String[] split = duration.split(":");
            dur = 60*Integer.valueOf(split[0])+Integer.valueOf(split[1]);
        }

        Trackinfo trackinfo = new Trackinfo(Integer.valueOf(bpm),dur);

        return trackinfo;
    }

    public static HostnameVerifier allowAllHostNames() {
        return (hostname, sslSession) -> true;
    }

    private static void allowSelfSignedSSL()
    {
        HttpsURLConnection.setDefaultHostnameVerifier(allowAllHostNames());
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

// Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {
        }
    }

    private static String queryTrackId(final String title, final String author) throws IOException {
        allowSelfSignedSSL();
        URL url = new URL("https://songbpm.com/api/search");
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        final OutputStream outputStream = urlConnection.getOutputStream();
        outputStream.write(("{\"query\":\""+author+" "+title+"\"}").getBytes(StandardCharsets.UTF_8));

        byte[] bytes = urlConnection.getInputStream().readAllBytes();
        String json = new String(bytes, Charset.defaultCharset());

        JSONObject jsonObject = new JSONObject(json);

        if(jsonObject.getString("status").equals("ok"))
        {
            String songId = jsonObject.getJSONObject("data").getString("id");
            return songId;
        }
        else
        {
            return null;
        }
    }

    public static Trackinfo getTrackInfo(final String title, final String author)
    {
        try {
            String traclId = queryTrackId(title, author);
            return querySong(traclId);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            return new Trackinfo(0, -1);
        }
    }

    public static void main(final String[] args)
    {
        if(args.length == 0 || args[0].contains("help") || args[0].contains("?") || args.length > 2)
        {
            System.out.println("Usage: spotify-playlist id [output.csv] (e.g. '4fGATKVMlgZsfLt9pNg4f7' from the url like open.spotify.com/playlist/4fGATKVMlgZsfLt9pNg4f7\nthis playlistid\nthis 4fGATKVMlgZsfLt9pNg4f7");
            return;
        }

        String outfile = "playlist.csv";

        if(args.length == 2)
        {
            outfile = args[1];
        }


        System.out.println("Loading playlist...");
        List<Track> playlist = getPlaylist(args[0], 4);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(outfile);
            try {
                fileOutputStream.write("Author; Title; BPM\n".getBytes());
            } catch (IOException e) {
            }
        } catch (FileNotFoundException e) {
        }

        int c = 0;
        System.out.println("Fetching bpm: ");
        for (Track t : playlist)
        {
            Trackinfo trackInfo = new Trackinfo(0,-1);
            try {
                final Trackinfo[] trackinfos = new Trackinfo[1];
                Thread thread = new Thread(() -> {
                    trackinfos[0] = getTrackInfo(t.getTitle(), t.getAuthor());
                });
                thread.start();
                long start = Instant.now().toEpochMilli();
                while (trackinfos[0] == null && Instant.now().toEpochMilli() - start < 11000)
                {
                    Thread.sleep(10);
                };
                if(trackinfos[0]!=null)
                {
                    trackInfo = trackinfos[0];
                }
            }
            catch (Exception e)
            {

            }
            t.setBpm(trackInfo.getBpm());
            c++;
            System.out.print("\r" + 100.0f*c/ playlist.size() + "%");
        }

        playlist.sort(Track::compareTo);
        System.out.println("\n\nPlaylist:\n");

        for (Track t : playlist) {
            try {
                fileOutputStream.write((t.getTitle() + "; " + t.getAuthor() + "; " + t.getBpm() + "\n").getBytes());
            } catch (IOException e) {
            }
            System.out.println(t.getBpm() + "  " + t.getTitle() + " – " + t.getAuthor());
        }


    }


}
