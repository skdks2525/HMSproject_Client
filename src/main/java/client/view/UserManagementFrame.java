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
    private JTextField txtId, txtPw;
    private JComboBox<String> cmbRole;

    public UserManagementFrame() {
        setTitle("직원 및 권한 관리 시스템");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 이 창만 닫힘
        
        initComponents();
        
        // 창이 열리면 자동으로 목록을 불러옴
        loadUserList();
        
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // 메인 컨테이너 설정
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. 상단: 사용자 목록 (테이블)
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("사용자 목록 (서버 데이터)"));
        
        String[] columns = {"아이디", "권한"}; // 비밀번호는 보안상 보여주지 않음
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
        logScroll.setBorder(BorderFactory.createTitledBorder("작업 로그"));
        bottomPanel.add(logScroll, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 관리(추가/삭제) 기능을 위한 패널 생성
     */
    private JPanel createManagementPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("직원 추가 / 삭제"));

        // 입력 필드
        panel.add(new JLabel("아이디:"));
        txtId = new JTextField(10);
        panel.add(txtId);

        panel.add(new JLabel("비밀번호:"));
        txtPw = new JTextField(10);
        panel.add(txtPw);

        panel.add(new JLabel("권한:"));
        String[] roles = {"Manager", "CSR", "Customer"};
        cmbRole = new JComboBox<>(roles);
        panel.add(cmbRole);

        // 버튼 패널
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        JButton btnAdd = new JButton("추가");
        JButton btnDelete = new JButton("삭제");
        
        btnAdd.addActionListener(this::handleAddUser);
        btnDelete.addActionListener(this::handleDeleteUser);
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        
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

        // 2. 응답 처리 (프로토콜: "USER_LIST:id,role/id,role/...")
        if (response != null && response.startsWith("USER_LIST:")) {
            tableModel.setRowCount(0); // 기존 목록 초기화
            
            String data = response.substring("USER_LIST:".length());
            if (!data.isEmpty()) {
                String[] users = data.split("/");
                for (String userStr : users) {
                    String[] info = userStr.split(","); // info[0]=id, info[1]=role
                    if (info.length >= 2) {
                        tableModel.addRow(new Object[]{info[0], info[1]});
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
     * 사용자 추가 요청 (ADD_USER)
     */
    private void handleAddUser(ActionEvent e) {
        String id = txtId.getText().trim();
        String pw = txtPw.getText().trim();
        String role = (String) cmbRole.getSelectedItem();

        if (id.isEmpty() || pw.isEmpty()) {
            displayError("아이디와 비밀번호를 입력해주세요.");
            return;
        }

        // 1. 서버 요청 (프로토콜: "ADD_USER:id:pw:role")
        String request = String.format("ADD_USER:%s:%s:%s", id, pw, role);
        String response = NetworkService.getInstance().sendRequest(request);

        // 2. 응답 처리
        if ("ADD_SUCCESS".equals(response)) {
            displayLog("사용자 추가 성공: " + id);
            loadUserList(); // 목록 갱신
            txtId.setText(""); txtPw.setText(""); // 입력창 비우기
        } else {
            displayError("추가 실패: " + response);
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
}