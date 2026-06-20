import java.sql.*;
import java.util.Scanner;

// کلاس اصلی برنامه همراه با منوی کاربری
public class LaptopProject{
    private static final String URL = "jdbc:mysql://localhost:3306/laptop_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n* سیستم مدیریت لپ‌تاپ *");
            System.out.println("1. ثبت لپ‌تاپ جدید");
            System.out.println("2. نمایش همه لپ‌تاپ‌ها");
            System.out.println("3. تغییر قیمت یک لپ‌تاپ");
            System.out.println("4. حذف یک لپ‌تاپ");
            System.out.println("5. خروج");
            System.out.print("یک گزینه را انتخاب کنید: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("برند: ");
                    String brand = scanner.nextLine();
                    System.out.print("پردازنده: ");
                    String processor = scanner.nextLine();
                    System.out.print("مقدار رم (GB): ");
                    int ram = scanner.nextInt();
                    System.out.print("آیا SSD دارد؟ (true/false): ");
                    boolean hasSsd = scanner.nextBoolean();
                    System.out.print("قیمت: ");
                    int price = scanner.nextInt();

                    insertLaptop(brand, processor, ram, hasSsd, price);
                    break;

                case 2:
                    showAllLaptops();
                    break;

                case 3:
                    System.out.print("کد (ID) لپ‌تاپ مورد نظر: ");
                    int updateId = scanner.nextInt();
                    System.out.print("قیمت جدید را وارد کنید: ");
                    int newPrice = scanner.nextInt();

                    updateLaptopPrice(updateId, newPrice);
                    break;

                case 4:
                    System.out.print("کد (ID) لپ‌تاپ جهت حذف: ");
                    int deleteId = scanner.nextInt();

                    deleteLaptop(deleteId);
                    break;

                case 5:
                    System.out.println("خروج از برنامه. موفق باشید!");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("❌ گزینه نامعتبر است! دوباره تلاش کنید.");
            }
        }
    }

    // ۱. عملیات افزودن لپ‌تاپ جدید
    public static void insertLaptop(String brand, String processor, int ram, boolean hasSsd, int price) {
        String sql = "INSERT INTO laptops (brand, processor, ram_gb, has_ssd, price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, brand);
            pstmt.setString(2, processor);
            pstmt.setInt(3, ram);
            pstmt.setBoolean(4, hasSsd);
            pstmt.setInt(5, price);

            pstmt.executeUpdate();
            System.out.println("✅ لپ‌تاپ با موفقیت ثبت شد!");
        } catch (SQLException e) {
            System.out.println("❌ خطا در ثبت: " + e.getMessage());
        }
    }

    // ۲. عملیات نمایش همه لپ‌تاپ‌ها
    public static void showAllLaptops() {
        String sql = "SELECT * FROM laptops";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- لیست لپ‌تاپ‌های موجود ---");
            while (rs.next()) {
                System.out.println("کد: " + rs.getInt("id") +
                        " | برند: " + rs.getString("brand") +
                        " | پردازنده: " + rs.getString("processor") +
                        " | رم: " + rs.getInt("ram_gb") + "GB" +
                        " | حافظه SSD: " + (rs.getBoolean("has_ssd") ? "دارد" : "ندارد") +
                        " | قیمت: " + rs.getInt("price"));
            }
            System.out.println("----------------------------");
        } catch (SQLException e) {
            System.out.println("❌ خطا در نمایش داده‌ها: " + e.getMessage());
        }
    }

    // ۳. عملیات ویرایش قیمت
    public static void updateLaptopPrice(int id, int newPrice) {
        String sql = "UPDATE laptops SET price = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newPrice);
            pstmt.setInt(2, id);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ قیمت لپ‌تاپ با موفقیت به‌روزرسانی شد!");
            } else {
                System.out.println("⚠️ لپ‌تاپی با این کد پیدا نشد.");
            }
        } catch (SQLException e) {
            System.out.println("❌ خطا در ویرایش: " + e.getMessage());
        }
    }

    // ۴. عملیات حذف
    public static void deleteLaptop(int id) {
        String sql = "DELETE FROM laptops WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ لپ‌تاپ مورد نظر با موفقیت حذف شد!");
            } else {
                System.out.println("⚠️ لپ‌تاپی با این کد پیدا نشد.");
            }
        } catch (SQLException e) {
            System.out.println("❌ خطا در حذف: " + e.getMessage());
        }
    }
}
