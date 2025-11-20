/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cse.oop2.hms_project_client.view;
import cse.oop2.hms_project_client.net.NetworkService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author user
 */
public class LoginFrame extends JFrame {

    // ⭐️ UI 컴포넌트 ⭐️ (기존 코드 유지)
    private final JTextField userIdField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    /**
     * 생성자
     * [변경] 파라미터로 UserService를 받지 않습니다. 
     * 클라이언트는 데이터베이스에 직접 접근하지 않기 때문입니다.
     */
    public LoginFrame() {
        super("호텔 관리 시스템 - 로그인");

        // 1. 컴포넌트 초기화
        this.userIdField = new JTextField(15);
        this.passwordField = new JPasswordField(15);
        this.loginButton = new JButton("로그인");

        // 2. UI 디자인 및 레이아웃 구성 (작성하신 GridBagLayout 유지)
        initializeUI();

        // 3. 이벤트 리스너 연결
        setupEventListeners();
        
        // 4. 창 설정
        this.setSize(350, 250); // 높이를 조금 늘렸습니다.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // 화면 중앙 표시
        
        // [추가] 생성 즉시 화면에 보이게 설정
        this.setVisible(true);
    }

    private void initializeUI() {
        // 메인 패널 생성 (기존 코드 유지)
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // --- User ID ---
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ID:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(userIdField, gbc);

        // --- Password ---
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("PW:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(passwordField, gbc);

        // --- Login Button ---
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(loginButton, gbc);

        this.add(panel);
    }
    
    private void setupEventListeners() {
        // 람다식으로 로그인 시도 메서드 연결
        loginButton.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin());
    }

    /**
     * [핵심 변경] 로그인 시도 로직
     * 기존: LoginController -> UserService (로컬 처리)
     * 변경: NetworkService -> Server (네트워크 요청)
     */
    private void performLogin() {
        String id = userIdField.getText().trim();
        String pw = new String(passwordField.getPassword()).trim();
        
        // 1. 유효성 검사
        if (id.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 입력하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. 서버로 보낼 메시지 생성 (프로토콜: "LOGIN:아이디:비번")
        String request = "LOGIN:" + id + ":" + pw;

        try {
            // 3. 서버에 전송하고 응답 대기
            String response = NetworkService.getInstance().sendRequest(request);

            // 4. 응답 처리
            if (response != null && response.startsWith("LOGIN_SUCCESS")) {
                // --- 로그인 성공 ---
                
                // 서버 응답 파싱 (예: "LOGIN_SUCCESS:Manager")
                String[] parts = response.split(":");
                String role = (parts.length > 1) ? parts[1] : "Unknown";

                JOptionPane.showMessageDialog(this, "로그인 성공! (" + role + ")");

                // [화면 전환] 메인 프레임 열기
                //new MainFrame(role).setVisible(true);

                // [화면 전환] 현재 로그인 창 닫기
                this.dispose();

            } else {
                // --- 로그인 실패 ---
                String msg = "로그인 실패";
                if (response != null && response.contains(":")) {
                    msg = response.split(":")[1]; // 에러 메시지 추출
                } else if (response == null) {
                    msg = "서버 연결 실패";
                }
                JOptionPane.showMessageDialog(this, msg, "에러", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "시스템 오류: " + ex.getMessage());
        }
    }
}