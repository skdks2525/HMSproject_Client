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
        JPanel bgPanel = new JPanel(new GridBagLayout());
        bgPanel.setBackground(Color.WHITE);
        bgPanel.setPreferredSize(new Dimension(400, 540));

        JPanel formPanel = new JPanel(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(350, 500));
        formPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));

        JLabel lblTitle = new JLabel("SIGN UP");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        lblTitle.setForeground(new Color(10, 48, 87));
        lblTitle.setBounds(110, 20, 200, 40);
        formPanel.add(lblTitle);

        int y = 70;
        JLabel lblRole = new JLabel("Role");
        lblRole.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        lblRole.setBounds(40, y, 100, 20);
        formPanel.add(lblRole);
        String[] roles = {"Customer", "CSR", "Manager"};
        JComboBox<String> cmbRole = new JComboBox<>(roles);
        cmbRole.setBounds(140, y - 3, 170, 28);
        formPanel.add(cmbRole);

        y += 40;
        JLabel lblId = new JLabel("ID (Email)");
        lblId.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        lblId.setBounds(40, y, 100, 20);
        formPanel.add(lblId);
        JTextField txtId = new JTextField();
        txtId.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        txtId.setBounds(40, y + 20, 270, 32);
        txtId.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(txtId);

        y += 60;
        JLabel lblName = new JLabel("Name");
        lblName.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        lblName.setBounds(40, y, 100, 20);
        formPanel.add(lblName);
        JTextField txtName = new JTextField();
        txtName.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        txtName.setBounds(40, y + 20, 270, 32);
        txtName.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(txtName);

        y += 60;
        JLabel lblPw = new JLabel("Password");
        lblPw.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        lblPw.setBounds(40, y, 100, 20);
        formPanel.add(lblPw);
        JPasswordField txtPw = new JPasswordField();
        txtPw.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        txtPw.setBounds(40, y + 20, 270, 32);
        txtPw.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(txtPw);

        y += 60;
        JLabel lblPwConfirm = new JLabel("Confirm Password");
        lblPwConfirm.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        lblPwConfirm.setBounds(40, y, 150, 20);
        formPanel.add(lblPwConfirm);
        JPasswordField txtPwConfirm = new JPasswordField();
        txtPwConfirm.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        txtPwConfirm.setBounds(40, y + 20, 270, 32);
        txtPwConfirm.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(txtPwConfirm);

        y += 60;
        JLabel lblPhone = new JLabel("Phone");
        lblPhone.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        lblPhone.setBounds(40, y, 100, 20);
        formPanel.add(lblPhone);
        JTextField txtPhone = new JTextField();
        txtPhone.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        txtPhone.setBounds(40, y + 20, 270, 32);
        txtPhone.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        formPanel.add(txtPhone);

        JButton btnRegister = new JButton("SIGN UP");
        btnRegister.setBackground(new Color(10, 48, 87));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        btnRegister.setFocusPainted(false);
        btnRegister.setBounds(40, 420, 270, 40);
        formPanel.add(btnRegister);

        JButton btnBack = new JButton("← 로그인 화면으로");
        btnBack.setBackground(Color.WHITE);
        btnBack.setForeground(new Color(10, 48, 87));
        btnBack.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setFocusPainted(false);
        btnBack.setBounds(40, 470, 200, 30);
        formPanel.add(btnBack);

        cmbRole.addActionListener(e -> {
            String selected = (String) cmbRole.getSelectedItem();
            if ("CSR".equals(selected) || "Manager".equals(selected)) {
                String code = JOptionPane.showInputDialog(this, "인증코드를 입력하세요", "인증코드", JOptionPane.PLAIN_MESSAGE);
                if (code == null || !"0000".equals(code.trim())) {
                    JOptionPane.showMessageDialog(this, "인증코드가 올바르지 않습니다. 역할이 Customer로 변경됩니다.");
                    cmbRole.setSelectedIndex(0);
                }
            }
        });

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
            String role = (String) cmbRole.getSelectedItem();

            // 1. 유효성 검사
            if(id.isEmpty() || name.isEmpty() || pw.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 정보를 입력해주세요.");
                return;
            }
            if(!pw.equals(pw2)) {
                JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.");
                return;
            }

            // 2. 서버 전송 (선택한 권한)
            String request = String.format("ADD_USER:%s:%s:%s:%s:%s", id, name, pw, role, phone);
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