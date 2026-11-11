package com.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Yanıt gövdesini ve başlıklarını üreten arayüz (Interface Segregation & Dependency Inversion)
 */
interface HttpResponseRenderer {
    String renderHtml();
}

/**
 * Biyografi ve kullanıcı arayüzü HTML içeriğini üreten sınıf (Single Responsibility)
 */
class ProfilePageRenderer implements HttpResponseRenderer {
    @Override
    public String renderHtml() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"tr\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Öğrenci Profili</title>\n" +
                "    <style>\n" +
                "        :root {\n" +
                "            --primary: #2563eb;\n" +
                "            --bg: #f8fafc;\n" +
                "            --card-bg: #ffffff;\n" +
                "            --text-main: #1e293b;\n" +
                "            --text-muted: #64748b;\n" +
                "        }\n" +
                "        body {\n" +
                "            font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;\n" +
                "            background-color: var(--bg);\n" +
                "            color: var(--text-main);\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            min-height: 100vh;\n" +
                "            margin: 0;\n" +
                "            padding: 20px;\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "        .card {\n" +
                "            background: var(--card-bg);\n" +
                "            max-width: 650px;\n" +
                "            width: 100%;\n" +
                "            border-radius: 12px;\n" +
                "            box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);\n" +
                "            padding: 40px;\n" +
                "            border-top: 6px solid var(--primary);\n" +
                "        }\n" +
                "        h1 {\n" +
                "            margin: 0 0 8px 0;\n" +
                "            font-size: 28px;\n" +
                "            color: var(--text-main);\n" +
                "            letter-spacing: -0.5px;\n" +
                "        }\n" +
                "        h2 {\n" +
                "            margin: 0 0 24px 0;\n" +
                "            font-size: 18px;\n" +
                "            color: var(--primary);\n" +
                "            font-weight: 600;\n" +
                "        }\n" +
                "        .bio-title {\n" +
                "            font-size: 14px;\n" +
                "            font-weight: 700;\n" +
                "            text-transform: uppercase;\n" +
                "            letter-spacing: 0.05em;\n" +
                "            color: var(--text-muted);\n" +
                "            margin-bottom: 8px;\n" +
                "        }\n" +
                "        .bio-text {\n" +
                "            font-size: 15px;\n" +
                "            line-height: 1.7;\n" +
                "            color: #334155;\n" +
                "            background-color: #f1f5f9;\n" +
                "            padding: 16px;\n" +
                "            border-radius: 8px;\n" +
                "            border-left: 4px solid var(--primary);\n" +
                "        }\n" +
                "        .tag-container {\n" +
                "            margin-top: 20px;\n" +
                "            display: flex;\n" +
                "            gap: 8px;\n" +
                "            flex-wrap: wrap;\n" +
                "        }\n" +
                "        .tag {\n" +
                "            background: #e2e8f0;\n" +
                "            color: #475569;\n" +
                "            padding: 4px 10px;\n" +
                "            border-radius: 9999px;\n" +
                "            font-size: 12px;\n" +
                "            font-weight: 500;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"card\">\n" +
                "        <!-- İsim ve Öğrenci No alanlarını güncelleyiniz -->\n" +
                "        <h1>Kaan Öz</h1>\n" +
                "        <h2>Öğrenci No: 1250505048</h2>\n" +
                "        \n" +
                "        <div class=\"bio-title\">Hakkımda</div>\n" +
                "        <div class=\"bio-text\">\n" +
                "            Yazılım Mühendisliği öğrencisiyim. Nesne yönelimli programlama, modern web mimarileri,\n" +
                "            dağıtık sistemler ve düşük seviyeli ağ protokolleri üzerine odaklanıyorum. Bu proje kapsamında,\n" +
                "            üçüncü parti kütüphane kullanılmadan ham TCP/IP soketleri ve HTTP 1.1 protokol standartları\n" +
                "            kullanılarak saf Java ile bir sunucu altyapısı inşa edilmiştir.\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"tag-container\">\n" +
                "            <span class=\"tag\">Java</span>\n" +
                "            <span class=\"tag\">Socket Programming</span>\n" +
                "            <span class=\"tag\">HTTP/1.1</span>\n" +
                "            <span class=\"tag\">SOLID</span>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}

/**
 * Gelen istemci bağlantısını işleyen iş parçacığı görevi (Single Responsibility)
 */
class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final HttpResponseRenderer renderer;

    public ClientHandler(Socket clientSocket, HttpResponseRenderer renderer) {
        this.clientSocket = clientSocket;
        this.renderer = renderer;
    }

    @Override
    public void run() {
        try (
                InputStream input = clientSocket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
                OutputStream output = clientSocket.getOutputStream()
        ) {
            // HTTP Request Satırını Oku
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            // HTTP Başlıklarını (Header) tüket (Gövdeye kadar olan boş satıra kadar)
            String headerLine;
            while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
                // Header ayrıştırma ihtiyacı olursa burada işlenebilir
            }

            // HTML Gövdesini hazırla
            byte[] bodyBytes = renderer.renderHtml().getBytes(StandardCharsets.UTF_8);

            // Standart HTTP/1.1 Yanıt Başlıkları
            String httpResponse = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/html; charset=UTF-8\r\n" +
                    "Content-Length: " + bodyBytes.length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";

            // Yanıtı soket üzerinden istemciye (tarayıcıya) yaz
            output.write(httpResponse.getBytes(StandardCharsets.UTF_8));
            output.write(bodyBytes);
            output.flush();

        } catch (IOException e) {
            System.err.println("İstemci iletişim hatası: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Soket kapatma hatası: " + e.getMessage());
            }
        }
    }
}

/**
 * Sunucu soketini dinleyen ve bağlantıları yöneten ana sunucu sınıfı
 */
public class SimpleWebServer {
    private static final int PORT = 1989;
    private static final int THREAD_POOL_SIZE = 10;

    private final int port;
    private final HttpResponseRenderer renderer;
    private final ExecutorService threadPool;
    private volatile boolean isRunning = true;

    public SimpleWebServer(int port, HttpResponseRenderer renderer) {
        this.port = port;
        this.renderer = renderer;
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Web Sunucusu 1989 portunda başlatıldı...");
            System.out.println("Erişim adresi: http://localhost:" + port);

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    // Her gelen bağlantı thread havuzuna teslim edilir (Non-blocking ana döngü)
                    threadPool.execute(new ClientHandler(clientSocket, renderer));
                } catch (IOException e) {
                    if (!isRunning) {
                        break;
                    }
                    System.err.println("Bağlantı kabul hatası: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Sunucu başlatılamadı (Port meşgul olabilir): " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }

    public static void main(String[] args) {
        // Bağımlılık dışarıda oluşturulup sunucuya enjekte edilir (Dependency Inversion)
        HttpResponseRenderer renderer = new ProfilePageRenderer();
        SimpleWebServer server = new SimpleWebServer(PORT, renderer);
        server.start();
    }
}