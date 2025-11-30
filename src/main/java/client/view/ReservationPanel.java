package client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.toedter.calendar.JDateChooser;

import client.net.NetworkService;

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

    // 객실 선택 패널
private JPanel createRoomSelectionPanel() {
        // 테투리
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createTitledBorder("객실 선택"));

        // 카드 객체
        roomListPanel = new JPanel(); 
        roomListPanel.setLayout(new BoxLayout(roomListPanel, BoxLayout.Y_AXIS));
        roomListPanel.setBackground(Color.WHITE);
        
        // 스크롤
        JScrollPane scrollPane = new JScrollPane(roomListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setPreferredSize(new Dimension(850, 400)); 
        container.add(scrollPane, BorderLayout.CENTER); //스크롤 추가
        roomGroup = new ButtonGroup(); 
        return container; // 스크롤이 포함된 컨테이너 반환
    }

    // 서버에서 객실목록 가져오는 역할
    private void loadRoomDataFromServer() {
        Date in = dateCheckIn.getDate();
        Date out = dateCheckOut.getDate();
        if (in == null || out == null) return;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sIn = sdf.format(in);
        String sOut = sdf.format(out);

        // 기존 카드 싹 지우기
        roomListPanel.removeAll();
        roomGroup = new ButtonGroup(); // 그룹도 초기화

        // 서버 요청 (CHECK_ALL_ROOM_STATUS)
        String response = NetworkService.getInstance().sendRequest("CHECK_ALL_ROOM_STATUS:" + sIn + ":" + sOut);
        
        if (response != null && response.startsWith("ROOM_STATUS_LIST:")) {
            String data = response.substring("ROOM_STATUS_LIST:".length());
            if (data.isEmpty()) return;

            String[] rooms = data.split("\\|"); 
            
            for (String r : rooms) {
                String[] info = r.split(",");
                // 포맷: 번호,타입,가격,인원,설명,상태(BOOKED/AVAILABLE)
                if (info.length >= 6) {
                    JPanel card = createRoomCard(info);
                    roomListPanel.add(card);
                    roomListPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 간격
                }
            }
        } else {
            JLabel lblErr = new JLabel("서버 통신 오류 또는 데이터가 없습니다.");
            roomListPanel.add(lblErr);
        }
        
        roomListPanel.revalidate();
        roomListPanel.repaint();
    }

    // 객실 카드
    private JPanel createRoomCard(String[] info) {
        String roomNum = info[0];
        String type = info[1];
        String price = info[2];
        String capacity = info[3];
        String desc = info[4];
        String status = info[5]; // BOOKED or AVAILABLE

        boolean isBooked = "BOOKED".equals(status);

        JPanel card = new JPanel(new BorderLayout(15, 15));
        // 예약된 방은 회색 배경
        card.setBackground(isBooked ? new Color(240, 240, 240) : new Color(245, 255, 245)); 
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)));
        card.setMaximumSize(new Dimension(1000, 100)); 
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 왼쪽: 아이콘 (방번호)
        JLabel lblIcon = new JLabel(roomNum + "호", SwingConstants.CENTER);
        lblIcon.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        lblIcon.setPreferredSize(new Dimension(120, 70));
        lblIcon.setOpaque(true);
        // 예약되면 회색 아이콘, 가능하면 파란 아이콘
        lblIcon.setBackground(isBooked ? Color.LIGHT_GRAY : new Color(100, 150, 255));
        lblIcon.setForeground(Color.WHITE);
        card.add(lblIcon, BorderLayout.WEST);

        // 중앙: 정보
        JPanel infoP = new JPanel(new GridLayout(2, 1));
        infoP.setOpaque(false);
        
        JLabel lblTitle = new JLabel(type + " Room (" + price + "원) | 최대 인원수: " + capacity + "명");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        
        // 상태 메시지
        String statusText = isBooked ? "이미 예약된 객실입니다." : " " + desc;
        JLabel lblDesc = new JLabel(statusText);
        lblDesc.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        if (isBooked) lblDesc.setForeground(Color.black);
        else lblDesc.setForeground(Color.DARK_GRAY);

        infoP.add(lblTitle);
        infoP.add(lblDesc);
        card.add(infoP, BorderLayout.CENTER);

        // 오른쪽: 선택 버튼
        JRadioButton rbtn = new JRadioButton("선택");
        rbtn.setOpaque(false);
        // 예약된 방은 선택 불가
        rbtn.setEnabled(!isBooked); 
        
        // ActionCommand에 "방 번호"를 저장
        rbtn.setActionCommand(roomNum); 
        roomGroup.add(rbtn);
        
        JPanel btnP = new JPanel(new GridBagLayout());
        btnP.setOpaque(false);
        btnP.add(rbtn);
        
        card.add(btnP, BorderLayout.EAST);

        return card;
    }

    // 예약 확정
private void handleReserveAction(ActionEvent e) {
        ButtonModel selectedModel = roomGroup.getSelection();
        if (selectedModel == null) {
            JOptionPane.showMessageDialog(this, "객실을 선택해주세요.");
            return;
        }
        // 방번호 가져옴
        String selectedRoomNum = selectedModel.getActionCommand(); 

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String in = sdf.format(dateCheckIn.getDate());
        String out = sdf.format(dateCheckOut.getDate());
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        int guests = (int) spinGuests.getValue();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "필수 정보를 입력해주세요.");
            return;
        }
        String reqText = "없음"; 

        String request = String.format("ADD_RESERVATION:%s:%s:%s:%s:%d:%s:%s", 
                selectedRoomNum, name, in, out, guests, phone, reqText);
        
        String response = NetworkService.getInstance().sendRequest(request);

        if (response.startsWith("RESERVE_SUCCESS")) {
            JOptionPane.showMessageDialog(this, "예약 완료! (" + selectedRoomNum + "호)");
            txtName.setText("");
            txtPhone.setText("010-0000-0000");
            roomGroup.clearSelection();
            // 예약 후 목록 갱신 (해당 방을 회색으로 만들기 위해)
            loadRoomDataFromServer(); 
        } else {
            JOptionPane.showMessageDialog(this, "예약 실패: " + response, "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}