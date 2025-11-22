package client.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RoomManagementFrame extends JFrame {

    private JTextArea outputArea;

    public RoomManagementFrame() {
        setTitle("호텔 객실 및 예약 관리 시스템");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        initComponents();
        
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // 출력 영역
        outputArea = new JTextArea(20, 80);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("실행 결과 및 조회 목록"));

        JTabbedPane tabbedPane = new JTabbedPane();
       
        tabbedPane.addTab("객실 및 예약 조회", new RoomStatusPanel(outputArea));
        tabbedPane.addTab("예약 관리 (생성/취소)", new ReservationAdminPanel(outputArea));
        
        add(tabbedPane, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
}