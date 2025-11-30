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
        bgPanel.setLayout(new GridBagLayout());
        bgPanel.setBackground(Color.WHITE);
        bgPanel.setPreferredSize(new Dimension(400, 420));

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(350, 370));
        formPanel.setLayout(null);
        formPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));

        JLabel lblTitle = new JLabel("LOGIN");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 36));
        lblTitle.setForeground(new Color(10, 48, 87)); // 딥블루
        lblTitle.setBounds(110, 30, 200, 40);
        formPanel.add(lblTitle);

        JLabel lblEmail = new JLabel("ID (Email)");
        lblEmail.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        lblEmail.setBounds(40, 90, 120, 20);
        formPanel.add(lblEmail);

        JTextField txtEmail = new JTextField();
        txtEmail.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        txtEmail.setBounds(40, 110, 270, 35);
        txtEmail.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(txtEmail);

        JLabel lblPw = new JLabel("Password");
        lblPw.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        lblPw.setBounds(40, 155, 120, 20);
        formPanel.add(lblPw);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        txtPass.setBounds(40, 175, 270, 35);
        txtPass.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(txtPass);

        JButton btnLogin = new JButton("LOGIN");
        btnLogin.setBackground(new Color(10, 48, 87));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        btnLogin.setFocusPainted(false);
        btnLogin.setBounds(40, 230, 270, 40);
        formPanel.add(btnLogin);

        JButton btnSignUp = new JButton("Sign Up");
        btnSignUp.setForeground(new Color(10, 48, 87));
        btnSignUp.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        btnSignUp.setBorderPainted(false);
        btnSignUp.setContentAreaFilled(false);
        btnSignUp.setFocusPainted(false);
        btnSignUp.setBounds(180, 280, 120, 40);
        formPanel.add(btnSignUp);

        bgPanel.add(formPanel);

        setContentPane(bgPanel);
        setTitle("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();

        // --- 이벤트 리스너 ---
        btnLogin.addActionListener(e -> {
            String id = txtEmail.getText().trim();
            String pw = new String(txtPass.getPassword()).trim();
            if(id.isEmpty() || pw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 입력하세요.");
                return;
            }
            String response = NetworkService.getInstance().sendRequest("LOGIN:" + id + ":" + pw);
            if (response != null && response.startsWith("LOGIN_SUCCESS")) {
                String[] parts = response.split(":");
                String role = (parts.length > 1) ? parts[1] : "Unknown";
                JOptionPane.showMessageDialog(this, id + "님 환영합니다");
                new MainFrame(id, role).setVisible(true);
                this.dispose();
            } else {
                String msg = (response != null && response.contains(":")) ? response.split(":")[1] : "로그인 실패";
                JOptionPane.showMessageDialog(this, msg, "에러", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnSignUp.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            this.dispose();
        });
    }
}