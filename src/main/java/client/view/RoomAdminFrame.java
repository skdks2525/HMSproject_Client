package client.view;

import java.awt.BorderLayout;
import java.awt.Color; // 달력 라이브러리
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.toedter.calendar.JDateChooser;

import client.net.NetworkService;

public class RoomAdminFrame extends JFrame {

    private JPanel roomGridPanel;
    private JDateChooser dateChooser; // [복구] 날짜 선택기

    // 우측 필드
    private JTextField txtRoomNum, txtType, txtPrice, txtCapacity;
    private JTextArea txtDesc;
    private JTextField txtGuestName, txtPhone, txtGuestNum, txtCheckInDate, txtCheckOutDate;
    private JTextArea txtGuestRequest;
    
    private JButton btnUpdateInfo; 
    private JButton btnSaveRequest; 
    
    // [수정] 상태 강제 변경용 컴포넌트
    private JComboBox<String> cmbForceStatus;
    private JButton btnForceStatus;

    private String selectedResId = null;
    private String selectedRoomNum = null;

    public RoomAdminFrame() {
        setTitle("객실 관리 시스템 (관리자 모드)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 850);
        setLocationRelativeTo(null);

        initComponents();
        loadRoomData(); 
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 240));

        // 상단 패널 (제목 + [날짜선택])
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Room Management");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        lblTitle.setForeground(new Color(0, 102, 51)); 
        
        // 날짜 선택 패널
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        datePanel.setBackground(Color.WHITE);
        
        datePanel.add(new JLabel("조회 기준일: "));
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setDate(new Date()); // 오늘 날짜
        dateChooser.setPreferredSize(new Dimension(120, 30));
        datePanel.add(dateChooser);
        
        JButton btnSearch = new JButton("조회");
        btnSearch.setBackground(Color.DARK_GRAY);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> loadRoomData());
        datePanel.add(btnSearch);

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(datePanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);

        // 좌측: 객실 현황판
        roomGridPanel = new JPanel(new GridLayout(0, 4, 10, 10)); 
        roomGridPanel.setBackground(Color.WHITE);
        roomGridPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(roomGridPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                null, "객실 현황 (클릭하여 관리)", 
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
                new Font("맑은 고딕", Font.BOLD, 16)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // 우측: 상세 정보 패널
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(400, 0));
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        rightPanel.setBackground(Color.WHITE);

        // 객실 정보 (기존 동일)
        JPanel roomInfoPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        roomInfoPanel.setBorder(BorderFactory.createTitledBorder("객실 기본 정보"));
        roomInfoPanel.setBackground(Color.WHITE);
        roomInfoPanel.setMaximumSize(new Dimension(1000, 150));

        txtRoomNum = new JTextField(); txtRoomNum.setEditable(false); 
        txtType = new JTextField();
        txtPrice = new JTextField();
        txtCapacity = new JTextField();
        
        roomInfoPanel.add(new JLabel("방 번호:")); roomInfoPanel.add(txtRoomNum);
        roomInfoPanel.add(new JLabel("객실 타입:")); roomInfoPanel.add(txtType);
        roomInfoPanel.add(new JLabel("1박 가격:")); roomInfoPanel.add(txtPrice);
        roomInfoPanel.add(new JLabel("수용 인원:")); roomInfoPanel.add(txtCapacity);

        // 설명 패널
        JPanel roomDescPanel = new JPanel(new BorderLayout(5, 5));
        roomDescPanel.setBorder(BorderFactory.createTitledBorder("객실 설명"));
        roomDescPanel.setBackground(Color.WHITE);
        roomDescPanel.setMaximumSize(new Dimension(1000, 100));

        txtDesc = new JTextArea(2, 20);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDesc);
        roomDescPanel.add(descScroll, BorderLayout.CENTER);

        // 수정 버튼
        JPanel pBtnRoom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBtnRoom.setBackground(Color.WHITE);
        btnUpdateInfo = new JButton("객실 정보 저장");
        btnUpdateInfo.addActionListener(this::handleUpdateRoom);
        pBtnRoom.add(btnUpdateInfo);
        
        // 3-2. 예약자 정보
        JPanel guestInfoPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        guestInfoPanel.setBorder(BorderFactory.createTitledBorder("예약자 정보"));
        guestInfoPanel.setBackground(Color.WHITE);
        guestInfoPanel.setMaximumSize(new Dimension(1000, 180));

        txtGuestName = new JTextField(); txtGuestName.setEditable(false);
        txtPhone = new JTextField(); txtPhone.setEditable(false);
        txtGuestNum = new JTextField(); txtGuestNum.setEditable(false);
        txtCheckInDate = new JTextField(); txtCheckInDate.setEditable(false);
        txtCheckOutDate = new JTextField(); txtCheckOutDate.setEditable(false);

        guestInfoPanel.add(new JLabel("예약자명:")); guestInfoPanel.add(txtGuestName);
        guestInfoPanel.add(new JLabel("연락처:")); guestInfoPanel.add(txtPhone);
        guestInfoPanel.add(new JLabel("인원:")); guestInfoPanel.add(txtGuestNum);
        guestInfoPanel.add(new JLabel("입실날짜:")); guestInfoPanel.add(txtCheckInDate); 
        guestInfoPanel.add(new JLabel("퇴실날짜:")); guestInfoPanel.add(txtCheckOutDate);

        // 요청사항
        JPanel requestPanel = new JPanel(new BorderLayout(5, 5));
        requestPanel.setBorder(BorderFactory.createTitledBorder("고객 요청사항"));
        requestPanel.setBackground(Color.WHITE);
        requestPanel.setMaximumSize(new Dimension(1000, 100));

        txtGuestRequest = new JTextArea(2, 20);
        txtGuestRequest.setLineWrap(true);
        JScrollPane reqScroll = new JScrollPane(txtGuestRequest);
        
        btnSaveRequest = new JButton("요청사항 저장");
        btnSaveRequest.addActionListener(this::handleSaveRequest);
        btnSaveRequest.setEnabled(false); 

        requestPanel.add(reqScroll, BorderLayout.CENTER);
        requestPanel.add(btnSaveRequest, BorderLayout.SOUTH);

        // 3-3. 객실 상태 강제로 변경하는 기능
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createTitledBorder("객실  변경"));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setMaximumSize(new Dimension(1000, 80));
        
        String[] statusOptions = {
            "상태 선택", 
            "예약 확정", 
            "입실 완료", 
            "퇴실 완료",
            "빈 방으로 변경(예약 삭제)"
        };
        cmbForceStatus = new JComboBox<>(statusOptions);
        cmbForceStatus.setPreferredSize(new Dimension(220, 30));
        
        btnForceStatus = new JButton("상태 변경");
        btnForceStatus.setBackground(new Color(255, 102, 102)); // 강조색 (빨강)
        btnForceStatus.setForeground(Color.WHITE);
        btnForceStatus.addActionListener(this::handleForceStatusChange);
        btnForceStatus.setEnabled(false);

        statusPanel.add(cmbForceStatus);
        statusPanel.add(btnForceStatus);

        // 우측 패널 조립
        rightPanel.add(roomInfoPanel);
        rightPanel.add(roomDescPanel);
        rightPanel.add(pBtnRoom);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(guestInfoPanel);
        rightPanel.add(requestPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(statusPanel);
        rightPanel.add(Box.createVerticalGlue()); 

        add(rightPanel, BorderLayout.EAST);

        // 하단
        JButton btnRefresh = new JButton("현황판 새로고침");
        btnRefresh.setBackground(Color.DARK_GRAY);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.addActionListener(e -> loadRoomData());
        add(btnRefresh, BorderLayout.SOUTH);
    }

    private void loadRoomData() {
        roomGridPanel.removeAll();
        
        // [수정] 선택된 날짜 전송
        Date selectedDate = dateChooser.getDate();
        if (selectedDate == null) selectedDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(selectedDate);
        
        String response = NetworkService.getInstance().sendRequest("GET_DASHBOARD:" + dateStr);

        if (response != null && response.startsWith("DASHBOARD_LIST:")) {
            String data = response.substring("DASHBOARD_LIST:".length());
            if (!data.isEmpty()) {
                String[] rooms = data.split("\\|"); 
                for (String r : rooms) {
                    String[] info = r.split(",", -1); 
                    if (info.length >= 12) {
                        JButton roomBtn = createRoomButton(info);
                        roomGridPanel.add(roomBtn);
                    }
                }
            }
        }
        roomGridPanel.revalidate();
        roomGridPanel.repaint();
    }

    private JButton createRoomButton(String[] info) {
        String roomNum = info[0];
        String status = info[3]; 
        
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(150, 100));
        btn.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        
        // 색상 로직
        if ("Empty".equals(status)) {
            btn.setBackground(Color.LIGHT_GRAY); 
            btn.setText("<html><center><h2>" + roomNum + "</h2>빈 방<br>(Available)</center></html>");
        } else if ("Confirmed".equals(status)) {
            btn.setBackground(new Color(144, 238, 144)); 
            btn.setText("<html><center><h2>" + roomNum + "</h2>예약확정<br>(입실 전)</center></html>");
        } else if ("CheckedIn".equals(status)) {
            btn.setBackground(new Color(135, 206, 250)); 
            btn.setText("<html><center><h2>" + roomNum + "</h2>입실 중<br>(CheckIn)</center></html>");
        } else if ("CheckedOut".equals(status)) {
            btn.setBackground(new Color(255, 182, 193)); 
            btn.setText("<html><center><h2>" + roomNum + "</h2>청소 중<br>(CheckOut)</center></html>");
        } else {
            btn.setBackground(new Color(255, 204, 102)); 
            btn.setText("<html><center><h2>" + roomNum + "</h2>" + status + "</center></html>");
        }

        btn.addActionListener(e -> selectRoom(info));
        return btn;
    }

    private void selectRoom(String[] info) {
        selectedRoomNum = info[0];
        selectedResId = info[5];
        String status = info[3];

        txtRoomNum.setText(info[0]);
        txtType.setText(info[1]);
        txtPrice.setText(info[2]);
        txtCapacity.setText("2"); 
        txtDesc.setText(info[10]);

        // 예약 정보 채우기
        if ("-".equals(selectedResId) || "Empty".equals(status)) {
            clearGuestInfo();
            txtGuestRequest.setText("");
            txtGuestRequest.setEditable(false);
            btnSaveRequest.setEnabled(false);
            // 빈 방이면 상태 변경 중 '빈 방 만들기'는 의미 없음, 하지만 예약 생성은 여기서 안 함
        } else {
            txtGuestName.setText(info[4]);
            txtGuestNum.setText(info[6]);
            txtPhone.setText(info[7]);
            txtCheckInDate.setText(info[8]);
            txtCheckOutDate.setText(info[9]);
            
            txtGuestRequest.setText(info[11]);
            txtGuestRequest.setEditable(true); 
            btnSaveRequest.setEnabled(true);
        }
        
        // 방 선택했으므로 버튼 활성화
        btnForceStatus.setEnabled(true);
        // 콤보박스 초기화
        cmbForceStatus.setSelectedIndex(0);
    }

    // 상태 변경 핸들러
    private void handleForceStatusChange(ActionEvent e) {
        if (selectedRoomNum == null) return;
        
        int idx = cmbForceStatus.getSelectedIndex();
        if (idx <= 0) return; 

        // [Case 1] 빈 방으로 변경 (예약 삭제) -> Index 4
        if (idx == 4) {
            if (selectedResId == null || "-".equals(selectedResId)) {
                JOptionPane.showMessageDialog(this, "삭제할 예약이 없습니다. (이미 빈 방입니다.)");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "정말 예약을 삭제하고 빈 방으로 만드시겠습니까?", "경고", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                NetworkService.getInstance().sendRequest("DELETE_RESERVATION:" + selectedResId);
                loadRoomData();
            }
            return;
        }

        

        // [Case 3] 예약 상태 변경 (Confirmed, CheckedIn, CheckedOut) -> Index 1, 2, 3
        // 특별 처리: CheckedOut(퇴실 완료, idx==3)는 메뉴 주문 결제(미결제 확인)를 먼저 요구
        if (idx == 3) {
            if (selectedResId == null || "-".equals(selectedResId)) {
                JOptionPane.showMessageDialog(this, "예약이 없는 방입니다.\n퇴실 처리를 하려면 예약이 있어야 합니다.");
                return;
            }

            String guestName = txtGuestName.getText();
            if (guestName == null || guestName.trim().isEmpty() || "-".equals(guestName)) {
                JOptionPane.showMessageDialog(this, "예약자 정보가 없습니다. 먼저 예약 정보를 확인하세요.");
                return;
            }

            // 서버에 해당 투숙객의 메뉴주문 내역 요청 (입실~퇴실 기간 필터링)
            String checkInDate = txtCheckInDate.getText();
            String checkOutDate = txtCheckOutDate.getText();
            String ordersResp = NetworkService.getInstance().sendRequest("GET_MENU_ORDERS_BY_DATE_RANGE:" + guestName + ":" + checkInDate + ":" + checkOutDate);
            int unpaidTotal = 0;
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("[청구 내역]\n");
            
            if (ordersResp != null && ordersResp.startsWith("MENU_ORDERS_DATE:")) {
                String payload = ordersResp.substring("MENU_ORDERS_DATE:".length());
                if (!payload.isEmpty()) {
                    String[] orders = payload.split("\\|");
                    for (String ord : orders) {
                        String[] f = ord.split(",");
                        if (f.length >= 3) {
                            try {
                                String foodNames = f[0];
                                int price = Integer.parseInt(f[1]);
                                String payment = f[2];
                                String paymentDisplay = "Paid".equalsIgnoreCase(payment) ? "결제됨" : "미결제";
                                // 음식명들을 "/" 기준으로 분할하여 각각 줄바꿈으로 표시
                                String[] foods = foodNames.split("/");
                                for (String food : foods) {
                                    logMessage.append(String.format("%s(%s)\n", food.trim(), paymentDisplay));
                                }
                                if (!"Paid".equalsIgnoreCase(payment)) unpaidTotal += price;
                            } catch (NumberFormatException ex) {
                                // 무시
                            }
                        }
                    }
                    logMessage.append("-----------------------------------------\n");
                } else {
                    logMessage.append("청구 내역 없음\n");
                    logMessage.append("-----------------------------------------\n");
                }
            } else {
                logMessage.append("청구 내역 없음\n");
                logMessage.append("-----------------------------------------\n");
            }
            
            // Late Checkout 추가 요금 확인 (오전 11시 이후)
            java.time.LocalTime now = java.time.LocalTime.now();
            int lateCheckoutFee = 0;
            if (now.getHour() >= 11) {
                lateCheckoutFee = 100000;
                logMessage.append("Late Checkout 추가요금(100,000원)\n");
            }
            
            int totalAmount = unpaidTotal + lateCheckoutFee;
            logMessage.append("-----------------------------------------\n");
            logMessage.append(String.format("총 %d원\n", totalAmount));

            // 청구 내역을 보여주고 결제 여부를 묻기 (금액이 0이어도 항상 물음)
            String paymentMsg = logMessage.toString() + "\n결제하시겠습니까?";
            int confirm = JOptionPane.showConfirmDialog(this, paymentMsg, "결제 확인", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                // 사용자가 결제를 거부한 경우 아무런 상태 변경 없이 반환
                JOptionPane.showMessageDialog(this, "퇴실 처리가 취소되었습니다.");
                return;
            }

            // 결제를 진행하는 경우
            if (totalAmount > 0) {
                // 서버에 결제 요청: 이미 저장된 결제정보(payments.csv)를 사용하도록 요청
                // 프로토콜 토큰 수(전체 8개)를 맞추기 위해 빈 필드를 포함합니다.
                String payReq = String.format("UPDATE_PAYMENT:%s:Stored:::::%d", selectedResId, totalAmount);
                String payRes = NetworkService.getInstance().sendRequest(payReq);
                if ("PAYMENT_SUCCESS".equals(payRes)) {
                    // 결제 성공 시 퇴실 처리
                    String co = NetworkService.getInstance().sendRequest("CHECK_OUT:" + selectedResId);
                    if ("SUCCESS".equals(co)) {
                        JOptionPane.showMessageDialog(this, "퇴실 처리가 완료되었습니다.");
                        loadRoomData();
                    } else {
                        JOptionPane.showMessageDialog(this, "퇴실 처리 실패: " + co);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "결제 실패: " + payRes);
                }
            } else {
                // 청구 금액이 없는 경우 카드 입력 없이 바로 퇴실 처리
                String co = NetworkService.getInstance().sendRequest("CHECK_OUT:" + selectedResId);
                if ("SUCCESS".equals(co)) {
                    JOptionPane.showMessageDialog(this, "퇴실 처리가 완료되었습니다.");
                    loadRoomData();
                } else {
                    JOptionPane.showMessageDialog(this, "퇴실 처리 실패: " + co);
                }
            }

            return;
        }

        String targetStatus = "";
        switch(idx) {
            case 1: targetStatus = "Confirmed"; break;
            case 2: targetStatus = "CheckedIn"; break;
        }

        if (selectedResId == null || "-".equals(selectedResId)) {
            JOptionPane.showMessageDialog(this, "예약이 없는 방입니다.\n예약 상태를 변경하려면 먼저 예약을 생성해야 합니다.");
            return;
        }

        String res = NetworkService.getInstance().sendRequest("UPDATE_RESERVATION_STATUS:" + selectedResId + ":" + targetStatus);
        if ("UPDATE_SUCCESS".equals(res)) {
            JOptionPane.showMessageDialog(this, "예약 상태가 변경되었습니다.");
            loadRoomData();
        } else {
            JOptionPane.showMessageDialog(this, "변경 실패: " + res);
        }
    }

    private void clearGuestInfo() {
        txtGuestName.setText("");
        txtPhone.setText("");
        txtGuestNum.setText("");
        txtCheckInDate.setText("");
        txtCheckOutDate.setText("");
        txtGuestRequest.setText("");
    }
    
    private void handleSaveRequest(ActionEvent e) {
        if (selectedResId == null || "-".equals(selectedResId)) return;
        String newRequest = txtGuestRequest.getText().replace("\n", " ").replace(":", " ");
        String res = NetworkService.getInstance().sendRequest("UPDATE_GUEST_REQ:" + selectedResId + ":" + newRequest);
        if ("UPDATE_SUCCESS".equals(res)) {
            JOptionPane.showMessageDialog(this, "저장되었습니다.");
            loadRoomData();
        }
    }

    private void handleUpdateRoom(ActionEvent e) {
        if (selectedRoomNum == null) return;
        String desc = txtDesc.getText().replace("\n", " ").replace(":", " ");
        String req = String.format("UPDATE_ROOM:%s:%s:%s:%s:%s", 
                txtRoomNum.getText(), txtType.getText(), txtPrice.getText(), 
                txtCapacity.getText(), desc);
        String res = NetworkService.getInstance().sendRequest(req);
        if ("UPDATE_SUCCESS".equals(res)) {
            JOptionPane.showMessageDialog(this, "수정되었습니다.");
            loadRoomData();
        }
    }
}