/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.view;
import client.net.NetworkService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** 직원 및 권한 관리 화면
 *
 * @author user
 */

public class UserManagementFrame extends JFrame {

    // UI 컴포넌트
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextArea outputArea; // 로그 출력용

    // 입력 필드
    private JTextField txtId, txtName, txtPw, txtPhone;
    private JComboBox<String> cmbRole;

    public UserManagementFrame() {
        setTitle("직원 및 권한 관리 시스템");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 750); // 높이 줄임
        getContentPane().setBackground(Color.WHITE);

        setLayout(new BorderLayout());
        // 상단 제목/뒤로가기 패널 제거

        JPanel mainPanel = initComponents();
        add(mainPanel, BorderLayout.CENTER);
        loadUserList();
        setLocationRelativeTo(null);
    }

    private JPanel initComponents() {
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        Color navy = new Color(10, 48, 87);
        Font btnFont = new Font("맑은 고딕", Font.BOLD, 15);

        // 1. 상단: 사용자 목록 (테이블)
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(navy, 1), "사용자 목록 (서버 데이터)", 0, 0, new Font("맑은 고딕", Font.BOLD, 15), navy));

        String[] columns = {"아이디", "이름", "비밀번호", "권한", "전화번호"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        userTable.getTableHeader().setFont(new Font("맑은 고딕", Font.BOLD, 14));
        userTable.getTableHeader().setBackground(navy);
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.setRowHeight(28);
        JScrollPane userTableScroll = new JScrollPane(userTable);
        userTableScroll.setPreferredSize(new Dimension(0, 220)); // 테이블 높이 약간 늘림
        listPanel.add(userTableScroll, BorderLayout.CENTER);

        JButton btnRefresh = new JButton("목록 새로고침");
        btnRefresh.setBackground(navy);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(btnFont);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadUserList());
        listPanel.add(btnRefresh, BorderLayout.SOUTH);

        // 2. 하단: 관리 패널 (추가/삭제) + 로그창
        JPanel bottomPanel = new JPanel(new BorderLayout(12, 12));
        bottomPanel.setBackground(Color.WHITE);

        JPanel inputPanel = createManagementPanel(navy, btnFont);
        inputPanel.setPreferredSize(new Dimension(420, 220)); // 직원 추가/삭제/수정 패널 넓힘
        bottomPanel.add(inputPanel, BorderLayout.WEST);

        outputArea = new JTextArea(5, 15); // 로그창 행 수, 열 수 축소
        outputArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        outputArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(outputArea);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(Color.WHITE);
        logPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(navy, 1), "작업 로그", 0, 0, new Font("맑은 고딕", Font.BOLD, 15), navy));
        logPanel.add(logScroll, BorderLayout.CENTER);

        JButton btnClearLog = new JButton("로그 지우기");
        btnClearLog.setBackground(Color.WHITE);
        btnClearLog.setForeground(navy);
        btnClearLog.setFont(btnFont);
        btnClearLog.setFocusPainted(false);
        btnClearLog.setBorder(BorderFactory.createLineBorder(navy, 1));
        btnClearLog.addActionListener(e -> outputArea.setText(""));
        logPanel.add(btnClearLog, BorderLayout.SOUTH);

        logPanel.setPreferredSize(new Dimension(420, 220));
        bottomPanel.add(logPanel, BorderLayout.CENTER);

        root.add(listPanel, BorderLayout.NORTH);
        root.add(bottomPanel, BorderLayout.CENTER);
        return root;
    }

    /**
     * 관리(추가/삭제) 기능을 위한 패널 생성
     */
    private JPanel createManagementPanel() {
        return createManagementPanel(new Color(10, 48, 87), new Font("맑은 고딕", Font.BOLD, 15));
    }

    private JPanel createManagementPanel(Color navy, Font btnFont) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(navy, 1), "직원 추가 / 삭제 / 수정", 0, 0, new Font("맑은 고딕", Font.BOLD, 15), navy));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        JLabel[] labels = {
            new JLabel("아이디:"), new JLabel("이름:"), new JLabel("비밀번호:"), new JLabel("권한:"), new JLabel("전화번호:")
        };
        for (JLabel l : labels) {
            l.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
            l.setForeground(navy);
        }

        // 아이디
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        panel.add(labels[0], gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.8;
        txtId = new JTextField(18);
        txtId.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        txtId.setPreferredSize(new Dimension(180, 36));
        panel.add(txtId, gbc);

        // 이름
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        panel.add(labels[1], gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.8;
        txtName = new JTextField(18);
        txtName.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        txtName.setPreferredSize(new Dimension(180, 36));
        panel.add(txtName, gbc);

        // 비밀번호
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        panel.add(labels[2], gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.8;
        txtPw = new JTextField(18);
        txtPw.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        txtPw.setPreferredSize(new Dimension(180, 36));
        panel.add(txtPw, gbc);

        // 권한
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.2;
        panel.add(labels[3], gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.8;
        String[] roles = {"Manager", "CSR", "Customer"};
        cmbRole = new JComboBox<>(roles);
        cmbRole.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        cmbRole.setPreferredSize(new Dimension(180, 36));
        panel.add(cmbRole, gbc);

        // 전화번호
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.2;
        panel.add(labels[4], gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.8;
        txtPhone = new JTextField(18);
        txtPhone.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        txtPhone.setPreferredSize(new Dimension(180, 36));
        panel.add(txtPhone, gbc);

        // 버튼 패널
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.2;
        JLabel opLabel = new JLabel("작업:");
        opLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        opLabel.setForeground(navy);
        panel.add(opLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0.8;
        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        btnPanel.setBackground(Color.WHITE);
        JButton btnAdd = new JButton("추가");
        JButton btnDelete = new JButton("삭제");
        JButton btnModify = new JButton("수정");
        JButton[] btns = {btnAdd, btnDelete, btnModify};
        for (JButton b : btns) {
            b.setBackground(navy);
            b.setForeground(Color.WHITE);
            b.setFont(new Font("맑은 고딕", Font.BOLD, 17));
            b.setFocusPainted(false);
            b.setPreferredSize(new Dimension(90, 40));
        }
        btnAdd.addActionListener(e -> handleAddUser());
        btnDelete.addActionListener(e -> handleDeleteUser(e));
        btnModify.addActionListener(e -> handleModifyUser());
        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        btnPanel.add(btnModify);
        panel.add(btnPanel, gbc);

        return panel;
    }

    // =======================================================
    // 📡 네트워크 통신 로직 (Controller 역할 대체)
    // =======================================================

    /**
     * 서버로부터 사용자 목록을 가져와 테이블에 표시 (GET_USERS)
     */
    private void loadUserList() {
        displayLog("서버에 사용자 목록 요청 중...");
        
        // 1. 서버 요청
        String response = NetworkService.getInstance().sendRequest("GET_USERS");

        // 2. 응답 처리 (프로토콜: "USER_LIST:id,password,role,phone,name/...")
        if (response != null && response.startsWith("USER_LIST:")) {
            tableModel.setRowCount(0); // 기존 목록 초기화
            
            String data = response.substring("USER_LIST:".length());
            if (!data.isEmpty()) {
                String[] users = data.split("/");
                for (String userStr : users) {
                    String[] info = userStr.split(","); // info[0]=id, info[1]=password, info[2]=role, info[3]=phone, info[4]=name
                    if (info.length >= 5) {
                        tableModel.addRow(new Object[]{info[0], info[4], info[1], info[2], info[3]}); // ID, Name, Password, Role, Phone 순서로 표시
                    } else if (info.length >= 3) { // 기존 형식 호환성
                        tableModel.addRow(new Object[]{info[0], "", "", info[1], info[2]});
                    }
                }
                displayLog("목록 갱신 완료 (" + users.length + "명)");
            } else {
                displayLog("등록된 사용자가 없습니다.");
            }
        } else {
            displayError("목록 불러오기 실패: " + response);
        }
    }

    /**
     * 사용자 삭제 요청 (DELETE_USER)
     */
    private void handleDeleteUser(ActionEvent e) {
        // 1. 테이블에서 선택된 행 확인
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            // 선택된 게 없으면 입력창의 ID를 기준으로 시도
            String inputId = txtId.getText().trim();
            if (inputId.isEmpty()) {
                displayError("삭제할 사용자를 테이블에서 선택하거나 ID를 입력하세요.");
                return;
            }
            requestDelete(inputId);
        } else {
            // 테이블에서 선택된 ID 가져오기
            String targetId = (String) tableModel.getValueAt(selectedRow, 0);
            requestDelete(targetId);
        }
    }

    private void requestDelete(String id) {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "정말로 '" + id + "' 사용자를 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // 1. 서버 요청 (프로토콜: "DELETE_USER:id")
            String response = NetworkService.getInstance().sendRequest("DELETE_USER:" + id);

            // 2. 응답 처리
            if ("DELETE_SUCCESS".equals(response)) {
                displayLog("사용자 삭제 성공: " + id);
                loadUserList();
                txtId.setText("");
            } else {
                displayError("삭제 실패: " + response);
            }
        }
    }

    // =======================================================
    // 📝로그 및 에러 출력 유틸리티
    // =======================================================

    private void displayLog(String message) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append("[INFO] " + message + "\n");     
            outputArea.setCaretPosition(outputArea.getDocument().getLength()); // 스크롤 자동 내림
        });
    }

    private void displayError(String message) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append("[ERROR] " + message + "\n");
            JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
        });
    }

    // =======================================================
    // 🔐 인증 코드 처리 + 확장된 사용자 작업
    // =======================================================
    private String getAuthCodeEnv() {
        String code = System.getenv("ADMIN_AUTH_CODE");
        return (code == null || code.isEmpty()) ? "0000" : code.trim();
    }

    private boolean verifyAuthCode() {
        String expected = getAuthCodeEnv();
        String input = JOptionPane.showInputDialog(this, "인증 코드를 입력하세요", "인증", JOptionPane.PLAIN_MESSAGE);
        if(input == null) return false; // 취소
        if(!expected.equals(input.trim())) {
            displayError("인증 코드가 올바르지 않습니다.");
            return false;
        }
        return true;
    }

    private void handleAddUser() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String pw = txtPw.getText().trim();
        String role = (String) cmbRole.getSelectedItem();
        String phone = txtPhone.getText().trim();
        if(id.isEmpty() || name.isEmpty() || pw.isEmpty() || phone.isEmpty()) {
            displayError("아이디/이름/비밀번호/전화번호를 입력하세요.");
            return;
        }
        String request = String.format("ADD_USER:%s:%s:%s:%s:%s", id, name, pw, role, phone);
        String response = NetworkService.getInstance().sendRequest(request);
        if("ADD_SUCCESS".equals(response)) {
            displayLog("추가 성공: " + id);
            loadUserList();
            clearInputFields();
        } else {
            displayError("추가 실패: " + response);
        }
    }

    private void handleModifyUser() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String pw = txtPw.getText().trim();
        String role = (String) cmbRole.getSelectedItem();
        String phone = txtPhone.getText().trim();
        if(id.isEmpty() || name.isEmpty() || pw.isEmpty() || phone.isEmpty()) {
            displayError("아이디/이름/비밀번호/전화번호를 입력하세요.");
            return;
        }
        String request = String.format("MODIFY_USER:%s:%s:%s:%s:%s", id, name, pw, role, phone);
        String response = NetworkService.getInstance().sendRequest(request);
        if("MODIFY_SUCCESS".equals(response)) {
            displayLog("수정 성공: " + id);
            loadUserList();
            clearInputFields();
        } else {
            displayError("수정 실패: " + response);
        }
    }

    /** 입력 필드 초기화 */
    private void clearInputFields() {
        txtId.setText("");
        txtName.setText("");
        txtPw.setText("");
        txtPhone.setText("");
        cmbRole.setSelectedIndex(0);
    }
}