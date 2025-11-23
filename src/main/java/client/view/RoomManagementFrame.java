package client.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RoomManagementFrame extends JFrame {

    private JTextArea outputArea;
    private String userName;
    
    public RoomManagementFrame(String userName) {
        this.userName = userName;
        setTitle("호텔 객실 및 예약 관리 시스템");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // 출력 영역
        outputArea = new JTextArea(20, 80);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("조회 결과"));

        JTabbedPane tabbedPane = new JTabbedPane(); // 탭패널
       
        tabbedPane.addTab("객실 예약", new ReservationPanel(userName));
        tabbedPane.addTab("내 예약 확인", new MyReservationPanel(userName));
        
        
        add(tabbedPane, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
}