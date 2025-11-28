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
        JPanel bgPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 좌측 전체 연두색 배경
                g.setColor(new Color(204, 255, 204));
                g.fillRect(0, 0, 400, getHeight());
            }
        };
        bgPanel.setBackground(Color.WHITE);
        bgPanel.setPreferredSize(new Dimension(800, 650));

        JPanel sidePanel = new JPanel(new GridBagLayout());
        sidePanel.setOpaque(false); // 배경색 투명 처리 (bgPanel에서 그림)
        sidePanel.setBounds(0, 0, 400, 650);

        JLabel lblLogo = new JLabel("JOIN US");
        lblLogo.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        lblLogo.setForeground(new Color(0, 102, 51));
        sidePanel.add(lblLogo);
        bgPanel.add(sidePanel);

        JPanel formPanel = new JPanel(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBounds(400, 0, 400, 650);

        JLabel lblTitle = new JLabel("SIGN UP");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 36));
        lblTitle.setForeground(new Color(0, 102, 51));
        lblTitle.setBounds(120, 10, 180, 50);
        formPanel.add(lblTitle);

        // SIGN UP과 ROLE 사이 간격 확보
        int roleY = 70 + 20; // 기존 70에서 20px 더 띄움

        // 역할 선택 (맨 위)
        JLabel lblRole = new JLabel("Role");
        lblRole.setBounds(40, roleY, 100, 20);
        formPanel.add(lblRole);
        String[] roles = {"Customer", "CSR", "Manager"};
        JComboBox<String> cmbRole = new JComboBox<>(roles);
        cmbRole.setBounds(140, roleY - 5, 220, 30);
        formPanel.add(cmbRole);

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
        btnRegister.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        btnRegister.setBounds(40, 510, 320, 45); // 넓게, 아래로
        formPanel.add(btnRegister);

        // 로그인으로 돌아가기 버튼 (SIGN UP 버튼과 동일한 연두색)
        JButton btnBack = new JButton("← 로그인 화면으로");
        btnBack.setBackground(new Color(204, 255, 204));
        btnBack.setForeground(new Color(0, 102, 51));
        btnBack.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        btnBack.setBounds(40, 570, 320, 40);
        btnBack.setFocusPainted(false);
        formPanel.add(btnBack);
        // 역할 선택 시 CSR/Manager면 즉시 인증코드 요구
        cmbRole.addActionListener(e -> {
            String selected = (String) cmbRole.getSelectedItem();
            if ("CSR".equals(selected) || "Manager".equals(selected)) {
                String code = JOptionPane.showInputDialog(this, "인증코드를 입력하세요", "인증코드", JOptionPane.PLAIN_MESSAGE);
                if (code == null || !"0000".equals(code.trim())) {
                    JOptionPane.showMessageDialog(this, "인증코드가 올바르지 않습니다. 역할이 Customer로 변경됩니다.");
                    cmbRole.setSelectedIndex(0); // Customer로 롤백
                }
            }
        });

        // 기존 돌아가기 라벨/버튼 제거 (위에서 btnBack으로 대체)

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