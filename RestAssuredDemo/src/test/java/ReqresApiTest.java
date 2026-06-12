import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class ReqresApiTest {

    @BeforeAll
    public static void setup() {
        // Yeni, tamamen ücretsiz ve anahtar istemeyen test servisimiz
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test
    @DisplayName("GET İsteği: 2 numaralı kullanıcıyı getir ve doğrula")
    public void getSingleUserTest() {
        System.out.println("\n==================================================");
        System.out.println("       GET İSTEĞİ TESTİ BAŞLIYOR");
        System.out.println("==================================================");

        // 1. İsteği at ve dönen cevabı (response) bir değişkene kaydet
        Response response = given()
                .when()
                .get("/users/2")
                .then()
                .extract().response(); // Yanıtı koparıp alıyoruz

        // Sadece gelen ham veriyi (body) görmek için yazdırıyoruz
        System.out.println("\n[SUNUCUDAN GELEN HAM YANIT]:");
        response.prettyPrint();
        System.out.println("--------------------------------------------------");

        // 2. KONTROLLERİ ADIM ADIM YAP VE YAZDIR
        System.out.println("1. Status Code Kontrolü Yapılıyor...");
        assertEquals(200, response.statusCode(), "HATA: Status Code 200 değil!");
        System.out.println("   [BAŞARILI] Status Code beklenen gibi: " + response.statusCode());

        System.out.println("\n2. Yanıt Süresi (Time) Kontrolü Yapılıyor...");
        long time = response.time();
        assertTrue(time < 3000L, "HATA: Yanıt süresi 3 saniyeyi aştı!");
        System.out.println("   [BAŞARILI] Yanıt Süresi çok iyi: " + time + " ms (Sınır: 3000 ms)");

        System.out.println("\n3. Body İçerisindeki 'id' Değeri Kontrolü Yapılıyor...");
        int id = response.jsonPath().getInt("id");
        assertEquals(2, id, "HATA: Gelen ID 2 değil!");
        System.out.println("   [BAŞARILI] Gelen ID değeri doğru: " + id);

        System.out.println("\n4. Body İçerisindeki 'name' Değeri Kontrolü Yapılıyor...");
        String name = response.jsonPath().getString("name");
        assertEquals("Ervin Howell", name, "HATA: Gelen isim uyuşmuyor!");
        System.out.println("   [BAŞARILI] Gelen isim beklenen kişi: " + name);

        System.out.println("==================================================\n");
    }

    @Test
    @DisplayName("POST İsteği: Yeni bir gönderi (post) oluştur")
    public void createPostTest() {
        System.out.println("\n==================================================");
        System.out.println("       POST İSTEĞİ TESTİ BAŞLIYOR");
        System.out.println("==================================================");

        String requestBody = "{\n" +
                "    \"title\": \"otomasyon\",\n" +
                "    \"body\": \"testimiz basariyla calisiyor\",\n" +
                "    \"userId\": 1\n" +
                "}";

        System.out.println("[GÖNDERİLEN VERİ]: \n" + requestBody);
        System.out.println("--------------------------------------------------");

        // 1. İsteği at ve dönen cevabı (response) değişkene kaydet
        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/posts")
                .then()
                .extract().response();

        // 2. KONTROLLERİ ADIM ADIM YAP VE YAZDIR
        System.out.println("1. Status Code Kontrolü Yapılıyor...");
        assertEquals(201, response.statusCode(), "HATA: Status Code 201 (Created) değil!");
        System.out.println("   [BAŞARILI] Kayıt başarıyla oluşturuldu. Status Code: " + response.statusCode());

        System.out.println("\n2. Yanıt Süresi (Time) Kontrolü Yapılıyor...");
        long time = response.time();
        assertTrue(time < 3000L, "HATA: Yanıt süresi 3 saniyeyi aştı!");
        System.out.println("   [BAŞARILI] Yanıt Süresi sınırlar içinde: " + time + " ms");

        System.out.println("\n3. Body İçerisindeki 'title' (Başlık) Kontrolü Yapılıyor...");
        String title = response.jsonPath().getString("title");
        assertEquals("otomasyon", title, "HATA: Başlık bizim gönderdiğimizle aynı değil!");
        System.out.println("   [BAŞARILI] Sistem başlığı doğru kaydetmiş: " + title);

        System.out.println("\n4. Sistemin Atadığı Yeni 'id' Kontrolü Yapılıyor...");
        Integer id = response.jsonPath().get("id"); // Null kontrolü yapabilmek için Integer nesnesi kullandık
        assertNotNull(id, "HATA: Sistem yeni veriye ID atamadı (Null döndü)!");
        System.out.println("   [BAŞARILI] Veritabanı bu kayda şu yeni ID'yi atadı: " + id);

        System.out.println("==================================================\n");
    }
}