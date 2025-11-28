package client.view;

import client.net.NetworkService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;

public class FutureOccupancyPanel extends JPanel {
    private final JTextField startDateField;
    private final JTextField endDateField;
    private final JButton predictButton;
    private final DefaultTableModel model;
    private final JTable table;
    private final JLabel infoLabel;
    private final JLabel avgLabel = new JLabel();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 미래 점유율 패널
     * - 상단: 기간 입력, 예측 버튼, 안내 메시지
     * - 상단 카드: 기간 평균 점유율 강조
     * - 표: 날짜별, 타입별, 평균 점유율 표시
     * - 서버와 통신: GET_FUTURE_OCCUPANCY 요청/응답 파싱
     */
    public FutureOccupancyPanel() {
        // 표: 날짜, 타입별, 평균 점유율
        model = new DefaultTableModel(new String[]{"날짜", "스탠다드 점유율", "디럭스 점유율", "스위트 점유율", "평균 점유율"}, 0);
        table = new JTable(model);

        setLayout(new BorderLayout());
        // 상단 입력 패널: 기간 입력, 예측 버튼, 안내 메시지
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        topPanel.add(new JLabel("시작일 (yyyy-MM-dd): "));
        startDateField = new JTextField(10); // 시작일 입력
        topPanel.add(startDateField);
        topPanel.add(new JLabel("~ 종료일 (yyyy-MM-dd): "));
        endDateField = new JTextField(10);   // 종료일 입력
        topPanel.add(endDateField);
        predictButton = new JButton("예측"); // 예측 버튼
        topPanel.add(predictButton);
        infoLabel = new JLabel();            // 안내 메시지
        topPanel.add(infoLabel);
        JButton printButton = new JButton("인쇄");
        topPanel.add(printButton);

        // 인쇄 기능: 요약+표 전체 인쇄 (Printable 구현)
        printButton.addActionListener(e -> {
            try {
                java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
                job.setJobName("미래 점유율 보고서");
                job.setPrintable((graphics, pageFormat, pageIndex) -> {
                    if (pageIndex > 0) return java.awt.print.Printable.NO_SUCH_PAGE;
                    Graphics2D g2 = (Graphics2D) graphics;
                    g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                    int y = 20;
                    g2.setFont(new Font("맑은 고딕", Font.BOLD, 16));
                    g2.drawString("미래 점유율 보고서", 20, y);
                    y += 30;
                    g2.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
                    g2.drawString(avgLabel.getText(), 20, y);
                    y += 30;
                    // 표 인쇄 (JTable.print() 활용)
                    g2.translate(0, y);
                    table.print();
                    return java.awt.print.Printable.PAGE_EXISTS;
                });
                if (job.printDialog()) {
                    job.print();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "인쇄 실패: " + ex.getMessage());
            }
        });

        // 상단 요약 카드: 기간 평균 점유율 강조
        JPanel cards = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        avgLabel.setText("기간 평균 점유율: 0%");
        avgLabel.setFont(avgLabel.getFont().deriveFont(14f));
        avgLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220,220,220)),
            BorderFactory.createEmptyBorder(8,12,8,12)));
        cards.add(avgLabel);

        // 상단 전체 래핑
        JPanel northWrap = new JPanel(new BorderLayout());
        northWrap.add(topPanel, BorderLayout.NORTH);
        northWrap.add(cards, BorderLayout.CENTER);
        add(northWrap, BorderLayout.NORTH);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        add(tableScroll, BorderLayout.CENTER);

        setDefaultDates(); // 기본 시작일/종료일 세팅
        predictButton.addActionListener(this::handlePredict); // 예측 버튼 이벤트
    }

    private void setDefaultDates() {
        // 기본 시작일: 2025-10-01, 종료일: 오늘+7일
        startDateField.setText("2025-10-01");
        endDateField.setText(LocalDate.now().plusDays(7).format(DATE_FORMAT));
    }

    private void handlePredict(ActionEvent ignored) {
        // 날짜 입력 검증 및 서버 요청
        String start = startDateField.getText().trim();
        String end = endDateField.getText().trim();
        try {
            LocalDate startDate = LocalDate.parse(start, DATE_FORMAT);
            LocalDate endDate = LocalDate.parse(end, DATE_FORMAT);
            if (endDate.isBefore(startDate)) {
                infoLabel.setText("종료일이 시작일보다 빠를 수 없습니다.");
                return;
            }
            infoLabel.setText("");
            fetchAndFillTable(start, end); // 서버에서 데이터 받아 표 채움
        } catch (Exception ex) {
            infoLabel.setText("날짜 형식 오류");
        }
    }

    private void fetchAndFillTable(String start, String end) {
        // 표 초기화 및 서버 데이터 요청/파싱
        model.setRowCount(0);
        avgLabel.setText("");
        String req = "GET_FUTURE_OCCUPANCY:" + start + ":" + end;
        String resp = NetworkService.getInstance().sendRequest(req);
        if (resp == null || !resp.startsWith("FUTURE_OCCUPANCY:")) {
            infoLabel.setText("서버 오류 또는 데이터 없음");
            return;
        }
        String data = resp.substring("FUTURE_OCCUPANCY:".length());
        if (data.trim().isEmpty()) return;
        String[] parts = data.split("\\|");
        if (parts.length >= 2) {
            avgLabel.setText("기간 평균 점유율: " + parts[0] + "%"); // 상단 카드에 평균 표시
            for (String entry : parts[1].split(";")) {
                String[] cols = entry.split(",");
                if (cols.length == 5) {
                    model.addRow(cols); // 표에 한 줄씩 추가
                }
                }
        }
    }
}
