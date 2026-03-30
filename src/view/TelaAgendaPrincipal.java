package view;

import controller.BarberController;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TelaAgendaPrincipal extends JFrame {
    private final BarberController controller;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private JPanel pnlAgenda;
    private TelaClientes pnlClientes;
    private TelaProfissionais pnlProfissionais;
    private TelaServicos pnlServicos;
    private TelaRelatorios pnlRelatorios;

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblDataAtual;
    private LocalDate dataExibicao;
    
    private JButton btnNovo;
    private JButton btnConcluir;
    private JButton btnCancelar;
    private Timer timer;
    private JLabel lblClock;

    public TelaAgendaPrincipal() {
        super("BarberSystem - Agendamento Desktop");
        this.controller = new BarberController();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);
        this.dataExibicao = LocalDate.now();

        initPanels();
        setJMenuBar(createMenuBar());

        add(mainPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        
        aplicarRestricoes();

        timer = new Timer(1000, e -> updateClock());
        timer.start();
    }

    private void initPanels() {
        pnlClientes = new TelaClientes(controller, () -> showCard("AGENDA"));
        pnlProfissionais = new TelaProfissionais(controller, () -> showCard("AGENDA"));
        pnlServicos = new TelaServicos(controller, () -> showCard("AGENDA"));
        pnlRelatorios = new TelaRelatorios(controller, () -> showCard("AGENDA"));

        initAgendaPanel();

        mainPanel.add(pnlAgenda, "AGENDA");
        mainPanel.add(pnlClientes, "CLIENTES");
        mainPanel.add(pnlProfissionais, "PROFISSIONAIS");
        mainPanel.add(pnlServicos, "SERVICOS");
        mainPanel.add(pnlRelatorios, "RELATORIOS");
    }

    private void initAgendaPanel() {
        pnlAgenda = new JPanel(new BorderLayout());
        pnlAgenda.setBackground(BarberStyle.COLOR_SECONDARY);


        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(BarberStyle.COLOR_PRIMARY);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel lblTitle = new JLabel("Agenda do Dia");
        lblTitle.setFont(BarberStyle.FONT_TITLE);
        lblTitle.setForeground(BarberStyle.COLOR_WHITE);
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        lblClock = new JLabel();
        lblClock.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblClock.setForeground(BarberStyle.COLOR_WHITE);
        pnlHeader.add(lblClock, BorderLayout.EAST);
        pnlAgenda.add(pnlHeader, BorderLayout.NORTH);

        JPanel pnlToolbar = new JPanel(new BorderLayout(10, 0));
        pnlToolbar.setBackground(BarberStyle.COLOR_WHITE);
        pnlToolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BarberStyle.COLOR_ACCENT),
                new EmptyBorder(10, 15, 10, 15)
        ));


        JPanel pnlEsquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlEsquerda.setOpaque(false);
        btnNovo = new JButton("+ Novo Agendamento");
        BarberStyle.applyButtonStyle(btnNovo);
        btnNovo.addActionListener(e -> {
            TelaNovoAgendamento tela = new TelaNovoAgendamento(this, controller);
            tela.setVisible(true);
            if (tela.isSalvo()) atualizarAgenda();
        });
        pnlEsquerda.add(btnNovo);
        pnlToolbar.add(pnlEsquerda, BorderLayout.WEST);


        JPanel pnlData = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlData.setOpaque(false);

        JButton btnAnterior = new JButton("◀ Anterior");
        BarberStyle.applyBackButtonStyle(btnAnterior);
        btnAnterior.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btnAnterior.addActionListener(e -> { dataExibicao = dataExibicao.minusDays(1); atualizarAgenda(); });

        lblDataAtual = new JLabel(dataExibicao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblDataAtual.setFont(BarberStyle.FONT_LABEL);
        lblDataAtual.setPreferredSize(new Dimension(100, 30));
        lblDataAtual.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnProximo = new JButton("Próximo ▶");
        BarberStyle.applyBackButtonStyle(btnProximo);
        btnProximo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btnProximo.addActionListener(e -> { dataExibicao = dataExibicao.plusDays(1); atualizarAgenda(); });

        pnlData.add(btnAnterior);
        pnlData.add(lblDataAtual);
        pnlData.add(btnProximo);
        pnlToolbar.add(pnlData, BorderLayout.CENTER);

        JPanel pnlDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlDireita.setOpaque(false);

        btnConcluir = new JButton("✔ Concluir");
        btnConcluir.setBackground(new Color(40, 167, 69));
        btnConcluir.setForeground(Color.WHITE);
        btnConcluir.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btnConcluir.setFocusPainted(false);
        btnConcluir.setBorder(new EmptyBorder(8, 16, 8, 16));
        btnConcluir.addActionListener(e -> atualizarStatus("CONCLUIDO"));

        btnCancelar = new JButton("✖ Cancelar");
        btnCancelar.setBackground(new Color(220, 53, 69));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorder(new EmptyBorder(8, 16, 8, 16));
        btnCancelar.addActionListener(e -> atualizarStatus("CANCELADO"));

        pnlDireita.add(btnConcluir);
        pnlDireita.add(btnCancelar);
        pnlToolbar.add(pnlDireita, BorderLayout.EAST);
        
        JPanel pnlTabela = new JPanel(new BorderLayout());
        pnlTabela.setBorder(new EmptyBorder(15, 20, 20, 20));
        pnlTabela.setOpaque(false);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Hora", "Cliente", "Profissional", "Serviço", "Status"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setBackground(BarberStyle.COLOR_PRIMARY);
        table.getTableHeader().setForeground(BarberStyle.COLOR_WHITE);
        table.getTableHeader().setFont(BarberStyle.FONT_LABEL);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BarberStyle.COLOR_ACCENT),
                "Agendamentos do Dia"
        ));
        pnlTabela.add(scroll, BorderLayout.CENTER);

        JPanel contentWrap = new JPanel(new BorderLayout());
        contentWrap.setOpaque(false);
        contentWrap.add(pnlToolbar, BorderLayout.NORTH);
        contentWrap.add(pnlTabela, BorderLayout.CENTER);
        pnlAgenda.add(contentWrap, BorderLayout.CENTER);

        atualizarAgenda();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(BarberStyle.COLOR_WHITE);

        JMenu menuNavegacao = new JMenu("Navegação");
        NivelAcesso nivel = SessaoUsuario.getInstance().getNivelAcesso();

        JMenuItem miAgenda = new JMenuItem("Agenda Principal");
        miAgenda.addActionListener(e -> showCard("AGENDA"));
        menuNavegacao.add(miAgenda);

        if (nivel == NivelAcesso.ADMINISTRADOR || nivel == NivelAcesso.FUNCIONARIO) {
            JMenuItem miClientes = new JMenuItem("Gerir Clientes");
            miClientes.addActionListener(e -> showCard("CLIENTES"));
            menuNavegacao.add(miClientes);
        }

        if (nivel == NivelAcesso.ADMINISTRADOR) {
            JMenuItem miProfissionais = new JMenuItem("Gerir Profissionais");
            miProfissionais.addActionListener(e -> showCard("PROFISSIONAIS"));
            menuNavegacao.add(miProfissionais);

            JMenuItem miServicos = new JMenuItem("Gerir Serviços");
            miServicos.addActionListener(e -> showCard("SERVICOS"));
            menuNavegacao.add(miServicos);

            JMenuItem miRelatorios = new JMenuItem("Relatórios");
            miRelatorios.addActionListener(e -> showCard("RELATORIOS"));
            menuNavegacao.add(miRelatorios);
        }

        menuBar.add(menuNavegacao);
        menuBar.add(Box.createHorizontalGlue());

        // Menu de Usuário / Login
        if (nivel == null) {
            // Usuário não autenticado - mostrar botão de Login
            JMenu menuLogin = new JMenu("Acesso");
            JMenuItem miLogin = new JMenuItem("🔐 Login (Admin/Profissional)");
            miLogin.addActionListener(e -> abrirTelaLogin());
            menuLogin.add(miLogin);
            menuBar.add(menuLogin);
        } else {
            // Usuário autenticado - mostrar nome e opção de Sair
            JMenu menuUsuario = new JMenu("👤 " + SessaoUsuario.getInstance().getUsuario());
            JMenuItem miSair = new JMenuItem("Sair");
            miSair.addActionListener(e -> sairSistema());
            menuUsuario.add(miSair);
            menuBar.add(menuUsuario);
        }
        
        return menuBar;
    }

    private void abrirTelaLogin() {
        TelaLogin login = new TelaLogin();
        login.setVisible(true);
        if (login.isAutenticado()) {
            // Atualizar a tela após login bem-sucedido
            setJMenuBar(createMenuBar());
            atualizarAgenda();
            aplicarRestricoes();
        }
    }

    private void sairSistema() {
        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja sair da sua conta?",
                "Confirmar Saída",
                JOptionPane.YES_NO_OPTION
        );
        if (resposta == JOptionPane.YES_OPTION) {
            SessaoUsuario.getInstance().logout();
            setJMenuBar(createMenuBar());
            atualizarAgenda();
            aplicarRestricoes();
        }
    }

    private void aplicarRestricoes() {
        NivelAcesso nivel = SessaoUsuario.getInstance().getNivelAcesso();
        if (nivel == NivelAcesso.CLIENTE) {
            btnConcluir.setVisible(false);
            btnCancelar.setVisible(false);
        }
    }

    private void showCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
        if (cardName.equals("AGENDA")) atualizarAgenda();
        if (cardName.equals("CLIENTES")) pnlClientes.atualizarTabela();
        if (cardName.equals("PROFISSIONAIS")) pnlProfissionais.atualizarTabela();
        if (cardName.equals("SERVICOS")) pnlServicos.atualizarTabela();
    }

    private void atualizarAgenda() {
        lblDataAtual.setText(dataExibicao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        tableModel.setRowCount(0);
        List<Agendamento> agendamentos = controller.listarAgendamentosPorData(dataExibicao);

        NivelAcesso nivel = SessaoUsuario.getInstance().getNivelAcesso();
        int idVinculado = SessaoUsuario.getInstance().getIdVinculado();

        if (nivel == NivelAcesso.CLIENTE) {
            agendamentos = agendamentos.stream()
                    .filter(a -> a.getIdCliente() == idVinculado)
                    .collect(Collectors.toList());
        }

        for (Agendamento a : agendamentos) {
            Cliente c = controller.getClienteDAO().buscarPorId(a.getIdCliente());
            Profissional p = controller.getProfissionalDAO().buscarPorId(a.getIdProfissional());
            Servico s = controller.getServicoDAO().buscarPorId(a.getIdServico());

            tableModel.addRow(new Object[]{
                    a.getId(),
                    a.getHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm")),
                    c != null ? c.getNome() : "N/A",
                    p != null ? p.getNome() : "N/A",
                    s != null ? s.getNome() : "N/A",
                    a.getStatus()
            });
        }
    }

    private void atualizarStatus(String status) {
        int row = table.getSelectedRow();
        if (row != -1) {
            int id = (int) tableModel.getValueAt(row, 0);
            controller.atualizarStatusAgendamento(id, status);
            atualizarAgenda();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um agendamento na tabela.");
        }
    }

    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        lblClock.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
}
