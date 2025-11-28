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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 이 창만 닫힘
        setSize(1000, 700); // 창 크기 고정
        
        initComponents();
        
        // 창이 열리면 자동으로 목록을 불러옴
        loadUserList();
        
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // 메인 컨테이너 설정
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. 상단: 사용자 목록 (테이블)
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("사용자 목록 (서버 데이터)"));
        
        String[] columns = {"아이디", "이름", "비밀번호", "권한", "전화번호"}; // 모든 정보 표시
        tableModel = new DefaultTableModel(columns, 0) {
            @Override // 테이블 수정 불가 설정
            public boolean isCellEditable(int row, int column) { return false; }
        };
        userTable = new JTable(tableModel);
        listPanel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        
        // 새로고침 버튼
        JButton btnRefresh = new JButton("목록 새로고침");
        btnRefresh.addActionListener(e -> loadUserList());
        listPanel.add(btnRefresh, BorderLayout.SOUTH);

        add(listPanel, BorderLayout.CENTER);

        // 2. 하단: 관리 패널 (추가/삭제) + 로그창
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        
        // 2-1. 입력 폼 (Left)
        JPanel inputPanel = createManagementPanel();
        bottomPanel.add(inputPanel, BorderLayout.WEST);
        
        // 2-2. 로그 출력 영역 (Center)
        outputArea = new JTextArea(8, 30);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(outputArea);
        
        // 로그 패널에 클리어 버튼 추가
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("작업 로그"));
        logPanel.add(logScroll, BorderLayout.CENTER);
        
        JButton btnClearLog = new JButton("로그 지우기");
        btnClearLog.addActionListener(e -> outputArea.setText(""));
        logPanel.add(btnClearLog, BorderLayout.SOUTH);
        
        bottomPanel.add(logPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 관리(추가/삭제) 기능을 위한 패널 생성
     */
    private JPanel createManagementPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("직원 추가 / 삭제 / 수정"));

        // 입력 필드
        panel.add(new JLabel("아이디:"));
        txtId = new JTextField(10);
        panel.add(txtId);

        panel.add(new JLabel("이름:"));
        txtName = new JTextField(10);
        panel.add(txtName);

        panel.add(new JLabel("비밀번호:"));
        txtPw = new JTextField(10);
        panel.add(txtPw);

        panel.add(new JLabel("권한:"));
        String[] roles = {"Manager", "CSR", "Customer"};
        cmbRole = new JComboBox<>(roles);
        panel.add(cmbRole);

        panel.add(new JLabel("전화번호:"));
        txtPhone = new JTextField(10);
        panel.add(txtPhone);

        // 버튼 패널
        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        JButton btnAdd = new JButton("추가");
        JButton btnDelete = new JButton("삭제");
        JButton btnModify = new JButton("수정");
        
        btnAdd.addActionListener(e -> handleAddUser());
        btnDelete.addActionListener(e -> handleDeleteUser(e));
        btnModify.addActionListener(e -> handleModifyUser());
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        btnPanel.add(btnModify);
        
        // 마지막 행에 버튼 배치 (Grid Layout 특성상 컴포넌트로 추가)
        panel.add(new JLabel("작업:")); 
        panel.add(btnPanel);

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