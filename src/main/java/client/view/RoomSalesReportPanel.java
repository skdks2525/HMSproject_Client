package client.view;

import client.net.NetworkService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RoomSalesReportPanel extends JPanel {
    /**
     * 객실 매출 리포트 패널
     * - 상단: 시작/종료일 입력, 조회 버튼, 상태 메시지
     * - 요약 카드: 총 매출, 일 평균 매출
     * - 중앙: 커스텀 그래프 패널 (스무딩된 선, 그리드, 축 레이블)
     *
     * 이 패널은 간단한 텍스트 기반 네트워크 프로토콜을 사용합니다:
     * 클라이언트 -> 서버: `GET_ROOM_SALES:yyyy-MM-dd:yyyy-MM-dd`
     * 서버 -> 클라이언트: `ROOM_SALES:yyyy-MM-dd=amount,yyyy-MM-dd=amount,...`
     */
    private final JTextField startDateField;
    private final JTextField endDateField;
    private final JButton searchButton;
    private final SalesGraphPanel graphPanel;
    private final JLabel infoLabel;
    // 상단 요약 카드
    private final JLabel totalSalesLabel;
    private final JLabel avgSalesLabel;
    private final JLabel titleLabel;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 객실 매출 리포트 패널
     * - 상단: 기간 입력, 조회 버튼, 안내 메시지
     * - 상단 카드: 총 매출, 일 평균 매출 강조
     * - 중앙: 매출 그래프(스무딩 곡선, 그리드, 축 레이블)
     * - 서버와 통신: GET_ROOM_SALES 요청/응답 파싱
     */
    public RoomSalesReportPanel() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        Color navy = new Color(10, 48, 87);
        Font labelFont = new Font("맑은 고딕", Font.BOLD, 16);
        Font cardFont = new Font("맑은 고딕", Font.BOLD, 15);
        Font btnFont = new Font("맑은 고딕", Font.BOLD, 15);

        //검색 카드
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        topPanel.setBackground(Color.WHITE);
        JLabel lblStart = new JLabel("시작일 (yyyy-MM-dd): ");
        lblStart.setFont(labelFont);
        lblStart.setForeground(navy);
        topPanel.add(lblStart);
        startDateField = new JTextField(10);
        startDateField.setFont(labelFont);
        topPanel.add(startDateField);
        JLabel lblEnd = new JLabel("~ 종료일 (yyyy-MM-dd): ");
        lblEnd.setFont(labelFont);
        lblEnd.setForeground(navy);
        topPanel.add(lblEnd);
        endDateField = new JTextField(10);
        endDateField.setFont(labelFont);
        topPanel.add(endDateField);
        searchButton = new JButton("조회");
        searchButton.setBackground(navy);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(btnFont);
        searchButton.setFocusPainted(false);
        topPanel.add(searchButton);
        infoLabel = new JLabel();
        infoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(200, 0, 0));
        topPanel.add(infoLabel);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        titleLabel = new JLabel("객실 매출 그래프");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        titleLabel.setForeground(navy);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel cards = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        cards.setBackground(Color.WHITE);
        totalSalesLabel = new JLabel("총 매출: ₩0");
        totalSalesLabel.setFont(cardFont);
        totalSalesLabel.setForeground(navy);
        totalSalesLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(navy, 1),
            BorderFactory.createEmptyBorder(10,18,10,18)));
        avgSalesLabel = new JLabel("일 평균 매출: ₩0");
        avgSalesLabel.setFont(cardFont);
        avgSalesLabel.setForeground(navy);
        avgSalesLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(navy, 1),
            BorderFactory.createEmptyBorder(10,18,10,18)));
        cards.add(totalSalesLabel);
        cards.add(avgSalesLabel);
        headerPanel.add(cards, BorderLayout.CENTER);

        JPanel northWrap = new JPanel(new BorderLayout());
        northWrap.setBackground(Color.WHITE);
        northWrap.add(topPanel, BorderLayout.NORTH);
        northWrap.add(headerPanel, BorderLayout.CENTER);
        add(northWrap, BorderLayout.PAGE_START);

        graphPanel = new SalesGraphPanel();
        graphPanel.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
        graphPanel.setBackground(Color.WHITE);
        add(graphPanel, BorderLayout.CENTER);

        searchButton.addActionListener(this::handleSearch);
        setDefaultDates();
    }

    private void setDefaultDates() {
        // 기본 시작일: 2025-10-01, 종료일: 어제
        startDateField.setText("2025-10-01");
        endDateField.setText(LocalDate.now().minusDays(1).format(DATE_FORMAT));
    }

    @SuppressWarnings("unused")
    private void handleSearch(ActionEvent ignored) {
        // 사용자 입력 검증 및 서버 요청을 순서대로 처리합니다.
        // - 날짜 포맷 검증
        // - 종료일이 어제(오늘-1)보다 이후인지 확인
        // - 시작일 <= 종료일 검증
        // 검증이 통과하면 fetchAndDrawGraph를 호출해 서버에서 집계를 받아 그래프를 갱신합니다.
        String start = startDateField.getText().trim();
        String end = endDateField.getText().trim();
        LocalDate today = LocalDate.now();
        try {
            LocalDate startDate = LocalDate.parse(start, DATE_FORMAT);
            LocalDate endDate = LocalDate.parse(end, DATE_FORMAT);
            if (endDate.isAfter(today.minusDays(1))) {
                infoLabel.setText("종료일은 어제까지 선택 가능합니다.");
                return;
            }
            if (endDate.isBefore(startDate)) {
                infoLabel.setText("종료일이 시작일보다 빠를 수 없습니다.");
                return;
            }
            infoLabel.setText("");
            fetchAndDrawGraph(start, end);
        } catch (Exception ex) {
            infoLabel.setText("날짜 형식 오류");
        }
    }

    private void fetchAndDrawGraph(String start, String end) {
        // 서버에 GET_ROOM_SALES 요청을 전송하고, 응답을 파싱해 내부 Map<LocalDate,Integer>로 변환합니다.
        // 응답 예시: ROOM_SALES:2025-10-01=220000,2025-10-02=440000,...
        String req = "GET_ROOM_SALES:" + start + ":" + end;
        String resp = NetworkService.getInstance().sendRequest(req);
        if (resp == null || !resp.startsWith("ROOM_SALES:")) {
            infoLabel.setText("서버 오류 또는 데이터 없음");
            graphPanel.setSalesData(Collections.emptyMap(), null, null);
            return;
        }
        String data = resp.substring("ROOM_SALES:".length());
        Map<LocalDate, Integer> sales = new LinkedHashMap<>();
        LocalDate first = null, last = null;
        int max = 0;
        long total = 0L;
        if (!data.trim().isEmpty()) {
            for (String entry : data.split(",")) {
                String[] pair = entry.split("=");
                if (pair.length == 2) {
                    LocalDate date = LocalDate.parse(pair[0], DATE_FORMAT);
                    int value = Integer.parseInt(pair[1]);
                    sales.put(date, value);
                    if (first == null) first = date;
                    last = date;
                    if (value > max) max = value;
                    total += value;
                }
            }
        }
        // 총합/일평균 업데이트
        if (!sales.isEmpty()) {
            long avg = total / sales.size();
            totalSalesLabel.setText("총 매출: ₩" + total);
            avgSalesLabel.setText("일 평균 매출: ₩" + avg);
        } else {
            totalSalesLabel.setText("총 매출: ₩0");
            avgSalesLabel.setText("일 평균 매출: ₩0");
        }
        graphPanel.setSalesData(sales, first, last);
    }

    static class SalesGraphPanel extends JPanel {
        private Map<LocalDate, Integer> salesData = Collections.emptyMap();
        private int maxValue = 0;

        public void setSalesData(Map<LocalDate, Integer> data, LocalDate first, LocalDate last) {
            // 외부에서 받은 날짜->매출 맵을 저장하고, 내부에서 사용할 최대값을 계산합니다.
            // repaint() 호출로 Swing이 그래프를 다시 그리게 합니다.
            this.salesData = data;
            this.maxValue = data.values().stream().max(Integer::compareTo).orElse(0);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (salesData == null || salesData.isEmpty()) {
                g.drawString("데이터 없음", getWidth() / 2 - 30, getHeight() / 2);
                return;
            }
            // Graphics2D로 그리기를 수행합니다. 안티앨리어싱을 설정해 선을 부드럽게 표현합니다.
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int marginLeft = 70;
            int marginTop = 40;
            int marginBottom = 60;
            int w = getWidth() - marginLeft - 40;
            int h = getHeight() - marginTop - marginBottom;
            int n = salesData.size();
            if (n < 2) {
                g2.drawString("데이터가 부족합니다.", getWidth() / 2 - 40, getHeight() / 2);
                return;
            }
            int[] xs = new int[n];
            int[] ys = new int[n];
            double[] vals = new double[n];
            int i = 0;
            for (Map.Entry<LocalDate, Integer> e : salesData.entrySet()) {
                vals[i] = e.getValue();
                xs[i] = marginLeft + (int) ((long) w * i / (n - 1));
                i++;
            }

            // 간단한 이동평균으로 스무딩 (인접값 평균)
            // 이유: 원본 값이 너무 급격히 변하면 그래프가 뾰족하게 튀어 보이므로
            // 이웃값과 평균하여 전반적으로 부드러운 곡선을 만듭니다.
            double[] smooth = new double[n];
            for (i = 0; i < n; i++) {
                double prev = i > 0 ? vals[i - 1] : vals[i];
                double next = i < n - 1 ? vals[i + 1] : vals[i];
                smooth[i] = (prev + vals[i] + next) / 3.0;
            }
            for (i = 0; i < n; i++) {
                ys[i] = marginTop + h - (int) ((long) h * smooth[i] / (maxValue == 0 ? 1 : maxValue));
            }

            // 배경 그리드 (수평 4줄, 수직 N구간)
            // 수평선은 y값 범위를 4등분하여 표시하고, 각 레이블은 그 위치의 예상 값을 보여줍니다.
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(new Color(220, 220, 220));
            for (int row = 0; row <= 4; row++) {
                int yy = marginTop + (int) ((long) h * row / 4);
                Stroke old = g2.getStroke();
                float[] dash = {4f, 4f};
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 4f, dash, 0f));
                g2.drawLine(marginLeft, yy, marginLeft + w, yy);
                g2.setStroke(old);
                // y 레이블
                int labelVal = (int) (maxValue * (4 - row) / 4.0);
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(String.valueOf(labelVal), 10, yy + 5);
                g2.setColor(new Color(220, 220, 220));
            }

            // 수직 그리드와 x 라벨 (최대 7개)
            // x 라벨은 날짜 키에서 추출한 월/일(MM/dd) 형식으로 표시합니다.
            int ticks = Math.min(7, n);
            for (int t = 0; t < ticks; t++) {
                int idx = (int) Math.round((n - 1) * (t / (double) (ticks - 1)));
                int x = xs[idx];
                Stroke old = g2.getStroke();
                float[] dash = {4f, 4f};
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 4f, dash, 0f));
                g2.drawLine(x, marginTop, x, marginTop + h);
                g2.setStroke(old);
                g2.setColor(Color.DARK_GRAY);
                // 날짜 라벨은 MM/dd 또는 M/d
                LocalDate date = (LocalDate) salesData.keySet().toArray()[idx];
                String lbl = String.format("%02d/%02d", date.getMonthValue(), date.getDayOfMonth());
                g2.drawString(lbl, x - 20, marginTop + h + 20);
                g2.setColor(new Color(220, 220, 220));
            }

            // 선 그리기 (스무딩된 값으로 자연스럽게 연결)
            // GeneralPath 대신 단순한 lineTo를 사용해 점들을 연결합니다.
            // CAP_ROUND, JOIN_ROUND 스타일로 모서리를 부드럽게 처리합니다.
            g2.setColor(new Color(30, 120, 220));
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
            path.moveTo(xs[0], ys[0]);
            for (i = 1; i < n; i++) {
                path.lineTo(xs[i], ys[i]);
            }
            g2.draw(path);
        }
    }
}
