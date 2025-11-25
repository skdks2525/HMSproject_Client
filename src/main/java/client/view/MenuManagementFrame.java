/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.view;
import client.net.NetworkService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MenuManagementFrame extends JFrame {
    // UI 컴포넌트
    private final JTable menuTable;
    private final DefaultTableModel tableModel;
    private JComboBox<String> cmbCategory;
    private JComboBox<String> cmbIsAvailable;
    
    private final JButton addButton;
    private final JButton updateButton;
    private final JButton deleteButton;
    private final JButton clearButton;
    
    // 입력 필드 (등록 / 수정용)
    private final JTextField menuIdField = new JTextField(15);
    private final JTextField nameField = new JTextField(15);
    private final JTextField priceField = new JTextField(15);
    
    // 테이블 헤더 정의
    private final String[] columnNames = {"ID", "이름", "가격", "종류", "판매 여부"};
    
    public MenuManagementFrame() {
        setTitle("식음료 메뉴 관리");
        
        // 테이블 모델 및 JTable 초기화
        this.tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 테이블 셀 직접 수정 금지
                return false;
            }
        };
        
        this.menuTable = new JTable(tableModel);
        
        addButton = new JButton("메뉴 등록");
        updateButton = new JButton("메뉴 수정");
        deleteButton = new JButton("메뉴 삭제");
        clearButton = new JButton("입력칸 지우기");
        
        // UI 디자인 및 레이아웃 구성
        initializeUI();
        
        // 이벤트 리스너 설정
        setupEventListeners();
        
        // 초기 데이터 로딩
        loadMenuData();
        
        // 창 설정
        this.setSize(800, 500);
        this.setLocationRelativeTo(null);
        
        // 메인 뷰에서 이 창을 띄울 것이므로, 닫을 때는 이 창만 닫히도록 설정
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    //  UI 디자인 및 컴포넌트 배치
    private void initializeUI() {
        this.setLayout(new BorderLayout(10, 10));
        
        // 테이블 패널 (중앙)
        JScrollPane tableScrollPane = new JScrollPane(menuTable);
        this.add(tableScrollPane, BorderLayout.CENTER);
        
        // 입력 및 버튼 패널 (남쪽)
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        
        // 입력 필드 패널
        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("메뉴 정보 입력/수정"));
        
        inputPanel.add(new JLabel("ID: "));
        inputPanel.add(menuIdField);
        inputPanel.add(new JLabel("이름: "));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("가격: "));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("종류: "));
        String [] categorys = {"메인음식", "디저트", "음료", "미분류"};
        cmbCategory = new JComboBox<>(categorys);
        inputPanel.add(cmbCategory);
        inputPanel.add(new JLabel("판매여부: "));
        String [] isavailable = {"판매중", "판매중지"};
        cmbIsAvailable = new JComboBox<>(isavailable);
        inputPanel.add(cmbIsAvailable);
        
        // 마지막 빈칸들 채우기 (GridLayout은 칸을 정확히 맞춰야 함)
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));
        southPanel.add(inputPanel, BorderLayout.CENTER);
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        this.add(southPanel, BorderLayout.SOUTH);
    }
    
    // 이벤트 처리 및 Controller 연동
    
    private void setupEventListeners() {
        // 등록 버튼 클릭
        addButton.addActionListener(e -> handleAddMenu());
        
        // 삭제 버튼 클릭
        deleteButton.addActionListener(e -> handleDeleteMenu());
        
        // [테이블 Row 선택] 시, 입력 필드에 데이터 로드 (수정 준비)
        menuTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && menuTable.getSelectedRow() != -1) {
                loadFieldsFromSelectedRow();
            }
        });
        
        updateButton.addActionListener(e -> handleUpdateMenu());
        clearButton.addActionListener(e -> handleClearInput());
    }
    
    // 메뉴 목록 호출 (참고: UserManagementFrame.java)
    private void loadMenuData() {
        String response = NetworkService.getInstance().sendRequest("GET_MENUS");
        
        if (response != null && response.startsWith("MENU_LIST:")) {
            tableModel.setRowCount(0); // 목록 초기화
           
            String data = response.substring("MENU_LIST:".length());
            if (!data.isEmpty()) {
                String[] menus = data.split("/");
                for (String menuStr : menus) {
                    String[] info = menuStr.split(",");
                    if (info.length >= 5) {
                        String status = info[4].equals("true") ? "판매중" : "판매중지"; // 관리 ui에서 판매 여부를 판매중, 판매중지로 표시 (없으면 true/false로 표기됨)
                        tableModel.addRow(new Object[] {info[0], info[1], info[2], info[3], status});
                    }
                }
                JOptionPane.showMessageDialog(this, "목록 갱신 완료 (" + menus.length + "개)");
            }
            else {
                JOptionPane.showMessageDialog(this, "등록된 메뉴가 존재하지 않습니다.", "호출 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        
        /**
        for (Menu menu : menus) {
        Object[] rowData = {
            menu.getMenuId(),
            menu.getName(),
            menu.getCategory().getDescription(),
            menu.getPrice(),
            menu.getIsAvailable()
        };
        tableModel.addRow(rowData);
        } */
    }
    
    // 메뉴 등록 처리
    private void handleAddMenu() {
        String menuId = menuIdField.getText().trim();
        String name = nameField.getText().trim();
        int price = Integer.parseInt(priceField.getText().trim());
        String category = (String) cmbCategory.getSelectedItem();
        String isavailableStr = (String) cmbIsAvailable.getSelectedItem();
        boolean isAvailable = isavailableStr.equals("판매중");
        
        if (menuId.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "메뉴 ID와 이름은 필수 항목입니다.");
        }
        
        // 서버 요청 (프로토콜: "ADD_MENU:menuId:name:price:category:isavailable")
        String request = String.format("ADD_MENU:%s:%s:%s:%s:%b", menuId,name,price,category,isAvailable);
        String response = NetworkService.getInstance().sendRequest(request);
        
        System.out.println("보낸 요청 = [" + request + "]");
        // Controller에 등록 요청 위임
        // String message = controller.addNewMenu(menuId, name, price);
        
        // Controller가 반환한 메시지를 팝업으로 표시
        JOptionPane.showMessageDialog(this, response);
        
        // 성공적으로 등록되었다면 테이블 갱신
        if ("ADD_SUCCESS".equals(response)) {
            loadMenuData();
            clearFields();
        }
    }
    
    private void handleDeleteMenu() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 메뉴를 테이블에서 선택해주세요.");
            return;
        }

        // 선택된 행의 첫 번째 컬럼(ID) 가져오다.
        String menuIdToDelete = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "정말로 [" + menuIdToDelete + "] 을(를) 삭제하시겠습니까?", "삭제 확인", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String response = NetworkService.getInstance().sendRequest("DELETE_MENU:" + menuIdToDelete);
            
            if ("DELETE_SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "[" + menuIdToDelete + "] 삭제 완료.");
                loadMenuData(); // 테이블 갱신
                clearFields();
            }
            else {
                JOptionPane.showMessageDialog(this, "[" + menuIdToDelete + "] 삭제 실패.", "삭제 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleUpdateMenu() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "수정할 메뉴를 테이블에서 선택해주세요.");
            return;
        }
        
        // 현재 선택된 ID와 수정된 정보를 가져와 새 Menu 객체를 생성
        String menuId = menuIdField.getText().trim();
        String name = nameField.getText().trim();
        int price = Integer.parseInt(priceField.getText().trim());
        String category = (String) cmbCategory.getSelectedItem();
        
        String isAvailableStr = (String) cmbIsAvailable.getSelectedItem();     
        boolean isAvailable = isAvailableStr.equals("판매중"); // String -> boolean 변환
        
        if (menuId.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "메뉴 ID와 이름 작성은 필수 항목입니다.");
        }
        
        String request = String.format("UPDATE_MENU:%s:%s:%s:%s:%b", menuId, name, price, category, isAvailable);
        String response = NetworkService.getInstance().sendRequest(request);
        String finalMessage = "정보 수정 완료: ";
        
        // 최종 결과 메시지 표시 및 갱신
        if ("UPDATE_SUCCESS".equals(response)) {
            JOptionPane.showMessageDialog(this, finalMessage, "수정 완료", JOptionPane.INFORMATION_MESSAGE);
            loadMenuData(); // 테이블 갱신 (성공 시)
            clearFields();
        }
        else {
            JOptionPane.showMessageDialog(this, "정보 수정 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleClearInput() {
        clearFields();
        cmbCategory.setSelectedIndex(0);
        cmbIsAvailable.setSelectedIndex(0);
    }

    // 기타 유틸리티 메서드
    private void loadFieldsFromSelectedRow() {
        DefaultTableModel model = (DefaultTableModel) menuTable.getModel();
        int row = menuTable.getSelectedRow();
        if (row >= 0) {
            // 테이블의 데이터를 입력 필드에 표시 (PW는 보안상 제외)
            menuIdField.setText((String) tableModel.getValueAt(row, 0));
            nameField.setText((String) tableModel.getValueAt(row, 1));
            // 권한은 필드에 표시하지 않음
            priceField.setText((String) tableModel.getValueAt(row, 2));
            
            String category = (String) tableModel.getValueAt(row, 3);
            cmbCategory.setSelectedItem(category);
            
            String isAvailable = (String) tableModel.getValueAt(row, 4);
            
            // 테이블에는 "false" / "true"가 들어있을 수 있으므로 UI 표시값으로 변환
            String displayStatus = "true".equalsIgnoreCase(isAvailable) || "판매중".equals(isAvailable) ? "판매중" : "판매중지";
            cmbIsAvailable.setSelectedItem(displayStatus); // 판매여부 콤보박스에 값 설정
            
            // 수정 시 ID 변경을 막기 위해 비활성화
            menuIdField.setEnabled(false); 
        }
    }
    
    private void clearFields() {
        menuIdField.setText("");
        nameField.setText("");
        priceField.setText("");
        menuIdField.setEnabled(true);
    }
}

