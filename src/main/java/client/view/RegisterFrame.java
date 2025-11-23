package client.view;

import client.net.NetworkService;
import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    public RegisterFrame() {
        initComponents();
        this.setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel bgPanel = new JPanel(null);
        bgPanel.setBackground(Color.WHITE);
        bgPanel.setPreferredSize(new Dimension(800, 500));

        JPanel sidePanel = new JPanel(new GridBagLayout());
        sidePanel.setBackground(new Color(204, 255, 204));
        sidePanel.setBounds(0, 0, 400, 500);

        JLabel lblLogo = new JLabel("JOIN US");
        lblLogo.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        lblLogo.setForeground(new Color(0, 102, 51));
        sidePanel.add(lblLogo);
        bgPanel.add(sidePanel);

        JPanel formPanel = new JPanel(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBounds(400, 0, 400, 500);

        JLabel lblTitle = new JLabel("SIGN UP");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 36));
        lblTitle.setForeground(new Color(0, 102, 51));
        lblTitle.setBounds(130, 40, 150, 50);
        formPanel.add(lblTitle);

        // ID 입력
        JLabel lblId = new JLabel("ID (Email)");
        lblId.setBounds(40, 110, 100, 20);
        formPanel.add(lblId);
        JTextField txtId = new JTextField();
        txtId.setBounds(40, 130, 320, 35);
        formPanel.add(txtId);

        // 이름 입력
        JLabel lblName = new JLabel("Name");
        lblName.setBounds(40, 180, 100, 20);
        formPanel.add(lblName);
        JTextField txtName = new JTextField();
        txtName.setBounds(40, 200, 320, 35);
        formPanel.add(txtName);

        // PW 입력
        JLabel lblPw = new JLabel("Password");
        lblPw.setBounds(40, 250, 100, 20);
        formPanel.add(lblPw);
        JPasswordField txtPw = new JPasswordField();
        txtPw.setBounds(40, 270, 320, 35);
        formPanel.add(txtPw);

        // PW 확인
        JLabel lblPwConfirm = new JLabel("Confirm Password");
        lblPwConfirm.setBounds(40, 320, 150, 20);
        formPanel.add(lblPwConfirm);
        JPasswordField txtPwConfirm = new JPasswordField();
        txtPwConfirm.setBounds(40, 340, 320, 35);
        formPanel.add(txtPwConfirm);

        // 전화번호 입력
        JLabel lblPhone = new JLabel("Phone");
        lblPhone.setBounds(40, 390, 100, 20);
        formPanel.add(lblPhone);
        JTextField txtPhone = new JTextField();
        txtPhone.setBounds(40, 410, 320, 35);
        formPanel.add(txtPhone);

        // 가입 버튼
        JButton btnRegister = new JButton("SIGN UP");
        btnRegister.setBackground(new Color(204, 255, 204));
        btnRegister.setForeground(new Color(0, 102, 51));
        btnRegister.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btnRegister.setBounds(40, 460, 120, 40);
        formPanel.add(btnRegister);

        // 돌아가기(로그인) 라벨/버튼
        JLabel lblHaveAccount = new JLabel("I have an account");
        lblHaveAccount.setBounds(40, 520, 150, 30);
        formPanel.add(lblHaveAccount);

        JButton btnBack = new JButton("Login");
        btnBack.setForeground(new Color(255, 51, 51));
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setBounds(180, 520, 80, 30);
        formPanel.add(btnBack);

        bgPanel.add(formPanel);

        setContentPane(bgPanel);
        setTitle("Sign Up");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();

        // --- 이벤트 핸들러 ---

        // [가입하기 버튼]
        btnRegister.addActionListener(e -> {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String pw = new String(txtPw.getPassword()).trim();
            String pw2 = new String(txtPwConfirm.getPassword()).trim();
            String phone = txtPhone.getText().trim();

            // 1. 유효성 검사
            if(id.isEmpty() || name.isEmpty() || pw.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 정보를 입력해주세요.");
                return;
            }
            if(!pw.equals(pw2)) {
                JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.");
                return;
            }

            // 2. 서버 전송 (기본 권한: Customer)
            // 프로토콜: "ADD_USER:아이디:이름:비번:권한:전화번호"
            String request = String.format("ADD_USER:%s:%s:%s:%s:%s", id, name, pw, "Customer", phone);
            String response = NetworkService.getInstance().sendRequest(request);

            if("ADD_SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "회원가입 성공! 로그인해주세요.");
                new LoginFrame().setVisible(true); // 로그인 창으로 이동
                this.dispose();
            } else {
                String msg = response.contains(":") ? response.split(":")[1] : "가입 실패";
                JOptionPane.showMessageDialog(this, msg);
            }
        });

        // [로그인으로 돌아가기 버튼]
        btnBack.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
    }
}