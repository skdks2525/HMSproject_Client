package client.view;

import client.net.NetworkService;
import com.toedter.calendar.JDateChooser; // 달력 라이브러리
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        // 1. 상단 패널 (제목 + [날짜선택])
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Room Management");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        lblTitle.setForeground(new Color(0, 102, 51)); 
        
        // [복구됨] 날짜 선택 패널
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

        // 2. 좌측: 객실 현황판
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

        // 3. 우측: 상세 정보 패널
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(400, 0));
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        rightPanel.setBackground(Color.WHITE);

        // 3-1. 객실 정보 (기존 동일)
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
            "상태 선택...", 
            "예약 확정", 
            "입실 완료", 
            "퇴실/청소", 
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

    // --- [기능] 상태 강제 변경 핸들러 ---
    private void handleForceStatusChange(ActionEvent e) {
        if (selectedRoomNum == null) return;
        
        int idx = cmbForceStatus.getSelectedIndex();
        if (idx <= 0) return; // "선택..." 일 때 무시

        String targetStatus = "";
        switch(idx) {
            case 1: targetStatus = "Confirmed"; break;
            case 2: targetStatus = "CheckedIn"; break;
            case 3: targetStatus = "CheckedOut"; break;
            case 4: targetStatus = "Empty"; break;
        }

        // 빈 방으로 만들기 = 예약 삭제
        if ("Empty".equals(targetStatus)) {
            if (selectedResId == null || "-".equals(selectedResId)) {
                JOptionPane.showMessageDialog(this, "이미 빈 방입니다.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "정말 방을 비우시겠습니까?\n(현재 예약이 삭제됩니다)", "경고", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                NetworkService.getInstance().sendRequest("DELETE_RESERVATION:" + selectedResId);
                loadRoomData();
            }
        } else {
            // 상태 변경 (예약이 있어야 가능)
            if (selectedResId == null || "-".equals(selectedResId)) {
                JOptionPane.showMessageDialog(this, "예약이 없는 방의 상태를 바꿀 수 없습니다.\n먼저 예약을 생성해주세요.");
                return;
            }
            // 서버 요청: UPDATE_RESERVATION_STATUS:예약ID:새상태
            String res = NetworkService.getInstance().sendRequest("UPDATE_RESERVATION_STATUS:" + selectedResId + ":" + targetStatus);
            
            if ("UPDATE_SUCCESS".equals(res)) {
                JOptionPane.showMessageDialog(this, "상태가 변경되었습니다.");
                loadRoomData();
            } else {
                JOptionPane.showMessageDialog(this, "변경 실패: " + res);
            }
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