/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.view;
import client.net.NetworkService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author user
 */
public class MainFrame extends JFrame {
    
    private final String userRole; //Manager.CSR,CUSTOMER

    // 관리자 전용 메뉴
    private final JButton userManagementButton;
    private final JButton systemReportButton;
    private final JButton roomTypeManagementButton;

    // CSR/고객 공통 메뉴
    private final JButton reservationButton;
    private final JButton checkInOutButton;
    
    private final JPanel menuPanel;


    public MainFrame(String role) {
        super("호텔 관리 시스템 - " + role); // 창 제목에 권한 표시
        this.userRole = role;
        
        userManagementButton = new JButton("직원/권한 관리");
        systemReportButton = new JButton("시스템 보고서");
        roomTypeManagementButton = new JButton("객실 유형 관리");
        
        reservationButton = new JButton("예약 및 조회");
        checkInOutButton = new JButton("체크인/아웃");

        menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        
        menuPanel.add(userManagementButton);
        menuPanel.add(systemReportButton);
        menuPanel.add(roomTypeManagementButton);
        menuPanel.add(reservationButton);
        menuPanel.add(checkInOutButton);
        
        this.add(menuPanel, BorderLayout.NORTH); 
        
        JLabel welcomeLabel = new JLabel("환영합니다! 현재 권한: " + role, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        this.add(welcomeLabel, BorderLayout.CENTER);

        applyAuthorization(role);
        
        userManagementButton.addActionListener(this::handleUserManagementClick);
        reservationButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "예약 기능 준비 중"));

        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); 
    }
    
    private void applyAuthorization(String role) {
       
        boolean isAdmin = role.equalsIgnoreCase("Manager") || role.equalsIgnoreCase("Admin");  // 관리자 여부 확인
        
        userManagementButton.setVisible(isAdmin);
        systemReportButton.setVisible(isAdmin);
        roomTypeManagementButton.setVisible(isAdmin);

        boolean isStaff = isAdmin || role.equalsIgnoreCase("CSR"); // 직원 여부 확인
        
        reservationButton.setVisible(isStaff);
        checkInOutButton.setVisible(isStaff);    
    }
    
    private void handleUserManagementClick(ActionEvent e) {
        System.out.println("[클라이언트] 직원 관리 화면 열기 시도");
        
        // 1. 서버에 데이터 요청이 필요하다면 여기서 수행
        // String response = NetworkService.getInstance().sendRequest("GET_ALL_USERS");
        
        // 2. 새로운 화면(UserManagementFrame) 열기
        // 주의: UserManagementView(또는 Frame)도 클라이언트용으로 새로 만들어야 함
        JOptionPane.showMessageDialog(this, "직원 관리 화면은 아직 구현되지 않았습니다.");
        
        // 예시: 구현 후 주석 해제
        // new UserManagementFrame().setVisible(true);
    }
}