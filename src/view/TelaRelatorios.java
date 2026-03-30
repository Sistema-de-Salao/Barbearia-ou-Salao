package view;

import controller.BarberController;
import model.Agendamento;
import model.Cliente;
import model.Profissional;
import model.Servico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;

public class TelaRelatorios extends JPanel {
    private final BarberController controller;
    private final Runnable onBackToMain;
    private JSpinner spInicio, spFim;
    private JRadioButton rbDiario, rbGeral;
    private JTable table;
    private JLabel lblTotalAgend, lblConcluidos, lblCancelados, lblPendentes;
    private Timer timerFaturamento;
    private JLabel lblFaturamentoHoje;

    public TelaRelatorios(BarberController controller, Runnable onBackToMain) {
        this.controller = controller;
        this.onBackToMain = onBackToMain;
        setLayout(new BorderLayout());
        setBackground(BarberStyle.COLOR_SECONDARY);
        initComponents();
    }

    private void initComponents() {
        // Cabeçalho
        JPanel header = BarberStyle.createHeader("Relatórios e Faturamento");
        JButton btnVoltar = new JButton("Voltar ao Menu");
        BarberStyle.applyBackButtonStyle(btnVoltar);
        btnVoltar.addActionListener(e -> onBackToMain.run());
        header.add(btnVoltar, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Painel Central
        JPanel centerPanel = new JPanel(new BorderLayout(10, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.setBackground(BarberStyle.COLOR_SECONDARY);

        // Painel de Filtros: campos à esquerda, botão à direita
        JPanel pnlFiltros = new JPanel(new BorderLayout(15, 0));
        pnlFiltros.setBackground(BarberStyle.COLOR_WHITE);
        pnlFiltros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BarberStyle.COLOR_PRIMARY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel pnlTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTipo.setOpaque(false);
        rbDiario = new JRadioButton("Diário", true);
        rbGeral = new JRadioButton("Geral");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbDiario);
        bg.add(rbGeral);
        pnlTipo.add(rbDiario);
        pnlTipo.add(rbGeral);
        pnlFiltros.add(pnlTipo, BorderLayout.NORTH);

        JPanel pnlInputs = new JPanel(new GridLayout(2, 2, 10, 12));
        pnlInputs.setOpaque(false);

        JLabel lblInicio = new JLabel("Data Início:");
        lblInicio.setFont(BarberStyle.FONT_LABEL);
        lblInicio.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlInputs.add(lblInicio);
        spInicio = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor edInicio = new JSpinner.DateEditor(spInicio, "dd/MM/yyyy");
        spInicio.setEditor(edInicio);
        spInicio.setValue(java.util.Date.from(LocalDate.now().withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        pnlInputs.add(spInicio);

        JLabel lblFim = new JLabel("Data Fim:");
        lblFim.setFont(BarberStyle.FONT_LABEL);
        lblFim.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlInputs.add(lblFim);
        spFim = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor edFim = new JSpinner.DateEditor(spFim, "dd/MM/yyyy");
        spFim.setEditor(edFim);
        spFim.setValue(java.util.Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        pnlInputs.add(spFim);

        pnlFiltros.add(pnlInputs, BorderLayout.CENTER);

        // Botão à direita dos campos
        JPanel pnlBotao = new JPanel(new GridLayout(2, 1, 0, 10));
        pnlBotao.setOpaque(false);
        JButton btnGerar = new JButton("Gerar Relatório");
        BarberStyle.applyButtonStyle(btnGerar);
        btnGerar.addActionListener(e -> gerar());
        pnlBotao.add(btnGerar);
        JButton btnExportar = new JButton("Exportar PDF");
        BarberStyle.applyButtonStyle(btnExportar);
        btnExportar.addActionListener(e -> exportarPDF());
        pnlBotao.add(btnExportar);
        pnlFiltros.add(pnlBotao, BorderLayout.EAST);

        centerPanel.add(pnlFiltros, BorderLayout.NORTH);


        JPanel pnlResultado = new JPanel(new BorderLayout());
        pnlResultado.setBackground(BarberStyle.COLOR_WHITE);
        pnlResultado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BarberStyle.COLOR_ACCENT, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel pnlTotals = new JPanel(new GridLayout(1, 4, 10, 0));
        pnlTotals.setOpaque(false);
        lblTotalAgend = new JLabel("Total Agendamentos: 0");
        lblTotalAgend.setFont(BarberStyle.FONT_LABEL);
        pnlTotals.add(lblTotalAgend);
        lblConcluidos = new JLabel("Concluídos: 0");
        lblConcluidos.setFont(BarberStyle.FONT_LABEL);
        pnlTotals.add(lblConcluidos);
        lblCancelados = new JLabel("Cancelados: 0");
        lblCancelados.setFont(BarberStyle.FONT_LABEL);
        pnlTotals.add(lblCancelados);
        lblPendentes = new JLabel("Pendentes: 0");
        lblPendentes.setFont(BarberStyle.FONT_LABEL);
        pnlTotals.add(lblPendentes);
        pnlResultado.add(pnlTotals, BorderLayout.NORTH);

        table = new JTable();
        table.setModel(new DefaultTableModel(new Object[]{"Data", "Hora", "Cliente", "Profissional", "Serviço", "Status", "Faturamento"}, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(table);
        pnlResultado.add(scroll, BorderLayout.CENTER);

        centerPanel.add(pnlResultado, BorderLayout.CENTER);

        // Painel de Faturamento Diário
        JPanel pnlFaturamento = new JPanel();
        pnlFaturamento.setBackground(BarberStyle.COLOR_WHITE);
        pnlFaturamento.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BarberStyle.COLOR_ACCENT, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        pnlFaturamento.setLayout(new BoxLayout(pnlFaturamento, BoxLayout.Y_AXIS));

        JLabel lblTituloFaturamento = new JLabel("Faturamento Diário");
        lblTituloFaturamento.setFont(BarberStyle.FONT_TITLE);
        lblTituloFaturamento.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlFaturamento.add(lblTituloFaturamento);

        lblFaturamentoHoje = new JLabel("R$ 0,00");
        lblFaturamentoHoje.setFont(BarberStyle.FONT_LABEL);
        lblFaturamentoHoje.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlFaturamento.add(lblFaturamentoHoje);

        centerPanel.add(pnlFaturamento, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Timer para atualização do faturamento diário
        timerFaturamento = new Timer(1000, e -> atualizarFaturamentoDiario());
        timerFaturamento.start();
    }

    private void atualizarFaturamentoDiario() {
        try {
            double totalFaturamento = 0.0;
            LocalDate hoje = LocalDate.now();
            List<Agendamento> agendamentosHoje = controller.listarTodosAgendamentos().stream()
                    .filter(a -> a.getData().isEqual(hoje))
                    .collect(Collectors.toList());

            for (Agendamento a : agendamentosHoje) {
                Servico s = controller.getServicoDAO().buscarPorId(a.getIdServico());
                if (s != null) {
                    totalFaturamento += s.getPreco();
                }
            }

            lblFaturamentoHoje.setText("MT" + String.format("%.2f", totalFaturamento).replace(".", ","));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar faturamento diário: " + ex.getMessage());
        }
    }

    private void gerar() {
        try {
            LocalDate inicio = ((java.util.Date) spInicio.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate fim = ((java.util.Date) spFim.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            List<Agendamento> agendamentos;
            if (rbGeral.isSelected()) {
                agendamentos = controller.listarTodosAgendamentos();
            } else {
                agendamentos = controller.listarTodosAgendamentos().stream()
                        .filter(a -> !a.getData().isBefore(inicio) && !a.getData().isAfter(fim))
                        .collect(Collectors.toList());
            }

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            long total = agendamentos.size();
            long concluidos = agendamentos.stream().filter(a -> "CONCLUIDO".equals(a.getStatus())).count();
            long cancelados = agendamentos.stream().filter(a -> "CANCELADO".equals(a.getStatus())).count();
            long pendentes = total - concluidos - cancelados;
            lblTotalAgend.setText("Total Agendamentos: " + total);
            lblConcluidos.setText("Concluídos: " + concluidos);
            lblCancelados.setText("Cancelados: " + cancelados);
            lblPendentes.setText("Pendentes: " + pendentes);

            for (Agendamento a : agendamentos) {
                Cliente c = controller.getClienteDAO().buscarPorId(a.getIdCliente());
                Profissional p = controller.getProfissionalDAO().buscarPorId(a.getIdProfissional());
                Servico s = controller.getServicoDAO().buscarPorId(a.getIdServico());
                double fat = "CONCLUIDO".equals(a.getStatus()) ? s.getPreco() : 0.0;
                model.addRow(new Object[]{
                        a.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        a.getHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm")),
                        c != null ? c.getNome() : "N/A",
                        p != null ? p.getNome() : "N/A",
                        s != null ? s.getNome() : "N/A",
                        a.getStatus(),
                        String.format("%.2f", fat)
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void exportarPDF() {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("relatorio.pdf"));
            document.open();
            document.add(new Paragraph("Relatório de Agendamentos"));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(lblTotalAgend.getText()));
            document.add(new Paragraph(lblConcluidos.getText()));
            document.add(new Paragraph(lblCancelados.getText()));
            document.add(new Paragraph(lblPendentes.getText()));
            document.add(new Paragraph(" "));

            PdfPTable pdfTable = new PdfPTable(7);
            pdfTable.addCell("Data");
            pdfTable.addCell("Hora");
            pdfTable.addCell("Cliente");
            pdfTable.addCell("Profissional");
            pdfTable.addCell("Serviço");
            pdfTable.addCell("Status");
            pdfTable.addCell("Faturamento");

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    pdfTable.addCell(model.getValueAt(i, j).toString());
                }
            }

            document.add(pdfTable);
            document.close();
            JOptionPane.showMessageDialog(this, "PDF gerado com sucesso: relatorio.pdf");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar PDF: " + ex.getMessage());
        }
    }
}

