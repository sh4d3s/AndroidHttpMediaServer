package server.http.android.androidhttpserver.server;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class MyServer extends NanoHTTPD {
    private final static int PORT = 8080;
    public static String PATH;

    public MyServer(String x) throws IOException {
        super(PORT);
        start();
        PATH = x;
        System.out.println("\nRunning! Point your browers to http://localhost:8080/ \n");
    }

    private Response createResponse(Response.Status status, String mimeType, InputStream message){
        Response res = new Response(status, mimeType, message);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    @Override
    public Response serve(String uri, Method method,
                          Map<String, String> header, Map<String, String> parameters,
                          Map<String, String> files) {
        String answer = "";

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(PATH);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new NanoHTTPD.Response(Response.Status.OK, "audio/mpeg", fis);
    }
}