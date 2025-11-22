package client.view;

import client.net.NetworkService;
import javax.swing.*;
import java.awt.*;

public class RoomStatusPanel extends JPanel {

    private JTextArea outputArea;
    private JTextField txtRoomNumQuery;
    private JTextField txtGuestNameQuery;
    private JTextField txtCheckInDateQuery;
    private JTextField txtCheckOutDateQuery;

    public RoomStatusPanel(JTextArea sharedOutputArea) {
        this.outputArea = sharedOutputArea;
        setLayout(new GridLayout(4, 3, 5, 5));
        setBorder(BorderFactory.createTitledBorder("객실 및 예약 조회"));

        initComponents();
    }

    private void initComponents() {
        // 전체 객실 조회
        JButton btnGetAllRooms = new JButton("전체 객실 조회");
        btnGetAllRooms.addActionListener(e -> requestGetAllRooms());
        add(btnGetAllRooms);
        add(new JLabel("")); add(new JLabel(""));

        // 객실 번호 조회
        add(new JLabel("객실 번호:"));
        txtRoomNumQuery = new JTextField(10);
        add(txtRoomNumQuery);
        JButton btnGetRoomByNum = new JButton("객실 조회");
        btnGetRoomByNum.addActionListener(e -> requestGetRoomByNum());
        add(btnGetRoomByNum);

        // 예약자 이름 조회
        add(new JLabel("예약자명:"));
        txtGuestNameQuery = new JTextField(10);
        add(txtGuestNameQuery);
        JButton btnGetResByName = new JButton("예약 조회");
        btnGetResByName.addActionListener(e -> requestGetReservationByName());
        add(btnGetResByName);

        // 날짜별 가능 객실 조회
        add(new JLabel("기간 (YYYY-MM-DD):"));
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        txtCheckInDateQuery = new JTextField("2025-01-01", 8);
        txtCheckOutDateQuery = new JTextField("2025-01-02", 8);
        datePanel.add(txtCheckInDateQuery);
        datePanel.add(new JLabel("~"));
        datePanel.add(txtCheckOutDateQuery);
        add(datePanel);
        
        JButton btnGetAvailable = new JButton("가능 객실 조회");
        btnGetAvailable.addActionListener(e -> requestGetAvailableRooms());
        add(btnGetAvailable);
    }

    private void requestGetAllRooms() {
        displayText("[요청] 전체 객실 목록 조회 중...");
        String response = NetworkService.getInstance().sendRequest("GET_ALL_ROOMS");
        
        if (response != null && response.startsWith("ROOM_LIST:")) {
            displayRoomList(response.substring("ROOM_LIST:".length()));
        } else {
            displayText("[오류] " + response);
        }
    }

    private void requestGetRoomByNum() {
        String roomNum = txtRoomNumQuery.getText().trim();
        if (roomNum.isEmpty()) { showMsg("객실 번호를 입력하세요."); return; }

        displayText("[요청] 객실 정보 조회: " + roomNum);
        String response = NetworkService.getInstance().sendRequest("GET_ROOM:" + roomNum);
        
        if (response != null && response.startsWith("ROOM_INFO:")) {
            displayText(response.substring("ROOM_INFO:".length()));
        } else {
            displayText("[오류] " + response);
        }
    }

    private void requestGetReservationByName() {
        String name = txtGuestNameQuery.getText().trim();
        if (name.isEmpty()) { showMsg("이름을 입력하세요."); return; }

        displayText("[요청] 예약 조회 (이름: " + name + ")");
        String response = NetworkService.getInstance().sendRequest("GET_RES_BY_NAME:" + name);
        
        if (response != null && response.startsWith("RES_LIST:")) {
            displayReservationList(response.substring("RES_LIST:".length()));
        } else {
            displayText("[결과] 예약 내역이 없거나 오류입니다: " + response);
        }
    }

    private void requestGetAvailableRooms() {
        String in = txtCheckInDateQuery.getText().trim();
        String out = txtCheckOutDateQuery.getText().trim();
        
        displayText("[요청] 가능 객실 조회 (" + in + " ~ " + out + ")");
        String request = String.format("GET_AVAILABLE_ROOMS:%s:%s", in, out);
        String response = NetworkService.getInstance().sendRequest(request);
        
        if (response != null && response.startsWith("ROOM_LIST:")) {
            displayRoomList(response.substring("ROOM_LIST:".length()));
        } else {
            displayText("[오류] " + response);
        }
    }

    private void displayRoomList(String data) {
        if (data.isEmpty()) return;
        String[] rooms = data.split("/");
        for (String r : rooms) {
            String[] info = r.split(",");
            if (info.length >= 3) {
                displayText(String.format("방번호:%s, 타입:%s, 가격:%s원", info[0], info[1], info[2]));
            }
        }
    }

    private void displayReservationList(String data) {
        if (data.isEmpty()) return;
        String[] resList = data.split("/");
        for (String res : resList) {
            String[] info = res.split(",");
            if (info.length >= 4) {
                displayText(String.format("ID:%s, 방:%s, 이름:%s, 날짜:%s~%s", info[0], info[1], info[2], info[3], info[4]));
            }
        }
    }

    private void displayText(String msg) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(msg + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    private void showMsg(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}