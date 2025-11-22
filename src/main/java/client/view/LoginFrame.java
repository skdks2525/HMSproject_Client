package client.view;

import client.net.NetworkService;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        initComponents();
        this.setLocationRelativeTo(null); // 화면 중앙 배치
    }

    private void initComponents() {

        JPanel bgPanel = new JPanel();
        bgPanel.setLayout(null);
        bgPanel.setBackground(new Color(255, 255, 255));
        bgPanel.setPreferredSize(new Dimension(800, 500));

        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(new Color(204, 255, 204)); // 연한 초록색
        sidePanel.setBounds(0, 0, 400, 500);
        sidePanel.setLayout(new GridBagLayout()); // 중앙 정렬용

        JLabel lblLogo = new JLabel("HOTEL SYSTEM");
        lblLogo.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        lblLogo.setForeground(new Color(0, 102, 51));
        // 아이콘이 있다면 아래 주석 해제
        // lblLogo.setIcon(new ImageIcon(getClass().getResource("/images/logo.png")));
        sidePanel.add(lblLogo);
        
        bgPanel.add(sidePanel);

        // --- [오른쪽 패널] 입력 폼 영역 (기존 Left 패널) ---
        JPanel formPanel = new JPanel();
        formPanel.setBackground(new Color(255, 255, 255));
        formPanel.setBounds(400, 0, 400, 500);
        formPanel.setLayout(null);

        JLabel lblTitle = new JLabel("LOGIN");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 36));
        lblTitle.setForeground(new Color(0, 102, 51));
        lblTitle.setBounds(140, 50, 150, 50);
        formPanel.add(lblTitle);

        JLabel lblEmail = new JLabel("ID (Email)");
        lblEmail.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        lblEmail.setBounds(40, 130, 100, 20);
        formPanel.add(lblEmail);

        JTextField txtEmail = new JTextField();
        txtEmail.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        txtEmail.setBounds(40, 150, 320, 40);
        formPanel.add(txtEmail);

        JLabel lblPw = new JLabel("Password");
        lblPw.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        lblPw.setBounds(40, 210, 100, 20);
        formPanel.add(lblPw);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        txtPass.setBounds(40, 230, 320, 40);
        formPanel.add(txtPass);

        // 로그인 버튼
        JButton btnLogin = new JButton("LOGIN");
        btnLogin.setBackground(new Color(204, 255, 204));
        btnLogin.setForeground(new Color(0, 102, 51));
        btnLogin.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btnLogin.setBounds(40, 300, 100, 40);
        formPanel.add(btnLogin);

        JLabel lblNoAccount = new JLabel("I don't have an account");
        lblNoAccount.setBounds(40, 360, 150, 30);
        formPanel.add(lblNoAccount);

        // 회원가입 이동 버튼
        JButton btnSignUp = new JButton("Sign Up");
        btnSignUp.setForeground(new Color(255, 51, 51));
        btnSignUp.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        btnSignUp.setBorderPainted(false);
        btnSignUp.setContentAreaFilled(false);
        btnSignUp.setBounds(180, 360, 80, 30);
        formPanel.add(btnSignUp);

        bgPanel.add(formPanel);

        // 프레임 설정
        setContentPane(bgPanel);
        setTitle("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();

        // --- 이벤트 리스너 ---
        
        // 1. 로그인 버튼 동작
        btnLogin.addActionListener(e -> {
            String id = txtEmail.getText().trim();
            String pw = new String(txtPass.getPassword()).trim();

            if(id.isEmpty() || pw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 입력하세요.");
                return;
            }

            // 서버 통신
            String response = NetworkService.getInstance().sendRequest("LOGIN:" + id + ":" + pw);

            if (response != null && response.startsWith("LOGIN_SUCCESS")) {
                String[] parts = response.split(":");
                String role = (parts.length > 1) ? parts[1] : "Unknown";
                
                JOptionPane.showMessageDialog(this, "로그인 성공! (" + role + ")");
                new MainFrame(role).setVisible(true); // 메인 화면 이동
                this.dispose();
            } else {
                String msg = (response != null && response.contains(":")) ? response.split(":")[1] : "로그인 실패";
                JOptionPane.showMessageDialog(this, msg, "에러", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 2. 회원가입 버튼 동작 -> RegisterFrame 열기
        btnSignUp.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            this.dispose(); // 로그인 창 닫기
        });
    }
}