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
        private final JTextField stockField = new JTextField(15); // 추가된 재고 필드
    
        private final String[] columnNames = {"ID", "이름", "가격", "종류", "판매 여부", "재고"};
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
            // [UI 구조] 입력 패널을 3행 2열로 재배치하여 ID/이름, 가격/종류, 판매여부/재고를 각각 한 줄에 배치
            // [UI 개선] 판매여부, 재고 필드가 명확하게 구분되어 입력/수정이 쉬움
        this.setLayout(new BorderLayout(10, 10));

        // 테이블 패널 (중앙)
        JScrollPane tableScrollPane = new JScrollPane(menuTable);
        this.add(tableScrollPane, BorderLayout.CENTER);

        // 입력 및 버튼 패널 (남쪽)
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));

        // 입력 패널: 3행 2열(상단 ID/이름, 중간 가격/종류, 하단 판매여부/재고)
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("메뉴 정보 입력/수정"));

        inputPanel.add(new JLabel("ID: "));
        inputPanel.add(menuIdField);
        inputPanel.add(new JLabel("이름: "));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("가격: "));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("종류: "));
        String[] categorys = {"메인음식", "디저트", "음료", "미분류"};
        cmbCategory = new JComboBox<>(categorys);
        inputPanel.add(cmbCategory);
        inputPanel.add(new JLabel("판매여부: "));
        String[] isavailable = {"판매중", "판매중지"};
        cmbIsAvailable = new JComboBox<>(isavailable);
        inputPanel.add(cmbIsAvailable);
        inputPanel.add(new JLabel("재고: "));
        inputPanel.add(stockField);

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
                        // [이벤트 처리 상세 설명]
                        // - 판매여부 콤보박스(cmbIsAvailable)에서 '판매중지' 선택 시 재고 입력란을 0으로 만들고 비활성화
                        // - '판매중' 선택 시 재고 입력란 활성화
                        // - 등록/수정/삭제/지우기 버튼에 각각 이벤트 리스너 연결
                        // - 테이블 행 선택 시 입력란 자동 채움(loadFieldsFromSelectedRow)
                    // [이벤트] 판매여부 콤보박스가 '판매중지'로 바뀌면 재고 입력란을 0으로 만들고 비활성화, '판매중'이면 활성화
                // 판매여부 콤보박스 변경 시 재고 자동 처리
                cmbIsAvailable.addActionListener(e -> {
                    String sel = (String) cmbIsAvailable.getSelectedItem();
                    if (sel != null && sel.equals("판매중지")) {
                        stockField.setText("0");
                        stockField.setEnabled(false);
                    } else {
                        stockField.setEnabled(true);
                    }
                });
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
                // [서버 데이터 파싱 및 테이블 반영 상세 설명]
                // - 서버로부터 "GET_MENUS" 요청 결과를 받아 각 메뉴 정보를 테이블에 추가
                // - info[0]=ID, info[1]=이름, info[2]=가격, info[3]=종류, info[4]=판매여부, info[5]=재고
                // - 판매중지 메뉴는 재고를 0으로 표시
                // - 테이블에는 모든 메뉴(판매중/중지)와 재고가 표시됨
            // [서버 연동] GET_MENUS 요청 결과를 파싱하여 테이블에 표시
            // [로직] 판매중지 메뉴는 재고를 0으로 표시, 테이블에는 판매여부/재고 모두 표시
        tableModel.setRowCount(0);
        String response = NetworkService.getInstance().sendRequest("GET_MENUS");
        if (response == null || !response.startsWith("MENU_LIST:")) {
            JOptionPane.showMessageDialog(this, "메뉴 목록을 불러오지 못했습니다.");
            return;
        }
        String data = response.substring("MENU_LIST:".length());
        if (data.isEmpty()) return;
        String[] menus = data.split("/");
        for (String m : menus) {
            String[] info = m.split(",");
            if (info.length < 6) continue;
            String menuid = info[0];
            String name = info[1];
            int price;
            int stock;
            try {
                price = Integer.parseInt(info[2]);
                stock = Integer.parseInt(info[5]);
            } catch (NumberFormatException e) {
                continue;
            }
            String category = info[3];
            boolean isAvailable = Boolean.parseBoolean(info[4]);
            // 판매중 메뉴만 표시, 판매중지면 재고 0으로
            if (!isAvailable) {
                stock = 0;
            }
            tableModel.addRow(new Object[]{menuid, name, price, category, isAvailable ? "판매중" : "판매중지", stock});
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
                // [메뉴 등록 처리 상세 설명]
                // 1. 입력값(메뉴ID, 이름, 가격, 종류, 판매여부, 재고) 검증
                //    - 메뉴ID, 이름이 비어있으면 등록 불가
                //    - 재고가 숫자가 아니면 등록 불가
                // 2. 서버로 "ADD_MENU:menuId:name:price:category:isAvailable:stock" 형식으로 요청 전송
                // 3. 서버 응답(ADD_SUCCESS)이면 테이블 갱신 및 입력란 초기화
            // [입력값 검증] 재고 입력란이 비어있거나 숫자가 아니면 등록 불가
            // [프로토콜] ADD_MENU:menuId:name:price:category:isAvailable:stock 형식으로 서버에 전송
            // [서버 저장] 정상 등록 시 menus.csv에 반영됨
        String menuId = menuIdField.getText().trim();
        String name = nameField.getText().trim();
        int price = Integer.parseInt(priceField.getText().trim());
        String category = (String) cmbCategory.getSelectedItem();
        String isavailableStr = (String) cmbIsAvailable.getSelectedItem();
        boolean isAvailable = isavailableStr.equals("판매중");
        int stock = 0;
        try {
            stock = Integer.parseInt(stockField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "재고는 숫자로 입력해야 합니다.");
            return;
        }
        if (menuId.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "메뉴 ID와 이름은 필수 항목입니다.");
            return;
        }
        // 서버 요청 (프로토콜: "ADD_MENU:menuId:name:price:category:isavailable:stock")
        String request = String.format("ADD_MENU:%s:%s:%d:%s:%b:%d", menuId, name, price, category, isAvailable, stock);
        String response = NetworkService.getInstance().sendRequest(request);
        System.out.println("보낸 요청 = [" + request + "]");
        JOptionPane.showMessageDialog(this, response);
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
                // [메뉴 수정 처리 상세 설명]
                // 1. 테이블에서 수정할 행을 선택하지 않으면 경고
                // 2. 입력값(메뉴ID, 이름, 가격, 종류, 판매여부, 재고) 검증
                //    - 메뉴ID, 이름이 비어있으면 수정 불가
                //    - 재고가 숫자가 아니면 수정 불가
                // 3. 서버로 "UPDATE_MENU:menuId:name:price:category:isAvailable:stock" 형식으로 요청 전송
                // 4. 서버 응답(UPDATE_SUCCESS)이면 테이블 갱신 및 입력란 초기화
            // [입력값 검증] 재고 입력란이 비어있거나 숫자가 아니면 수정 불가
            // [프로토콜] UPDATE_MENU:menuId:name:price:category:isAvailable:stock 형식으로 서버에 전송
            // [서버 저장] 정상 수정 시 menus.csv에 반영됨
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "수정할 메뉴를 테이블에서 선택해주세요.");
            return;
        }

        String menuId = menuIdField.getText().trim();
        String name = nameField.getText().trim();
        int price = Integer.parseInt(priceField.getText().trim());
        String category = (String) cmbCategory.getSelectedItem();
        String isAvailableStr = (String) cmbIsAvailable.getSelectedItem();
        boolean isAvailable = isAvailableStr.equals("판매중");
        int stock = 0;
        try {
            stock = Integer.parseInt(stockField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "재고는 숫자로 입력해야 합니다.");
            return;
        }
        if (menuId.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "메뉴 ID와 이름 작성은 필수 항목입니다.");
            return;
        }
        // 서버 요청 (프로토콜: UPDATE_MENU:menuId:name:price:category:isAvailable:stock)
        String request = String.format("UPDATE_MENU:%s:%s:%d:%s:%b:%d", menuId, name, price, category, isAvailable, stock);
        String response = NetworkService.getInstance().sendRequest(request);
        String finalMessage = "정보 수정 완료: ";
        if ("UPDATE_SUCCESS".equals(response)) {
            JOptionPane.showMessageDialog(this, finalMessage, "수정 완료", JOptionPane.INFORMATION_MESSAGE);
            loadMenuData();
            clearFields();
        } else {
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
                // [입력란 동기화 상세 설명]
                // - 테이블에서 행을 선택하면 해당 값이 입력란에 자동 채워짐
                // - 판매여부가 '판매중지'면 재고 입력란 비활성화, '판매중'이면 활성화
                // - 메뉴ID(menuIdField)는 항상 수정 가능하게 설정
            // [UI 동기화] 테이블에서 행을 선택하면 입력란에 값이 채워지고, 판매여부/재고 필드도 동기화됨
            // [ID 정책] 메뉴ID(menuIdField)는 항상 수정 가능하게 설정
        int row = menuTable.getSelectedRow();
        if (row >= 0) {
            menuIdField.setText(tableModel.getValueAt(row, 0).toString());
            nameField.setText(tableModel.getValueAt(row, 1).toString());
            priceField.setText(tableModel.getValueAt(row, 2).toString());
            String category = tableModel.getValueAt(row, 3).toString();
            cmbCategory.setSelectedItem(category);
            String isAvailable = tableModel.getValueAt(row, 4).toString();
            String displayStatus = "true".equalsIgnoreCase(isAvailable) || "판매중".equals(isAvailable) ? "판매중" : "판매중지";
            cmbIsAvailable.setSelectedItem(displayStatus);
            String stock = tableModel.getValueAt(row, 5).toString();
            stockField.setText(stock);
            stockField.setEnabled(!"판매중지".equals(displayStatus));
            menuIdField.setEnabled(true); // 항상 수정 가능하게
        }
    }
    
    private void clearFields() {
        menuIdField.setText("");
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        menuIdField.setEnabled(true);
    }
}

