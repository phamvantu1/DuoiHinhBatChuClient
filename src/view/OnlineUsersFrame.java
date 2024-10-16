///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
package view;


//import javax.swing.*;
//import java.awt.*;
//import java.util.List;
//import model.User;
//
//public class OnlineUsersFrame extends JFrame {
//    private DefaultListModel<String> onlineUsersListModel; // Mô hình dữ liệu cho danh sách người dùng
//    private JList<String> onlineUsersList; // Danh sách hiển thị người dùng online
//
//    public OnlineUsersFrame() {
//        setTitle("Danh sách người dùng online");
//        setSize(300, 400);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Đóng cửa sổ sẽ không kết thúc chương trình
//
//        onlineUsersListModel = new DefaultListModel<>(); // Khởi tạo mô hình danh sách
//        onlineUsersList = new JList<>(onlineUsersListModel); // Tạo danh sách từ mô hình
//
//        // Đặt JList vào JScrollPane để có thanh cuộn
//        JScrollPane scrollPane = new JScrollPane(onlineUsersList);
//        add(scrollPane, BorderLayout.CENTER); // Thêm thanh cuộn vào khung chính
//
//        setVisible(true); // Hiển thị cửa sổ
//    }
// 
//    // Phương thức để cập nhật danh sách người dùng online
//    public void updateOnlineUsers(List<User> onlineUsers) {
//          System.out.println("Updating online users: " + onlineUsers); 
//        onlineUsersListModel.clear();  // Xóa danh sách cũ
//        for (User user : onlineUsers) {
//            onlineUsersListModel.addElement(user.getNickname());  // Thêm từng người dùng vào danh sách
//        }
//        
//       // edit
//        onlineUsersList.repaint();  // Vẽ lại danh sách để cập nhật hiển thị
//    }
//}



import controller.Client;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.User;

public class OnlineUsersFrame extends JFrame {
    private JTable usersTable;
    private DefaultTableModel tableModel;

    public OnlineUsersFrame() {
        setTitle("Danh sách người dùng online");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Tạo bảng và model
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Avatar");
        tableModel.addColumn("Nickname");

        usersTable = new JTable(tableModel) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Để hiển thị ảnh, cột đầu tiên phải trả về Icon
                return columnIndex == 0 ? ImageIcon.class : String.class;
            }
        };

        // Đặt kích thước cột cho hình ảnh
        usersTable.getColumnModel().getColumn(0).setPreferredWidth(90); // Kích thước cột Avatar
        usersTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Kích thước cột Nickname

        // Đặt chiều cao hàng để vừa với ảnh 90x90
        usersTable.setRowHeight(90); // Kích thước chiều cao hàng

        add(new JScrollPane(usersTable), BorderLayout.CENTER);
    }

    public void updateOnlineUsers(List<User> onlineUsers) {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        for (User user : onlineUsers) {
            // Thêm ảnh và tên vào bảng
            ImageIcon avatar = new ImageIcon("assets/avatar/" + user.getAvatar() + ".jpg");
            System.out.println("anh : "+ Client.user.getAvatar());
            System.out.println("em : " + user.getAvatar());
            avatar = resizeImage(avatar, 90, 90); // Resize ảnh về kích thước 90x90
            tableModel.addRow(new Object[]{avatar, user.getNickname()});

        }
    }

    private ImageIcon resizeImage(ImageIcon originalImage, int width, int height) {
        Image img = originalImage.getImage();
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }
}
