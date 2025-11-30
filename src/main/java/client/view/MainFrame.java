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
    private final JButton logoutButton;
    private final JButton menukioskButton;


    public MainFrame(String userId, String role) {
        super("호텔 관리 시스템 - " + role);
        this.userId = userId;
        this.userRole = role;
        this.setLayout(new BorderLayout());
        Color navy = new Color(10, 48, 87);
        Color lightBg = new Color(245, 248, 252);
        Font btnFont = new Font("맑은 고딕", Font.BOLD, 16);

        // --- 상단 로고/타이틀/환영 메시지 영역 ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel logoLabel = new JLabel("🏨", SwingConstants.LEFT);
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
        logoLabel.setBorder(BorderFactory.createEmptyBorder(18, 24, 0, 0));
        JLabel titleLabel = new JLabel("HMS HOTEL SYSTEM", SwingConstants.LEFT);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titleLabel.setForeground(navy);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(18, 18, 0, 0));
        JPanel logoTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoTitlePanel.setBackground(Color.WHITE);
        logoTitlePanel.add(logoLabel);
        logoTitlePanel.add(Box.createHorizontalStrut(10));
        logoTitlePanel.add(titleLabel);
        topPanel.add(logoTitlePanel, BorderLayout.WEST);
        JLabel subtitle = new JLabel("호텔 통합 관리 시스템", SwingConstants.LEFT);
        subtitle.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        subtitle.setForeground(new Color(80, 80, 80));
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 0));
        topPanel.add(subtitle, BorderLayout.SOUTH);
        
        // 오른쪽 상단에 환영 메시지
        JLabel welcomeLabelTop = new JLabel("환영합니다! 현재 권한: " + role);
        welcomeLabelTop.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        welcomeLabelTop.setForeground(new Color(80, 80, 80));
        welcomeLabelTop.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 24));
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(welcomeLabelTop, BorderLayout.NORTH);
        topPanel.add(rightPanel, BorderLayout.EAST);
        this.add(topPanel, BorderLayout.NORTH);

        // --- 메뉴 버튼 카드 ---
        userManagementButton = new JButton("직원/권한 관리");
        systemReportButton = new JButton("식음료 판매 관리");
        menukioskButton = new JButton("식음료 구매");
        roomTypeManagementButton = new JButton("객실 관리");
        paymentManagementButton = new JButton("결제 관리");
        reservationButton = new JButton("예약 및 조회");
        checkInOutButton = new JButton("보고서");

        JPanel menuCardPanel;
        if (role.equalsIgnoreCase("Customer")) {
            menuCardPanel = new JPanel();
            menuCardPanel.setBackground(lightBg);
            menuCardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(60, 0, 60, 0),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true)));
            menuCardPanel.setLayout(new GridLayout(1, 2, 40, 0)); // 1행 2열, 넓은 간격
            JButton[] customerBtns = {reservationButton, menukioskButton};
            for (JButton btn : customerBtns) {
                btn.setBackground(navy);
                btn.setForeground(Color.WHITE);
                btn.setFont(btnFont.deriveFont(Font.BOLD, 20f));
                btn.setFocusPainted(false);
                btn.setPreferredSize(new Dimension(240, 100));
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    BorderFactory.createEmptyBorder(18, 0, 18, 0)));
                menuCardPanel.add(btn);
            }
        } else if (role.equalsIgnoreCase("CSR")) {
            menuCardPanel = new JPanel();
            menuCardPanel.setBackground(lightBg);
            menuCardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(60, 0, 60, 0),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true)));
            menuCardPanel.setLayout(new GridLayout(2, 2, 40, 32)); // 2행 2열, 넓은 간격
            JButton[] csrBtns = {systemReportButton, menukioskButton, roomTypeManagementButton, reservationButton};
            for (JButton btn : csrBtns) {
                btn.setBackground(navy);
                btn.setForeground(Color.WHITE);
                btn.setFont(btnFont.deriveFont(Font.BOLD, 20f));
                btn.setFocusPainted(false);
                btn.setPreferredSize(new Dimension(220, 90));
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    BorderFactory.createEmptyBorder(16, 0, 16, 0)));
                menuCardPanel.add(btn);
            }
        } else {
            menuCardPanel = new JPanel();
            menuCardPanel.setBackground(lightBg);
            menuCardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(30, 0, 30, 0),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true)));
            menuCardPanel.setLayout(new GridLayout(2, 3, 32, 32)); // 2행 3열, 간격 넓힘
            JButton[] menuButtons = {userManagementButton, systemReportButton, menukioskButton, roomTypeManagementButton, reservationButton, checkInOutButton};
            for (JButton btn : menuButtons) {
                btn.setBackground(navy);
                btn.setForeground(Color.WHITE);
                btn.setFont(btnFont.deriveFont(Font.BOLD, 18f));
                btn.setFocusPainted(false);
                btn.setPreferredSize(new Dimension(200, 80));
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    BorderFactory.createEmptyBorder(12, 0, 12, 0)));
                menuCardPanel.add(btn);
            }
        }

        JPanel menuPanelWrap = new JPanel(new GridBagLayout());
        menuPanelWrap.setBackground(Color.WHITE);
        menuPanelWrap.add(menuCardPanel, new GridBagConstraints());




        JPanel mainWrap = new JPanel(new BorderLayout());
        mainWrap.setBackground(Color.WHITE);
        mainWrap.add(menuPanelWrap, BorderLayout.CENTER);
        this.add(mainWrap, BorderLayout.CENTER);

        applyAuthorization(role);
        roomTypeManagementButton.addActionListener(this::handleRoomAdminClick);
        reservationButton.addActionListener(this::handleRoomManagementClick);
        userManagementButton.addActionListener(this::handleUserManagementClick);
        systemReportButton.addActionListener(this::handleMenuManagementClick);
        menukioskButton.addActionListener(this::handleMenuKioskClick);
        checkInOutButton.addActionListener(e -> {
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("객실 매출 보고서", new RoomSalesReportPanel());
            tabs.addTab("식음료 매출 보고서", new MenuSalesReportPanel());
            tabs.addTab("점유율 보고서", new OccupancyReportPanel());
            JFrame f = new JFrame("보고서");
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setSize(1000,650);
            f.setLocationRelativeTo(this);
            f.add(tabs);
            f.setVisible(true);
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        logoutButton = new JButton("로그아웃");
        logoutButton.setFocusPainted(false);
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(navy);
        logoutButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        logoutButton.setBorder(BorderFactory.createLineBorder(navy, 1));
        logoutButton.setPreferredSize(new Dimension(110, 36));
        logoutButton.addActionListener(this::handleLogout);
        bottomPanel.add(logoutButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        this.setSize(1000, 700);
        this.getContentPane().setBackground(Color.WHITE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
    
    private void applyAuthorization(String role) {
       
        boolean isAdmin = role.equalsIgnoreCase("Manager") || role.equalsIgnoreCase("Admin");
        boolean isCSR = role.equalsIgnoreCase("CSR");
        boolean isCustomer = role.equalsIgnoreCase("Customer");

        // 관리자: 모든 메뉴
        if (isAdmin) {
            userManagementButton.setVisible(true);
            systemReportButton.setVisible(true);
            roomTypeManagementButton.setVisible(true);
            reservationButton.setVisible(true);
            checkInOutButton.setVisible(true);
            menukioskButton.setVisible(true);
        } else if (isCSR) {
                // CSR: 객실관리, 식음료 판매 관리, 식음료 구매, 예약/조회 가능(보고서만 불가)
            userManagementButton.setVisible(false);
            systemReportButton.setVisible(true); // 식음료 판매 관리
            roomTypeManagementButton.setVisible(true); // 객실관리
                reservationButton.setVisible(true); // 예약/조회 가능
            checkInOutButton.setVisible(false); // 보고서 불가
                menukioskButton.setVisible(true); // 식음료 구매 가능
        } else if (isCustomer) {
            // Customer: 예약/조회, 식음료 구매만
            userManagementButton.setVisible(false);
            systemReportButton.setVisible(false);
            roomTypeManagementButton.setVisible(false);
            reservationButton.setVisible(true); // 예약 및 조회
            checkInOutButton.setVisible(false); // 보고서 불가
            menukioskButton.setVisible(true); // 식음료 구매
        } else {
            // 기타: 모두 숨김
            userManagementButton.setVisible(false);
            systemReportButton.setVisible(false);
            roomTypeManagementButton.setVisible(false);
            reservationButton.setVisible(false);
            checkInOutButton.setVisible(false);
            menukioskButton.setVisible(false);
        }
    }
    
    private void handleRoomAdminClick(ActionEvent e) {
        System.out.println("[관리자] 객실관리 화면");
        new RoomAdminFrame().setVisible(true);
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
        new MenuKioskModernFrame(userId).setVisible(true);
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