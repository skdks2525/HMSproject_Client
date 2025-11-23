package client.view;

import client.net.NetworkService;
import com.toedter.calendar.JDateChooser; 

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ReservationPanel extends JPanel {

    //입력받는 사용자 정보 5개(아이디는 자동)
    private JDateChooser dateCheckIn, dateCheckOut;
    private JTextField txtName, txtPhone;
    private JSpinner spinGuests;
    private String userId; // 얘는 자동으로 입력받음
    
    private ButtonGroup roomGroup; 
    
    
    // 객실 카드 패널
    private JPanel roomListPanel; 

    public ReservationPanel(String userId) {
        this.userId = userId;
        initComponents();
        // 화면 생성 후 서버에서 객실 목록 불러옴
        loadRoomDataFromServer();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        // 메인 패널(스크롤 기능도 넣어둠)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // 타이틀
        JLabel lblTitle = new JLabel("객실 예약");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 사용자 정보 입력 패널 <- 여기에 Rooms.csv의 정보가 띄워짐
        mainPanel.add(createInfoPanel()); 
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 객실 선택 패널
        mainPanel.add(createRoomSelectionPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 예약 버튼
        JButton btnReserve = new JButton("객실 예약");
        btnReserve.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        btnReserve.setBackground(new Color(0, 0, 0)); // 진한 초록색
        btnReserve.setForeground(Color.WHITE);
        btnReserve.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReserve.setMaximumSize(new Dimension(200, 50));
        btnReserve.addActionListener(this::handleReserveAction);
        
        mainPanel.add(btnReserve);
    } 

    // -사용자 정보 입력받는 패널 상세-
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("예약 정보 입력"));
        panel.setMaximumSize(new Dimension(1000, 150)); 

        // 입실 날짜 입력받기
        JPanel p1 = new JPanel(new BorderLayout());
        p1.setBackground(Color.WHITE);
        p1.add(new JLabel("입실 날짜"), BorderLayout.NORTH);
        dateCheckIn = new JDateChooser();
        dateCheckIn.setDateFormatString("yyyy-MM-dd");
        dateCheckIn.setDate(new Date());
        p1.add(dateCheckIn, BorderLayout.CENTER);
        panel.add(p1);

        // 퇴실날짜 입력받기
        JPanel p2 = new JPanel(new BorderLayout());
        p2.setBackground(Color.WHITE);
        p2.add(new JLabel("퇴실 날짜"), BorderLayout.NORTH);
        dateCheckOut = new JDateChooser();
        dateCheckOut.setDateFormatString("yyyy-MM-dd");
        dateCheckOut.setDate(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24))); 
        p2.add(dateCheckOut, BorderLayout.CENTER);
        panel.add(p2);

        // 인원 수 입력받음
        JPanel p3 = new JPanel(new BorderLayout());
        p3.setBackground(Color.WHITE);
        p3.add(new JLabel("인원수"), BorderLayout.NORTH);
        spinGuests = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        p3.add(spinGuests, BorderLayout.CENTER);
        panel.add(p3);

        // 예약자 성함 받기
        JPanel p4 = new JPanel(new BorderLayout());
        p4.setBackground(Color.WHITE);
        p4.add(new JLabel("예약자 성함"), BorderLayout.NORTH);
        txtName = new JTextField();
        p4.add(txtName, BorderLayout.CENTER);
        panel.add(p4);

        // 전화번호도 받기
        JPanel p5 = new JPanel(new BorderLayout());
        p5.setBackground(Color.WHITE);
        p5.add(new JLabel("전화번호"), BorderLayout.NORTH);
        txtPhone = new JTextField("010-0000-0000");
        p5.add(txtPhone, BorderLayout.CENTER);
        panel.add(p5);
        
         // ID는 자동 입력되고 비활성화 됨
        JPanel p6 = new JPanel(new BorderLayout());
        p6.setBackground(Color.WHITE);
        p6.add(new JLabel("예약자 ID"), BorderLayout.NORTH); // 라벨 변경      
        txtName = new JTextField(userId); 
        txtName.setEditable(false); // 수정 불가
        txtName.setBackground(new Color(240, 240, 240)); // 회색 배경으로 표시
        
        p6.add(txtName, BorderLayout.CENTER);
        panel.add(p6);

        return panel;
    }

    // --- [객실 선택 패널; 아직은 껍데기] ---
    private JPanel createRoomSelectionPanel() {
        // roomListPanel을 초기화하여 나중에 카드를 추가할 수 있게 함
        roomListPanel = new JPanel();
        roomListPanel.setLayout(new BoxLayout(roomListPanel, BoxLayout.Y_AXIS));
        roomListPanel.setBackground(Color.WHITE);
        roomListPanel.setBorder(BorderFactory.createTitledBorder("객실 선택"));
        
        roomGroup = new ButtonGroup(); 
        
        return roomListPanel;
    }

    // 서버에서 객실목록 가져오는 역할
    private void loadRoomDataFromServer() {
        String response = NetworkService.getInstance().sendRequest("GET_ALL_ROOMS"); //서버에 목록 요청
        
        if (response != null && response.startsWith("ROOM_LIST:")) {
            String data = response.substring("ROOM_LIST:".length());
            if (data.isEmpty()) return;

            String[] rooms = data.split("/");
            
            // 중복 타입 제거 (같은 타입의 방이 여러 개여도 카드는 종류별로 하나만 표시)
            Set<String> addedTypes = new HashSet<>(); 

            for (String r : rooms) {
                // 데이터 포맷: 번호, 타입, 가격, 인원, 설명
                String[] info = r.split(",");
                if (info.length >= 5) {
                    String type = info[1];
                    String price = info[2];
                    String desc = info[4]; // 설명

                    // 이미 화면에 추가한 타입이면 건너뜀
                    if (addedTypes.contains(type)) continue; 

                    // 카드 생성 및 추가 (ActionCommand로 '타입'을 저장)
                    JPanel card = createRoomCard(type + " Room", price + "원", desc, type);
                    roomListPanel.add(card);
                    roomListPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                    
                    addedTypes.add(type);
                }
            }
            // 화면 갱신
            roomListPanel.revalidate();
            roomListPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "객실 정보를 불러오지 못했습니다.\n서버 연결을 확인해주세요.", "통신 오류", JOptionPane.WARNING_MESSAGE);
        }
    }

    // 객실 카드
    private JPanel createRoomCard(String title, String price, String options, String actionCommand) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)));
        card.setMaximumSize(new Dimension(1000, 120)); 

        // 아이콘 (이미지 대신 텍스트 박스)
        JLabel lblIcon = new JLabel("ROOM", SwingConstants.CENTER);
        lblIcon.setPreferredSize(new Dimension(120, 80));
        lblIcon.setOpaque(true);
        lblIcon.setBackground(Color.GRAY);
        lblIcon.setForeground(Color.WHITE);
        card.add(lblIcon, BorderLayout.WEST);

        // 정보 (타입, 가격, 설명)
        JPanel infoP = new JPanel(new GridLayout(3, 1));
        infoP.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        
        JLabel lblPrice = new JLabel(price + " / 1박");
        lblPrice.setForeground(new Color(255, 102, 0)); // 주황색 강조

        JLabel lblOpt = new JLabel(options);
        lblOpt.setForeground(Color.DARK_GRAY);

        infoP.add(lblTitle);
        infoP.add(lblPrice);
        infoP.add(lblOpt);
        
        card.add(infoP, BorderLayout.CENTER);

        // 선택 버튼
        JRadioButton rbtn = new JRadioButton("선택");
        rbtn.setOpaque(false);
        rbtn.setActionCommand(actionCommand); // "Standard", "Deluxe" 등이 저장됨
        roomGroup.add(rbtn);
        
        JPanel btnP = new JPanel(new GridBagLayout());
        btnP.setOpaque(false);
        btnP.add(rbtn);
        card.add(btnP, BorderLayout.EAST);

        return card;
    }

    // 예약 확정
    private void handleReserveAction(ActionEvent e) {
        // 객실 선택 여부 확인
        ButtonModel selectedModel = roomGroup.getSelection();
        if (selectedModel == null) {
            JOptionPane.showMessageDialog(this, "객실을 선택해주세요.");
            return;
        }
        String roomType = selectedModel.getActionCommand(); // 선택한 객실의 타입

        // 날짜 확인
        Date dateIn = dateCheckIn.getDate();
        Date dateOut = dateCheckOut.getDate();
        if (dateIn == null || dateOut == null) {
            JOptionPane.showMessageDialog(this, "입실/퇴실 날짜를 선택해주세요.");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String in = sdf.format(dateIn);
        String out = sdf.format(dateOut);

        // 입력값 확인
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        int guests = (int) spinGuests.getValue();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "예약자 성함과 전화번호를 입력해주세요.");
            return;
        }

        // 서버 전송 (번호 대신 '타입'을 보내면, 서버가 알아서 빈 방을 배정)
        String request = String.format("ADD_RESERVATION:%s:%s:%s:%s:%d:%s", 
                roomType, name, in, out, guests, phone);
        
        String response = NetworkService.getInstance().sendRequest(request);

        if (response != null && response.startsWith("RESERVE_SUCCESS")) {
            String assignedRoom = response.contains(":") ? response.split(":")[1] : "배정중";
            
            JOptionPane.showMessageDialog(this, 
                    String.format("예약이 완료되었습니다!\n[%s] %s호\n%s ~ %s", roomType, assignedRoom, in, out));
            
            txtName.setText("");
            txtPhone.setText("010-0000-0000");
            roomGroup.clearSelection();
        } else {
            JOptionPane.showMessageDialog(this, "예약 실패: " + response, "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}