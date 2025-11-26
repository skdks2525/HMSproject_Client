/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.view;
import client.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 *
 * @author user
 */
public class MainFrame extends JFrame {
    
    private final String userRole; //Manager.CSR,CUSTOMER
    private final String userId;
    
    // 관리자 전용 메뉴
    private final JButton userManagementButton;
    private final JButton systemReportButton;
    private final JButton roomTypeManagementButton;
    private final JButton paymentManagementButton;

    // CSR/고객 공통 메뉴
    private final JButton reservationButton;
    private final JButton checkInOutButton;
    private final JPanel menuPanel;
    private final JButton logoutButton;
    private final JButton menukioskButton;


    public MainFrame(String userId, String role) {
        super("호텔 관리 시스템 - " + role); // 창 제목에 권한 표시
        this.userId = userId;
        this.userRole = role;
        this.setLayout(new BorderLayout());
        userManagementButton = new JButton("직원/권한 관리");
        systemReportButton = new JButton("식음료 판매 관리");
        menukioskButton = new JButton("식음료 구매");
        roomTypeManagementButton = new JButton("객실 관리");
        paymentManagementButton = new JButton("결제 관리");
        reservationButton = new JButton("예약 및 조회");
        checkInOutButton = new JButton("보고서");

        menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        
        menuPanel.add(userManagementButton);
        menuPanel.add(systemReportButton);
        menuPanel.add(menukioskButton);
        menuPanel.add(roomTypeManagementButton);
        menuPanel.add(reservationButton);
        menuPanel.add(checkInOutButton);
        
        this.add(menuPanel, BorderLayout.NORTH); 
        
        JLabel welcomeLabel = new JLabel("환영합니다! 현재 권한: " + role, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        this.add(welcomeLabel, BorderLayout.CENTER);

        applyAuthorization(role);
        reservationButton.addActionListener(this::handleRoomManagementClick); //예약 조회 클릭        
        userManagementButton.addActionListener(this::handleUserManagementClick); //직원 관리 클릭
        systemReportButton.addActionListener(this::handleMenuManagementClick); //식음료 관리 관리 클릭
        menukioskButton.addActionListener(this::handleMenuKioskClick); //식음료 관리 관리 클릭
        // '보고서' 버튼 클릭 시 리포트 창 오픈 (객실 매출 기본 탭)
        checkInOutButton.addActionListener(e -> {
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("객실 매출 보고서", new RoomSalesReportPanel());
            JFrame f = new JFrame("보고서");
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setSize(900,600);
            f.setLocationRelativeTo(this);
            f.add(tabs);
            f.setVisible(true);
        });
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        logoutButton = new JButton("로그아웃");
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(this::handleLogout);
        bottomPanel.add(logoutButton);
        this.add(bottomPanel, BorderLayout.SOUTH);
        
        this.setSize(900, 600);
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
        System.out.println("[클라이언트] 직원 관리 화면");
        
        new UserManagementFrame().setVisible(true);
    }
    
    private void handleRoomManagementClick(ActionEvent e) {
        System.out.println("예약 조회 화면");
        new RoomManagementFrame(userId).setVisible(true);
    }
    
    private void handleMenuManagementClick(ActionEvent e) {
        System.out.println("[클라이언트] 식음료 관리 화면");
        
        new MenuManagementFrame().setVisible(true);
    }
    
    private void handleMenuKioskClick(ActionEvent e) {
        System.out.println("[클라이언트] 식음료 키오스크 화면");
        
        new MenuKioskModernFrame().setVisible(true);
    }
    
    private void handleLogout(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "정말 로그아웃 하시겠습니까?", "로그아웃 확인", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("[Client] 로그아웃 합니다.");
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }
}