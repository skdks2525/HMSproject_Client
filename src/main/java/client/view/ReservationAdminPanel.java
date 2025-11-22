package client.view;

import client.net.NetworkService;
import javax.swing.*;
import java.awt.*;

public class ReservationAdminPanel extends JPanel {

    private JTextArea outputArea;
    private JTextField txtRoomNumCreate;
    private JTextField txtGuestNameCreate;
    private JTextField txtCheckInDateCreate;
    private JTextField txtCheckOutDateCreate;
    private JTextField txtResIdCancel;

    public ReservationAdminPanel(JTextArea sharedOutputArea) {
        this.outputArea = sharedOutputArea;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("예약 관리 (CRUD)"));

        add(createReservationCreationPanel());
        add(createReservationCancelPanel());
    }

    private JPanel createReservationCreationPanel() {
        JPanel pCreate = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pCreate.setBorder(BorderFactory.createTitledBorder("예약 생성"));
        txtRoomNumCreate = new JTextField(5);
        txtGuestNameCreate = new JTextField(8);
        txtCheckInDateCreate = new JTextField("YYYY-MM-DD", 8);
        txtCheckOutDateCreate = new JTextField("YYYY-MM-DD", 8);
        
        pCreate.add(new JLabel("방번호:")); pCreate.add(txtRoomNumCreate);
        pCreate.add(new JLabel("이름:")); pCreate.add(txtGuestNameCreate);
        pCreate.add(new JLabel("In:")); pCreate.add(txtCheckInDateCreate);
        pCreate.add(new JLabel("Out:")); pCreate.add(txtCheckOutDateCreate);
        
        JButton btnCreate = new JButton("예약하기");
        btnCreate.addActionListener(e -> requestCreateReservation());
        pCreate.add(btnCreate);
        return pCreate;
    }

    private JPanel createReservationCancelPanel() {
        JPanel pCancel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pCancel.setBorder(BorderFactory.createTitledBorder("예약 취소"));
        txtResIdCancel = new JTextField(10);
        pCancel.add(new JLabel("취소할 예약 ID:"));
        pCancel.add(txtResIdCancel);
        JButton btnCancel = new JButton("취소하기");
        btnCancel.addActionListener(e -> requestCancelReservation());
        pCancel.add(btnCancel);
        return pCancel;
    }

    private void requestCreateReservation() {
        String rNum = txtRoomNumCreate.getText().trim();
        String name = txtGuestNameCreate.getText().trim();
        String in = txtCheckInDateCreate.getText().trim();
        String out = txtCheckOutDateCreate.getText().trim();

        if (rNum.isEmpty() || name.isEmpty()) { showMsg("필수 정보를 입력하세요."); return; }

        String request = String.format("ADD_RESERVATION:%s:%s:%s:%s", rNum, name, in, out);
        displayText("[요청] 예약 생성 중...");
        
        String response = NetworkService.getInstance().sendRequest(request);
        if ("RESERVE_SUCCESS".equals(response)) {
            displayText("✅ 예약 성공!");
            txtRoomNumCreate.setText(""); txtGuestNameCreate.setText("");
        } else {
            displayText("❌ 예약 실패: " + response);
        }
    }

    private void requestCancelReservation() {
        String resId = txtResIdCancel.getText().trim();
        if (resId.isEmpty()) { showMsg("예약 ID를 입력하세요."); return; }

        String response = NetworkService.getInstance().sendRequest("DELETE_RESERVATION:" + resId);
        
        if ("DELETE_SUCCESS".equals(response)) {
            displayText("✅ 예약 취소 성공: " + resId);
            txtResIdCancel.setText("");
        } else {
            displayText("❌ 취소 실패: " + response);
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