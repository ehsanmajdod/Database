import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Main extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/laptop_db";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    private JTextField txtBrand, txtProcessor, txtRam, txtPrice, txtId;
    private JCheckBox chkSsd;
    private JTable table;
    private DefaultTableModel tableModel;

    public Main() {
        setTitle("سیستم گرافیکی مدیریت لپ‌تاپ");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // پنل ورود اطلاعات (سمت راست)
        JPanel panelForm = new JPanel(new GridLayout(6, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelForm.add(new JLabel("برند:"));
        txtBrand = new JTextField(); panelForm.add(txtBrand);

        panelForm.add(new JLabel("پردازنده:"));
        txtProcessor = new JTextField(); panelForm.add(txtProcessor);

        panelForm.add(new JLabel("رم (GB):"));
        txtRam = new JTextField(); panelForm.add(txtRam);

        panelForm.add(new JLabel("قیمت:"));
        txtPrice = new JTextField(); panelForm.add(txtPrice);

        panelForm.add(new JLabel("وضعیت حافظه:"));
        chkSsd = new JCheckBox("دارای SSD"); panelForm.add(chkSsd);

        panelForm.add(new JLabel("کد لپ‌تاپ (برای حذف/ویرایش):"));
        txtId = new JTextField(); panelForm.add(txtId);

        // دکمه‌ها
        JPanel panelButtons = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("ثبت جدید");
        JButton btnRefresh = new JButton("نمایش همه");
        JButton btnUpdate = new JButton("ویرایش قیمت");
        JButton btnDelete = new JButton("حذف");

        panelButtons.add(btnAdd); panelButtons.add(btnRefresh);
        panelButtons.add(btnUpdate); panelButtons.add(btnDelete);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(panelForm, BorderLayout.CENTER);
        rightPanel.add(panelButtons, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);

        // جدول نمایش داده‌ها (سمت چپ)
        tableModel = new DefaultTableModel(new String[]{"کد", "برند", "پردازنده", "رم", "SSD", "قیمت"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // رویداد دکمه‌ها
        btnAdd.addActionListener(e -> {
            insertLaptop(txtBrand.getText(), txtProcessor.getText(), Integer.parseInt(txtRam.getText()), chkSsd.isSelected(), Integer.parseInt(txtPrice.getText()));
            showAllLaptops();
        });

        btnRefresh.addActionListener(e -> showAllLaptops());

        btnUpdate.addActionListener(e -> {
            updateLaptopPrice(Integer.parseInt(txtId.getText()), Integer.parseInt(txtPrice.getText()));
            showAllLaptops();
        });

        btnDelete.addActionListener(e -> {
            deleteLaptop(Integer.parseInt(txtId.getText()));
            showAllLaptops();
        });

        showAllLaptops(); // بارگذاری اولیه داده‌ها
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

    // متدهای اتصال به دیتابیس
    private void insertLaptop(String brand, String processor, int ram, boolean hasSsd, int price) {
        String sql = "INSERT INTO laptops (brand, processor, ram_gb, has_ssd, price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, brand);
            pstmt.setString(2, processor);
            pstmt.setInt(3, ram);
            pstmt.setBoolean(4, hasSsd);
            pstmt.setInt(5, price);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "لپ‌تاپ ثبت شد!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "خطا: " + e.getMessage());
        }
    }

    private void showAllLaptops() {
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM laptops";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("brand"),
                        rs.getString("processor"),
                        rs.getInt("ram_gb"),
                        rs.getBoolean("has_ssd") ? "بلی" : "نخیر",
                        rs.getInt("price")
                });
            }
        } catch (Exception e) {
            System.out.println("خطا در لود جدول: " + e.getMessage());
        }
    }

    private void updateLaptopPrice(int id, int newPrice) {
        String sql = "UPDATE laptops SET price = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newPrice);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "قیمت بروز شد!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "خطا: " + e.getMessage());
        }
    }

    private void deleteLaptop(int id) {
        String sql = "DELETE FROM laptops WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "لپ‌تاپ حذف شد!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "خطا: " + e.getMessage());
        }
    }
}