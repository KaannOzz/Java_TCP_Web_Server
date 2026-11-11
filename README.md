# Java TCP Socket Web Server

Saf Java (Vanilla Java) ve TCP Soket programlama kullanılarak, herhangi bir üçüncü parti kütüphane veya web çatısı kullanılmadan geliştirilmiş hafif ve çok iş parçacıklı (multi-threaded) HTTP/1.1 web sunucusu.

## 📌 Proje Özellikleri

- **Saf Java Mimarisi:** Yalnızca `java.net` ve `java.io` kütüphaneleri kullanılmıştır.
- **Port:** `1989` portu üzerinden gelen HTTP isteklerini karşılar.
- **Çok İş Parçacıklı Destek (Multi-threaded):** `ExecutorService` (Thread Pool) mimarisi ile eş zamanlı tarayıcı isteklerini sunucu kilitlenmeden yönetir.
- **SOLID Tasarımı:** Modüler mimari, gevşek bağlılık (loose coupling) ve yüksek uyum (high cohesion) standartlarına göre tasarlanmıştır.
- **Biçimlendirilmiş Profil:** CSS ile stillendirilmiş H1, H2 ve biyografi kartı sunar.

---

## 🏗️ Mimari ve SOLID Yaklaşımı

| Sınıf / Arayüz | SOLID Prensibi | Görevi |
| :--- | :--- | :--- |
| `SimpleWebServer` | **SRP** | Port dinleme ve soket yaşam döngüsü yönetimi |
| `ClientHandler` | **SRP** | Gelen HTTP istek satırını okuma ve yanıtı sokete yazma |
| `HttpResponseRenderer` | **ISP / DIP** | Yanıt içeriği üretimini soyutlayan tekil arayüz |
| `ProfilePageRenderer` | **SRP / LSP** | Biyografi HTML/CSS çıktısını üreten somut sınıf |

---

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler
- Java Development Kit (JDK 11 veya üzeri)

### Çalıştırma
IntelliJ IDEA üzerinden `SimpleWebServer.java` dosyasını çalıştırabilir veya terminalden şu komutları kullanabilirsiniz:

```bash
# Kaynak kodları derleme
javac -d bin src/main/java/com/webserver/*.java

# Sunucuyu başlatma
java -cp bin com.webserver.SimpleWebServer