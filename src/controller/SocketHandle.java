/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import static controller.Client.homePageFrm;
import model.User;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import view.HomePageFrm;
import view.OnlineUsersFrame;
import java.io.BufferedWriter;

/**
 * @author Admin
 */
public class SocketHandle implements Runnable {
    private BufferedWriter outputWriter;
    private Socket socketOfClient;
    private OnlineUsersFrame onlineUsersFrame;
    private HomePageFrm homePageFrm;
    private Socket socket;
    
   
    public List<User> getListUser(String[] message) {
        List<User> friend = new ArrayList<>();
        for (int i = 1; i < message.length; i = i + 4) {
            friend.add(new User(Integer.parseInt(message[i]),
                    message[i + 1],
                    message[i + 2].equals("1"),
                    message[i + 3].equals("1")));
        }
        return friend;
    }

    public List<User> getListRank(String[] message) {
        List<User> friend = new ArrayList<>();
        for (int i = 1; i < message.length; i = i + 9) {
            friend.add(new User(Integer.parseInt(message[i]),
                    message[i + 1],
                    message[i + 2],
                    message[i + 3],
                    message[i + 4],
                    Integer.parseInt(message[i + 5]),
                    Integer.parseInt(message[i + 6]),
                    Integer.parseInt(message[i + 7]),
                    Integer.parseInt(message[i + 8])));
        }
        return friend;
    }

    public User getUserFromString(int start, String[] message) {
        return new User(Integer.parseInt(message[start]),
                message[start + 1],
                message[start + 2],
                message[start + 3],
                message[start + 4],
                Integer.parseInt(message[start + 5]),
                Integer.parseInt(message[start + 6]),
                Integer.parseInt(message[start + 7]),
                Integer.parseInt(message[start + 8]));
    }

    @Override
    public void run() {

        try {
            socketOfClient = new Socket("127.0.0.1", 7777);
            System.out.println("Kết nối thành công!");
            outputWriter = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
            String message;
            while (true) {
                message = inputReader.readLine();
                if (message == null) {
                    break;
                }
                String[] messageSplit = message.split(",");
                if (messageSplit[0].equals("server-send-id")) {
                    int serverId = Integer.parseInt(messageSplit[1]);
                }
                //Đăng nhập thành công
                if (messageSplit[0].equals("login-success")) {
                    System.out.println("Đăng nhập thành công");
                    Client.closeAllViews();
                    Client.user = getUserFromString(1, messageSplit);
                    homePageFrm = new HomePageFrm();
                     homePageFrm.setVisible(true);
//                    Client.openView(Client.View.HOMEPAGE);
                }
                // Xử lý danh sách người dùng online
 

                    if (messageSplit[0].equals("online-users")) {
        System.out.println("Received online users message: " + message);  // Debug output
        String[] users = messageSplit[1].split(";");
        List<User> onlineUsers = new ArrayList<>();

 for (String userInfo : users) {
        String[] userParts = userInfo.split(":");
        if (userParts.length >= 2) {  // Đảm bảo có đủ dữ liệu nickname và avatar
            String avatar = userParts[0].trim();

            String nickname = userParts[1].trim();

            // Khởi tạo đối tượng User
            User user = new User(nickname,"", nickname, avatar);  
            onlineUsers.add(user);
        } else {
            System.out.println("Dữ liệu người dùng không hợp lệ: " + userInfo);
        }
    }


            try {
                // Kiểm tra nếu homePageFrm đã được khởi tạo và hiển thị
                if (homePageFrm != null && homePageFrm.isVisible()) {
                    homePageFrm.updateOnlineUsers(onlineUsers);
                    System.out.println("Danh sách người dùng đã được cập nhật");
                } else {

                     homePageFrm = new HomePageFrm();
                     Client.closeAllViews();

             homePageFrm.updateOnlineUsers(onlineUsers);
                    System.out.println("homePageFrm chưa được khởi tạo hoặc không hiển thị");
                }
            } catch (Exception e) {
                e.printStackTrace();  // Debugging để xem lỗi nếu xảy ra
            }

                // Hiển thị danh sách người dùng online trong giao diện mới
            SwingUtilities.invokeLater(() -> {
                // Kiểm tra nếu onlineUsersFrame chưa được khởi tạo hoặc không còn hiển thị
                if (onlineUsersFrame == null || !onlineUsersFrame.isVisible()) {
                    onlineUsersFrame = new OnlineUsersFrame();

                    onlineUsersFrame.dispose();
                }
                // Cập nhật danh sách người dùng online
                onlineUsersFrame.updateOnlineUsers(onlineUsers);
            });
            }

                //Thông tin tài khoản sai
                if (messageSplit[0].equals("wrong-user")) {
                    System.out.println("Thông tin sai");
                    Client.closeView(Client.View.GAME_NOTICE);
                    Client.openView(Client.View.LOGIN, messageSplit[1], messageSplit[2]);
                    Client.loginFrm.showError("Tài khoản hoặc mật khẩu không chính xác");
                }
                //Tài khoản đã đăng nhập ở nơi khác
                if (messageSplit[0].equals("dupplicate-login")) {
                    System.out.println("Đã đăng nhập");
                    Client.closeView(Client.View.GAME_NOTICE);
                    Client.openView(Client.View.LOGIN, messageSplit[1], messageSplit[2]);
                    Client.loginFrm.showError("Tài khoản đã đăng nhập ở nơi khác");
                }
                //Tài khoản đã bị banned
                if (messageSplit[0].equals("banned-user")) {
                    Client.closeView(Client.View.GAME_NOTICE);
                    Client.openView(Client.View.LOGIN, messageSplit[1], messageSplit[2]);
                    Client.loginFrm.showError("Tài khoản đã bị ban");
                }
                //Xử lý register trùng tên
                if (messageSplit[0].equals("duplicate-username")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.REGISTER);
                    JOptionPane.showMessageDialog(Client.registerFrm, "Tên tài khoản đã được người khác sử dụng");
                }
                //Xử lý nhận thông tin, chat từ toàn server
                if (messageSplit[0].equals("chat-server")) {
                    if (Client.homePageFrm != null) {
                        Client.homePageFrm.addMessage(messageSplit[1]);
                    }
                }
                //Xử lý hiển thị thông tin đối thủ là bạn bè/không
                if (messageSplit[0].equals("check-friend-response")) {
//                    if (Client.competitorInfoFrm != null) {
//                        Client.competitorInfoFrm.checkFriend((messageSplit[1].equals("1")));
//                    }
                }
                //Xử lý kết quả tìm phòng từ server
                if (messageSplit[0].equals("room-fully")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                    JOptionPane.showMessageDialog(Client.homePageFrm, "Phòng chơi đã đủ 2 người chơi");
                }
                // Xử lý không tìm thấy phòng trong chức năng vào phòng
                if (messageSplit[0].equals("room-not-found")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                    JOptionPane.showMessageDialog(Client.homePageFrm, "Không tìm thấy phòng");
                }
                // Xử lý phòng có mật khẩu sai
                if (messageSplit[0].equals("room-wrong-password")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                    JOptionPane.showMessageDialog(Client.homePageFrm, "Mật khẩu phòng sai");
                }
                //Xử lý xem rank
                if (messageSplit[0].equals("return-get-rank-charts")) {
//                    if (Client.rankFrm != null) {
//                        Client.rankFrm.setDataToTable(getListRank(messageSplit));
//                    }
                }
                //Xử lý lấy danh sách phòng
                if (messageSplit[0].equals("room-list")) {
                    Vector<String> rooms = new Vector<>();
                    Vector<String> passwords = new Vector<>();
                    for (int i = 1; i < messageSplit.length; i = i + 2) {
                        rooms.add("Phòng " + messageSplit[i]);
                        passwords.add(messageSplit[i + 1]);
                    }
//                    Client.roomListFrm.updateRoomList(rooms, passwords);
                }
                if (messageSplit[0].equals("return-friend-list")) {
//                    if (Client.friendListFrm != null) {
//                        Client.friendListFrm.updateFriendList(getListUser(messageSplit));
//                    }
                }
//                if (messageSplit[0].equals("go-to-room")) {
//                    System.out.println("Vào phòng");
//                    int roomID = Integer.parseInt(messageSplit[1]);
//                    String competitorIP = messageSplit[2];
//                    int isStart = Integer.parseInt(messageSplit[3]);
//
//                    User competitor = getUserFromString(4, messageSplit);
//                    if (Client.findRoomFrm != null) {
//                        Client.findRoomFrm.showFoundRoom();
//                        try {
//                            Thread.sleep(3000);
//                        } catch (InterruptedException ex) {
//                            JOptionPane.showMessageDialog(Client.findRoomFrm, "Lỗi khi sleep thread");
//                        }
//                    } else if (Client.waitingRoomFrm != null) {
//                        Client.waitingRoomFrm.showFoundCompetitor();
//                        try {
//                            Thread.sleep(3000);
//                        } catch (InterruptedException ex) {
//                            JOptionPane.showMessageDialog(Client.waitingRoomFrm, "Lỗi khi sleep thread");
//                        }
//                    }
//                    Client.closeAllViews();
//                    System.out.println("Đã vào phòng: " + roomID);
//                    //Xử lý vào phòng
//                    Client.openView(Client.View.GAME_CLIENT
//                            , competitor
//                            , roomID
//                            , isStart
//                            , competitorIP);
//                    Client.gameClientFrm.newgame();
//                }
                //Tạo phòng và server trả về tên phòng
                if (messageSplit[0].equals("your-created-room")) {
//                    Client.closeAllViews();
//                    Client.openView(Client.View.WAITING_ROOM);
//                    Client.waitingRoomFrm.setRoomName(messageSplit[1]);
//                    if (messageSplit.length == 3)
//                        Client.waitingRoomFrm.setRoomPassword("Mật khẩu phòng: " + messageSplit[2]);
                }
                //Xử lý yêu cầu kết bạn tới
                if (messageSplit[0].equals("make-friend-request")) {
                    int ID = Integer.parseInt(messageSplit[1]);
                    String nickname = messageSplit[2];
                    Client.openView(Client.View.FRIEND_REQUEST, ID, nickname);
                }
                //Xử lý khi nhận được yêu cầu thách đấu
//                if (messageSplit[0].equals("duel-notice")) {
//                    int res = JOptionPane.showConfirmDialog(Client.getVisibleJFrame(), "Bạn nhận được lời thách đấu của " + messageSplit[2] + " (ID=" + messageSplit[1] + ")", "Xác nhận thách đấu", JOptionPane.YES_NO_OPTION);
//                    if (res == JOptionPane.YES_OPTION) {
//                        Client.socketHandle.write("agree-duel," + messageSplit[1]);
//                    } else {
//                        Client.socketHandle.write("disagree-duel," + messageSplit[1]);
//                    }
//                }
                //Xử lý không đồng ý thách đấu
                if (messageSplit[0].equals("disagree-duel")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                    JOptionPane.showMessageDialog(Client.homePageFrm, "Đối thủ không đồng ý thách đấu");
                }
                //Xử lý đánh một nước trong ván chơi
                if (messageSplit[0].equals("caro")) {
//                    Client.gameClientFrm.addCompetitorMove(messageSplit[1], messageSplit[2]);
                }
                if (messageSplit[0].equals("chat")) {
//                    Client.gameClientFrm.addMessage(messageSplit[1]);
                }
                if (messageSplit[0].equals("draw-request")) {
//                    Client.gameClientFrm.showDrawRequest();
                }

                if (messageSplit[0].equals("draw-refuse")) {
//                    if (Client.gameNoticeFrm != null) Client.closeView(Client.View.GAME_NOTICE);
//                    Client.gameClientFrm.displayDrawRefuse();
                }

                if (messageSplit[0].equals("new-game")) {
                    System.out.println("New game");
//                    Thread.sleep(4000);
//                    Client.gameClientFrm.updateNumberOfGame();
//                    Client.closeView(Client.View.GAME_NOTICE);
//                    Client.gameClientFrm.newgame();
                }
                if (messageSplit[0].equals("draw-game")) {
                    System.out.println("Draw game");
                    Client.closeView(Client.View.GAME_NOTICE);
                    Client.openView(Client.View.GAME_NOTICE, "Ván chơi hòa", "Ván chơi mới dang được thiết lập");
//                    Client.gameClientFrm.displayDrawGame();
//                    Thread.sleep(4000);
//                    Client.gameClientFrm.updateNumberOfGame();
//                    Client.closeView(Client.View.GAME_NOTICE);
//                    Client.gameClientFrm.newgame();
                }
                if (messageSplit[0].equals("competitor-time-out")) {
//                    Client.gameClientFrm.increaseWinMatchToUser();
//                    Client.openView(Client.View.GAME_NOTICE, "Bạn đã thắng do đối thủ quá thới gian", "Đang thiết laapju ván chơi mới");
//                    Thread.sleep(4000);
//                    Client.closeView(Client.View.GAME_NOTICE);
//                    Client.gameClientFrm.updateNumberOfGame();
//                    Client.gameClientFrm.newgame();
                }
                if (messageSplit[0].equals("voice-message")) {
//                    switch (messageSplit[1]) {
//                        case "close-mic":
//                            Client.gameClientFrm.addVoiceMessage("đã tắt mic");
//                            break;
//                        case "open-mic":
//                            Client.gameClientFrm.addVoiceMessage("đã bật mic");
//                            break;
//                        case "close-speaker":
//                            Client.gameClientFrm.addVoiceMessage("đã tắt âm thanh cuộc trò chuyện");
//                            break;
//                        case "open-speaker":
//                            Client.gameClientFrm.addVoiceMessage("đã bật âm thanh cuộc trò chuyện");
//                            break;
//                    }
                }
//                if (messageSplit[0].equals("left-room")) {
//                    Client.gameClientFrm.stopTimer();
//                    Client.closeAllViews();
//                    Client.openView(Client.View.GAME_NOTICE, "Đối thủ đã thoát khỏi phòng", "Đang trở về trang chủ");
//                    Thread.sleep(3000);
//                    Client.closeAllViews();
//                    Client.openView(Client.View.HOMEPAGE);
//                }
                //Xử lý bị banned
//                if (messageSplit[0].equals("banned-notice")) {
//                    Client.socketHandle.write("offline," + Client.user.getID());
//                    Client.closeAllViews();
//                    Client.openView(Client.View.LOGIN);
//                    JOptionPane.showMessageDialog(Client.loginFrm, messageSplit[1], "Bạn đã bị BAN", JOptionPane.WARNING_MESSAGE);
//                }
//                //Xử lý cảnh cáo
//                if (messageSplit[0].equals("warning-notice")) {
//                    JOptionPane.showMessageDialog(null, messageSplit[1], "Bạn nhận được một cảnh cáo", JOptionPane.WARNING_MESSAGE);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(String message) throws IOException {
        outputWriter.write(message);
        outputWriter.newLine();
        outputWriter.flush();
    }

    public Socket getSocketOfClient() {
        return socketOfClient;
    }

}
